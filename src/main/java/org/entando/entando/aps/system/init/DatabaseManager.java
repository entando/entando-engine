/*
 * Copyright 2015-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.entando.aps.system.init;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.aps.util.DateConverter;
import com.agiletec.aps.util.FileTextReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSetStatus;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.io.output.StringBuilderWriter;
import org.entando.entando.aps.system.init.IInitializerManager.DatabaseMigrationStrategy;
import org.entando.entando.aps.system.init.exception.DatabaseMigrationException;
import org.entando.entando.aps.system.init.model.Component;
import org.entando.entando.aps.system.init.model.ComponentInstallationReport;
import org.entando.entando.aps.system.init.model.DataSourceDumpReport;
import org.entando.entando.aps.system.init.model.LiquibaseInstallationReport;
import org.entando.entando.aps.system.init.model.SystemInstallationReport;
import org.entando.entando.aps.system.init.model.SystemInstallationReport.Status;
import org.entando.entando.aps.system.init.util.TableDataUtils;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.entando.entando.aps.system.services.storage.StorageManagerUtil;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ServletContextAware;

/**
 * @author E.Santoboni
 */
public class DatabaseManager extends AbstractInitializerManager
        implements IDatabaseManager, IDatabaseInstallerManager, ServletContextAware {

    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(DatabaseManager.class);

    private static final String LOG_PREFIX = "|   ";
    public static final int STATUS_READY = 0;
    public static final int STATUS_DUMPING_IN_PROGRESS = 1;
    public static final String INIT_MSG_L = "+ [ Component: {} ] :: Liquibase\n{}";
    private static final String LIQUIBASE_CHANGELOG_TABLE = "DATABASECHANGELOG";

    private Map<String, Resource> defaultSqlDump;
    private int status;

    private DatabaseDumper databaseDumper;
    private DatabaseRestorer databaseRestorer;
    private List<DataSource> defaultDataSources;

    private ServletContext servletContext;

    public void init() {
        logger.debug("{} ready", this.getClass().getName());
    }

    @Override
    public SystemInstallationReport installDatabase(SystemInstallationReport report, DatabaseMigrationStrategy migrationStrategy) throws Exception {
        String lastLocalBackupFolder = null;
        migrationStrategy = (null == migrationStrategy) ? DatabaseMigrationStrategy.DISABLED : migrationStrategy;
        if (null == report) {
            report = SystemInstallationReport.getInstance();
            lastLocalBackupFolder = checkRestore(report, migrationStrategy);
        }

        // Check if we are dealing with an old database version (not Liquibase compliant - Entando <= 6.3.2)
        legacyDatabaseCheck();

        try {
            initComponents(report, migrationStrategy);
            if (DatabaseMigrationStrategy.AUTO.equals(migrationStrategy) && report.getStatus()
                    .equals(Status.RESTORE)) {
                //ALTER SESSION SET NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH:MI:SS.FF'
                if (null != lastLocalBackupFolder) {
                    this.restoreBackup(lastLocalBackupFolder);
                } else {
                    this.restoreDefaultDump();
                }
            }
        } catch (DatabaseMigrationException de) {
            throw de;
        } catch (Throwable t) {
            logger.error("Error while initializating Db Installer", t);
            throw new Exception("Error while initializating Db Installer", t);
        } finally {
            if (null != report && report.isUpdated()) {
                report.setUpdated();
                report.setStatus(Status.INCOMPLETE);
            }
        }
        return report;
    }

    private String checkRestore(SystemInstallationReport report, DatabaseMigrationStrategy migrationStrategy) {
        String lastLocalBackupFolder = null;
        if (!DatabaseMigrationStrategy.DISABLED.equals(migrationStrategy) && !Environment.test.equals(this.getEnvironment())) {
            // There's no local database installed, lookup in the local databases
            DataSourceDumpReport lastDumpReport = this.getLastDumpReport();
            if (null != lastDumpReport) {
                lastLocalBackupFolder = lastDumpReport.getSubFolderName();
                report.setStatus(Status.RESTORE);
            } else {
                // Try with the default db dump
                Map<String, Resource> sqlDump = this.getDefaultSqlDump();
                if (null != sqlDump && sqlDump.size() > 0) {
                    report.setStatus(Status.RESTORE);
                }
            }
        }
        return lastLocalBackupFolder;
    }

    private void initComponents(SystemInstallationReport report, DatabaseMigrationStrategy migrationStrategy) throws DatabaseMigrationException, EntException {
        List<Component> components = this.getComponentManager().getCurrentComponents();
        Map<String, List<ChangeSetStatus>> pendingChangeSetMap = new HashMap<>();
        for (Component entandoComponentConfiguration : components) {
            List<ChangeSetStatus> pendingChangeSet = this.initLiquiBaseResources(entandoComponentConfiguration,
                    report, migrationStrategy);
            if (!pendingChangeSet.isEmpty()) {
                pendingChangeSetMap.put(entandoComponentConfiguration.getCode(), pendingChangeSet);
            }
        }
        if (!pendingChangeSetMap.isEmpty()) {
            throw new DatabaseMigrationException(pendingChangeSetMap);
        }
    }

    private void legacyDatabaseCheck() throws DatabaseMigrationException, SQLException {

        for (DataSource dataSource : defaultDataSources) {

            Connection connection = null;
            ResultSet rs = null;
            try {
                connection = dataSource.getConnection();
                final DatabaseMetaData databaseMetaData = connection.getMetaData();

                // Liquibase tables are relevant only if related to portdb/servdb schema/catalog, we are not interested in others.
                // Each DB vendor has a different objects hierarchy so we can't check only for schema or catalog
                rs = databaseMetaData.getTables(connection.getCatalog(), connection.getSchema(), null,
                        new String[]{"TABLE"});

                boolean liquibaseChangelogTableFound = false;
                int tablesCount = 0;
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    if (tableName.equalsIgnoreCase(LIQUIBASE_CHANGELOG_TABLE)) {
                        liquibaseChangelogTableFound = true;
                    }
                    tablesCount++;
                }

                // If we have some tables in the DB but Liquibase changelog table is not found we are running on a legacy DB
                if (!liquibaseChangelogTableFound && tablesCount > 0) {
                    throw new DatabaseMigrationException(
                            "Detected an Entando 6.x database on datasource " + databaseMetaData.getURL()
                                    + ". Please refer to dev.entando.org on how to prepare the database for Entando 7");
                }
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception e) { /* Ignored */ }
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception e) { /* Ignored */ }
            }
        }
    }

    //---------------- DATA ------------------- START

    public List<ChangeSetStatus> initLiquiBaseResources(Component componentConfiguration, SystemInstallationReport report, DatabaseMigrationStrategy migrationStrategy) throws EntException {
        List<ChangeSetStatus> pendingChangeSet = new ArrayList<>();
        logger.info(INIT_MSG_L, componentConfiguration.getCode(), LOG_PREFIX);
        ComponentInstallationReport componentReport = report.getComponentReport(componentConfiguration.getCode(), true);
        LiquibaseInstallationReport liquibaseReport = componentReport.getLiquibaseReport();
        try {
            ApsSystemUtils.directStdoutTrace(LOG_PREFIX + "Starting installation\n" + LOG_PREFIX);
            String[] dataSourceNames = this.extractBeanNames(DataSource.class);
            for (String dataSourceName : dataSourceNames) {
                String changeLogFile = (null != componentConfiguration.getLiquibaseChangeSets()) ? componentConfiguration.getLiquibaseChangeSets().get(dataSourceName) : null;
                if (null != changeLogFile) {
                    liquibaseReport.getDatabaseStatus().put(dataSourceName, Status.INCOMPLETE);
                    List<ChangeSetStatus> changeSetToExecute = this.executeLiquibaseUpdate(report.getCreation(), 
                            componentConfiguration.getCode(), changeLogFile, dataSourceName, report.getStatus(), migrationStrategy);
                    pendingChangeSet.addAll(changeSetToExecute);
                    ApsSystemUtils.directStdoutTrace("|   ( ok )  " + dataSourceName);
                    if (!DatabaseMigrationStrategy.DISABLED.equals(migrationStrategy)) {
                        liquibaseReport.getDatabaseStatus().put(dataSourceName, Status.OK);
                    } else {
                        liquibaseReport.getDatabaseStatus().put(dataSourceName, Status.SKIPPED);
                    }
                }
            }
            if (report.getStatus().equals(SystemInstallationReport.Status.RESTORE) || report.getStatus().equals(SystemInstallationReport.Status.PORTING)) {
                componentReport.setPostProcessStatus(report.getStatus());
            }
            ApsSystemUtils.directStdoutTrace(LOG_PREFIX + "\n" + LOG_PREFIX + "Installation complete\n" + LOG_PREFIX);
            logger.debug(LOG_PREFIX + "\n" + LOG_PREFIX + "Installation complete\n" + LOG_PREFIX);
        } catch (Throwable t) {
            logger.error("Error executing liquibase initialization for component {}", componentConfiguration.getCode(), t);
            throw new EntException("Error executing liquibase initialization for component " + componentConfiguration.getCode(), t);
        }
        return pendingChangeSet;
    }

    private List<ChangeSetStatus> executeLiquibaseUpdate(Date timestamp, String componentCode,
            String changeLogFile, String dataSourceName, Status status, DatabaseMigrationStrategy migrationStrategy)
            throws LiquibaseException, IOException, SQLException {
        List<ChangeSetStatus> changeSetToExecute = new ArrayList<>();
        Connection connection = null;
        Liquibase liquibase = null;
        Writer writer = null;
        try {
            DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
            connection = dataSource.getConnection();
            JdbcConnection liquibaseConnection = new JdbcConnection(connection);
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(liquibaseConnection);
            liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database); // NOSONAR
            Contexts contexts = getContexts(status);
            if (DatabaseMigrationStrategy.AUTO.equals(migrationStrategy)) {
                liquibase.update(contexts, new LabelExpression());
            } else {
                List<ChangeSetStatus> statusList = liquibase.getChangeSetStatuses(contexts, new LabelExpression());
                changeSetToExecute = statusList.stream().filter(ChangeSetStatus::getWillRun).collect(Collectors.toList());
                if (!changeSetToExecute.isEmpty()) {
                    String messagePrefix = "Component '" + componentCode + "', database '" + dataSourceName + "'";
                    changeSetToExecute.stream().forEach(cs -> logger.warn("{}, changeSet '{}' has to be executed manually", messagePrefix, cs.getChangeSet().getId()));
                    writer = new StringBuilderWriter();
                    liquibase.update(contexts, new LabelExpression(), writer);
                    if (DatabaseMigrationStrategy.GENERATE_SQL.equals(migrationStrategy)) {
                        ByteArrayInputStream stream = new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8));
                        String path = "liquibase" + File.separator + DateConverter.getFormattedDate(timestamp, "yyyyMMddHHmmss") + File.separator + componentCode + "_" + dataSourceName + ".sql";
                        this.getStorageManager().saveFile(path, true, stream);
                        logger.warn("{}, Please find the update SQL script under \"<PROTECTED_ENTANDO_DATA>{}{}\"", messagePrefix, File.separator, path);
                    } else {
                        logger.warn("{}, Please update the DB manually", messagePrefix);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error executing liquibase update - " + changeLogFile, e);
        } finally {
            if (null != writer) {
                writer.close();
            }
            if (null != liquibase) {
                handleLiquibaseClose(liquibase);
            }
            if (null != connection) {
                connection.close();
            }
        }
        return changeSetToExecute;
    }

    private Contexts getContexts(Status status) {
        String context = null;
        if (status.equals(Status.RESTORE)) {
            context = "restore";
        } else {
            context = (this.getEnvironment().toString().equalsIgnoreCase("test")) ? "test" : "production";
        }
        return new Contexts(context);
    }

    private void handleLiquibaseClose(Liquibase liquibase) throws LiquibaseException {
        try {
            liquibase.close();
        } catch (DatabaseException ex) {
            if ("Error closing derby cleanly".equals(ex.getMessage())) {
                logger.warn("Error while closing Liquibase connection. This is a known issue when running the application on Derby and Wildfly");
            } else {
                logger.error("Unexpected error while closing Liquibase connection", ex);
            }
        }
    }

    private void restoreDefaultDump() throws EntException {
        try {
            String[] dataSourceNames = this.extractBeanNames(DataSource.class);
            Map<String, Resource> defaultDump = this.getDefaultSqlDump();
            if (null == defaultDump || defaultDump.isEmpty()) {
                return;
            }
            for (String dataSourceName : dataSourceNames) {
                DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
                Resource resource = defaultDump.get(dataSourceName);
                String script = this.readFile(resource);
                if (null != script && script.trim().length() > 0) {
                    this.getDatabaseRestorer().initOracleSchema(dataSource);
                    TableDataUtils.valueDatabase(script, dataSourceName, dataSource);
                }
            }
        } catch (Throwable t) {
            logger.error("Error restoring default Dump", t);
            throw new EntException("Error restoring default Dump", t);
        }
    }

    private String readFile(Resource resource) throws Throwable {
        if (resource == null) {
            return null;
        }
        InputStream is = null;
        String text = null;
        try {
            is = resource.getInputStream();
            if (null == is) {
                return null;
            }
            text = FileTextReader.getText(is, "UTF-8");
        } catch (Throwable t) {
            logger.error("Error reading resource", t);
            throw new EntException("Error reading resource", t);
        } finally {
            if (null != is) {
                is.close();
            }
        }
        return text;
    }

    //---------------- DATA ------------------- END
    @Override
    public void createBackup() throws EntException {
        if (this.getStatus() != STATUS_READY) {
            return;
        }
        try {
            this.setStatus(DatabaseManager.STATUS_DUMPING_IN_PROGRESS);
            DatabaseDumperThread thread = new DatabaseDumperThread(this);
            String threadName = "DatabaseDumper_" + DateConverter.getFormattedDate(new Date(), "yyyyMMddHHmmss");
            thread.setName(threadName);
            thread.start();
        } catch (Throwable t) {
            this.setStatus(DatabaseManager.STATUS_READY);
            logger.error("Error while creating backup", t);
            throw new EntException("Error while creating backup", t);
        }
    }

    protected void executeBackup() throws EntException {
        try {
            this.getDatabaseDumper().createBackup(this.getEnvironment(), this.extractReport());
        } catch (Throwable t) {
            logger.error("Error while creating backup", t);
            throw new EntException("Error while creating backup", t);
        } finally {
            this.setStatus(DatabaseManager.STATUS_READY);
        }
    }

    @Override
    public void deleteBackup(String subFolderName) throws EntException {
        try {
            String baseDir = this.getLocalBackupsFolder();
            String directoryName = baseDir + subFolderName;
            this.getStorageManager().deleteDirectory(directoryName, true);
        } catch (Throwable t) {
            logger.error("Error while deleting backup", t);
            throw new EntException("Error while deleting backup", t);
        }
    }

    protected DataSourceDumpReport getLastDumpReport() {
        if (Environment.develop.equals(this.getEnvironment())) {
            return this.getBackupReport(this.getEnvironment().toString());
        }
        List<DataSourceDumpReport> reports = this.getBackupReports();
        if (null == reports || reports.isEmpty()) {
            return null;
        }
        return reports.get(reports.size() - 1);
    }

    @Override
    public DataSourceDumpReport getBackupReport(String subFolderName) {
        try {
            if (this.checkBackupFolder(subFolderName)) {
                return this.getDumpReport(subFolderName);
            }
        } catch (Throwable t) {
            logger.error("Error while extracting Backup Report of subfolder {}", subFolderName, t);
            throw new RuntimeException("Error while extracting Backup Report of subfolder " + subFolderName);
        }
        return null;
    }

    @Override
    public List<DataSourceDumpReport> getBackupReports() {
        List<DataSourceDumpReport> reports = new ArrayList<DataSourceDumpReport>();
        try {
            String[] children = this.getStorageManager().listDirectory(this.getLocalBackupsFolder(), true); //backupsFolder.list();
            if (null == children || children.length == 0) {
                return null;
            }
            for (String subFolderName : children) {
                if (this.checkBackupFolder(subFolderName)) {
                    DataSourceDumpReport report = this.getDumpReport(subFolderName);
                    reports.add(report);
                }
            }
            Collections.sort(reports, new BeanComparator("date"));
        } catch (Throwable t) {
            logger.error("Error while extracting Backup Reports", t);
            throw new RuntimeException("Error while extracting Backup Reports");
        }
        return reports;
    }

    private boolean checkBackupFolder(String subFolderName) throws EntException, IOException {
        String localBackupFolderRoot = this.getLocalBackupsFolder();
        String reportFileName = localBackupFolderRoot + subFolderName + File.separator + DUMP_REPORT_FILE_NAME;

        if (StorageManagerUtil.doesPathContainsPath(localBackupFolderRoot, reportFileName)) {
            if (!this.getStorageManager().exists(reportFileName, true)) {
                logger.warn("dump report file name not found in path {}", reportFileName);
                return false;
            }
        } else {
            throw new EntRuntimeException(
                    String.format("Path validation failed: \"%s\" not in \"%s\"", reportFileName, localBackupFolderRoot)
            );
        }

        return true;
    }

    private DataSourceDumpReport getDumpReport(String subFolderName) throws EntException {
        InputStream is = null;
        DataSourceDumpReport report = null;
        try {
            String key = this.getLocalBackupsFolder() + subFolderName + File.separator + DUMP_REPORT_FILE_NAME;
            is = this.getStorageManager().getStream(key, true);
            String xml = FileTextReader.getText(is);
            report = new DataSourceDumpReport(xml);
        } catch (Throwable t) {
            logger.error("Error while extracting Dump Report of subfolder {}", subFolderName, t);
            throw new RuntimeException("Error while extracting Dump Report of subfolder " + subFolderName);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }
        }
        return report;
    }

    @Override
    public boolean dropAndRestoreBackup(String subFolderName) throws EntException {
        try {
            if (!this.checkBackupFolder(subFolderName)) {
                logger.error("backup not available - subfolder '{}'", subFolderName);
                return false;
            }
            //TODO future improvement - execute 'lifeline' backup
            this.getDatabaseRestorer().dropAndRestoreBackup(subFolderName);
            ApsWebApplicationUtils.executeSystemRefresh(this.getServletContext());
            return true;
        } catch (Throwable t) {
            //TODO future improvement - restore 'lifeline' backup
            logger.error("Error while restoring backup - subfolder {}", subFolderName, t);
            throw new EntException("Error while restoring backup - subfolder " + subFolderName, t);
        } finally {
            //TODO future improvement - delete 'lifeline' backup
        }
    }

    private boolean restoreBackup(String subFolderName) throws EntException {
        try {
            if (!this.checkBackupFolder(subFolderName)) {
                logger.error("backup not available - subfolder '{}'", subFolderName);
                return false;
            }
            this.getDatabaseRestorer().restoreBackup(subFolderName);
            return true;
        } catch (Throwable t) {
            logger.error("Error while restoring local backup", t);
            throw new EntException("Error while restoring local backup", t);
        }
    }

    private String[] extractBeanNames(Class beanClass) {
        ListableBeanFactory factory = (ListableBeanFactory) this.getBeanFactory();
        return factory.getBeanNamesForType(beanClass);
    }

    @Override
    public InputStream getTableDump(String tableName, String dataSourceName, String subFolderName) throws EntException {
        try {
            if (null == subFolderName) {
                return null;
            }
            StringBuilder fileName = new StringBuilder(this.getLocalBackupsFolder());
            fileName.append(subFolderName).append(File.separator)
                    .append(dataSourceName).append(File.separator).append(tableName).append(".sql");
            return this.getStorageManager().getStream(fileName.toString(), true);
        } catch (Throwable t) {
            logger.error("Error while extracting table dump - " + "table '{}' - datasource '{}' - SubFolder '{}'", tableName, dataSourceName, subFolderName, t);
            throw new RuntimeException("Error while extracting table dump - " + "table '" + tableName + "' - datasource '" + dataSourceName + "' - SubFolder '" + subFolderName + "'", t);
        }
    }

    @Override
    public DatabaseType getDatabaseType(DataSource dataSource) throws EntException {
        return this.getDatabaseRestorer().getType(dataSource);
    }

    private IStorageManager getStorageManager() {
        return (IStorageManager) this.getBeanFactory().getBean("StorageManager");
    }

    protected String getLocalBackupsFolder() {
        return this.getDatabaseDumper().getLocalBackupsFolder();
    }

    protected Map<String, Resource> getDefaultSqlDump() {
        return defaultSqlDump;
    }

    public void setDefaultSqlDump(Map<String, Resource> defaultSqlDump) {
        this.defaultSqlDump = defaultSqlDump;
    }

    @Override
    public int getStatus() {
        return status;
    }

    protected void setStatus(int status) {
        this.status = status;
    }

    protected DatabaseDumper getDatabaseDumper() {
        return databaseDumper;
    }

    public void setDatabaseDumper(DatabaseDumper databaseDumper) {
        this.databaseDumper = databaseDumper;
    }

    protected DatabaseRestorer getDatabaseRestorer() {
        return databaseRestorer;
    }

    public void setDatabaseRestorer(DatabaseRestorer databaseRestorer) {
        this.databaseRestorer = databaseRestorer;
    }

    protected ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setDefaultDataSources(List<DataSource> defaultDataSources) {
        this.defaultDataSources = defaultDataSources;
    }
}

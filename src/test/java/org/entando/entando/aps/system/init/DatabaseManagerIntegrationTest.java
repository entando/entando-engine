/*
 * Copyright 2021-Present Entando Inc. (http://www.entando.com) All rights reserved.
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

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;
import java.io.File;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.entando.entando.aps.system.init.model.Component;
import org.entando.entando.aps.system.init.model.ComponentInstallationReport;
import org.entando.entando.aps.system.init.model.DataSourceDumpReport;
import org.entando.entando.aps.system.init.model.LiquibaseInstallationReport;
import org.entando.entando.aps.system.init.model.SystemInstallationReport;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DatabaseManagerIntegrationTest extends BaseTestCase {

    private IDatabaseManager databaseManager = null;
    private IStorageManager storageManager = null;
    private IComponentManager componentManager = null;

    @BeforeEach
    private void init() throws Exception {
        this.databaseManager = (IDatabaseManager) this.getApplicationContext().getBean("DatabaseManager");
        this.storageManager = (IStorageManager) this.getApplicationContext().getBean(SystemConstants.STORAGE_MANAGER);
        this.componentManager = (IComponentManager) this.getApplicationContext().getBean(IComponentManager.class);
    }

    @Test
    void testReports() throws Exception {
        List<DataSourceDumpReport> reports = this.databaseManager.getBackupReports();
        Assertions.assertNull(reports);
    }

    @Test
    void testCreateDeleteBackup() throws Exception {
        Component mainComponent = this.componentManager.getInstalledComponent(IComponentManager.MAIN_COMPONENT);
        Assertions.assertNotNull(mainComponent);
        Map<String, List<String>> tables = mainComponent.getTableNames();
        Assertions.assertEquals(13, tables.get("portDataSource").size());
        Assertions.assertEquals(17, tables.get("servDataSource").size());
        try {
            this.databaseManager.createBackup();
            BaseTestCase.waitThreads("DatabaseDumper_");
            List<DataSourceDumpReport> reports = this.databaseManager.getBackupReports();
            Assertions.assertNotNull(reports);
            Assertions.assertEquals(1, reports.size());
            DataSourceDumpReport report = reports.get(0);

            List<ComponentInstallationReport> compReports = report.getComponentsHistory();
            Assertions.assertEquals(1, compReports.size());
            ComponentInstallationReport engineReport = compReports.get(0);
            Assertions.assertEquals(SystemInstallationReport.Status.OK, engineReport.getStatus());
            LiquibaseInstallationReport liquibaseReport = engineReport.getLiquibaseReport();
            Assertions.assertEquals(SystemInstallationReport.Status.OK, liquibaseReport.getStatus());
            Assertions.assertNotNull(liquibaseReport);
            Assertions.assertEquals(2, liquibaseReport.getDatabaseStatus().size());

            String reportFolder = report.getSubFolderName();
            String[] datasourceNames = this.getApplicationContext().getBeanNamesForType(DataSource.class);
            Assertions.assertEquals(2, datasourceNames.length);
            for (int i = 0; i < datasourceNames.length; i++) {
                String dataSourceName = datasourceNames[i];
                List<String> tableNames = mainComponent.getTableNames().get(dataSourceName);
                for (int j = 0; j < tableNames.size(); j++) {
                    String tableName = tableNames.get(j);
                    String filepath = this.getLocalBackupsFolder() + reportFolder + File.separator + dataSourceName + File.separator + tableName + ".sql";
                    Assertions.assertTrue(this.storageManager.exists(filepath, true));
                }
            }
            boolean result = this.databaseManager.dropAndRestoreBackup(reportFolder);
            Assertions.assertTrue(result);
            this.databaseManager.deleteBackup(reportFolder);
            reports = this.databaseManager.getBackupReports();
            Assertions.assertNull(reports);
        } catch (Exception e) {
            throw e;
        } finally {
            this.storageManager.deleteDirectory(this.getLocalBackupsFolder(), true);
        }
    }

    protected String getLocalBackupsFolder() {
        StringBuilder dirName = new StringBuilder();
        dirName.append("databaseBackups").append(File.separator);
        return dirName.toString();
    }

}

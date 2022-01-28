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

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.entando.entando.ent.exception.EntException;
import org.entando.entando.aps.system.init.cache.IInitializerManagerCacheWrapper;
import org.entando.entando.aps.system.init.model.Component;
import org.entando.entando.aps.system.init.model.ComponentEnvironment;
import org.entando.entando.aps.system.init.model.ComponentInstallationReport;
import org.entando.entando.aps.system.init.model.IPostProcess;
import org.entando.entando.aps.system.init.model.InvalidPostProcessResultException;
import org.entando.entando.aps.system.init.model.SystemInstallationReport;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;

/**
 * @author E.Santoboni
 */
public class InitializerManager extends AbstractInitializerManager implements IInitializerManager {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(getClass());

    public static final String REPORT_CONFIG_ITEM = "entandoComponentsReport";

    private IInitializerManagerCacheWrapper cacheWrapper;

    private String migrationStrategy = DatabaseMigrationStrategy.DISABLED.toString();

    private Map<String, IPostProcessor> postProcessors;

    private IDatabaseManager databaseManager;

    @Override
    public SystemInstallationReport getCurrentReport() {
        return this.getCacheWrapper().getReport();
    }

    protected IInitializerManagerCacheWrapper getCacheWrapper() {
        return cacheWrapper;
    }

    public void setCacheWrapper(IInitializerManagerCacheWrapper cacheWrapper) {
        this.cacheWrapper = cacheWrapper;
    }

    public String getMigrationStrategy() {
        return migrationStrategy;
    }

    public void setMigrationStrategy(String migrationStrategy) {
        this.migrationStrategy = migrationStrategy;
    }

    public Map<String, IPostProcessor> getPostProcessors() {
        return postProcessors;
    }

    public void setPostProcessors(Map<String, IPostProcessor> postProcessors) {
        this.postProcessors = postProcessors;
    }

    protected IDatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public void setDatabaseManager(IDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    private DatabaseMigrationStrategy getMigrationStrategyEnum() throws EntException {
        try {
            return Enum.valueOf(DatabaseMigrationStrategy.class, this.getMigrationStrategy().toUpperCase());
        } catch (IllegalArgumentException e) {
            String message = "Migration Strategy - Invalid value '" + this.getMigrationStrategy() + "' - Allowed values disabled|auto|generate_sql";
            throw new EntException(message, e);
        }
    }

    public void init() throws Exception {
        SystemInstallationReport report = null;
        try {
            DatabaseMigrationStrategy migrationStrategyEnum = this.getMigrationStrategyEnum();
            report = this.extractReport();
            report = ((IDatabaseInstallerManager) this.getDatabaseManager()).installDatabase(report, migrationStrategyEnum);
            this.getCacheWrapper().initCache(report);
        } catch (Throwable t) {
            logger.error("Error while initializating Db Installer", t);
            throw new Exception("Error while initializating Db Installer", t);
        } finally {
            if (null != report && report.isUpdated()) {
                this.saveReport(report);
            }
        }
        logger.debug("{}: initialized - migration strategy {}", this.getClass().getName(), this.getMigrationStrategy());
    }

    public void executePostInitProcesses() throws BeansException {
        SystemInstallationReport report = null;
        try {
            report = this.extractReport();
            List<Component> components = this.getComponentManager().getCurrentComponents();
            for (Component component : components) {
                executeComponentPostInitProcesses(component, report);
            }
        } catch (Throwable t) {
            logger.error("Error while executing post processes", t);
            throw new FatalBeanException("Error while executing post processes", t);
        } finally {
            if (null != report && report.isUpdated()) {
                this.saveReport(report);
            }
        }
    }

    public void executeComponentPostInitProcesses(Component component, SystemInstallationReport report) throws EntException {
        ComponentInstallationReport componentReport = report.getComponentReport(component.getCode(), false);
        SystemInstallationReport.Status postProcessStatus = componentReport.getPostProcessStatus();
        if (!postProcessStatus.equals(SystemInstallationReport.Status.INIT)) {
            return;
        }
        String compEnvKey = (AbstractInitializerManager.Environment.test.equals(this.getEnvironment()))
                ? AbstractInitializerManager.Environment.test.toString() : AbstractInitializerManager.Environment.production.toString();
        ComponentEnvironment componentEnvironment = (null != component.getEnvironments()) ? component.getEnvironments().get(compEnvKey) : null;
        List<IPostProcess> postProcesses = (null != componentEnvironment) ? componentEnvironment.getPostProcesses() : null;
        if (null == postProcesses || postProcesses.isEmpty()) {
            postProcessStatus = SystemInstallationReport.Status.NOT_AVAILABLE;
        } else if (DatabaseMigrationStrategy.DISABLED.equals(this.getMigrationStrategyEnum())) {
            postProcessStatus = SystemInstallationReport.Status.SKIPPED;
        } else if (componentReport.getStatus().equals(SystemInstallationReport.Status.RESTORE) || componentReport.getStatus().equals(SystemInstallationReport.Status.PORTING)) {
            //Porting or restore
            postProcessStatus = componentReport.getStatus();
        } else {
            postProcessStatus = this.executePostProcesses(postProcesses);
        }
        componentReport.setPostProcessStatus(postProcessStatus);
        report.setUpdated();
    }

    protected SystemInstallationReport.Status executePostProcesses(List<IPostProcess> postProcesses) throws EntException {
        if (null == postProcesses || postProcesses.isEmpty()) {
            return SystemInstallationReport.Status.NOT_AVAILABLE;
        }
        for (int i = 0; i < postProcesses.size(); i++) {
            IPostProcess postProcess = postProcesses.get(i);
            try {
                IPostProcessor postProcessor = this.getPostProcessors().get(postProcess.getCode());
                if (null != postProcessor) {
                    postProcessor.executePostProcess(postProcess);
                } else {
                    logger.error("Missing Post Processor for process '{}'", postProcess.getCode());
                }
            } catch (InvalidPostProcessResultException t) {
                logger.error("Error while executing post process of index {}", i, t);
                return SystemInstallationReport.Status.INCOMPLETE;
            } catch (Throwable t) {
                logger.error("Error while executing post process - index {}", i, t);
                return SystemInstallationReport.Status.INCOMPLETE;
            }
        }
        return SystemInstallationReport.Status.OK;
    }

    @Override
    public void reloadCurrentReport() {
        try {
            SystemInstallationReport report = this.extractReport();
            this.getCacheWrapper().setCurrentReport(report);
        } catch (Throwable t) {
            logger.error("Error reloading report", t);
            throw new RuntimeException("Error reloading report", t);
        }
    }

    //-------------------- REPORT -------- START
    public void saveReport(SystemInstallationReport report) throws BeansException {
        if (null == report || report.getReports().isEmpty()) {
            return;
        }
        try {
            InstallationReportDAO dao = new InstallationReportDAO();
            DataSource dataSource = (DataSource) this.getBeanFactory().getBean("portDataSource");
            dao.setDataSource(dataSource);
            dao.saveConfigItem(report.toXml(), this.getConfigVersion());
            this.getCacheWrapper().setCurrentReport(report);
        } catch (Throwable t) {
            logger.error("Error saving report", t);
            throw new FatalBeanException("Error saving report", t);
        }
    }

}

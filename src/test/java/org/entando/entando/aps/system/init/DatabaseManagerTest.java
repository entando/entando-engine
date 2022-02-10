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

import com.agiletec.aps.util.FileTextReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.ChangeSetStatus;
import liquibase.database.DatabaseFactory;
import liquibase.exception.DatabaseException;
import org.entando.entando.aps.system.init.AbstractInitializerManager.Environment;
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
import org.entando.entando.ent.exception.EntException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@ExtendWith(MockitoExtension.class)
class DatabaseManagerTest {

    @Mock
    private IComponentManager componentManager;

    @Mock
    private IStorageManager storageManager;

    @Mock
    private DatabaseRestorer databaseRestorer;

    @Mock
    private LiquibaseInstallationReport liquibaseInstallationReport;

    @InjectMocks
    @Spy
    private DatabaseManager databaseManager;

    @BeforeEach
    public void setUp() throws Exception {

        databaseManager.setDefaultDataSources(new ArrayList<>());

        List<Component> components = new ArrayList<>();
        components.add(this.loadComponent());
        Mockito.lenient().when(componentManager.getCurrentComponents()).thenReturn(components);
    }

    private Component loadComponent() throws Exception {
        Component component = null;
        PathMatchingResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(ComponentManager.DEFAULT_LOCATION_PATTERN);
        ComponentDefDOMForTest dom = null;
        Resource resource = resources[0];
        InputStream is = null;
        try {
            is = resource.getInputStream();
            String xml = FileTextReader.getText(is);
            dom = new ComponentDefDOMForTest(xml);
            component = dom.getComponent(new HashMap<>());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            if (null != is) {
                is.close();
            }
        }
        return component;
    }

    @Test
    void testDisabledDatabaseMigrationStrategy() throws Exception {
        List<ChangeSetStatus> changeSetToExecute = new ArrayList<>();
        changeSetToExecute.add(Mockito.mock(ChangeSetStatus.class));
        Mockito.lenient().doReturn(changeSetToExecute).when(databaseManager)
                .initLiquiBaseResources(Mockito.any(), Mockito.any(), Mockito.any());

        DatabaseMigrationStrategy migrationStrategyEnum = DatabaseMigrationStrategy.DISABLED;
        Assertions.assertThrows(DatabaseMigrationException.class, () -> {
            databaseManager.installDatabase(new SystemInstallationReport(null), migrationStrategyEnum);
        });
    }

    @Test
    void testInstallDatabaseRestoreLastDump() throws Exception {

        ListableBeanFactory beanFactory = getMockedBeanFactory();
        Mockito.when(beanFactory.getBean("StorageManager")).thenReturn(storageManager);

        DataSourceDumpReport dataSourceDumpReport = Mockito.mock(DataSourceDumpReport.class);
        Mockito.when(dataSourceDumpReport.getSubFolderName()).thenReturn("/path/to/dump");
        Mockito.doReturn(Arrays.asList(dataSourceDumpReport)).when(databaseManager).getBackupReports();
        Mockito.doReturn("/path/to/dump").when(databaseManager).getLocalBackupsFolder();

        Mockito.when(storageManager.exists(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())).thenReturn(true);
        databaseManager.setEnvironment(Environment.production);

        try (MockedConstruction<Liquibase> construction = Mockito.mockConstruction(Liquibase.class);
                MockedStatic<DatabaseFactory> dbFactory = Mockito.mockStatic(DatabaseFactory.class)) {
            dbFactory.when(DatabaseFactory::getInstance).thenReturn(Mockito.mock(DatabaseFactory.class));

            databaseManager.installDatabase(null, DatabaseMigrationStrategy.AUTO);
            Mockito.verify(databaseRestorer).restoreBackup("/path/to/dump");
        }
    }

    @Test
    void testInstallDatabaseRestoreDefaultDump() throws Throwable {

        getMockedBeanFactory();

        Mockito.doReturn(null).when(databaseManager).getBackupReports();
        databaseManager.setEnvironment(Environment.production);

        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getInputStream()).thenReturn(
                new ByteArrayInputStream("SQL CONTENT".getBytes(StandardCharsets.UTF_8)));
        Map<String, Resource> resourceMap = Collections.singletonMap("portDataSource", resource);
        databaseManager.setDefaultSqlDump(resourceMap);

        try (MockedConstruction<Liquibase> construction = Mockito.mockConstruction(Liquibase.class);
                MockedStatic<DatabaseFactory> dbFactory = Mockito.mockStatic(DatabaseFactory.class);
                MockedStatic<TableDataUtils> tableDataUtils = Mockito.mockStatic(TableDataUtils.class)) {

            dbFactory.when(DatabaseFactory::getInstance).thenReturn(Mockito.mock(DatabaseFactory.class));
            tableDataUtils.when(() -> TableDataUtils.valueDatabase(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                    .thenAnswer(invocationOnMock -> null);

            databaseManager.installDatabase(null, DatabaseMigrationStrategy.AUTO);
            Mockito.verify(databaseRestorer).initOracleSchema(ArgumentMatchers.any());
        }
    }

    @Test
    void testGenerateSqlStrategy() throws Exception {

        Map<String, String> liquibaseChangeSets = new HashMap<>();
        liquibaseChangeSets.put("portDataSource", "changeSetPort.xml");
        Component componentConfiguration = Mockito.mock(Component.class);
        Mockito.when(componentConfiguration.getLiquibaseChangeSets()).thenReturn(liquibaseChangeSets);
        Mockito.when(componentConfiguration.getCode()).thenReturn("test-component");

        Mockito.when(componentManager.getCurrentComponents()).thenReturn(Arrays.asList(componentConfiguration));

        ListableBeanFactory beanFactory = getMockedBeanFactory();
        Mockito.when(beanFactory.getBean("StorageManager")).thenReturn(storageManager);

        ChangeSetStatus changeSetStatus = Mockito.mock(ChangeSetStatus.class);
        Mockito.when(changeSetStatus.getWillRun()).thenReturn(true);
        Mockito.when(changeSetStatus.getChangeSet()).thenReturn(Mockito.mock(ChangeSet.class));

        try (MockedConstruction<Liquibase> construction = Mockito.mockConstruction(Liquibase.class, (liquibase, context) -> {
            Mockito.when(liquibase.getChangeSetStatuses(ArgumentMatchers.any(), ArgumentMatchers.any()))
                    .thenReturn(Arrays.asList(changeSetStatus));
        }); MockedStatic<DatabaseFactory> dbFactory = Mockito.mockStatic(DatabaseFactory.class)) {
            dbFactory.when(DatabaseFactory::getInstance).thenReturn(Mockito.mock(DatabaseFactory.class));

            Assertions.assertThrows(DatabaseMigrationException.class, () -> {
                databaseManager.installDatabase(getMockedReport(), DatabaseMigrationStrategy.GENERATE_SQL);
            });
            Mockito.verify(storageManager).saveFile(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
        }
    }

    @Test
    void testDerbyErrorOnLiquibaseClose() throws Throwable {
        testLiquibaseCloseException(new DatabaseException("Error closing derby cleanly"));
    }

    @Test
    void testUnexpectedErrorOnLiquibaseClose() throws Throwable {
        testLiquibaseCloseException(new DatabaseException("Unexpected error"));
    }

    private void testLiquibaseCloseException(DatabaseException ex) throws Throwable {

        getMockedBeanFactory();

        Map<String, String> liquibaseChangeSets = new HashMap<>();
        liquibaseChangeSets.put("portDataSource", "changeSetPort.xml");
        Component componentConfiguration = Mockito.mock(Component.class);
        Mockito.when(componentConfiguration.getLiquibaseChangeSets()).thenReturn(liquibaseChangeSets);

        Mockito.when(componentManager.getCurrentComponents()).thenReturn(Arrays.asList(componentConfiguration));

        Map<String, Status> databaseStatus = new HashMap<>();
        Mockito.when(liquibaseInstallationReport.getDatabaseStatus()).thenReturn(databaseStatus);

        try (MockedConstruction<Liquibase> construction = Mockito.mockConstruction(Liquibase.class,
                (liquibase, context) -> Mockito.doThrow(ex).when(liquibase).close());
                MockedStatic<DatabaseFactory> dbFactory = Mockito.mockStatic(DatabaseFactory.class)) {
            dbFactory.when(DatabaseFactory::getInstance).thenReturn(Mockito.mock(DatabaseFactory.class));
            databaseManager.installDatabase(getMockedReport(), DatabaseMigrationStrategy.AUTO);
            Assertions.assertEquals(Status.OK, databaseStatus.get("portDataSource"));
        }
    }

    private SystemInstallationReport getMockedReport() {

        SystemInstallationReport report = Mockito.mock(SystemInstallationReport.class);
        ComponentInstallationReport componentReport = Mockito.mock(ComponentInstallationReport.class);

        Mockito.when(report.getComponentReport(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())).thenReturn(componentReport);
        Mockito.when(report.getStatus()).thenReturn(Status.INIT);
        Mockito.when(componentReport.getLiquibaseReport()).thenReturn(liquibaseInstallationReport);

        return report;
    }

    private ListableBeanFactory getMockedBeanFactory() {
        ListableBeanFactory beanFactory = Mockito.mock(ListableBeanFactory.class);
        Mockito.when(databaseManager.getBeanFactory()).thenReturn(beanFactory);
        Mockito.when(beanFactory.getBeanNamesForType(DataSource.class)).thenReturn(new String[]{"portDataSource"});
        Mockito.when(beanFactory.getBean("portDataSource")).thenReturn(Mockito.mock(DataSource.class));
        return beanFactory;
    }

    private static class ComponentDefDOMForTest {

        public ComponentDefDOMForTest(String xmlText) throws EntException {
            this.decodeDOM(xmlText);
        }

        protected Component getComponent(Map<String, String> postProcessClasses) throws EntException {
            Component component = null;
            try {
                Element rootElement = this._doc.getRootElement();
                component = new Component(rootElement, postProcessClasses);
            } catch (Throwable t) {
                throw new EntException("Error loading component", t);
            }
            return component;
        }

        private void decodeDOM(String xmlText) throws EntException {
            SAXBuilder builder = new SAXBuilder();
            builder.setValidation(false);
            StringReader reader = new StringReader(xmlText);
            try {
                this._doc = builder.build(reader);
            } catch (Throwable t) {
                throw new EntException("Error detected while parsing the XML", t);
            }
        }

        private Document _doc;

    }
    
}

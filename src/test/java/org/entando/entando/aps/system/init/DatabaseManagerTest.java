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
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSetStatus;
import liquibase.database.DatabaseFactory;
import liquibase.exception.DatabaseException;
import org.entando.entando.aps.system.init.IInitializerManager.DatabaseMigrationStrategy;
import org.entando.entando.aps.system.init.exception.DatabaseMigrationException;
import org.entando.entando.aps.system.init.model.Component;
import org.entando.entando.aps.system.init.model.ComponentInstallationReport;
import org.entando.entando.aps.system.init.model.LiquibaseInstallationReport;
import org.entando.entando.aps.system.init.model.SystemInstallationReport;
import org.entando.entando.aps.system.init.model.SystemInstallationReport.Status;
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
    void testDisabledDatabaseMigrationStrategy() throws Throwable {
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
    void testDerbyErrorOnLiquibaseClose() throws Throwable {
        testLiquibaseCloseException(new DatabaseException("Error closing derby cleanly"));
    }

    @Test
    void testUnexpectedErrorOnLiquibaseClose() throws Throwable {
        EntException exception = Assertions.assertThrows(EntException.class, () ->
                testLiquibaseCloseException(new DatabaseException("Unexpected error")));
        Assertions.assertEquals("Unexpected error", exception.getCause().getMessage());
    }

    private void testLiquibaseCloseException(DatabaseException ex) throws Throwable {

        SystemInstallationReport report = Mockito.mock(SystemInstallationReport.class);
        Component componentConfiguration = Mockito.mock(Component.class);
        ComponentInstallationReport componentReport = Mockito.mock(ComponentInstallationReport.class);
        LiquibaseInstallationReport liquibaseInstallationReport = Mockito.mock(LiquibaseInstallationReport.class);

        Mockito.when(report.getComponentReport(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())).thenReturn(componentReport);
        Mockito.when(report.getStatus()).thenReturn(Status.INIT);
        Mockito.when(componentReport.getLiquibaseReport()).thenReturn(liquibaseInstallationReport);

        ListableBeanFactory beanFactory = Mockito.mock(ListableBeanFactory.class);
        Mockito.when(databaseManager.getBeanFactory()).thenReturn(beanFactory);

        Mockito.when(beanFactory.getBeanNamesForType(DataSource.class)).thenReturn(new String[]{"portDataSource"});
        Mockito.when(beanFactory.getBean("portDataSource")).thenReturn(Mockito.mock(DataSource.class));

        Map<String, String> liquibaseChangeSets = new HashMap<>();
        liquibaseChangeSets.put("portDataSource", "changeSetPort.xml");

        Mockito.when(componentConfiguration.getLiquibaseChangeSets()).thenReturn(liquibaseChangeSets);

        try (MockedConstruction<Liquibase> construction = Mockito.mockConstruction(Liquibase.class,
                (liquibase, context) -> Mockito.doThrow(ex).when(liquibase).close());
                MockedStatic<DatabaseFactory> dbFactory = Mockito.mockStatic(DatabaseFactory.class)) {
            dbFactory.when(DatabaseFactory::getInstance).thenReturn(Mockito.mock(DatabaseFactory.class));
            databaseManager.initLiquiBaseResources(componentConfiguration, report, DatabaseMigrationStrategy.AUTO);
        }
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

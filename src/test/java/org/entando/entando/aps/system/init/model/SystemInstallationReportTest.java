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
package org.entando.entando.aps.system.init.model;

import com.agiletec.aps.util.FileTextReader;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author E.Santoboni
 */
public class SystemInstallationReportTest {
    
    @Test
    void readReport() throws Exception {
        InputStream xmlStream = this.getClass().getResourceAsStream("report_test.xml");
        String xml = FileTextReader.getText(xmlStream);
        SystemInstallationReport report = new SystemInstallationReport(xml);
        List<ComponentInstallationReport> componentReports = report.getReports();
        Assertions.assertEquals(5, componentReports.size());
        ComponentInstallationReport engineReport = report.getComponentReport("entandoCore", false);
        Assertions.assertNotNull(engineReport);
        Assertions.assertNotNull(engineReport.getLiquibaseReport());
        ComponentInstallationReport comTest1Report = report.getComponentReport("comp_test_1", false);
        Assertions.assertNotNull(comTest1Report);
        Assertions.assertNotNull(comTest1Report.getLiquibaseReport());
        ComponentInstallationReport comTest2Report = report.getComponentReport("comp_test_2", false);
        Assertions.assertNotNull(comTest2Report);
        Assertions.assertNotNull(comTest2Report.getLiquibaseReport());
    }
    
}

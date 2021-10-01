/*
 * Copyright 2021-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
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

import java.util.Iterator;
import java.util.List;
import org.jdom.Element;

public class LiquibaseInstallationReport extends AbstractReport {

    protected LiquibaseInstallationReport() {}

    protected LiquibaseInstallationReport(Element element) {
        List<Element> databaseElements = element.getChildren(SystemInstallationReport.DATASOURCE_ELEMENT);
        for (int i = 0; i < databaseElements.size(); i++) {
            Element liquibaseElement = databaseElements.get(i);
            String dbName = liquibaseElement.getAttributeValue(SystemInstallationReport.NAME_ATTRIBUTE);
            String liquibaseStatusString = liquibaseElement.getAttributeValue(SystemInstallationReport.STATUS_ATTRIBUTE);
            SystemInstallationReport.Status dbStatus = Enum.valueOf(SystemInstallationReport.Status.class, liquibaseStatusString.toUpperCase());
            this.getDatabaseStatus().put(dbName, dbStatus);
        }
    }

    protected Element toJdomElement() {
        Element element = new Element(SystemInstallationReport.LIQUIBASE_ELEMENT);
        element.setAttribute(SystemInstallationReport.STATUS_ATTRIBUTE, this.getStatus().toString());
        Iterator<String> nameIter = this.getDatabaseStatus().keySet().iterator();
        while (nameIter.hasNext()) {
            String dbName = nameIter.next();
            Element dbElement = new Element(SystemInstallationReport.DATASOURCE_ELEMENT);
            dbElement.setAttribute(SystemInstallationReport.NAME_ATTRIBUTE, dbName);
            dbElement.setAttribute(SystemInstallationReport.STATUS_ATTRIBUTE, this.getDatabaseStatus().get(dbName).toString());
            element.addContent(dbElement);
        }
        return element;
    }

}

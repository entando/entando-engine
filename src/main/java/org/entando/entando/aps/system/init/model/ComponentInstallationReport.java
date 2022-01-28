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

import java.io.Serializable;
import java.util.Date;

import com.agiletec.aps.util.DateConverter;
import org.entando.entando.aps.system.init.model.SystemInstallationReport.Status;
import org.jdom.Element;

/**
 * @author E.Santoboni
 */
public class ComponentInstallationReport implements Serializable {

    private ComponentInstallationReport() {
    }

    protected ComponentInstallationReport(Element element) {
        String componentCode = element.getAttributeValue(SystemInstallationReport.CODE_ATTRIBUTE);
        this.setComponentCode(componentCode);
        String dateString = element.getAttributeValue(SystemInstallationReport.DATE_ATTRIBUTE);
        Date date = DateConverter.parseDate(dateString, SystemInstallationReport.DATE_FORMAT);
        this.setDate(date);
        Element liquibaseElement = element.getChild(SystemInstallationReport.LIQUIBASE_ELEMENT);
        if (null != liquibaseElement) {
            this.setLiquibaseReport(new LiquibaseInstallationReport(liquibaseElement));
        } else {
            this.setLiquibaseReport(new LiquibaseInstallationReport());
        }
        Element postProcessElement = element.getChild(SystemInstallationReport.COMPONENT_POST_PROCESS_ELEMENT);
        if (null != postProcessElement) {
            String postProcessStatusString = postProcessElement
                    .getAttributeValue(SystemInstallationReport.STATUS_ATTRIBUTE);
            if (null != postProcessStatusString) {
                SystemInstallationReport.Status postProcessStatus =
                        Enum.valueOf(SystemInstallationReport.Status.class, postProcessStatusString.toUpperCase());
                this.setPostProcessStatus(postProcessStatus);
            }
        }
    }

    public static ComponentInstallationReport getInstance(String componentCode) {
        ComponentInstallationReport report = new ComponentInstallationReport();
        report.setDate(new Date());
        report.setComponentCode(componentCode);
        report.setLiquibaseReport(new LiquibaseInstallationReport());
        return report;
    }

    protected Element toJdomElement() {
        Element element = new Element(SystemInstallationReport.COMPONENT_ELEMENT);
        element.setAttribute(SystemInstallationReport.CODE_ATTRIBUTE, this.getComponentCode());
        String dateString = DateConverter.getFormattedDate(this.getDate(), SystemInstallationReport.DATE_FORMAT);
        element.setAttribute(SystemInstallationReport.DATE_ATTRIBUTE, dateString);
        if (null != this.getStatus()) {
            element.setAttribute(SystemInstallationReport.STATUS_ATTRIBUTE, this.getStatus().toString());
        }
        if (null != this.getLiquibaseReport()) {
            Element liquibaseElement = this.getLiquibaseReport().toJdomElement();
            element.addContent(liquibaseElement);
        }
        if (null != this.getPostProcessStatus()) {
            Element postProcessElement = new Element(SystemInstallationReport.COMPONENT_POST_PROCESS_ELEMENT);
            postProcessElement
                    .setAttribute(SystemInstallationReport.STATUS_ATTRIBUTE, this.getPostProcessStatus().toString());
            element.addContent(postProcessElement);
        }
        return element;
    }

    public SystemInstallationReport.Status getStatus() {
        SystemInstallationReport.Status postProcessStatus = this.getPostProcessStatus();
        boolean isPostProcessStatusSafe = SystemInstallationReport.isSafeStatus(postProcessStatus);
        if (!isPostProcessStatusSafe && !postProcessStatus.equals(SystemInstallationReport.Status.INIT)) {
            return SystemInstallationReport.Status.INCOMPLETE;
        } else if (isPostProcessStatusSafe) {
            return SystemInstallationReport.Status.OK;
        } else {
            return SystemInstallationReport.Status.INIT;
        }
    }

    public boolean isUninstalled() {
        return this.getStatus().equals(Status.UNINSTALLED);
    }

    public String getComponentCode() {
        return _componentCode;
    }

    public void setComponentCode(String componentCode) {
        this._componentCode = componentCode;
    }

    public Date getDate() {
        return _date;
    }

    protected void setDate(Date date) {
        this._date = date;
    }

    public Status getPostProcessStatus() {
        return _postProcessStatus;
    }

    public void setPostProcessStatus(Status postProcessStatus) {
        this._postProcessStatus = postProcessStatus;
    }

    public LiquibaseInstallationReport getLiquibaseReport() {
        return liquibaseReport;
    }

    private void setLiquibaseReport(LiquibaseInstallationReport liquibaseReport) {
        this.liquibaseReport = liquibaseReport;
    }

    private String _componentCode;
    private Date _date;
    private SystemInstallationReport.Status _postProcessStatus = SystemInstallationReport.Status.INIT;
    
    private LiquibaseInstallationReport liquibaseReport;

}

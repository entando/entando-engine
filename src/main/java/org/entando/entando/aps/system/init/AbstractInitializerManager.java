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

import javax.sql.DataSource;

import org.entando.entando.aps.system.init.model.SystemInstallationReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import org.entando.entando.ent.exception.EntException;

/**
 * @author E.Santoboni
 */
public abstract class AbstractInitializerManager implements BeanFactoryAware {

	private static final Logger _logger = LoggerFactory.getLogger(AbstractInitializerManager.class);
	
	protected SystemInstallationReport extractReport() throws EntException {
		SystemInstallationReport report = null;
		try {
			InstallationReportDAO dao = new InstallationReportDAO();
			DataSource dataSource = (DataSource) this.getBeanFactory().getBean("portDataSource");
			dao.setDataSource(dataSource);
			report = dao.loadReport(this.getConfigVersion());
		} catch (Throwable t) {
			_logger.error("error Error extracting report", t);
			throw new EntException("Error extracting report", t);
		}
		return report;
	}
	
	protected String getConfigVersion() {
		return _configVersion;
	}
	public void setConfigVersion(String configVersion) {
		this._configVersion = configVersion;
	}
	
	protected Environment getEnvironment() {
		return _environment;
	}
	public void setEnvironment(Environment environment) {
		this._environment = environment;
	}
	
	protected BeanFactory getBeanFactory() {
		return _beanFactory;
	}
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this._beanFactory = beanFactory;
	}
	
	protected IComponentManager getComponentManager() {
		return _componentManager;
	}
	public void setComponentManager(IComponentManager componentManager) {
		this._componentManager = componentManager;
	}
	
	private String _configVersion;
	private Environment _environment = Environment.production;
	
	private BeanFactory _beanFactory;
	private IComponentManager _componentManager;
	
	public enum Environment {test, develop, production}
	
}

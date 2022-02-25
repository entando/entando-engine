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

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.entando.entando.aps.system.init.IDatabaseManager.DatabaseType;
import org.entando.entando.aps.system.init.model.Component;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;

import org.entando.entando.ent.exception.EntException;

/**
 * @author E.Santoboni
 */
public abstract class AbstractDatabaseUtils implements BeanFactoryAware {

	private static final EntLogger _logger = EntLogFactory.getSanitizedLogger(AbstractDatabaseUtils.class);

	protected IDatabaseManager.DatabaseType getType(DataSource dataSource) throws EntException {
		try {
			String dbProductName;
			try (Connection connection = dataSource.getConnection()) {
				dbProductName = connection.getMetaData().getDatabaseProductName().toLowerCase();
			}

			if (dbProductName.contains("derby")) {
				return DatabaseType.DERBY;
			} else if (dbProductName.contains("postgres")) {
				return DatabaseType.POSTGRESQL;
			} else if (dbProductName.contains("mysql")) {
				return DatabaseType.MYSQL;
			} else if (dbProductName.contains("oracle")) {
				return DatabaseType.ORACLE;
			} else if (dbProductName.contains("sql server")) {
				return DatabaseType.SQLSERVER;
			}

			String recognizedTypes = Arrays.toString(IDatabaseManager.DatabaseType.values());
			_logger.error("Type not recognized for database product name '{}' - Recognized types '{}'", dbProductName, recognizedTypes);

			return DatabaseType.UNKNOWN;

		} catch (Throwable t) {
			throw new EntException("Unable to retrieve database product name", t);
		}
	}
	
	protected String getLocalBackupsFolder() {
		StringBuilder dirName = new StringBuilder();
		dirName.append("databaseBackups").append(File.separator);
		return dirName.toString();
	}
	
	protected List<Component> getComponents() throws EntException {
		return this.getComponentManager().getCurrentComponents();
	}
	
	protected String[] extractBeanNames(Class beanClass) {
		ListableBeanFactory factory = (ListableBeanFactory) this.getBeanFactory();
		return factory.getBeanNamesForType(beanClass);
	}
	
	protected BeanFactory getBeanFactory() {
		return _beanFactory;
	}
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this._beanFactory = beanFactory;
	}
	
	protected Properties getDatabaseTypeDrivers() {
		return _databaseTypeDrivers;
	}
	public void setDatabaseTypeDrivers(Properties databaseTypeDrivers) {
		this._databaseTypeDrivers = databaseTypeDrivers;
	}
	
	protected IComponentManager getComponentManager() {
		return _componentManager;
	}
	public void setComponentManager(IComponentManager componentManager) {
		this._componentManager = componentManager;
	}
	
	protected IStorageManager getStorageManager() {
		return _storageManager;
	}
	public void setStorageManager(IStorageManager storageManager) {
		this._storageManager = storageManager;
	}
	
	private BeanFactory _beanFactory;
	
	private Properties _databaseTypeDrivers;
	
	private IComponentManager _componentManager;
	private IStorageManager _storageManager;
	
}

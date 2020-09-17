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

import org.entando.entando.ent.exception.EntException;
import com.agiletec.aps.util.FileTextReader;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.entando.entando.aps.system.init.model.Component;
import org.entando.entando.aps.system.init.util.QueryExtractor;
import org.entando.entando.aps.system.init.util.TableDataUtils;
import org.entando.entando.aps.system.init.util.TableFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author E.Santoboni
 */
public class DatabaseRestorer extends AbstractDatabaseUtils {

	private static final Logger _logger = LoggerFactory.getLogger(DatabaseRestorer.class);

	protected void initOracleSchema(DataSource dataSource) throws Throwable {
		IDatabaseManager.DatabaseType type = this.getType(dataSource);
		try {
			if (!type.equals(IDatabaseManager.DatabaseType.ORACLE)) {
				return;
			}
			String[] queryTimestampFormat = new String[]{"ALTER SESSION SET NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF'"};
			TableDataUtils.executeQueries(dataSource, queryTimestampFormat, false);
		} catch (Throwable t) {
			_logger.error("Error initializing oracle schema ", t);
			throw new EntException("Error initializing oracle schema", t);
		}
	}

	protected void initDerbySchema(DataSource dataSource) throws Throwable {
		String username = this.invokeGetMethod("getUsername", dataSource);
		try {
			String[] queryCreateSchema = new String[]{"CREATE SCHEMA " + username.toUpperCase()};
			TableDataUtils.executeQueries(dataSource, queryCreateSchema, false);
		} catch (Throwable t) {
			_logger.info("Error creating derby schema" + t);
			throw new EntException("Error creating derby schema", t);
		}
		try {
			String[] initSchemaQuery = new String[]{"SET SCHEMA \"" + username.toUpperCase() + "\""};
			TableDataUtils.executeQueries(dataSource, initSchemaQuery, true);
		} catch (Throwable t) {
			_logger.error("Error initializating Derby Schema", t);
			throw new EntException("Error initializating Derby Schema", t);
		}
	}

	protected void dropAndRestoreBackup(String backupSubFolder) throws EntException {
		try {
			List<Component> components = this.getComponents();
			int size = components.size();
			for (int i = 0; i < components.size(); i++) {
				Component componentConfiguration = components.get(size - i - 1);
				this.dropTables(componentConfiguration.getTableMapping());
			}
			this.dropTables(this.getEntandoTableMapping());
			this.restoreBackup(backupSubFolder);
		} catch (Throwable t) {
			_logger.error("Error while restoring backup: {}", backupSubFolder, t);
			throw new EntException("Error while restoring backup", t);
		}
	}

	private void dropTables(Map<String, List<String>> tableMapping) throws EntException {
		if (null == tableMapping) {
			return;
		}
		try {
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			for (int i = 0; i < dataSourceNames.length; i++) {
				String dataSourceName = dataSourceNames[i];
				List<String> tableClasses = tableMapping.get(dataSourceName);
				if (null == tableClasses || tableClasses.isEmpty()) {
					continue;
				}
				DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
				int size = tableClasses.size();
				for (int j = 0; j < tableClasses.size(); j++) {
					String tableClassName = tableClasses.get(size - j - 1);
					Class tableClass = Class.forName(tableClassName);
					String tableName = TableFactory.getTableName(tableClass);
					String[] queries = {"DELETE FROM " + tableName};
					TableDataUtils.executeQueries(dataSource, queries, true);
				}
			}
		} catch (Throwable t) {
			_logger.error("Error while dropping tables", t);
			throw new RuntimeException("Error while dropping tables", t);
		}
	}

	protected void restoreBackup(String backupSubFolder) throws EntException {
		try {
			this.restoreLocalDump(this.getEntandoTableMapping(), backupSubFolder);
			List<Component> components = this.getComponents();
			for (int i = 0; i < components.size(); i++) {
				Component componentConfiguration = components.get(i);
				this.restoreLocalDump(componentConfiguration.getTableMapping(), backupSubFolder);
			}
		} catch (Throwable t) {
			_logger.error("Error while restoring local backup", t);
			throw new EntException("Error while restoring local backup", t);
		}
	}

	private void restoreLocalDump(Map<String, List<String>> tableMapping, String backupSubFolder) throws EntException {
		if (null == tableMapping) {
			return;
		}
		try {
			StringBuilder folder = new StringBuilder(this.getLocalBackupsFolder())
					.append(backupSubFolder).append(File.separator);
			String[] dataSourceNames = this.extractBeanNames(DataSource.class);
			for (int i = 0; i < dataSourceNames.length; i++) {
				String dataSourceName = dataSourceNames[i];
				List<String> tableClasses = tableMapping.get(dataSourceName);
				if (null == tableClasses || tableClasses.isEmpty()) {
					continue;
				}
				DataSource dataSource = (DataSource) this.getBeanFactory().getBean(dataSourceName);
				this.initOracleSchema(dataSource);
				for (int j = 0; j < tableClasses.size(); j++) {
					String tableClassName = tableClasses.get(j);
					Class tableClass = Class.forName(tableClassName);
					String tableName = TableFactory.getTableName(tableClass);
					String fileName = folder.toString() + dataSourceName + File.separator + tableName + ".sql";
					InputStream is = this.getStorageManager().getStream(fileName, true);
					if (null != is) {
						this.restoreTableData(is, dataSource);
					}
				}
			}
		} catch (Throwable t) {
			_logger.error("Error while restoring local dump", t);
			throw new RuntimeException("Error while restoring local dump", t);
		}
	}

	private void restoreTableData(InputStream is, DataSource dataSource) {
		try {
			String script = FileTextReader.getText(is, "UTF-8");
			String[] queries = (null != script) ? QueryExtractor.extractInsertQueries(script) : null;
			if (null == queries) {
				return;
			}
			TableDataUtils.executeQueries(dataSource, queries, true);
		} catch (Throwable t) {
			_logger.error("Error executing queries", t);
		}
	}

}

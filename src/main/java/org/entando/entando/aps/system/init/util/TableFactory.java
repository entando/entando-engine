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
package org.entando.entando.aps.system.init.util;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.entando.entando.aps.system.init.IDatabaseManager;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

import org.entando.entando.ent.exception.EntException;
import com.j256.ormlite.db.MysqlDatabaseType;
import com.j256.ormlite.db.PostgresDatabaseType;
import com.j256.ormlite.db.SqlServerDatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

/**
 * @author E.Santoboni
 */
public class TableFactory {

	private static final EntLogger _logger = EntLogFactory.getSanitizedLogger(TableFactory.class);
	
	public TableFactory(String databaseName, DataSource dataSource, IDatabaseManager.DatabaseType type) {
		this.setDataSource(dataSource);
		this.setDatabaseName(databaseName);
		this.setType(type);
	}
	
	public void dropTables(List<String> tableClassNames) throws EntException {
		ConnectionSource connectionSource = null;
		try {
			connectionSource = this.createConnectionSource();
			this.dropTables(tableClassNames, connectionSource);
		} catch (Throwable t) {
			_logger.error("Error dropping tables to db {}", this.getDatabaseName(), t);
			throw new EntException("Error dropping tables to db " + this.getDatabaseName(), t);
		} finally {
			if (connectionSource != null) {
				try {
					connectionSource.close();
				} catch (SQLException ex) {}
			}
		}
	}

	private ConnectionSource createConnectionSource() throws EntException {
		ConnectionSource connectionSource = null;
		try {
			DataSource dataSource = this.getDataSource();
			IDatabaseManager.DatabaseType type = this.getType();
			String url = this.invokeGetMethod("getUrl", dataSource);
			String username = this.invokeGetMethod("getUsername", dataSource);
			String password = this.invokeGetMethod("getPassword", dataSource);
			com.j256.ormlite.db.DatabaseType dataType = null;
			if (type.equals(IDatabaseManager.DatabaseType.DERBY)) {
				dataType = new ApsDerbyEmbeddedDatabaseType();
				url = url + ";user=" + username + ";password=" + password;
				connectionSource = new JdbcConnectionSource(url, dataType);
			} else {
				if (type.equals(IDatabaseManager.DatabaseType.POSTGRESQL)) {
					dataType = new PostgresDatabaseType();
				} else if (type.equals(IDatabaseManager.DatabaseType.MYSQL)) {
					dataType = new MysqlDatabaseType();
				} else if (type.equals(IDatabaseManager.DatabaseType.ORACLE)) {
					dataType = new ApsOracleDatabaseType();
				} else if (type.equals(IDatabaseManager.DatabaseType.SQLSERVER)) {
					dataType = new SqlServerDatabaseType();
				}
				connectionSource = new JdbcConnectionSource(url, username, password, dataType);
			}
		} catch (Throwable t) {
			_logger.error("Error creating connectionSource to db {}", this.getDatabaseName(), t);
			throw new EntException("Error creating connectionSource to db " + this.getDatabaseName(), t);
		}
		return connectionSource;
	}

	private String invokeGetMethod(String methodName, DataSource dataSource) throws Throwable {
		Method method = dataSource.getClass().getDeclaredMethod(methodName);
		return (String) method.invoke(dataSource);
	}
	
	private void dropTables(List<String> tableClassNames,
			ConnectionSource connectionSource) throws EntException {
		try {
			for (int i = 0; i < tableClassNames.size(); i++) {
				String tableClassName = tableClassNames.get(i);
				Class tableClass = Class.forName(tableClassName, true, Thread.currentThread().getContextClassLoader());
				try {
					TableUtils.dropTable(connectionSource, tableClass, true);
				} catch (Throwable t) {
					String message = "Error dropping table " + this.getDatabaseName() + "/" + tableClassName + " - " + t.getMessage();
					_logger.error("Error dropping table {}/{}",this.getDatabaseName(), tableClassName, t);
					throw new EntException(message, t);
				}
			}
		} catch (Throwable t) {
			_logger.error("Error on setup Database - {}", this.getDatabaseName(), t);
			throw new EntException("Error on setup Database", t);
		}
	}

	public static String getTableName(Class tableClass) {
		DatabaseTable tableAnnotation = (DatabaseTable) tableClass.getAnnotation(DatabaseTable.class);
		return tableAnnotation.tableName();
	}

	protected DataSource getDataSource() {
		return _dataSource;
	}
	protected void setDataSource(DataSource dataSource) {
		this._dataSource = dataSource;
	}

	protected String getDatabaseName() {
		return _databaseName;
	}
	protected void setDatabaseName(String databaseName) {
		this._databaseName = databaseName;
	}

	protected IDatabaseManager.DatabaseType getType() {
		return _type;
	}
	protected void setType(IDatabaseManager.DatabaseType type) {
		this._type = type;
	}
	
	private String _databaseName;
	private DataSource _dataSource;
	private IDatabaseManager.DatabaseType _type;
	
}

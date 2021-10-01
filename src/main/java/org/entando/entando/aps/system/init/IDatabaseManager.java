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

import java.io.InputStream;
import java.util.List;

import org.entando.entando.aps.system.init.model.DataSourceDumpReport;

import org.entando.entando.ent.exception.EntException;
import javax.sql.DataSource;

/**
 * @author E.Santoboni
 */
public interface IDatabaseManager {
	
	public void createBackup() throws EntException;
	
	public void deleteBackup(String subFolderName) throws EntException;
	
	public int getStatus();
	
	public InputStream getTableDump(String tableName, String dataSourceName, String subFolderName) throws EntException;
	
	public boolean dropAndRestoreBackup(String subFolderName) throws EntException;
	
	public DataSourceDumpReport getBackupReport(String subFolderName) throws EntException;
	
	public List<DataSourceDumpReport> getBackupReports() throws EntException;
	
	public DatabaseType getDatabaseType(DataSource dataSource) throws EntException;
	
	public enum DatabaseType {DERBY, POSTGRESQL, MYSQL, ORACLE, SQLSERVER, UNKNOWN}
	
	public static final String DUMP_REPORT_FILE_NAME = "dumpReport.xml";
	
}

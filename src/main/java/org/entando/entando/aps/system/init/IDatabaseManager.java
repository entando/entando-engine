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

import com.agiletec.aps.system.exception.ApsSystemException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.entando.entando.aps.system.init.model.DataSourceDumpReport;

/**
 * @author E.Santoboni
 */
public interface IDatabaseManager {

    String DUMP_REPORT_FILE_NAME = "dumpReport.xml";

    void createBackup() throws ApsSystemException;

    void deleteBackup(String subFolderName) throws ApsSystemException;

    int getStatus();

    InputStream getTableDump(String tableName, String dataSourceName, String subFolderName) throws ApsSystemException;

    boolean dropAndRestoreBackup(String subFolderName) throws ApsSystemException;

    DataSourceDumpReport getBackupReport(String subFolderName) throws ApsSystemException;

    List<DataSourceDumpReport> getBackupReports() throws ApsSystemException;

    Map<String, List<String>> getEntandoTableMapping();

    DatabaseType getDatabaseType(DataSource dataSource) throws ApsSystemException;

    enum DatabaseType {
        DERBY, POSTGRESQL, MYSQL, ORACLE, SQLSERVER, UNKNOWN
    }

}

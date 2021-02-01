/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.database;

import org.entando.entando.ent.exception.EntException;
import java.io.ByteArrayInputStream;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.aps.system.init.IDatabaseManager;
import org.entando.entando.aps.system.init.model.DataSourceDumpReport;
import org.entando.entando.aps.system.services.database.model.DumpReportDto;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DatabaseServiceTest {

    @InjectMocks
    private DatabaseService databaseService;

    @Mock
    private IDatabaseManager databaseManager;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getInvalidReport() throws Throwable {
        when(databaseManager.getBackupReport(ArgumentMatchers.anyString())).thenReturn(null);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            this.databaseService.getDumpReportDto("reportCode");
        });
    }

    @Test
    void getValidReport() throws Throwable {
        String xml = null;
        DataSourceDumpReport report = new DataSourceDumpReport(xml);
        when(databaseManager.getBackupReport(ArgumentMatchers.anyString())).thenReturn(report);
        DumpReportDto dto = this.databaseService.getDumpReportDto("reportCode");
        Assertions.assertNotNull(dto);
    }

    @Test
    void getValidTableDump() throws Throwable {
        ByteArrayInputStream is = new ByteArrayInputStream("dump".getBytes());
        when(databaseManager.getTableDump(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(is);
        byte[] base64 = this.databaseService.getTableDump("reportCode", "dataSourcePort", "categories");
        Assertions.assertNotNull(base64);
    }

    @Test
    void getInValidTableDump_1() throws Throwable {
        when(databaseManager.getTableDump(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(null);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            this.databaseService.getTableDump("reportCode", "dataSourcePort", "categories");
        });
    }

    @Test
    void getInValidTableDump_2() throws Throwable {
        when(databaseManager.getTableDump(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenThrow(new EntException("Error"));
        Assertions.assertThrows(RestServerError.class, () -> {
            this.databaseService.getTableDump("reportCode", "dataSourcePort", "categories");
        });
    }

}

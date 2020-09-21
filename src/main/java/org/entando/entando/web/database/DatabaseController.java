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
package org.entando.entando.web.database;

import com.agiletec.aps.system.services.role.Permission;
import org.entando.entando.aps.system.services.database.IDatabaseService;
import org.entando.entando.aps.system.services.database.model.ComponentDto;
import org.entando.entando.aps.system.services.database.model.DumpReportDto;
import org.entando.entando.aps.system.services.database.model.ShortDumpReportDto;
import org.entando.entando.aps.system.services.storage.StorageManagerUtil;
import org.entando.entando.web.common.annotation.RestAccessControl;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.common.model.RestResponse;
import org.entando.entando.web.database.validator.DatabaseValidator;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.entando.entando.web.common.model.PagedRestResponse;
import org.entando.entando.web.common.model.SimpleRestResponse;

/**
 * @author E.Santoboni
 */
@RestController
@RequestMapping(value = "/database")
public class DatabaseController {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(this.getClass());

    @Autowired
    private DatabaseValidator databaseValidator;
    @Autowired
    private IDatabaseService databaseService;

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedRestResponse<ShortDumpReportDto>> getDumpReports(@Valid RestListRequest requestList) {
        this.getDatabaseValidator().validateRestListRequest(requestList, DumpReportDto.class);
        PagedMetadata<ShortDumpReportDto> result = this.getDatabaseService().getShortDumpReportDtos(requestList);
        this.getDatabaseValidator().validateRestListResult(requestList, result);
        return new ResponseEntity<>(new PagedRestResponse<>(result), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<Map>> getStatus() {
        Integer status = this.getDatabaseService().getStatus();
        Map<String, String> response = new HashMap<>();
        response.put("status", String.valueOf(status));
        logger.debug("Required database status -> {}", status);
        return new ResponseEntity<>(new SimpleRestResponse<>(response), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/initBackup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<List<ComponentDto>>> initBackup() {
        logger.debug("Required actual component configuration");
        List<ComponentDto> dtos = this.getDatabaseService().getCurrentComponents();
        logger.debug("Actual component configuration -> {}", dtos);
        return new ResponseEntity<>(new SimpleRestResponse<>(dtos), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/startBackup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<Map>> startBackup() throws Throwable {
        logger.debug("Starting database backup");
        this.getDatabaseService().startDatabaseBackup();
        Map<String, String> response = new HashMap<>();
        response.put("status", String.valueOf(this.getDatabaseService().getStatus()));
        logger.debug("Database backup started");
        return new ResponseEntity<>(new SimpleRestResponse<>(response), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/restoreBackup/{reportCode}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<Map<String, String>>> restoreBackup(
            @PathVariable String reportCode) {
        //-
        String safeReportCode = StorageManagerUtil.mustBeValidFilename(reportCode);
        logger.debug("Starting database restore -> code {}", safeReportCode);
        this.getDatabaseService().startDatabaseRestore(safeReportCode);
        Map<String, String> response = new HashMap<>();
        response.put("status", String.valueOf(this.getDatabaseService().getStatus()));
        logger.debug("Database restore started -> code {}", safeReportCode);
        return new ResponseEntity<>(new SimpleRestResponse<>(response), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/report/{reportCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<DumpReportDto>> getDumpReport(@PathVariable String reportCode) {
        String safeReportCode = StorageManagerUtil.mustBeValidFilename(reportCode);
        logger.debug("Required dump report -> code {}", safeReportCode);
        DumpReportDto result = this.getDatabaseService().getDumpReportDto(safeReportCode);
        logger.debug("Extracted dump report -> {}", result);
        return new ResponseEntity<>(new SimpleRestResponse<>(result), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/report/{reportCode}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<Map<String, String>>> deleteDumpReport(@PathVariable String reportCode) {
        String safeReportCode = StorageManagerUtil.mustBeValidFilename(reportCode);
        logger.debug("Deleting dump report -> code {}", safeReportCode);
        this.getDatabaseService().deleteDumpReport((safeReportCode));
        logger.debug("Deleted dump report -> {}", safeReportCode);
        Map<String, String> response = new HashMap<>();
        response.put("code", safeReportCode);
        return new ResponseEntity<>(new SimpleRestResponse<>(response), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/report/{reportCode}/dump/{dataSource}/{tableName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<Map<String, Object>, Map<String, String>>> getTableDump(
            @PathVariable String reportCode,
            @PathVariable String dataSource,
            @PathVariable String tableName
    ) {
        String safeReportCode = StorageManagerUtil.mustBeValidFilename(reportCode);
        logger.debug("Required dump report -> code {} - database {} - table {}", safeReportCode, dataSource, tableName);
        byte[] base64 = this.getDatabaseService().getTableDump(safeReportCode, dataSource, tableName);
        Map<String, Object> response = new HashMap<>();
        response.put("base64", base64);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("reportCode", safeReportCode);
        metadata.put("dataSource", dataSource);
        metadata.put("tableName", tableName);
        return new ResponseEntity<>(new RestResponse<>(response, metadata), HttpStatus.OK);
    }

    public DatabaseValidator getDatabaseValidator() {
        return databaseValidator;
    }

    public void setDatabaseValidator(DatabaseValidator databaseValidator) {
        this.databaseValidator = databaseValidator;
    }

    public IDatabaseService getDatabaseService() {
        return databaseService;
    }

    public void setDatabaseService(IDatabaseService databaseService) {
        this.databaseService = databaseService;
    }

}

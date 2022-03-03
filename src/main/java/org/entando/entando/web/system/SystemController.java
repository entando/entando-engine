/*
 * Copyright 2018-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.web.system;

import com.agiletec.aps.system.services.role.Permission;
import org.entando.entando.aps.system.init.IComponentManager;
import org.entando.entando.web.common.annotation.RestAccessControl;
import org.entando.entando.web.common.model.SimpleRestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/system")
public class SystemController {

    static final String CONTENT_SCHEDULER_CODE="jpcontentscheduler";

    @Autowired
    private IComponentManager componentManager;

    @RestAccessControl(permission = Permission.ENTER_BACKEND)
    @GetMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<Map<String,Boolean>>> getReport() {

        Map<String, Boolean> report = new HashMap<>();

        boolean componentInstalled = componentManager.isComponentInstalled(CONTENT_SCHEDULER_CODE);
        report.put("contentSchedulerPluginInstalled", componentInstalled);

        return new ResponseEntity<>(new SimpleRestResponse<>(report), HttpStatus.OK);
    }
}

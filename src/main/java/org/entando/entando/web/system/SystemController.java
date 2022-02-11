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
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.web.common.annotation.RestAccessControl;
import org.entando.entando.web.common.model.SimpleRestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/system")
public class SystemController {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(getClass());

    final static String CONTENT_SCHEDULER_CODE="jpcontentscheduler";
    final static String CONTENT_SCHEDULER_INSTALLED ="contentSchedulerPluginInstalled";

    @Autowired
    private IComponentManager componentManager;

    @RestAccessControl(permission = Permission.CONTENT_SUPERVISOR)
    @RequestMapping(value = "/report", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<Map>> getReport() {

        logger.debug("system - getting system report");
        Map<String, Boolean> report = new HashMap<>();

        if (componentManager.isComponentInstalled(CONTENT_SCHEDULER_CODE)) {
            report.put(CONTENT_SCHEDULER_INSTALLED, true);
        } else {
            report.put(CONTENT_SCHEDULER_INSTALLED, false);
        }
        return new ResponseEntity<SimpleRestResponse<Map>>(new SimpleRestResponse(report), HttpStatus.OK);
    }
}

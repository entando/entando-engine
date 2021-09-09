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
package org.entando.entando.web.guifragment;

import org.entando.entando.aps.system.services.guifragment.IGuiFragmentManager;
import org.entando.entando.ent.exception.EntException;
import com.agiletec.aps.system.services.role.Permission;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import org.entando.entando.web.common.annotation.RestAccessControl;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
import org.entando.entando.web.common.model.SimpleRestResponse;
import org.entando.entando.web.guifragment.model.GuiFragmentSettingsBody;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/fragmentsSettings")
public class GuiFragmentSettingsController {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(this.getClass());

    public static final String RESULT_PARAM_NAME = "enableEditingWhenEmptyDefaultGui";

    @Autowired
    private IGuiFragmentManager guiFragmentManager;

    protected IGuiFragmentManager getGuiFragmentManager() {
        return guiFragmentManager;
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<Map>> getSettings() {
        String paramValue = this.getGuiFragmentManager().getConfig(IGuiFragmentManager.CONFIG_PARAM_EDIT_EMPTY_FRAGMENT_ENABLED);
        Boolean value = null;
        try {
            value = Boolean.parseBoolean(paramValue);
        } catch (Exception e) {
            value = Boolean.FALSE;
        }
        Map<String, Boolean> result = new HashMap<>();
        result.put(RESULT_PARAM_NAME, value);
        logger.debug("Extracted fragment setting -> {}", result);
        return new ResponseEntity<>(new SimpleRestResponse<>(result), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<Map<String, Boolean>>> updateSettings(
            @Valid @RequestBody GuiFragmentSettingsBody bodyRequest,
            BindingResult bindingResult) throws EntException {
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        Boolean value = bodyRequest.getEnableEditingWhenEmptyDefaultGui();
        Map<String, String> map = new HashMap<>();
        map.put(IGuiFragmentManager.CONFIG_PARAM_EDIT_EMPTY_FRAGMENT_ENABLED, String.valueOf(value));
        this.getGuiFragmentManager().updateParams(map);
        Map<String, Boolean> result = new HashMap<>();
        result.put(RESULT_PARAM_NAME, value);
        logger.debug("Updated fragment setting -> {}", result);
        return new ResponseEntity<>(new SimpleRestResponse<>(result), HttpStatus.OK);
    }

}

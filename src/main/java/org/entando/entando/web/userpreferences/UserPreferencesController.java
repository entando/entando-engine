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
package org.entando.entando.web.userpreferences;

import com.agiletec.aps.system.services.user.UserDetails;
import javax.validation.Valid;
import org.entando.entando.aps.system.services.userpreferences.IUserPreferencesService;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.web.common.model.SimpleRestResponse;
import org.entando.entando.web.userpreferences.model.UserPreferencesDto;
import org.entando.entando.web.userpreferences.model.UserPreferencesRequest;
import org.entando.entando.web.userpreferences.validator.UserPreferencesValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserPreferencesController {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(this.getClass());

    @Autowired
    private IUserPreferencesService userPreferencesService;

    @Autowired
    private UserPreferencesValidator userPreferencesValidator;

    @GetMapping(value = "/userPreferences/{preferencesUsername:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<UserPreferencesDto>> getUserPreferences(
            @RequestAttribute("user") UserDetails user, @PathVariable String preferencesUsername) {
        logger.debug("Getting user '{}' preferences ", preferencesUsername);
        DataBinder binder = new DataBinder(preferencesUsername);
        userPreferencesValidator.validate(user, preferencesUsername, binder.getBindingResult());
        UserPreferencesDto response = userPreferencesService.getUserPreferences(preferencesUsername);
        return new ResponseEntity<>(new SimpleRestResponse<>(response), HttpStatus.OK);
    }

    @PutMapping(value = "/userPreferences/{preferencesUsername:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<UserPreferencesDto>> updateUserPreferences(
            @RequestAttribute("user") UserDetails user, @PathVariable String preferencesUsername,
            @Valid @RequestBody UserPreferencesRequest bodyRequest, BindingResult bindingResult) {
        logger.debug("Updating user '{}' preferences to -> {}", preferencesUsername, bodyRequest);
        userPreferencesValidator.validate(user, preferencesUsername, bindingResult);
        UserPreferencesDto response = userPreferencesService.updateUserPreferences(preferencesUsername, bodyRequest);
        return new ResponseEntity<>(new SimpleRestResponse<>(response), HttpStatus.OK);
    }
}

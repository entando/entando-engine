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
package org.entando.entando.web.widget;

import com.agiletec.aps.system.services.page.widget.INavigatorParser;
import com.agiletec.aps.system.services.page.widget.NavigatorExpression;
import com.agiletec.aps.system.services.role.Permission;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.entando.entando.web.widget.model.NavigatorExpressionDto;
import org.entando.entando.web.common.annotation.RestAccessControl;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
import org.entando.entando.web.common.model.SimpleRestResponse;
import org.entando.entando.web.widget.model.NavigatorConfigDto;
import org.entando.entando.web.widget.validator.NavigatorWidgetConfigValidator;
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

/**
 * @author E.Santoboni
 */
@RestController
@RequestMapping(value = "/widget/navigator")
public class NavigatorWidgetConfigController {
    
    private final EntLogger logger = EntLogFactory.getSanitizedLogger(this.getClass());
    
    @Autowired
    private NavigatorWidgetConfigValidator navigatorConfigValidator;
    
    @Autowired
    private INavigatorParser navigatorParser;
    
    @RestAccessControl(permission = Permission.MANAGE_PAGES)
    @RequestMapping(value = "/navspec", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<NavigatorConfigDto>> getNavSpecByExpressions(@Valid @RequestBody NavigatorConfigDto bodyRequest, BindingResult bindingResult) {
        logger.debug("Extract Expression -> {}", bodyRequest);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        this.getNavigatorConfigValidator().validateExpressions(bodyRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        List<NavigatorExpression> expressions = bodyRequest.getExpressions()
                .stream().map(dto -> dto.buildExpression()).collect(Collectors.toList());
        String navSpec = this.getNavigatorParser().getSpec(expressions);
        bodyRequest.setNavSpec(navSpec);
        return new ResponseEntity<>(new SimpleRestResponse<>(bodyRequest), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_PAGES)
    @RequestMapping(value = "/expressions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<NavigatorConfigDto>> getExpressionsByNavSpec(@Valid @RequestBody NavigatorConfigDto bodyRequest, BindingResult bindingResult) {
        logger.debug("Extract Expression -> {}", bodyRequest);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        this.getNavigatorConfigValidator().validateNavSpec(bodyRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        List<NavigatorExpression> expressions = this.getNavigatorParser().getExpressions(bodyRequest.getNavSpec());
        List<NavigatorExpressionDto> expressionsDto = expressions
                .stream().map(ex -> new NavigatorExpressionDto(ex)).collect(Collectors.toList());
        bodyRequest.setExpressions(expressionsDto);
        return new ResponseEntity<>(new SimpleRestResponse<>(bodyRequest), HttpStatus.OK);
    }

    public NavigatorWidgetConfigValidator getNavigatorConfigValidator() {
        return navigatorConfigValidator;
    }
    public void setNavigatorConfigValidator(NavigatorWidgetConfigValidator navigatorConfigValidator) {
        this.navigatorConfigValidator = navigatorConfigValidator;
    }

    public INavigatorParser getNavigatorParser() {
        return navigatorParser;
    }
    public void setNavigatorParser(INavigatorParser navigatorParser) {
        this.navigatorParser = navigatorParser;
    }
    
}

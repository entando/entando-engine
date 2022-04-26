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
package org.entando.entando.web.page;

import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.services.page.IPageService;
import org.entando.entando.aps.system.services.page.PageAuthorizationService;
import org.entando.entando.aps.system.services.page.model.PageConfigurationDto;
import org.entando.entando.aps.system.services.page.model.WidgetConfigurationDto;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.web.common.EntandoMessageCodesResolver;
import org.entando.entando.web.common.annotation.ActivityStreamAuditable;
import org.entando.entando.web.common.annotation.RestAccessControl;
import org.entando.entando.web.common.exceptions.ResourcePermissionsException;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
import org.entando.entando.web.common.model.RestResponse;
import org.entando.entando.web.common.model.SimpleRestResponse;
import org.entando.entando.web.page.model.WidgetConfigurationRequest;
import org.entando.entando.web.page.validator.PageConfigurationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PageConfigurationController {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(getClass());

    @Autowired
    private PageConfigurationValidator validator;

    @Autowired
    private IPageService pageService;

    @Autowired
    private PageAuthorizationService authorizationService;

    protected IPageService getPageService() {
        return pageService;
    }

    public void setPageService(IPageService pageService) {
        this.pageService = pageService;
    }

    @RestAccessControl(permission = Permission.MANAGE_PAGES)
    @RequestMapping(value = "/pages/{pageCode}/configuration", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<PageConfigurationDto, Map<String, String>>> getPageConfiguration(
            @RequestAttribute("user") UserDetails user,
            @PathVariable String pageCode,
            @RequestParam(value = "status", required = false, defaultValue = IPageService.STATUS_DRAFT) String status) {
        logger.debug("requested {} configuration", pageCode);
        if (!this.authorizationService.isAuth(user, pageCode, false)) {
            throw new ResourcePermissionsException(user.getUsername(), pageCode);
        }
        PageConfigurationDto pageConfiguration = this.getPageService().getPageConfiguration(pageCode, status);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("status", status);
        return new ResponseEntity<>(new RestResponse<>(pageConfiguration, metadata), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_PAGES)
    @RequestMapping(value = "/pages/{pageCode}/widgets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<List<WidgetConfigurationDto>, Map<String, String>>> getPageWidgets(
            @PathVariable String pageCode,
            @RequestParam(value = "status", required = false, defaultValue = IPageService.STATUS_DRAFT) String status,
            @RequestAttribute("user") UserDetails user) {
        logger.debug("requested {} widgets detail", pageCode);
        if (!this.authorizationService.isAuth(user, pageCode, false)) {
            throw new ResourcePermissionsException(user.getUsername(), pageCode);
        }
        PageConfigurationDto pageConfiguration = this.getPageService().getPageConfiguration(pageCode, status);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("status", status);

        List<WidgetConfigurationDto> widgetConfigDtos = new ArrayList<>();
        if(pageConfiguration.getWidgets() != null){
            widgetConfigDtos = Arrays.asList(pageConfiguration.getWidgets());
        }
        return new ResponseEntity<>(new RestResponse<>(widgetConfigDtos, metadata), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_PAGES)
    @RequestMapping(value = "/pages/{pageCode}/widgets/{frameId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<WidgetConfigurationDto, Map<String, String>>> getPageWidget(
            @RequestAttribute("user") UserDetails user,
            @PathVariable String pageCode,
            @PathVariable String frameId,
            @RequestParam(value = "status", required = false, defaultValue = IPageService.STATUS_DRAFT) String status
    ) {
        logger.debug("requested widget detail for page {} and frame {}", pageCode, frameId);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(frameId, "frameId");
        this.validateFrameId(frameId, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        if (!this.authorizationService.isAuth(user, pageCode, false)) {
            throw new ResourcePermissionsException(user.getUsername(), pageCode);
        }

        WidgetConfigurationDto widgetConfiguration = this.getPageService().getWidgetConfiguration(pageCode, Integer.valueOf(frameId), status);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("status", status);
        return new ResponseEntity<>(new RestResponse<>(widgetConfiguration, metadata), HttpStatus.OK);
    }

    @ActivityStreamAuditable
    @RestAccessControl(permission = Permission.MANAGE_PAGES)
    @RequestMapping(value = "/pages/{pageCode}/widgets/{frameId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<WidgetConfigurationDto, Map<String, String>>> updatePageWidget(
            @RequestAttribute("user") UserDetails user,
            @PathVariable String pageCode,
            @PathVariable String frameId,
            @Valid @RequestBody WidgetConfigurationRequest widget,
            BindingResult bindingResult) {
        logger.debug("updating widget configuration in page {} and frame {}", pageCode, frameId);

        if (!this.authorizationService.isAuthOnGroup(user, pageCode)) {
            throw new ResourcePermissionsException(user.getUsername(), pageCode);
        }

        this.validateFrameId(frameId, bindingResult);

        validator.validateWidgetConfigOverridable(widget.getCode(), widget.getConfig() , bindingResult);

        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }

        WidgetConfigurationDto widgetConfiguration = this.getPageService().updateWidgetConfiguration(pageCode, Integer.valueOf(frameId), widget);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("status", IPageService.STATUS_DRAFT);
        return new ResponseEntity<>(new RestResponse<>(widgetConfiguration, metadata), HttpStatus.OK);
    }

    @ActivityStreamAuditable
    @RestAccessControl(permission = Permission.MANAGE_PAGES)
    @RequestMapping(value = "/pages/{pageCode}/widgets/{frameId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<Map<String, String>, Map<String, String>>> deletePageWidget(@RequestAttribute("user") UserDetails user, @PathVariable String pageCode, @PathVariable String frameId) {
        logger.debug("removing widget configuration in page {} and frame {}", pageCode, frameId);
        if (!this.authorizationService.isAuthOnGroup(user, pageCode)) {
            throw new ResourcePermissionsException(user.getUsername(), pageCode);
        }
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(frameId, "frameId");
        this.validateFrameId(frameId, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        this.getPageService().deleteWidgetConfiguration(pageCode, Integer.valueOf(frameId));
        Map<String, String> result = new HashMap<>();
        result.put("code", frameId);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("status", IPageService.STATUS_DRAFT);
        return new ResponseEntity<>(new RestResponse<>(result, metadata), HttpStatus.OK);
    }

    @ActivityStreamAuditable
    @RestAccessControl(permission = Permission.MANAGE_PAGES)
    @RequestMapping(value = "/pages/{pageCode}/configuration/restore", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<PageConfigurationDto, Map<String, String>>> updatePageConfiguration(@RequestAttribute("user") UserDetails user, @PathVariable String pageCode) {
        logger.debug("restore configuration on page {}", pageCode);
        if (!this.authorizationService.isAuthOnGroup(user, pageCode)) {
            throw new ResourcePermissionsException(user.getUsername(), pageCode);
        }
        PageConfigurationDto pageConfiguration = this.getPageService().restorePageConfiguration(pageCode);
        Map<String, String> metadata = new HashMap<>();
        return new ResponseEntity<>(new RestResponse<>(pageConfiguration, metadata), HttpStatus.OK);
    }

    @ActivityStreamAuditable
    @RestAccessControl(permission = Permission.MANAGE_PAGES)
    @RequestMapping(value = "/pages/{pageCode}/configuration/defaultWidgets", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<PageConfigurationDto>> applyDefaultWidgetsPageConfiguration(@RequestAttribute("user") UserDetails user, @PathVariable String pageCode) {
        logger.debug("applying default widgets on page {}", pageCode);
        if (!this.authorizationService.isAuthOnGroup(user, pageCode)) {
            throw new ResourcePermissionsException(user.getUsername(), pageCode);
        }
        PageConfigurationDto pageConfiguration = this.getPageService().applyDefaultWidgets(pageCode);
        return new ResponseEntity<>(new SimpleRestResponse<>(pageConfiguration), HttpStatus.OK);
    }

    protected void validateFrameId(String frameId, BindingResult errors) {
        if (!StringUtils.isNumeric(frameId)) {
            errors.reject(EntandoMessageCodesResolver.ERR_CODE_URI_PARAMETER, new Object[]{frameId}, "invalidParameter.framedId");
        }
    }

}

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
import com.agiletec.aps.util.ApsProperties;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.services.page.IPageService;
import org.entando.entando.aps.system.services.page.model.PageConfigurationDto;
import org.entando.entando.aps.system.services.page.model.WidgetConfigurationDto;
import org.entando.entando.web.common.EntandoMessageCodesResolver;
import org.entando.entando.web.common.annotation.ActivityStreamAuditable;
import org.entando.entando.web.common.annotation.RestAccessControl;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
import org.entando.entando.web.common.model.RestResponse;
import org.entando.entando.web.page.model.WidgetConfigurationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.entando.entando.web.common.model.SimpleRestResponse;
import org.entando.entando.aps.system.services.widgettype.validators.WidgetProcessorFactory;

@RestController
public class PageConfigurationController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPageService pageService;

    @Autowired
    private WidgetProcessorFactory widgetProcessorFactory;

    protected IPageService getPageService() {
        return pageService;
    }

    public void setPageService(IPageService pageService) {
        this.pageService = pageService;
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/pages/{pageCode}/configuration", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<PageConfigurationDto, Map>> getPageConfiguration(@PathVariable String pageCode, @RequestParam(value = "status", required = false, defaultValue = IPageService.STATUS_DRAFT) String status) {
        logger.debug("requested {} configuration", pageCode);
        PageConfigurationDto pageConfiguration = this.getPageService().getPageConfiguration(pageCode, status);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("status", status);
        return new ResponseEntity<>(new RestResponse<>(pageConfiguration, metadata), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/pages/{pageCode}/widgets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<WidgetConfigurationDto[], Map>> getPageWidgets(@PathVariable String pageCode, @RequestParam(value = "status", required = false, defaultValue = IPageService.STATUS_DRAFT) String status) {
        logger.debug("requested {} widgets detail", pageCode);
        PageConfigurationDto pageConfiguration = this.getPageService().getPageConfiguration(pageCode, status);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("status", status);
        WidgetConfigurationDto[] widgets = pageConfiguration.getWidgets();
        logger.info("widgets list  {}", widgets);

        for (int i=0; i < widgets.length; i++){
            logger.debug("extracting widget configuration {}",i);
            WidgetConfigurationDto widget = widgets[i];
            if (null != widget){
                ApsProperties outProperties = this.getWidgetProcessorFactory().get(widget.getCode()).extractConfiguration(widget.getConfig());
                widget.setConfig(outProperties);
                logger.debug("widgets configuration set to {}", outProperties);
            }
            widgets[i] = widget;
            i++;
        }
        return new ResponseEntity<>(new RestResponse(widgets, metadata), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/pages/{pageCode}/widgets/{frameId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<WidgetConfigurationDto, Map>> getPageWidget(@PathVariable String pageCode,
            @PathVariable String frameId,
            @RequestParam(value = "status", required = false, defaultValue = IPageService.STATUS_DRAFT) String status
    ) {
        logger.debug("requested widget detail for page {} and frame {}", pageCode, frameId);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(frameId, "frameId");
        this.validateFrameId(frameId, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }

        WidgetConfigurationDto widgetConfiguration = this.getPageService().getWidgetConfiguration(pageCode, Integer.valueOf(frameId), status);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("status", status);
        return new ResponseEntity<>(new RestResponse<>(widgetConfiguration, metadata), HttpStatus.OK);
    }

    @ActivityStreamAuditable
    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/pages/{pageCode}/widgets/{frameId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<WidgetConfigurationDto, Map>> updatePageWidget(
            @PathVariable String pageCode,
            @PathVariable String frameId,
            @Valid @RequestBody WidgetConfigurationRequest widget,
            BindingResult bindingResult) {
        logger.debug("updating widget configuration in page {} and frame {}", pageCode, frameId);

        this.validateFrameId(frameId, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }

        WidgetConfigurationDto widgetConfiguration = this.getPageService().updateWidgetConfiguration(pageCode, Integer.valueOf(frameId), widget);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("status", IPageService.STATUS_DRAFT);
        return new ResponseEntity<>(new RestResponse<>(widgetConfiguration, metadata), HttpStatus.OK);
    }

    @ActivityStreamAuditable
    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/pages/{pageCode}/widgets/{frameId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<Map, Map>> deletePageWidget(@PathVariable String pageCode, @PathVariable String frameId) {
        logger.debug("removing widget configuration in page {} and frame {}", pageCode, frameId);
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
    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/pages/{pageCode}/configuration/restore", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<PageConfigurationDto, Map>> updatePageConfiguration(@PathVariable String pageCode) {
        logger.debug("restore configuration on page {}", pageCode);
        PageConfigurationDto pageConfiguration = this.getPageService().restorePageConfiguration(pageCode);
        Map<String, String> metadata = new HashMap<>();
        return new ResponseEntity<>(new RestResponse<>(pageConfiguration, metadata), HttpStatus.OK);
    }

    @ActivityStreamAuditable
    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/pages/{pageCode}/configuration/defaultWidgets", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<PageConfigurationDto>> applyDefaultWidgetsPageConfiguration(@PathVariable String pageCode) {
        logger.debug("applying default widgets on page {}", pageCode);
        PageConfigurationDto pageConfiguration = this.getPageService().applyDefaultWidgets(pageCode);
        return new ResponseEntity<>(new SimpleRestResponse<>(pageConfiguration), HttpStatus.OK);
    }

    protected void validateFrameId(String frameId, BindingResult errors) {
        if (!StringUtils.isNumeric(frameId)) {
            errors.reject(EntandoMessageCodesResolver.ERR_CODE_URI_PARAMETER, new Object[]{frameId}, "invalidParameter.framedId");
        }
    }

    public WidgetProcessorFactory getWidgetProcessorFactory() {
        return widgetProcessorFactory;
    }

    public void setWidgetProcessorFactory(WidgetProcessorFactory widgetProcessorFactory) {
        this.widgetProcessorFactory = widgetProcessorFactory;
    }
}

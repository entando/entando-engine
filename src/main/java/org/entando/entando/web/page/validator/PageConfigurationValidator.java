/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.web.page.validator;

import java.util.Map;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.aps.system.services.widgettype.WidgetTypeManager;
import org.entando.entando.web.common.validator.AbstractPaginationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class PageConfigurationValidator extends AbstractPaginationValidator {

    private static final String ERRCODE_OPERATION_WIDEGT_CONF_NOT_OVERRIDABLE = "10";

    @Autowired
    private WidgetTypeManager widgetTypeManager;

    public void validateWidgetConfigOverridable(String widgetCode, Map<String, Object> widgetConfig, Errors errors) {

        final WidgetType type = widgetTypeManager.getWidgetType(widgetCode);
        if (null != widgetConfig && !widgetConfig.isEmpty() && type != null && type.isReadonlyPageWidgetConfig()) {
            errors.rejectValue("code", ERRCODE_OPERATION_WIDEGT_CONF_NOT_OVERRIDABLE,
                    new String[]{widgetCode}, "page.widgetconfig.notoverridable");
        }

    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        //nothing to do
    }
}

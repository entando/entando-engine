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
package org.entando.entando.web.widget.validator;

import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.widget.NavigatorExpression;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.web.common.validator.AbstractPaginationValidator;
import org.entando.entando.web.widget.model.NavigatorExpressionDto;
import org.entando.entando.web.widget.model.NavigatorConfigDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class NavigatorWidgetConfigValidator extends AbstractPaginationValidator {
    
    public static final String ERRCODE_EXPRESSIONS_NOT_FOUND = "1";
    public static final String ERRCODE_EXPRESSIONS_TARGET_NOT_FOUND = "2";
    public static final String ERRCODE_EXPRESSIONS_TARGET_INVALID = "3";
    
    public static final String ERRCODE_EXPRESSIONS_SPEC_SUPER_INVALID = "4";
    public static final String ERRCODE_EXPRESSIONS_SPEC_ABS_INVALID = "5";
    
    public static final String ERRCODE_EXPRESSIONS_OPERATOR_SUB_INVALID = "6";
    
    public static final String ERRCODE_NAV_SPEC_NOT_FOUND = "7";
    
    @Autowired
    private IPageManager pageManager;
    
    @Override
    public boolean supports(Class<?> paramClass) {
        return NavigatorConfigDto.class.equals(paramClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // nothing to do
    }
    
    public void validateNavSpec(NavigatorConfigDto bodyRequest, Errors errors) {
        if (null == bodyRequest.getNavSpec() || StringUtils.isBlank(bodyRequest.getNavSpec())) {
            errors.rejectValue("navSpec", ERRCODE_NAV_SPEC_NOT_FOUND, new String[]{}, "widget.navigator.navSpec.missing");
        }
    }
    
    public void validateExpressions(NavigatorConfigDto bodyRequest, Errors errors) {
        if (null == bodyRequest.getExpressions() || bodyRequest.getExpressions().isEmpty()) {
            errors.rejectValue("expressions", ERRCODE_EXPRESSIONS_NOT_FOUND, new String[]{}, "widget.navigator.expressions.missing");
            return;
        }
        for (int i = 0; i < bodyRequest.getExpressions().size(); i++) {
            NavigatorExpressionDto expression = bodyRequest.getExpressions().get(i);
            if (expression.getSpec().equals(NavigatorExpression.SPEC_PAGE_CODE)) {
                if (StringUtils.isBlank(expression.getTargetCode())) {
                    errors.rejectValue("expressions", ERRCODE_EXPRESSIONS_TARGET_NOT_FOUND, new String[]{String.valueOf(i+1)}, "widget.navigator.targetCode.missing");
                } else if (null == this.getPageManager().getOnlinePage(expression.getTargetCode())) {
                    errors.rejectValue("expressions", ERRCODE_EXPRESSIONS_TARGET_INVALID, new String[]{String.valueOf(i+1), expression.getTargetCode()}, "widget.navigator.targetCode.invalid");
                }
            } else if (expression.getSpec().equals(NavigatorExpression.SPEC_SUPER_CODE) && expression.getSpecSuperLevel() < 1) {
                errors.rejectValue("expressions", ERRCODE_EXPRESSIONS_SPEC_SUPER_INVALID, new String[]{String.valueOf(i+1)}, "widget.navigator.specSuper.invalid");
            } else if (expression.getSpec().equals(NavigatorExpression.SPEC_ABS_CODE) && expression.getSpecAbsLevel() < 1) {
                errors.rejectValue("expressions", ERRCODE_EXPRESSIONS_SPEC_ABS_INVALID, new String[]{String.valueOf(i+1)}, "widget.navigator.specAbs.invalid");
            }
            if (NavigatorExpression.OPERATOR_SUBTREE_CODE.equals(expression.getOperator()) && expression.getOperatorSubtreeLevel() < 1) {
                errors.rejectValue("expressions", ERRCODE_EXPRESSIONS_OPERATOR_SUB_INVALID, new String[]{String.valueOf(i+1)}, "widget.navigator.operatorSub.invalid");
            }
        }
    }
    
    public IPageManager getPageManager() {
        return pageManager;
    }
    public void setPageManager(IPageManager pageManager) {
        this.pageManager = pageManager;
    }
    
}

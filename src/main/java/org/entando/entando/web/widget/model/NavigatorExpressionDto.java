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
package org.entando.entando.web.widget.model;

import com.agiletec.aps.system.services.page.widget.NavigatorExpression;
import javax.validation.constraints.NotNull;
import org.entando.entando.web.common.annotation.ValidateString;

/**
 * @author E.Santoboni
 */
public class NavigatorExpressionDto {
    
    @NotNull(message = "page.navigator.spec.notBlank")
    @ValidateString(acceptedValues = {NavigatorExpression.SPEC_PAGE_CODE, 
        NavigatorExpression.SPEC_ABS_CODE, NavigatorExpression.SPEC_CURRENT_PAGE_CODE, 
        NavigatorExpression.SPEC_PARENT_PAGE_CODE, NavigatorExpression.SPEC_SUPER_CODE}, message = "page.navigator.spec.invalid")
	private String spec;
    
    private int specSuperLevel = 0;
    
	private int specAbsLevel = 0;
    
    private String targetCode;
	
    @ValidateString(acceptedValues = {NavigatorExpression.OPERATOR_CHILDREN_CODE, 
        NavigatorExpression.OPERATOR_PATH_CODE, NavigatorExpression.OPERATOR_SUBTREE_CODE}, message = "page.navigator.operator.invalid")
	private String operator;
    
	private int operatorSubtreeLevel = 0;

    public String getSpec() {
        return spec;
    }
    public void setSpec(String spec) {
        this.spec = spec;
    }

    public int getSpecSuperLevel() {
        return specSuperLevel;
    }
    public void setSpecSuperLevel(int specSuperLevel) {
        this.specSuperLevel = specSuperLevel;
    }

    public int getSpecAbsLevel() {
        return specAbsLevel;
    }
    public void setSpecAbsLevel(int specAbsLevel) {
        this.specAbsLevel = specAbsLevel;
    }

    public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
    }
    
    public String getOperator() {
        return operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getOperatorSubtreeLevel() {
        return operatorSubtreeLevel;
    }
    public void setOperatorSubtreeLevel(int operatorSubtreeLevel) {
        this.operatorSubtreeLevel = operatorSubtreeLevel;
    }
    
}

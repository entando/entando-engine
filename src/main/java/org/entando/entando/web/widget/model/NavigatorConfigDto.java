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

import java.util.List;
import javax.validation.Valid;

/**
 * @author E.Santoboni
 */
public class NavigatorConfigDto {
    
    private String navSpec;
    private List<NavigatorExpressionDto> expressions;

    public String getNavSpec() {
        return navSpec;
    }
    public void setNavSpec(String navSpec) {
        this.navSpec = navSpec;
    }

    public List<@Valid NavigatorExpressionDto> getExpressions() {
        return expressions;
    }
    public void setExpressions(List<@Valid NavigatorExpressionDto> expressions) {
        this.expressions = expressions;
    }
    
}

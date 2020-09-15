/*
 * Copyright 2020-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.common.entity.model.attribute;

import com.agiletec.aps.system.common.entity.model.attribute.MonoTextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.util.TextAttributeValidationRules;

/**
 * @author E.Santoboni
 */
public class EmailAttribute extends MonoTextAttribute {
    
    private String emailRegexp;

    @Override
    public Object getAttributePrototype() {
        EmailAttribute attribute = (EmailAttribute) super.getAttributePrototype();
        attribute.setEmailRegexp(this.getEmailRegexp());
        ((TextAttributeValidationRules) attribute.getValidationRules()).setRegexp(this.getEmailRegexp());
        return attribute;
    }

    public String getEmailRegexp() {
        return emailRegexp;
    }
    public void setEmailRegexp(String emailRegexp) {
        this.emailRegexp = emailRegexp;
    }
    
}

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
package org.entando.entando.web.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import javax.validation.constraints.NotNull;
import org.entando.entando.web.common.json.BooleanStringDeserializer;

public class UserWizardRequest {

    @NotNull(message = "user.wizard.NotBlank")
    @JsonProperty("wizardEnabled")
    @JsonDeserialize(using = BooleanStringDeserializer.class)
    private Boolean wizardEnabled;

    public Boolean getWizardEnabled() {
        return wizardEnabled;
    }

    public void setWizardEnabled(Boolean wizardEnabled) {
        this.wizardEnabled = wizardEnabled;
    }

    @Override
    public String toString() {
        return "UserWizardRequest{" +
                "enabled=" + wizardEnabled +
                '}';
    }
}

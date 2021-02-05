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
package org.entando.entando.web.userpreferences.model;

import java.util.List;

public class UserPreferencesRequest {

    private Boolean wizard;
    private Boolean loadOnPageSelect;
    private Boolean translationWarning;
    private Boolean displayAttributes;
    private String defaultOwnerGroup;
    private List<String> defaultJoinGroups;

    public Boolean getWizard() {
        return wizard;
    }

    public void setWizard(Boolean wizard) {
        this.wizard = wizard;
    }

    public Boolean getLoadOnPageSelect() {
        return loadOnPageSelect;
    }

    public void setLoadOnPageSelect(Boolean loadOnPageSelect) {
        this.loadOnPageSelect = loadOnPageSelect;
    }

    public Boolean getTranslationWarning() {
        return translationWarning;
    }

    public void setTranslationWarning(Boolean translationWarning) {
        this.translationWarning = translationWarning;
    }

    public Boolean getDisplayAttributes() {
        return displayAttributes;
    }

    public void setDisplayAttributes(Boolean displayAttributes) {
        this.displayAttributes = displayAttributes;
    }

    public String getDefaultOwnerGroup() {
        return defaultOwnerGroup;
    }

    public void setDefaultOwnerGroup(String defaultOwnerGroup) {
        this.defaultOwnerGroup = defaultOwnerGroup;
    }

    public List<String> getDefaultJoinGroups() {
        return defaultJoinGroups;
    }

    public void setDefaultJoinGroups(List<String> defaultJoinGroups) {
        this.defaultJoinGroups = defaultJoinGroups;
    }

    @Override
    public String toString() {
        return "UserPreferencesRequest{" +
                "wizard=" + wizard +
                ", loadOnPageSelect=" + loadOnPageSelect +
                ", translationWarning=" + translationWarning +
                ", displayAttributes=" + displayAttributes +
                ", defaultOwnerGroup='" + defaultOwnerGroup + '\'' +
                ", defaultJoinGroups=" + defaultJoinGroups +
                '}';
    }
}

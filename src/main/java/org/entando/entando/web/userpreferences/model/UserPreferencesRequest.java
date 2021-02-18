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
    private String defaultPageOwnerGroup;
    private List<String> defaultPageJoinGroups;
    private String defaultContentOwnerGroup;
    private List<String> defaultContentJoinGroups;
    private String defaultWidgetOwnerGroup;
    private List<String> defaultWidgetJoinGroups;

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

    public String getDefaultPageOwnerGroup() {
        return defaultPageOwnerGroup;
    }

    public void setDefaultPageOwnerGroup(String defaultPageOwnerGroup) {
        this.defaultPageOwnerGroup = defaultPageOwnerGroup;
    }

    public List<String> getDefaultPageJoinGroups() {
        return defaultPageJoinGroups;
    }

    public void setDefaultPageJoinGroups(List<String> defaultPageJoinGroups) {
        this.defaultPageJoinGroups = defaultPageJoinGroups;
    }

    public String getDefaultContentOwnerGroup() {
        return defaultContentOwnerGroup;
    }

    public void setDefaultContentOwnerGroup(String defaultContentOwnerGroup) {
        this.defaultContentOwnerGroup = defaultContentOwnerGroup;
    }

    public List<String> getDefaultContentJoinGroups() {
        return defaultContentJoinGroups;
    }

    public void setDefaultContentJoinGroups(List<String> defaultContentJoinGroups) {
        this.defaultContentJoinGroups = defaultContentJoinGroups;
    }

    public String getDefaultWidgetOwnerGroup() {
        return defaultWidgetOwnerGroup;
    }

    public void setDefaultWidgetOwnerGroup(String defaultWidgetOwnerGroup) {
        this.defaultWidgetOwnerGroup = defaultWidgetOwnerGroup;
    }

    public List<String> getDefaultWidgetJoinGroups() {
        return defaultWidgetJoinGroups;
    }

    public void setDefaultWidgetJoinGroups(List<String> defaultWidgetJoinGroups) {
        this.defaultWidgetJoinGroups = defaultWidgetJoinGroups;
    }

    @Override
    public String toString() {
        return "UserPreferencesRequest{" +
                "wizard=" + wizard +
                ", loadOnPageSelect=" + loadOnPageSelect +
                ", translationWarning=" + translationWarning +
                ", defaultPageOwnerGroup='" + defaultPageOwnerGroup + '\'' +
                ", defaultPageJoinGroups=" + defaultPageJoinGroups +
                ", defaultContentOwnerGroup='" + defaultContentOwnerGroup + '\'' +
                ", defaultContentJoinGroups=" + defaultContentJoinGroups +
                ", defaultWidgetOwnerGroup='" + defaultWidgetOwnerGroup + '\'' +
                ", defaultWidgetJoinGroups=" + defaultWidgetJoinGroups +
                '}';
    }
}

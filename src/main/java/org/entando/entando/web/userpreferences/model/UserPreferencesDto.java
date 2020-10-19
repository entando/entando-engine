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

import org.entando.entando.aps.system.services.userpreferences.UserPreferences;

public class UserPreferencesDto {

    private Boolean wizard;
    private Boolean loadOnPageSelect;
    private Boolean translationWarning;

    public UserPreferencesDto(UserPreferences userPreferences) {
        this.wizard = userPreferences.isWizard();
        this.loadOnPageSelect = userPreferences.isLoadOnPageSelect();
        this.translationWarning = userPreferences.isTranslationWarning();
    }

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

    @Override
    public String toString() {
        return "UserPreferencesRequest{" +
                "wizard=" + wizard +
                ", loadOnPageSelect=" + loadOnPageSelect +
                ", translationWarning=" + translationWarning +
                '}';
    }
}

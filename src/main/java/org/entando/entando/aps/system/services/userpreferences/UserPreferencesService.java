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
package org.entando.entando.aps.system.services.userpreferences;

import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.web.userpreferences.model.UserPreferencesDto;
import org.entando.entando.web.userpreferences.model.UserPreferencesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserPreferencesService implements IUserPreferencesService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String ERRCODE_USER_PREFERENCES_DOES_NOT_EXISTS = "1";
    public static final String DEFAULT_JOIN_GROUP_DELIMITER = ";";

    IUserPreferencesManager userPreferencesManager;

    @Override
    public UserPreferencesDto getUserPreferences(String username) {
        try {
            UserPreferences userPreferences = userPreferencesManager.getUserPreferences(username);
            if (userPreferences == null) {
                createNewDefaultUserPreferences(username);
                userPreferences = userPreferencesManager.getUserPreferences(username);
            }
            return new UserPreferencesDto(userPreferences);
        } catch (EntException e) {
            logger.error("Error getting userPreferences for {}", username, e);
            throw new RestServerError("Error getting userPreferences", e);
        }
    }

    @Override
    public UserPreferencesDto updateUserPreferences(String username, UserPreferencesRequest request) {
        try {
            UserPreferences userPreferences = userPreferencesManager.getUserPreferences(username);
            if (userPreferences != null) {
                if (request.getWizard() != null) {
                    userPreferences.setWizard(request.getWizard());
                }
                if (request.getLoadOnPageSelect() != null) {
                    userPreferences.setLoadOnPageSelect(request.getLoadOnPageSelect());
                }
                if (request.getTranslationWarning() != null) {
                    userPreferences.setTranslationWarning(request.getTranslationWarning());
                }
                if (request.getDefaultPageOwnerGroup() != null) {
                    userPreferences.setDefaultPageOwnerGroup(request.getDefaultPageOwnerGroup());
                }
                if (request.getDefaultPageJoinGroups() != null) {
                    StringBuilder sb = new StringBuilder();
                    for (String group : request.getDefaultPageJoinGroups()) {
                        sb.append(group);
                        sb.append(DEFAULT_JOIN_GROUP_DELIMITER);
                    }
                    userPreferences.setDefaultPageJoinGroups(sb.toString());
                }
                if (request.getDefaultContentOwnerGroup() != null) {
                    userPreferences.setDefaultContentOwnerGroup(request.getDefaultContentOwnerGroup());
                }
                if (request.getDefaultContentJoinGroups() != null) {
                    StringBuilder sb = new StringBuilder();
                    for (String group : request.getDefaultContentJoinGroups()) {
                        sb.append(group);
                        sb.append(DEFAULT_JOIN_GROUP_DELIMITER);
                    }
                    userPreferences.setDefaultContentJoinGroups(sb.toString());
                }
                if (request.getDefaultWidgetOwnerGroup() != null) {
                    userPreferences.setDefaultWidgetOwnerGroup(request.getDefaultWidgetOwnerGroup());
                }
                if (request.getDefaultWidgetJoinGroups() != null) {
                    StringBuilder sb = new StringBuilder();
                    for (String group : request.getDefaultWidgetJoinGroups()) {
                        sb.append(group);
                        sb.append(DEFAULT_JOIN_GROUP_DELIMITER);
                    }
                    userPreferences.setDefaultWidgetJoinGroups(sb.toString());
                }
                userPreferencesManager.updateUserPreferences(userPreferences);
                return new UserPreferencesDto(userPreferencesManager.getUserPreferences(username));
            } else {
                throw new ResourceNotFoundException(ERRCODE_USER_PREFERENCES_DOES_NOT_EXISTS, "userPreferences",
                        username);
            }
        } catch (EntException e) {
            logger.error("Error updating userPreferences for {}", username, e);
            throw new RestServerError("Error updating userPreferences", e);
        }
    }

    private void createNewDefaultUserPreferences(String username) {
        try {
            UserPreferences userPreferences = new UserPreferences();
            userPreferences.setUsername(username);
            userPreferences.setWizard(true);
            userPreferences.setTranslationWarning(true);
            userPreferences.setLoadOnPageSelect(true);
            userPreferencesManager.addUserPreferences(userPreferences);
        } catch (EntException e) {
            logger.error("Error in creating new default userPreferences for {}", username, e);
            throw new RestServerError("Error creating new default userPreferences", e);
        }
    }

    public void setUserPreferencesManager(
            IUserPreferencesManager userPreferencesManager) {
        this.userPreferencesManager = userPreferencesManager;
    }
}

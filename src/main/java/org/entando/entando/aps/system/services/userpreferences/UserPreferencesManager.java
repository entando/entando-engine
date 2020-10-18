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
package org.entando.entando.aps.system.services.userpreferences;

import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;

public class UserPreferencesManager implements IUserPreferencesManager {

    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(UserPreferencesManager.class);

    private UserPreferencesDAO userPreferencesDAO;

    @Override
    public void addUserPreferences(UserPreferences userPreferences) throws EntException {
        try {
            userPreferencesDAO.addUserPreferences(userPreferences);
        } catch (Throwable t) {
            logger.error("Error saving user preferences: {}", userPreferences, t);
            throw new EntException("Error saving user preferences", t);
        }
    }

    @Override
    public UserPreferences getUserPreferences(String username) throws EntException {
        try {
            return userPreferencesDAO.loadUserPreferences(username);
        } catch (Throwable t) {
            logger.error("Error loading user preferences for user: {}", username, t);
            throw new EntException("Error loading user preferences", t);
        }
    }

    @Override
    public void updateUserPreferences(UserPreferences userPreferences) throws EntException {
        try {
            userPreferencesDAO.updateUserPreferences(userPreferences);
        } catch (Throwable t) {
            logger.error("Error updating user preferences: {}", userPreferences, t);
            throw new EntException("Error updating user preferences", t);
        }
    }

    @Override
    public void deleteUserPreferences(String username) throws EntException {
        try {
            userPreferencesDAO.deleteUserPreferences(username);
        } catch (Throwable t) {
            logger.error("Error deleting user preferences for user: {}", username, t);
            throw new EntException("Error updating user preferences", t);
        }
    }

    public void setUserPreferencesDAO(
            UserPreferencesDAO userPreferencesDAO) {
        this.userPreferencesDAO = userPreferencesDAO;
    }
}

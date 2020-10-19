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

/**
 * The interface for user preferences DAO.
 *
 * @author b.queiroz
 */
public interface IUserPreferencesDAO {

    /**
     * Load user preferences user preferences for a given user.
     *
     * @param username the username
     * @return the user preferences
     */
    UserPreferences loadUserPreferences(String username);

    /**
     * Add user preferences.
     *
     * @param userPreferences the user preferences to be added
     */
    void addUserPreferences(UserPreferences userPreferences);

    /**
     * Update user preferences.
     *
     * @param userPreferences the user preferences to be updated
     */
    void updateUserPreferences(UserPreferences userPreferences);

    /**
     * Delete user preferences.
     *
     * @param username the username which will have its user preferences deleted
     */
    void deleteUserPreferences(String username);
}

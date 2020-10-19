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
     * @throws EntException the ent exception
     */
    UserPreferences loadUserPreferences(String username) throws EntException;

    /**
     * Add user preferences.
     *
     * @param userPreferences the user preferences to be added
     * @throws EntException the ent exception
     */
    void addUserPreferences(UserPreferences userPreferences) throws EntException;

    /**
     * Update user preferences.
     *
     * @param userPreferences the user preferences to be updated
     * @throws EntException the ent exception
     */
    void updateUserPreferences(UserPreferences userPreferences) throws EntException;

    /**
     * Delete user preferences.
     *
     * @param username the username which will have its user preferences deleted
     * @throws EntException the ent exception
     */
    void deleteUserPreferences(String username) throws EntException;
}

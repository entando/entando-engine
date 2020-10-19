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

import org.entando.entando.web.userpreferences.model.UserPreferencesDto;
import org.entando.entando.web.userpreferences.model.UserPreferencesRequest;

/**
 * The interface for user preferences service.
 *
 * @author b.queiroz
 */
public interface IUserPreferencesService {

    /**
     * Gets user preferences for a given user.
     *
     * @param username the username
     * @return the current user preferences
     */
    UserPreferencesDto getUserPreferences(String username);

    /**
     * Update user preferences for a given user.
     *
     * @param username the username
     * @param request  the request
     * @return the updated user preferences
     */
    UserPreferencesDto updateUserPreferences(String username, UserPreferencesRequest request);
}

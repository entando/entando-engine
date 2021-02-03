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
package org.entando.entando.aps.system.services.userprofilepicture;

import org.entando.entando.ent.exception.EntException;

/**
 * The interface for user profile picture manager.
 *
 * @author b.queiroz
 */
public interface IUserProfilePictureManager {

    /**
     * Gets user profile picture for a given user.
     *
     * @param username the username
     * @return the user profile picture
     * @throws EntException the ent exception
     */
    UserProfilePicture getUserProfilePicture(String username) throws EntException;

    /**
     * Add user profile picture.
     *
     * @param userProfilePicture the user profile picture to be added
     * @throws EntException the ent exception
     */
    void addUserProfilePicture(UserProfilePicture userProfilePicture) throws EntException;

    /**
     * Update user profile picture.
     *
     * @param userProfilePicture the user profile picture to be updated
     * @throws EntException the ent exception
     */
    void updateUserProfilePicture(UserProfilePicture userProfilePicture) throws EntException;

    /**
     * Delete user profile picture.
     *
     * @param username the username that will have its user profile picture deleted
     * @throws EntException the ent exception
     */
    void deleteUserProfilePicture(String username) throws EntException;
}
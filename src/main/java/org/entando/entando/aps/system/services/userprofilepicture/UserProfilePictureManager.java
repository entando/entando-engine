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

public class UserProfilePictureManager implements IUserProfilePictureManager {

    private IUserProfilePictureDAO userProfilePictureDAO;

    @Override
    public UserProfilePicture getUserProfilePicture(String username) throws EntException {
        return userProfilePictureDAO.loadUserProfilePicture(username);
    }

    @Override
    public void addUserProfilePicture(UserProfilePicture userProfilePicture) throws EntException {
        userProfilePictureDAO.addUserProfilePicture(userProfilePicture);
    }

    @Override
    public void updateUserProfilePicture(UserProfilePicture userProfilePicture) throws EntException {
        userProfilePictureDAO.updateUserProfilePicture(userProfilePicture);
    }

    @Override
    public void deleteUserProfilePicture(String username) throws EntException {
        userProfilePictureDAO.deleteUserProfilePicture(username);
    }

    public void setUserProfilePictureDAO(UserProfilePictureDAO userProfilePictureDAO) {
        this.userProfilePictureDAO = userProfilePictureDAO;
    }
}

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
package org.entando.entando.aps.system.services.userprofilepicture;

import com.agiletec.aps.system.services.user.UserDetails;
import org.entando.entando.web.userprofilepicture.model.UserProfilePictureDto;
import org.springframework.web.multipart.MultipartFile;

public interface IUserProfilePictureService {
    UserProfilePictureDto getUserProfilePicture(UserDetails user);
    UserProfilePictureDto addUserProfilePicture(MultipartFile file, UserDetails user);
    UserProfilePictureDto updateUserProfilePicture(MultipartFile file, UserDetails user);
    void deleteUserProfilePicture(UserDetails user);
}

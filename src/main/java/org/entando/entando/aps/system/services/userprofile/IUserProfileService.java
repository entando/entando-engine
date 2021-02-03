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
package org.entando.entando.aps.system.services.userprofile;

import com.agiletec.aps.system.services.user.UserDetails;
import org.entando.entando.aps.system.services.entity.model.EntityDto;
import org.entando.entando.web.userprofilepicture.model.UserProfilePictureDto;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author E.Santoboni
 */
public interface IUserProfileService {

    EntityDto getUserProfile(String username);
    EntityDto addUserProfile(EntityDto request, BindingResult bindingResult);
    EntityDto updateUserProfile(EntityDto request, BindingResult bindingResult);

}

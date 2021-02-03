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
package org.entando.entando.web.userprofile.validator;

import com.agiletec.aps.system.common.entity.IEntityManager;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.aps.system.services.userprofile.IUserProfileManager;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
import org.entando.entando.web.entity.validator.EntityValidator;
import org.entando.entando.web.user.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

/**
 * @author E.Santoboni
 */
@Component
public class ProfileValidator extends EntityValidator {

    @Autowired
    private IUserProfileManager userProfileManager;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private IUserManager userManager;

    public boolean existProfile(String username) {
        return super.existEntity(username);
    }

    public void validate(UserDetails user, String username, BindingResult bindingResult) {
        validateSameUser(user, username, bindingResult);
        if (!userExists(username)) {
            throw new ResourceNotFoundException(EntityValidator.ERRCODE_ENTITY_DOES_NOT_EXIST, "User", username);
        }
    }

    private void validateSameUser(UserDetails user, String username, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        userValidator.validateSameUser(username, user.getUsername(), bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
    }

    private boolean userExists(String username) {
        try {
            return userManager.getUser(username) != null;
        } catch (EntException e) {
            logger.error("Error in checking user existence {}", username, e);
            throw new RestServerError("Error in loading user", e);
        }
    }

    @Override
    protected IEntityManager getEntityManager() {
        return this.userProfileManager;
    }

}

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
package org.entando.entando.web.userprofilepicture;

import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import java.util.HashMap;
import java.util.Map;
import org.entando.entando.aps.system.services.userprofilepicture.IUserProfilePictureService;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.web.common.annotation.RestAccessControl;
import org.entando.entando.web.common.model.SimpleRestResponse;
import org.entando.entando.web.userprofile.validator.ProfileValidator;
import org.entando.entando.web.userprofilepicture.model.UserProfilePictureDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

@RestController
@SessionAttributes("user")
public class ProfilePictureController {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(this.getClass());

    @Autowired
    private IUserProfilePictureService service;
    @Autowired
    private ProfileValidator profileValidator;

    protected IUserProfilePictureService getService() {
        return service;
    }

    public void setService(IUserProfilePictureService service) {
        this.service = service;
    }

    public ProfileValidator getProfileValidator() {
        return profileValidator;
    }

    public void setProfileValidator(ProfileValidator profileValidator) {
        this.profileValidator = profileValidator;
    }

    @RestAccessControl(permission = Permission.MANAGE_USER_PROFILES)
    @GetMapping(value = "/userProfiles/{username}/profilePicture", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<UserProfilePictureDto>> getProfilePicture(
            @ModelAttribute("user") UserDetails user, @PathVariable String username, BindingResult bindingResult) {
        logger.debug("REST request - get profile picture");
        profileValidator.validate(user, username, bindingResult);
        UserProfilePictureDto result = service.getUserProfilePicture(user);
        return ResponseEntity.ok(new SimpleRestResponse<>(result));
    }

    @RestAccessControl(permission = Permission.MANAGE_USER_PROFILES)
    @PostMapping(value = "/userProfiles/{username}/profilePicture", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<UserProfilePictureDto>> addProfilePicture(
            @ModelAttribute("user") UserDetails user, @PathVariable String username, BindingResult bindingResult,
            @RequestParam(value = "file") MultipartFile file) {
        logger.debug("REST request - create profile picture");
        profileValidator.validate(user, username, bindingResult);
        UserProfilePictureDto result = service.addUserProfilePicture(file, user);
        return ResponseEntity.ok(new SimpleRestResponse<>(result));
    }

    @RestAccessControl(permission = Permission.MANAGE_USER_PROFILES)
    @PutMapping(value = "/userProfiles/{username}/profilePicture", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<UserProfilePictureDto>> editProfilePicture(
            @ModelAttribute("user") UserDetails user, @PathVariable String username, BindingResult bindingResult,
            @RequestParam(value = "file") MultipartFile file) {
        logger.debug("REST request - edit profile picture");
        profileValidator.validate(user, username, bindingResult);
        UserProfilePictureDto result = service.updateUserProfilePicture(file, user);
        return ResponseEntity.ok(new SimpleRestResponse<>(result));
    }

    @RestAccessControl(permission = Permission.MANAGE_USER_PROFILES)
    @DeleteMapping(value = "/userProfiles/{username}/profilePicture", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<Map<String, String>>> deleteProfilePicture(
            @ModelAttribute("user") UserDetails user, @PathVariable String username, BindingResult bindingResult) {
        logger.debug("REST request - remove profile picture");
        profileValidator.validate(user, username, bindingResult);
        service.deleteUserProfilePicture(user);
        Map<String, String> result = new HashMap<>();
        result.put("username", username);
        return ResponseEntity.ok(new SimpleRestResponse<>(result));
    }

}

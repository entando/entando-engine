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
package org.entando.entando.web.user;

import static com.agiletec.aps.system.SystemConstants.GUEST_USER_NAME;
import static org.entando.entando.web.user.validator.UserValidator.createDeleteAdminError;
import static org.entando.entando.web.user.validator.UserValidator.createSelfDeleteUserError;
import static org.entando.entando.web.user.validator.UserValidator.isAdminUser;
import static org.entando.entando.web.user.validator.UserValidator.isUserDeletingHimself;

import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.system.services.user.UserGroupPermissions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.entando.entando.aps.system.services.group.model.GroupDto;
import org.entando.entando.aps.system.services.user.IUserService;
import org.entando.entando.aps.system.services.user.model.UserAuthorityDto;
import org.entando.entando.aps.system.services.user.model.UserDto;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.web.common.annotation.RestAccessControl;
import org.entando.entando.web.common.exceptions.EntandoAuthorizationException;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.PagedRestResponse;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.common.model.SimpleRestResponse;
import org.entando.entando.web.user.model.UpdatePasswordRequest;
import org.entando.entando.web.user.model.UserAuthoritiesRequest;
import org.entando.entando.web.user.model.UserRequest;
import org.entando.entando.web.user.model.UserUpdatePasswordRequest;
import org.entando.entando.web.user.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author paddeo
 */
@Validated
@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    private UserValidator userValidator;

    public IUserService getUserService() {
        return userService;
    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    public UserValidator getUserValidator() {
        return userValidator;
    }

    public void setUserValidator(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

    @RestAccessControl(permission = Permission.ENTER_BACKEND)
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedRestResponse<UserDto>> getUsers(RestListRequest requestList, @RequestParam(value = "withProfile", required = false) String withProfile) {
        logger.debug("getting users details with request {}", requestList);
        this.getUserValidator().validateRestListRequest(requestList, UserDto.class);
        PagedMetadata<UserDto> result = this.getUserService().getUsers(requestList, withProfile);
        if (withProfile != null) {
            result.addAdditionalParams("withProfile", withProfile);
        }
        return new ResponseEntity<>(new PagedRestResponse<>(result), HttpStatus.OK);
    }

    @RestAccessControl(permission = {Permission.MANAGE_USERS, Permission.MANAGE_USER_PROFILES, Permission.VIEW_USERS})
    @RequestMapping(value = "/{username:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<UserDto>> getUser(@PathVariable String username) {
        logger.debug("getting user {} details", username);
        UserDto user = this.getUserService().getUser(username);
        return new ResponseEntity<>(new SimpleRestResponse<>(user), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_USERS)
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<UserDto>> addUser(
            @Valid @RequestBody UserRequest userRequest,
            BindingResult bindingResult) {
        logger.debug("adding user with request {}", userRequest);
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        this.getUserValidator().validateUserPost(userRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        UserDto dto = this.getUserService().addUser(userRequest);
        return new ResponseEntity<>(new SimpleRestResponse<>(dto), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_USERS)
    @RequestMapping(value = "/{target:.+}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<UserDto>> updateUser(@RequestAttribute("user") UserDetails user, @PathVariable String target, @Valid @RequestBody UserRequest userRequest, BindingResult bindingResult) {
        logger.debug("updating user {} with request {}", target, userRequest);
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        this.getUserValidator().validatePutBody(target, userRequest, bindingResult);
        this.getUserValidator().validateUpdateSelf(target, user.getUsername(), bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        UserDto userDto = this.getUserService().updateUser(userRequest);
        return new ResponseEntity<>(new SimpleRestResponse<>(userDto), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_USERS)
    @RequestMapping(value = "/{username:.+}/password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<UserDto>> updateUserPassword(@PathVariable String username, @Valid @RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest, BindingResult bindingResult) {
        logger.debug("changing password for user {}", username);
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        this.getUserValidator().validateChangePasswords(username, userUpdatePasswordRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        UserDto userDto = this.getUserService().updateUserPassword(userUpdatePasswordRequest);
        return new ResponseEntity<>(new SimpleRestResponse<>(userDto), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_USERS)
    @RequestMapping(value = "/{target:.+}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<Map<String, String>>> deleteUser(
            //-
            @RequestAttribute("user") UserDetails user,
            @PathVariable String target) {
        logger.debug("deleting {}", target);
        if (isAdminUser(target)) {
            throw new ValidationGenericException(createDeleteAdminError());
        }
        if (isUserDeletingHimself(target, user.getUsername())) {
            DataBinder binder = new DataBinder(target);
            BindingResult bindingResult = binder.getBindingResult();
            throw new ValidationGenericException(createSelfDeleteUserError(bindingResult));
        }
        this.getUserService().removeUser(target);
        Map<String, String> result = new HashMap<>();
        result.put("code", target);
        return new ResponseEntity<>(new SimpleRestResponse<>(result), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_USERS)
    @RequestMapping(value = "/{target:.+}/authorities", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<List<UserAuthorityDto>>> updateUserAuthorities(@RequestAttribute("user") UserDetails user, @PathVariable String target, @Valid @RequestBody UserAuthoritiesRequest authRequest, BindingResult bindingResult) {
        logger.debug("user {} requesting update authorities for username {} with req {}", user.getUsername(), target, authRequest);
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        //business validations
        this.getUserValidator().validate(authRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        this.getUserValidator().validateUpdateSelf(target, user.getUsername(), bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        List<UserAuthorityDto> authorities = this.getUserService().updateUserAuthorities(target, authRequest);
        return new ResponseEntity<>(new SimpleRestResponse<>(authorities), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_USERS)
    @RequestMapping(value = "/{target:.+}/authorities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<List<UserAuthorityDto>>> getUserAuthorities(@RequestAttribute("user") UserDetails user, @PathVariable String target) {
        logger.debug("requesting authorities for username {}", target);
        List<UserAuthorityDto> authorities = this.getUserService().getUserAuthorities(target);
        return new ResponseEntity<>(new SimpleRestResponse<>(authorities), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_USERS)
    @RequestMapping(value = "/{target:.+}/authorities", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<List<UserAuthorityDto>>> addUserAuthorities(
            @RequestAttribute("user") UserDetails user,
            @PathVariable String target,
            @Valid @RequestBody UserAuthoritiesRequest authRequest,
            BindingResult bindingResult) {
        logger.debug("user {} requesting add authorities for username {} with req {}",
                user.getUsername(), target, authRequest);
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        //business validations
        getUserValidator().validate(authRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        this.getUserValidator().validateUpdateSelf(target, user.getUsername(), bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        List<UserAuthorityDto> authorities = this.getUserService().addUserAuthorities(target, authRequest);
        return new ResponseEntity<>(new SimpleRestResponse<>(authorities), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_USERS)
    @RequestMapping(value = "/{target:.+}/authorities", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<ArrayList<Object>>> deleteUserAuthorities(
            @RequestAttribute("user") UserDetails user,
            @PathVariable String target) {
        //-
        logger.debug("user {} requesting delete authorities for username {}", user.getUsername(), target);
        DataBinder binder = new DataBinder(target);
        BindingResult bindingResult = binder.getBindingResult();
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        //business validations
        getUserValidator().validateUpdateSelf(target, user.getUsername(), bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        this.getUserService().deleteUserAuthorities(target);
        return new ResponseEntity<>(new SimpleRestResponse<>(new ArrayList<>()), HttpStatus.OK);
    }


    @GetMapping("/myGroupPermissions")
    public ResponseEntity<SimpleRestResponse<List<UserGroupPermissions>>> getMyGroupPermissions(@RequestAttribute("user") UserDetails userDetails) {
        List<UserGroupPermissions> currentUserPermissions = this.userService.getMyGroupPermissions(userDetails);

        return new ResponseEntity<>(new SimpleRestResponse<>(currentUserPermissions), HttpStatus.OK);
    }

    @GetMapping("/myGroups")
    public ResponseEntity<SimpleRestResponse<List<GroupDto>>> getMyGroups(@RequestAttribute("user") UserDetails userDetails) {
        List<GroupDto> groups = this.userService.getMyGroups(userDetails);
        return new ResponseEntity<>(new SimpleRestResponse<>(groups), HttpStatus.OK);
    }

    @PostMapping(value = "/myPassword", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<UserDto>> updateMyPassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest,
            BindingResult bindingResult, @RequestAttribute("user") UserDetails userDetails, HttpServletRequest request) {

        if (GUEST_USER_NAME.equals(userDetails.getUsername())) {
            throw new EntandoAuthorizationException(null, request, userDetails.getUsername());
        }

        UserUpdatePasswordRequest userPasswordRequest = new UserUpdatePasswordRequest();
        userPasswordRequest.setUsername(userDetails.getUsername());
        userPasswordRequest.setOldPassword(updatePasswordRequest.getOldPassword());
        userPasswordRequest.setNewPassword(updatePasswordRequest.getNewPassword());

        return updateUserPassword(userDetails.getUsername(), userPasswordRequest, bindingResult);
    }
}

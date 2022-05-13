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
package org.entando.entando.web.userprofile;

import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.services.entity.model.AttributeTypeDto;
import org.entando.entando.aps.system.services.entity.model.EntityTypeAttributeFullDto;
import org.entando.entando.aps.system.services.entity.model.EntityTypeShortDto;
import org.entando.entando.aps.system.services.entity.model.EntityTypesStatusDto;
import org.entando.entando.aps.system.services.user.IUserService;
import org.entando.entando.aps.system.services.user.model.UserDto;
import org.entando.entando.aps.system.services.userprofile.IUserProfileTypeService;
import org.entando.entando.aps.system.services.userprofile.model.UserProfileTypeDto;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.web.common.annotation.RestAccessControl;
import org.entando.entando.web.common.exceptions.ValidationConflictException;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.PagedRestResponse;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.common.model.RestResponse;
import org.entando.entando.web.common.model.SimpleRestResponse;
import org.entando.entando.web.entity.validator.AbstractEntityTypeValidator;
import org.entando.entando.web.userprofile.model.ProfileTypeDtoRequest;
import org.entando.entando.web.userprofile.model.ProfileTypeRefreshRequest;
import org.entando.entando.web.userprofile.validator.ProfileTypeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author E.Santoboni
 */
@RestController
public class ProfileTypeController {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(this.getClass());

    private static final String OBJECT_NAME_PROFILE_TYPE = "Profile Type";

    @Autowired
    private IUserProfileTypeService userProfileTypeService;

    @Autowired
    private ProfileTypeValidator profileTypeValidator;

    @Autowired
    private IUserService userService;

    protected IUserProfileTypeService getUserProfileTypeService() {
        return userProfileTypeService;
    }

    public void setUserProfileTypeService(IUserProfileTypeService userProfileTypeService) {
        this.userProfileTypeService = userProfileTypeService;
    }

    protected ProfileTypeValidator getProfileTypeValidator() {
        return profileTypeValidator;
    }

    public void setProfileTypeValidator(ProfileTypeValidator profileTypeValidator) {
        this.profileTypeValidator = profileTypeValidator;
    }

    @RestAccessControl(permission = {Permission.MANAGE_USERS, Permission.MANAGE_USER_PROFILES, Permission.VIEW_USERS})
    @RequestMapping(value = "/profileTypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedRestResponse<EntityTypeShortDto>> getUserProfileTypes(RestListRequest requestList) throws JsonProcessingException {
        this.getProfileTypeValidator().validateRestListRequest(requestList, UserProfileTypeDto.class);
        PagedMetadata<EntityTypeShortDto> result = this.getUserProfileTypeService().getShortUserProfileTypes(requestList);
        logger.debug("Main Response -> {}", result);
        this.getProfileTypeValidator().validateRestListResult(requestList, result);
        return new ResponseEntity<>(new PagedRestResponse<>(result), HttpStatus.OK);
    }

    @RestAccessControl(permission = {Permission.ENTER_BACKEND})
    @GetMapping(value = "/myProfileType", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<UserProfileTypeDto>> getMyProfileType(@RequestAttribute("user") UserDetails user) {
        logger.debug("Requested profile type for the logged user-> {}", user.getUsername());
        UserDto userDto = userService.getUser(user.getUsername());
        if (userDto != null && userDto.getProfileType() != null && userDto.getProfileType().getTypeCode() != null) {
            UserProfileTypeDto dto = this.getUserProfileTypeService()
                    .getUserProfileType(userDto.getProfileType().getTypeCode());
            logger.debug("Main Response -> {}", dto);
            return new ResponseEntity<>(new SimpleRestResponse<>(dto), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException(AbstractEntityTypeValidator.ERRCODE_ENTITY_TYPE_DOES_NOT_EXIST, OBJECT_NAME_PROFILE_TYPE, "");
        }
    }

    @RestAccessControl(permission = {Permission.MANAGE_USER_PROFILES})
    @RequestMapping(value = "/profileTypes/{profileTypeCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<UserProfileTypeDto>> getUserProfileType(@PathVariable String profileTypeCode) throws JsonProcessingException {
        logger.debug("Requested profile type -> {}", profileTypeCode);
        if (!this.getProfileTypeValidator().existType(profileTypeCode)) {
            throw new ResourceNotFoundException(AbstractEntityTypeValidator.ERRCODE_ENTITY_TYPE_DOES_NOT_EXIST, OBJECT_NAME_PROFILE_TYPE, profileTypeCode);
        }
        UserProfileTypeDto dto = this.getUserProfileTypeService().getUserProfileType(profileTypeCode);
        logger.debug("Main Response -> {}", dto);
        return new ResponseEntity<>(new SimpleRestResponse<>(dto), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_USER_PROFILES)
    @RequestMapping(value = "/profileTypes", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<UserProfileTypeDto>> addUserProfileTypes(@Valid @RequestBody ProfileTypeDtoRequest bodyRequest,
            BindingResult bindingResult) throws JsonProcessingException {
        //field validations
        this.getProfileTypeValidator().validate(bodyRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        //business validations
        if (this.getProfileTypeValidator().existType(bodyRequest.getCode())) {
            bindingResult.reject(AbstractEntityTypeValidator.ERRCODE_ENTITY_TYPE_ALREADY_EXISTS, new String[]{bodyRequest.getCode()}, "entityType.exists");
        }
        if (bindingResult.hasErrors()) {
            throw new ValidationConflictException(bindingResult);
        }
        UserProfileTypeDto result = this.getUserProfileTypeService().addUserProfileType(bodyRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        logger.debug("Main Response -> {}", result);
        return new ResponseEntity<>(new SimpleRestResponse<>(result), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/profileTypes/{profileTypeCode}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<UserProfileTypeDto>> updateUserProfileType(@PathVariable String profileTypeCode,
            @Valid @RequestBody ProfileTypeDtoRequest request, BindingResult bindingResult) throws JsonProcessingException {
        int result = this.getProfileTypeValidator().validateBodyName(profileTypeCode, request, bindingResult);
        if (bindingResult.hasErrors()) {
            if (result == 404) {
                throw new ResourceNotFoundException(AbstractEntityTypeValidator.ERRCODE_ENTITY_TYPE_DOES_NOT_EXIST, "profile type", profileTypeCode);
            } else {
                throw new ValidationGenericException(bindingResult);
            }
        }
        UserProfileTypeDto dto = this.getUserProfileTypeService().updateUserProfileType(request, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        logger.debug("Main Response -> {}", dto);
        return new ResponseEntity<>(new SimpleRestResponse<>(dto), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/profileTypes/{profileTypeCode}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<Map>> deleteUserProfileType(@PathVariable String profileTypeCode) {
        logger.debug("Deleting profile type -> {}", profileTypeCode);
        this.getUserProfileTypeService().deleteUserProfileType(profileTypeCode);
        Map<String, String> result = new HashMap<>();
        result.put("code", profileTypeCode);
        return new ResponseEntity<>(new SimpleRestResponse<>(result), HttpStatus.OK);
    }

    // ********************* ATTRIBUTE TYPES *********************
    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/profileTypeAttributes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedRestResponse<String>> getUserProfileAttributeTypes(RestListRequest requestList) throws JsonProcessingException {
        this.getProfileTypeValidator().validateRestListRequest(requestList, AttributeTypeDto.class);
        PagedMetadata<String> result = this.getUserProfileTypeService().getAttributeTypes(requestList);
        logger.debug("Main Response -> {}", result);
        this.getProfileTypeValidator().validateRestListResult(requestList, result);
        return new ResponseEntity<>(new PagedRestResponse<>(result), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/profileTypeAttributes/{attributeTypeCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<AttributeTypeDto>> getUserProfileAttributeType(@PathVariable String attributeTypeCode) throws JsonProcessingException {
        logger.debug("Extracting attribute type -> {}", attributeTypeCode);
        AttributeTypeDto attribute = this.getUserProfileTypeService().getAttributeType(attributeTypeCode);
        logger.debug("Main Response -> {}", attribute);
        return new ResponseEntity<>(new SimpleRestResponse<>(attribute), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @GetMapping(value = "/profileTypeAttributes/{profileTypeCode}/attribute/{attributeTypeCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<AttributeTypeDto>> getUserProfileAttributeType(@PathVariable String profileTypeCode, @PathVariable String attributeTypeCode) {
        logger.debug("Profile type {}, Extracting attribute type -> {}", profileTypeCode, attributeTypeCode);
        AttributeTypeDto attribute = this.getUserProfileTypeService().getAttributeType(profileTypeCode, attributeTypeCode);
        logger.debug("Main Response -> {}", attribute);
        return new ResponseEntity<>(new SimpleRestResponse<>(attribute), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/profileTypes/{profileTypeCode}/attribute/{attributeCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<EntityTypeAttributeFullDto, Map>> getUserProfileAttribute(@PathVariable String profileTypeCode, @PathVariable String attributeCode) throws JsonProcessingException {
        logger.debug("Requested profile type {} - attribute {}", profileTypeCode, attributeCode);
        EntityTypeAttributeFullDto dto = this.getUserProfileTypeService().getUserProfileAttribute(profileTypeCode, attributeCode);
        logger.debug("Main Response -> {}", dto);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("profileTypeCode", profileTypeCode);
        return new ResponseEntity<>(new RestResponse<>(dto, metadata), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/profileTypes/{profileTypeCode}/attribute", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<EntityTypeAttributeFullDto, Map>> addUserProfileAttribute(@PathVariable String profileTypeCode, @Valid @RequestBody EntityTypeAttributeFullDto bodyRequest,
            BindingResult bindingResult) throws JsonProcessingException {
        logger.debug("Profile type {} - Adding attribute {}", profileTypeCode, bodyRequest);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        EntityTypeAttributeFullDto result = this.getUserProfileTypeService().addUserProfileAttribute(profileTypeCode, bodyRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        logger.debug("Main Response -> {}", result);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("profileTypeCode", profileTypeCode);
        return new ResponseEntity<>(new RestResponse<>(result, metadata), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/profileTypes/{profileTypeCode}/attribute/{attributeCode}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<EntityTypeAttributeFullDto, Map>> updateUserProfileAttribute(@PathVariable String profileTypeCode,
            @PathVariable String attributeCode, @Valid @RequestBody EntityTypeAttributeFullDto bodyRequest, BindingResult bindingResult) throws JsonProcessingException {
        logger.debug("Profile type {} - Updating attribute {}", profileTypeCode, bodyRequest);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        } else if (!StringUtils.equals(attributeCode, bodyRequest.getCode())) {
            bindingResult.rejectValue("code", ProfileTypeValidator.ERRCODE_URINAME_MISMATCH, new String[]{attributeCode, bodyRequest.getCode()}, "entityType.attribute.code.mismatch");
            throw new ValidationConflictException(bindingResult);
        }
        EntityTypeAttributeFullDto result = this.getUserProfileTypeService().updateUserProfileAttribute(profileTypeCode, bodyRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        logger.debug("Main Response -> {}", result);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("profileTypeCode", profileTypeCode);
        return new ResponseEntity<>(new RestResponse<>(result, metadata), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/profileTypes/{profileTypeCode}/attribute/{attributeCode}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<Map>> deleteUserProfileAttribute(
            @PathVariable String profileTypeCode,
            @PathVariable String attributeCode) {
        logger.debug("Deleting attribute {} from profile type {}", attributeCode, profileTypeCode);
        this.getUserProfileTypeService().deleteUserProfileAttribute(profileTypeCode, attributeCode);
        Map<String, String> result = new HashMap<>();
        result.put("profileTypeCode", profileTypeCode);
        result.put("attributeCode", attributeCode);
        return new ResponseEntity<>(new SimpleRestResponse<>(result), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/profileTypes/refresh/{profileTypeCode}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<Map>> reloadReferences(@PathVariable String profileTypeCode) throws Throwable {
        logger.debug("reload references of profile type {}", profileTypeCode);
        this.getUserProfileTypeService().reloadProfileTypeReferences(profileTypeCode);
        Map<String, String> result = new HashMap<>();
        result.put("status", "success");
        result.put("profileTypeCode", profileTypeCode);
        logger.debug("started reload references of profile type {}", profileTypeCode);
        return new ResponseEntity<>(new SimpleRestResponse<>(result), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/profileTypesStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<Map>> reloadReferences(@Valid @RequestBody ProfileTypeRefreshRequest bodyRequest,
            BindingResult bindingResult) throws Throwable {
        logger.debug("reload references of profile types {}", bodyRequest.getProfileTypeCodes());
        Map<String, Integer> status = this.getUserProfileTypeService().reloadProfileTypesReferences(bodyRequest.getProfileTypeCodes());
        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        result.put("profileTypeCodes", status);
        logger.debug("started reload references of profile types {}", bodyRequest.getProfileTypeCodes());
        return new ResponseEntity<>(new SimpleRestResponse<>(result), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/profileTypesStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<EntityTypesStatusDto>> extractStatus() throws Throwable {
        logger.debug("Extract profile types status");
        EntityTypesStatusDto status = this.getUserProfileTypeService().getProfileTypesRefreshStatus();
        logger.debug("Extracted profile types status {}", status);
        return new ResponseEntity<>(new SimpleRestResponse<>(status), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/profileTypes/{profileTypeCode}/attribute/{attributeCode}/moveUp", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<Map<String, String>>> moveUserProfileAttributeUp(
            @PathVariable String profileTypeCode,
            @PathVariable String attributeCode) {
        //-
        logger.debug("Move UP attribute {} from profile type {}", attributeCode, profileTypeCode);
        return this.moveUserProfileAttribute(profileTypeCode, attributeCode, true);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/profileTypes/{profileTypeCode}/attribute/{attributeCode}/moveDown", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<Map<String, String>>> moveUserProfileAttributeDown(
            @PathVariable String profileTypeCode,
            @PathVariable String attributeCode) {
        //-
        logger.debug("Move DOWN attribute {} from profile type {}", attributeCode, profileTypeCode);
        return this.moveUserProfileAttribute(profileTypeCode, attributeCode, false);
    }

    private ResponseEntity<SimpleRestResponse<Map<String, String>>> moveUserProfileAttribute(
            String profileTypeCode,
            String attributeCode,
            boolean moveUp) {
        //-
        this.getUserProfileTypeService().moveUserProfileAttribute(profileTypeCode, attributeCode, moveUp);
        Map<String, String> result = new HashMap<>();
        result.put("profileTypeCode", profileTypeCode);
        result.put("attributeCode", attributeCode);
        String movement = (moveUp) ? "UP" : "DOWN";
        result.put("movement", movement);
        return new ResponseEntity<>(new SimpleRestResponse<>(result), HttpStatus.OK);
    }
}

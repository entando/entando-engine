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
package org.entando.entando.aps.system.services.userprofile.api;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.StringApiResponse;
import org.entando.entando.aps.system.services.api.server.IResponseBuilder;
import org.entando.entando.aps.system.services.userprofile.IUserProfileManager;
import org.entando.entando.aps.system.services.userprofile.api.model.JAXBUserProfileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agiletec.aps.system.common.entity.IEntityManager;
import com.agiletec.aps.system.common.entity.IEntityTypesConfigurer;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import org.entando.entando.ent.exception.EntException;

/**
 * @author E.Santoboni
 */
public class ApiUserProfileTypeInterface {
	
	private static final Logger _logger =  LoggerFactory.getLogger(ApiUserProfileTypeInterface.class);
    
    public JAXBUserProfileType getUserProfileType(Properties properties) throws ApiException, Throwable {
        JAXBUserProfileType jaxbProfileType = null;
        try {
            String typeCode = properties.getProperty("typeCode");
            IApsEntity masterProfileType = this.getUserProfileManager().getEntityPrototype(typeCode);
            if (null == masterProfileType) {
                throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "User Profile type with code '" + typeCode + "' does not exist");
            }
            jaxbProfileType = new JAXBUserProfileType(masterProfileType);
        } catch (ApiException ae) {
            throw ae;
        } catch (Throwable t) {
        	_logger.error("Error extracting user profile type", t);
            //ApsSystemUtils.logThrowable(t, this, "getProfileType");
            throw new EntException("Error extracting user profile type", t);
        }
        return jaxbProfileType;
    }
    
    public StringApiResponse addUserProfileType(JAXBUserProfileType jaxbProfileType) throws Throwable {
        StringApiResponse response = new StringApiResponse();
        try {
            String typeCode = jaxbProfileType.getTypeCode();
            IApsEntity masterProfileType = this.getUserProfileManager().getEntityPrototype(typeCode);
            if (null != masterProfileType) {
                throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "User Profile type with code '" + typeCode + "' already exists");
            }
            if (typeCode == null || typeCode.length() != 3) {
                throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "Invalid type code - '" + typeCode + "'");
            }
            Map<String, AttributeInterface> attributes = this.getUserProfileManager().getEntityAttributePrototypes();
            IApsEntity profileType = jaxbProfileType.buildEntityType(this.getUserProfileManager().getEntityClass(), attributes);
            ((IEntityTypesConfigurer) this.getUserProfileManager()).addEntityPrototype(profileType);
            response.setResult(IResponseBuilder.SUCCESS, null);
        } catch (ApiException ae) {
            response.addErrors(ae.getErrors());
            response.setResult(IResponseBuilder.FAILURE, null);
        } catch (Throwable t) {
        	_logger.error("Error adding user profile type", t);
            //ApsSystemUtils.logThrowable(t, this, "addProfileType");
            throw new EntException("Error adding user profile type", t);
        }
        return response;
    }
    
    public StringApiResponse updateUserProfileType(JAXBUserProfileType jaxbProfileType) throws Throwable {
        StringApiResponse response = new StringApiResponse();
        try {
            String typeCode = jaxbProfileType.getTypeCode();
            IApsEntity masterProfileType = this.getUserProfileManager().getEntityPrototype(typeCode);
            if (null == masterProfileType) {
                throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "User Profile type with code '" + typeCode + "' doesn't exist");
            }
            Map<String, AttributeInterface> attributes = this.getUserProfileManager().getEntityAttributePrototypes();
            IApsEntity profileType = jaxbProfileType.buildEntityType(this.getUserProfileManager().getEntityClass(), attributes);
            ((IEntityTypesConfigurer) this.getUserProfileManager()).updateEntityPrototype(profileType);
            response.setResult(IResponseBuilder.SUCCESS, null);
        } catch (ApiException ae) {
            response.addErrors(ae.getErrors());
            response.setResult(IResponseBuilder.FAILURE, null);
        } catch (Throwable t) {
        	_logger.error("Error updating user profile type", t);
            //ApsSystemUtils.logThrowable(t, this, "updateProfileType");
            throw new EntException("Error updating user profile type", t);
        }
        return response;
    }
    
    public void deleteUserProfileType(Properties properties) throws ApiException, Throwable {
        try {
            String typeCode = properties.getProperty("typeCode");
            IApsEntity masterProfileType = this.getUserProfileManager().getEntityPrototype(typeCode);
            if (null == masterProfileType) {
                throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "User Profile type with code '" + typeCode + "' doesn't exist");
            }
            EntitySearchFilter filter = new EntitySearchFilter(IEntityManager.ENTITY_TYPE_CODE_FILTER_KEY, false, typeCode, false);
            List<String> profileIds = this.getUserProfileManager().searchId(new EntitySearchFilter[]{filter});
            if (null != profileIds && !profileIds.isEmpty()) {
                throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "User profile type '" + typeCode + "' are used into " + profileIds.size() + " profiles");
            }
            ((IEntityTypesConfigurer) this.getUserProfileManager()).removeEntityPrototype(typeCode);
        } catch (ApiException ae) {
            throw ae;
        } catch (Throwable t) {
        	_logger.error("Error deleting user Profile type", t);
            //ApsSystemUtils.logThrowable(t, this, "deleteProfileType");
            throw new EntException("Error deleting user Profile type", t);
        }
    }
    
    protected IUserProfileManager getUserProfileManager() {
        return _userProfileManager;
    }
    public void setUserProfileManager(IUserProfileManager userProfileManager) {
        this._userProfileManager = userProfileManager;
    }
    
    private IUserProfileManager _userProfileManager;
    
}

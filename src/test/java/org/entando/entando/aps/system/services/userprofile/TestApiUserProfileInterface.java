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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.attribute.AbstractComplexAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.AbstractListAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.CompositeAttribute;
import org.entando.entando.aps.system.services.api.ApiBaseTestCase;
import org.entando.entando.aps.system.services.api.UnmarshalUtils;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.ApiResource;
import org.entando.entando.aps.system.services.api.model.StringApiResponse;
import org.entando.entando.aps.system.services.api.server.IResponseBuilder;
import org.entando.entando.aps.system.services.userprofile.api.ApiUserProfileInterface;
import org.entando.entando.aps.system.services.userprofile.api.model.JAXBUserProfile;
import org.entando.entando.aps.system.services.userprofile.model.IUserProfile;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author E.Santoboni
 */
class TestApiUserProfileInterface extends ApiBaseTestCase {
    
    public static final String TEST_USERNAME = "testusername";

    private IUserProfileManager userProfileManager;

    @Test
    void testGetXmlUserProfile() throws Throwable {
        MediaType mediaType = MediaType.APPLICATION_XML_TYPE;
        this.testGetUserProfile(mediaType, "admin", "supervisorCoach", "it");
    }
    
    @Test
    void testGetJsonUserProfile() throws Throwable {
        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        this.testGetUserProfile(mediaType, "admin", "supervisorCoach", "en");
    }

    @Test
    void testCreateNewUserProfileFromXml() throws Throwable {
        MediaType mediaType = MediaType.APPLICATION_XML_TYPE;
        this.testCreateNewUserProfile(mediaType, "supervisorCoach");
    }
    
    @Test
    void testCreateNewUserProfileFromJson() throws Throwable {
        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        this.testCreateNewUserProfile(mediaType, "supervisorCoach");
    }
    
    protected void testCreateNewUserProfile(MediaType mediaType, String username) throws Throwable {
        JAXBUserProfile jaxbUserProfile = this.testGetUserProfile(mediaType, "admin", username, "it");
        ApiResource dataTypeResource = this.getApiCatalogManager().getResource("core", "userProfile");
        ApiMethod postMethod = dataTypeResource.getPostMethod();
        Properties properties = super.createApiProperties("admin", "it", mediaType);
        try {
            jaxbUserProfile.setId(TEST_USERNAME);
            Object response = this.getResponseBuilder().createResponse(postMethod, jaxbUserProfile, properties);
            assertNotNull(response);
            assertTrue(response instanceof StringApiResponse);
            assertEquals(IResponseBuilder.SUCCESS, ((StringApiResponse) response).getResult());
            IUserProfile userProfile = this.userProfileManager.getProfile(TEST_USERNAME);
            IUserProfile masterUserProfile = this.userProfileManager.getProfile(username);
            List<AttributeInterface> attributes = masterUserProfile.getAttributeList();
            for (int i = 0; i < attributes.size(); i++) {
                AttributeInterface attribute = attributes.get(i);
                AttributeInterface newAttribute = (AttributeInterface) userProfile.getAttribute(attribute.getName());
                this.checkAttributes(attribute, newAttribute);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.userProfileManager.deleteProfile(TEST_USERNAME);
        }
    }

    private void checkAttributes(AttributeInterface oldAttribute, AttributeInterface newAttribute) {
        if (null == newAttribute) {
            fail();
        }
        assertEquals(oldAttribute.getName(), newAttribute.getName());
        assertEquals(oldAttribute.getType(), newAttribute.getType());
        if (!oldAttribute.isSimple()) {
            if (oldAttribute instanceof AbstractListAttribute) {
                List<AttributeInterface> oldListAttributes = ((AbstractComplexAttribute) oldAttribute).getAttributes();
                List<AttributeInterface> newListAttributes = ((AbstractComplexAttribute) newAttribute).getAttributes();
                assertEquals(oldListAttributes.size(), newListAttributes.size());
                for (int i = 0; i < oldListAttributes.size(); i++) {
                    AttributeInterface oldElement = oldListAttributes.get(i);
                    AttributeInterface newElement = newListAttributes.get(i);
                    this.checkAttributes(oldElement, newElement);
                }
            } else if (oldAttribute instanceof CompositeAttribute) {
                Map<String, AttributeInterface> oldAttributeMap = ((CompositeAttribute) oldAttribute).getAttributeMap();
                Map<String, AttributeInterface> newAttributeMap = ((CompositeAttribute) newAttribute).getAttributeMap();
                assertEquals(oldAttributeMap.size(), newAttributeMap.size());
                Iterator<String> iterator = oldAttributeMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    AttributeInterface oldElement = oldAttributeMap.get(key);
                    AttributeInterface newElement = newAttributeMap.get(key);
                    this.checkAttributes(oldElement, newElement);
                }
            }
        } else {
            assertEquals(oldAttribute.getValue(), newAttribute.getValue());
        }
    }

    protected JAXBUserProfile testGetUserProfile(MediaType mediaType, String username, String profileId, String langCode) throws Throwable {
        ApiResource dataResource = this.getApiCatalogManager().getResource("core", "userProfile");
        ApiMethod getMethod = dataResource.getGetMethod();
        Properties properties = super.createApiProperties(username, langCode, mediaType);
        properties.put("username", profileId);
        Object result = this.getResponseBuilder().createResponse(getMethod, properties);
        assertNotNull(result);
        ApiUserProfileInterface apiUserProfileInterface = (ApiUserProfileInterface) this.getApplicationContext().getBean("ApiUserProfileInterface");
        Object singleResult = apiUserProfileInterface.getUserProfile(properties);
        assertNotNull(singleResult);
        String toString = this.marshall(singleResult, mediaType);
        InputStream stream = new ByteArrayInputStream(toString.getBytes());
        JAXBUserProfile jaxbData = (JAXBUserProfile) UnmarshalUtils.unmarshal(super.getApplicationContext(), JAXBUserProfile.class, stream, mediaType);
        assertNotNull(jaxbData);
        return jaxbData;
    }

    @BeforeEach
    protected void init() {
        super.init();
        this.userProfileManager = (IUserProfileManager) this.getApplicationContext().getBean(SystemConstants.USER_PROFILE_MANAGER);
    }

}

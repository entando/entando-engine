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

import com.agiletec.aps.system.common.entity.IEntityTypesConfigurer;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.MonoTextAttribute;
import com.agiletec.aps.system.common.entity.parse.attribute.MonoTextAttributeHandler;
import com.agiletec.aps.system.services.authorization.Authorization;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.system.services.user.UserManager;
import com.agiletec.aps.util.FileTextReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.entando.entando.aps.system.services.userprofile.IUserProfileManager;
import org.entando.entando.aps.system.services.userprofile.IUserProfileTypeService;
import org.entando.entando.aps.system.services.userprofile.model.IUserProfile;
import org.entando.entando.aps.system.services.userprofile.model.UserProfile;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.userprofile.model.ProfileTypeRefreshRequest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileTypeControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private IUserProfileTypeService userProfileTypeService;

    @Autowired
    private IUserProfileManager userProfileManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    @InjectMocks
    private ProfileTypeController controller;
    
    @Test
    void testGetUserProfileTypes() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/profileTypes")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        testCors("/profileTypes");
    }

    @Test
    void testGetValidUserProfileType() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/profileTypes/{profileTypeCode}", new Object[]{"PFL"})
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }

    @Test
    void testGetInvalidUserProfileType() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/profileTypes/{profileTypeCode}", new Object[]{"XXX"})
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isNotFound());
    }

    @Test
    void testAddInvalidProfileType() throws Exception {
        Assertions.assertNull(this.userProfileManager.getEntityPrototype("XXX"));
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        String body1 = "{\"code\": \"XXX\", \"name\": \"\", \"attributes\": []}";
        ResultActions result1 = mockMvc
                .perform(post("/profileTypes").content(body1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result1.andExpect(status().isBadRequest());
        result1.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
        result1.andExpect(jsonPath("$.errors[0].code", is("52")));

        String body2 = "{\"code\": \"\", \"name\": \"Description\", \"attributes\": []}";
        ResultActions result2 = mockMvc
                .perform(put("/profileTypes/{profileTypeCode}", new Object[]{"AAA"}).content(body2)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result2.andExpect(status().isBadRequest());
        result2.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
        result2.andExpect(jsonPath("$.errors[0].code", is("54")));
    }

    @Test
    void testAddGetUserProfileType_1() throws Exception {
        try {
            Assertions.assertNull(this.userProfileManager.getEntityPrototype("TST"));
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            this.executeProfileTypePost("2_POST_valid.json", accessToken, status().isOk());

            UserProfile addedType = (UserProfile) this.userProfileManager.getEntityPrototype("TST");
            Assertions.assertNotNull(addedType);
            Assertions.assertEquals("Profile Type TST", addedType.getTypeDescription());
            Assertions.assertEquals(3, addedType.getAttributeList().size());

            ResultActions result = mockMvc
                    .perform(get("/profileTypes/{profileTypeCode}", new Object[]{"TST"})
                            .header("Authorization", "Bearer " + accessToken));
            result.andExpect(status().isOk());
            result.andExpect(jsonPath("$.payload.code", is("TST")));
            result.andExpect(jsonPath("$.payload.attributes.size()", is(3)));
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
        } finally {
            if (null != this.userProfileManager.getEntityPrototype("TST")) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype("TST");
            }
        }
    }

    @Test
    void testGetMyUserProfileTypeOk() throws Exception {
        String loggedUsername = "logged_user";
        String loggedUserPassword = "0x24";
        String testTypeCode = "TST";
        try {
            Assertions.assertNull(this.userProfileManager.getEntityPrototype(testTypeCode));
            UserDetails adminUser = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                    .withAuthorization(Group.FREE_GROUP_NAME, "manageUserProfile", Permission.MANAGE_USER_PROFILES)
                    .build();
            String accessTokenAdmin = mockOAuthInterceptor(adminUser);
            this.executeProfileTypePost("11_POST_type_valid.json", accessTokenAdmin, status().isOk());
            Assertions.assertNotNull(this.userProfileManager.getEntityPrototype(testTypeCode));
            Assertions.assertNull(userManager.getUser(loggedUsername));

            final IUserProfile loggedUserProfile = userProfileManager.getProfileType(testTypeCode);

            User newUser = new User();
            newUser.setUsername(loggedUsername);
            newUser.setPassword(loggedUserPassword);
            newUser.setProfile(loggedUserProfile);
            Assertions.assertNull(userManager.getUser(loggedUsername));
            this.userManager.addUser(newUser);
            Assertions.assertNotNull(userManager.getUser(loggedUsername));

            UserDetails loggedUser = new OAuth2TestUtils.UserBuilder(loggedUsername, loggedUserPassword)
                    .withAuthorization(Group.FREE_GROUP_NAME, "editor", Permission.ENTER_BACKEND)
                    .withUserProfile(loggedUserProfile)
                    .build();

            String accessToken = mockOAuthInterceptor(loggedUser);

            executeGetMyProfileType(loggedUser, accessToken, status().isOk())
                    .andExpect(jsonPath("$.payload.code", is(testTypeCode)));;

        } finally {
            this.userProfileManager.deleteProfile(loggedUsername);
            this.userManager.removeUser(loggedUsername);
            if (null != this.userProfileManager.getEntityPrototype(testTypeCode)) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype(testTypeCode);
            }
        }
    }

    @Test
    void testGetMyUserProfileTypeInvalid() throws Exception {
        String loggedUsername = "logged_user";
        String loggedUserPassword = "0x24";
            final IUserProfile invalidProfile = new UserProfile();
            invalidProfile.setTypeCode("ABC");
            UserDetails loggedUser = new OAuth2TestUtils.UserBuilder(loggedUsername, loggedUserPassword)
                    .withAuthorization(Group.FREE_GROUP_NAME, "editor", Permission.ENTER_BACKEND)
                    .withUserProfile(invalidProfile)
                    .build();

            String accessToken = mockOAuthInterceptor(loggedUser);

            executeGetMyProfileType(loggedUser, accessToken, status().isNotFound());
    }

    @Test
    void testGetMyUserProfileTypeNotFound() throws Exception {
        String loggedUsername = "logged_user";
        String loggedUserPassword = "0x24";

        UserDetails loggedUser = new OAuth2TestUtils.UserBuilder(loggedUsername, loggedUserPassword)
                .withAuthorization(Group.FREE_GROUP_NAME, "editor", Permission.ENTER_BACKEND)
                .withUserProfile(null)
                .build();

        String accessToken = mockOAuthInterceptor(loggedUser);

        executeGetMyProfileType(loggedUser, accessToken, status().isNotFound());
    }

    @Test
    void testAddUpdateUserProfileType_1() throws Exception {
        try {
            Assertions.assertNull(this.userProfileManager.getEntityPrototype("AAA"));
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            this.executeProfileTypePost("1_POST_valid.json", accessToken, status().isOk());

            UserProfile addedType = (UserProfile) this.userProfileManager.getEntityPrototype("AAA");
            Assertions.assertNotNull(addedType);
            Assertions.assertEquals("Profile Type AAA", addedType.getTypeDescription());
            Assertions.assertEquals(1, addedType.getAttributeList().size());

            this.executeProfileTypePut("1_PUT_invalid.json", "AAA", accessToken, status().isBadRequest());

            this.executeProfileTypePut("1_PUT_valid.json", "AAA", accessToken, status().isOk());

            addedType = (UserProfile) this.userProfileManager.getEntityPrototype("AAA");
            Assertions.assertEquals("Profile Type AAA Modified", addedType.getTypeDescription());
            Assertions.assertEquals(2, addedType.getAttributeList().size());

            ResultActions result4 = mockMvc
                    .perform(delete("/profileTypes/{profileTypeCode}", new Object[]{"AAA"})
                            .header("Authorization", "Bearer " + accessToken));
            result4.andExpect(status().isOk());
            Assertions.assertNull(this.userProfileManager.getEntityPrototype("AAA"));
        } finally {
            if (null != this.userProfileManager.getEntityPrototype("AAA")) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype("AAA");
            }
        }
    }

    @Test
    void testAddUpdateUserProfileType_2() throws Exception {
        try {
            Assertions.assertNull(this.userProfileManager.getEntityPrototype("TST"));
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            ResultActions result1 = this.executeProfileTypePost("2_POST_invalid_1.json", accessToken, status().isBadRequest());
            result1.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            result1.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            Assertions.assertNull(this.userProfileManager.getEntityPrototype("TST"));

            ResultActions result2 = this.executeProfileTypePost("2_POST_invalid_2.json", accessToken, status().isBadRequest());
            result2.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            result2.andExpect(jsonPath("$.errors", Matchers.hasSize(3)));
            Assertions.assertNull(this.userProfileManager.getEntityPrototype("TST"));

            this.executeProfileTypePost("2_POST_valid.json", accessToken, status().isOk());
            UserProfile addedDataObject = (UserProfile) this.userProfileManager.getEntityPrototype("TST");
            Assertions.assertNotNull(addedDataObject);
            Assertions.assertEquals(3, addedDataObject.getAttributeList().size());

            this.executeProfileTypePost("2_POST_valid.json", accessToken, status().isConflict());

            this.executeProfileTypePut("2_PUT_valid.json", "AAA", accessToken, status().isBadRequest());

            this.executeProfileTypePut("2_PUT_valid.json", "TST", accessToken, status().isOk());

            UserProfile modifiedDataObject = (UserProfile) this.userProfileManager.getEntityPrototype("TST");
            Assertions.assertNotNull(modifiedDataObject);
            Assertions.assertEquals(5, modifiedDataObject.getAttributeList().size());

            ResultActions result4 = mockMvc
                    .perform(delete("/profileTypes/{profileTypeCode}", new Object[]{"TST"})
                            .header("Authorization", "Bearer " + accessToken));
            result4.andExpect(status().isOk());
            Assertions.assertNull(this.userProfileManager.getEntityPrototype("TST"));
        } finally {
            if (null != this.userProfileManager.getEntityPrototype("TST")) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype("TST");
            }
        }
    }
    
    private ResultActions executeProfileTypePost(String fileName, String accessToken, ResultMatcher expected) throws Exception {
        InputStream isJsonPostValid = this.getClass().getResourceAsStream(fileName);
        String jsonPostValid = FileTextReader.getText(isJsonPostValid);
        ResultActions result = mockMvc
                .perform(post("/profileTypes")
                        .content(jsonPostValid)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }

    private ResultActions executeProfileTypePut(String fileName, String typeCode, String accessToken, ResultMatcher expected) throws Exception {
        InputStream isJsonPostValid = this.getClass().getResourceAsStream(fileName);
        String jsonPostValid = FileTextReader.getText(isJsonPostValid);
        ResultActions result = mockMvc
                .perform(put("/profileTypes/{profileTypeCode}", new Object[]{typeCode})
                        .content(jsonPostValid)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }
    
    // attributes
    @Test
    void testGetUserProfileAttributeTypes_1() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/profileTypeAttributes")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        testCors("/profileTypeAttributes");
    }

    @Test
    void testGetUserProfileAttributeTypes_2() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/profileTypeAttributes").param("pageSize", "5")
                        .param("sort", "code").param("direction", "DESC")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(5)));
        result.andExpect(jsonPath("$.metaData.pageSize", is(5)));
        result.andExpect(jsonPath("$.metaData.lastPage", is(4)));
        result.andExpect(jsonPath("$.metaData.totalItems", is(16)));
        result.andExpect(jsonPath("$.payload[0]", is("Timestamp")));
    }

    @Test
    void testGetUserProfileAttributeTypes_3() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/profileTypeAttributes").param("pageSize", "7")
                        .param("sort", "code").param("direction", "ASC")
                        .param("filters[0].attribute", "code").param("filters[0].value", "tex")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(4)));
        result.andExpect(jsonPath("$.metaData.pageSize", is(7)));
        result.andExpect(jsonPath("$.metaData.lastPage", is(1)));
        result.andExpect(jsonPath("$.metaData.totalItems", is(4)));
        result.andExpect(jsonPath("$.payload[0]", is("Hypertext")));
    }
    
    @Test
    void testGetUserProfileAttributeType_1() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/profileTypeAttributes/{attributeTypeCode}", new Object[]{"Monotext"})
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload.code", is("Monotext")));
        result.andExpect(jsonPath("$.payload.multilingual", is(false)));
        result.andExpect(jsonPath("$.payload.dateFilterSupported", is(false)));
        result.andExpect(jsonPath("$.payload.allowedRoles", Matchers.hasSize(5)));
        result.andExpect(jsonPath("$.payload.simple", is(true)));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.metaData.size()", is(0)));
    }

    @Test
    void testGetUserProfileAttributeType_2() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/profileTypeAttributes/{attributeTypeCode}", new Object[]{"WrongTypeCode"})
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
        result.andExpect(jsonPath("$.metaData.size()", is(0)));
    }

    @Test
    void testGetUserProfileAttributeType_3() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/profileTypeAttributes/{profileTypeCode}/attribute/{attributeTypeCode}", new Object[]{"XXX", "Monotext"})
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
        result.andExpect(jsonPath("$.errors[0].code", is("1")));
        result.andExpect(jsonPath("$.metaData.size()", is(0)));

        result = mockMvc
                .perform(get("/profileTypeAttributes/{profileTypeCode}/attribute/{attributeTypeCode}", new Object[]{"PFL", "Monotext"})
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload.multilingual", is(false)));
        result.andExpect(jsonPath("$.payload.dateFilterSupported", is(false)));
        result.andExpect(jsonPath("$.payload.assignedRoles.size()", is(2)));
        result.andExpect(jsonPath("$.payload.assignedRoles.userprofile:fullname", is("fullname")));
        result.andExpect(jsonPath("$.payload.assignedRoles.userprofile:email", is("email")));
        result.andExpect(jsonPath("$.payload.allowedRoles", Matchers.hasSize(5)));
        result.andExpect(jsonPath("$.payload.dateFilterSupported", is(false)));
        result.andExpect(jsonPath("$.payload.simple", is(true)));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.metaData.size()", is(0)));
    }
    
    // ------------------------------------
    @Test
    void testGetUserProfileAttribute() throws Exception {
        try {
            Assertions.assertNull(this.userProfileManager.getEntityPrototype("TST"));
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            this.executeProfileTypePost("2_POST_valid.json", accessToken, status().isOk());

            ResultActions result1 = mockMvc
                    .perform(get("/profileTypes/{profileTypeCode}/attribute/{attributeCode}", new Object[]{"XXX", "TextAttribute"})
                            .header("Authorization", "Bearer " + accessToken));
            result1.andExpect(status().isNotFound());
            result1.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            result1.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            result1.andExpect(jsonPath("$.metaData.size()", is(0)));

            ResultActions result2 = mockMvc
                    .perform(get("/profileTypes/{profileTypeCode}/attribute/{attributeCode}", new Object[]{"TST", "WrongCpde"})
                            .header("Authorization", "Bearer " + accessToken));
            result2.andExpect(status().isNotFound());
            result2.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            result2.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            result2.andExpect(jsonPath("$.metaData.size()", is(0)));

            ResultActions result3 = mockMvc
                    .perform(get("/profileTypes/{profileTypeCode}/attribute/{attributeCode}", new Object[]{"TST", "TextAttribute"})
                            .header("Authorization", "Bearer " + accessToken));
            result3.andExpect(status().isOk());
            result3.andExpect(jsonPath("$.payload.code", is("TextAttribute")));
            result3.andExpect(jsonPath("$.payload.type", is("Text")));
            result3.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result3.andExpect(jsonPath("$.metaData.size()", is(1)));
            result3.andExpect(jsonPath("$.metaData.profileTypeCode", is("TST")));

        } finally {
            if (null != this.userProfileManager.getEntityPrototype("TST")) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype("TST");
            }
        }
    }

    @Test
    void testAddUserProfileAttribute() throws Exception {
        String typeCode = "TST";
        try {
            Assertions.assertNull(this.userProfileManager.getEntityPrototype(typeCode));
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            this.executeProfileTypePost("2_POST_valid.json", accessToken, status().isOk());

            ResultActions result1 = this.executeProfileAttributePost("3_POST_attribute_invalid_1.json", typeCode, accessToken, status().isConflict());
            result1.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            result1.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            result1.andExpect(jsonPath("$.errors[0].code", is("14")));
            result1.andExpect(jsonPath("$.metaData.size()", is(0)));

            ResultActions result2 = this.executeProfileAttributePost("3_POST_attribute_invalid_2.json", typeCode, accessToken, status().isBadRequest());
            result2.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            result2.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            result2.andExpect(jsonPath("$.errors[0].code", is("53")));
            result2.andExpect(jsonPath("$.metaData.size()", is(0)));

            ResultActions result3 = this.executeProfileAttributePost("3_POST_attribute_invalid_3.json", typeCode, accessToken, status().isBadRequest());
            result3.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            result3.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            result3.andExpect(jsonPath("$.errors[0].code", is("13")));
            result3.andExpect(jsonPath("$.metaData.size()", is(0)));

            ResultActions result4 = this.executeProfileAttributePost("3_POST_attribute_invalid_4.json", typeCode, accessToken, status().isBadRequest());
            result4.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            result4.andExpect(jsonPath("$.errors", Matchers.hasSize(2)));
            result4.andExpect(jsonPath("$.metaData.size()", is(0)));

            ResultActions result5 = this.executeProfileAttributePost("3_POST_attribute_valid.json", typeCode, accessToken, status().isOk());
            result5.andExpect(jsonPath("$.payload.code", is("added_mt")));
            result5.andExpect(jsonPath("$.payload.roles", Matchers.hasSize(1)));
            result5.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result5.andExpect(jsonPath("$.metaData.size()", is(1)));
            result5.andExpect(jsonPath("$.metaData.profileTypeCode", is(typeCode)));

            IApsEntity profileType = this.userProfileManager.getEntityPrototype(typeCode);
            Assertions.assertEquals(4, profileType.getAttributeList().size());
        } finally {
            if (null != this.userProfileManager.getEntityPrototype(typeCode)) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype(typeCode);
            }
        }
    }

    @Test
    void testUpdateUserProfileAttribute() throws Exception {
        String typeCode = "TST";
        try {
            Assertions.assertNull(this.userProfileManager.getEntityPrototype(typeCode));
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            this.executeProfileTypePost("2_POST_valid.json", accessToken, status().isOk());
            IApsEntity profileType = this.userProfileManager.getEntityPrototype(typeCode);
            Assertions.assertEquals(3, profileType.getAttributeList().size());

            ResultActions result1 = this.executeProfileAttributePut("4_PUT_attribute_invalid_1.json", typeCode, "list_wrong", accessToken, status().isNotFound());
            result1.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            result1.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            result1.andExpect(jsonPath("$.errors[0].code", is("15")));
            result1.andExpect(jsonPath("$.metaData.size()", is(0)));

            ResultActions result2 = this.executeProfileAttributePut("4_PUT_attribute_invalid_2.json", typeCode, "list", accessToken, status().isBadRequest());
            result2.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            result2.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            result2.andExpect(jsonPath("$.errors[0].code", is("16")));
            result2.andExpect(jsonPath("$.metaData.size()", is(0)));

            ResultActions result3 = this.executeProfileAttributePut("4_PUT_attribute_valid.json", typeCode, "wrongname", accessToken, status().isConflict());
            result3.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            result3.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            result3.andExpect(jsonPath("$.errors[0].code", is("6")));
            result3.andExpect(jsonPath("$.metaData.size()", is(0)));

            ResultActions result4 = this.executeProfileAttributePut("4_PUT_attribute_valid.json", typeCode, "list", accessToken, status().isOk());
            result4.andExpect(jsonPath("$.payload.code", is("list")));
            result4.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result4.andExpect(jsonPath("$.metaData.size()", is(1)));
            result4.andExpect(jsonPath("$.metaData.profileTypeCode", is(typeCode)));

            profileType = this.userProfileManager.getEntityPrototype(typeCode);
            Assertions.assertEquals(3, profileType.getAttributeList().size());
            Assertions.assertNotNull(profileType.getAttribute("list"));
        } finally {
            if (null != this.userProfileManager.getEntityPrototype(typeCode)) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype(typeCode);
            }
        }
    }

    @Test
    void testDeleteUserProfileAttribute() throws Exception {
        String typeCode = "TST";
        try {
            Assertions.assertNull(this.userProfileManager.getEntityPrototype(typeCode));
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            this.executeProfileTypePost("2_POST_valid.json", accessToken, status().isOk());
            IApsEntity profileType = this.userProfileManager.getEntityPrototype(typeCode);
            Assertions.assertEquals(3, profileType.getAttributeList().size());
            Assertions.assertNotNull(profileType.getAttribute("list"));

            ResultActions result1 = this.executeProfileAttributeDelete("wrongCode", "list_wrong", accessToken, status().isNotFound());
            result1.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            result1.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            result1.andExpect(jsonPath("$.errors[0].code", is("1")));
            result1.andExpect(jsonPath("$.metaData.size()", is(0)));

            ResultActions result2 = this.executeProfileAttributeDelete(typeCode, "list_wrong", accessToken, status().isOk());
            result2.andExpect(jsonPath("$.payload.profileTypeCode", is(typeCode)));
            result2.andExpect(jsonPath("$.payload.attributeCode", is("list_wrong")));
            result2.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result2.andExpect(jsonPath("$.metaData.size()", is(0)));

            profileType = this.userProfileManager.getEntityPrototype(typeCode);
            Assertions.assertEquals(3, profileType.getAttributeList().size());

            ResultActions result3 = this.executeProfileAttributeDelete(typeCode, "list", accessToken, status().isOk());
            result3.andExpect(jsonPath("$.payload.profileTypeCode", is(typeCode)));
            result3.andExpect(jsonPath("$.payload.attributeCode", is("list")));
            result3.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result3.andExpect(jsonPath("$.metaData.size()", is(0)));

            profileType = this.userProfileManager.getEntityPrototype(typeCode);
            Assertions.assertEquals(2, profileType.getAttributeList().size());
            Assertions.assertNull(profileType.getAttribute("list"));
        } finally {
            if (null != this.userProfileManager.getEntityPrototype(typeCode)) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype(typeCode);
            }
        }
    }
    
    private ResultActions executeProfileAttributePost(String fileName, String typeCode, String accessToken, ResultMatcher expected) throws Exception {
        InputStream isJsonPostValid = this.getClass().getResourceAsStream(fileName);
        String jsonPostValid = FileTextReader.getText(isJsonPostValid);
        ResultActions result = mockMvc
                .perform(post("/profileTypes/{profileTypeCode}/attribute", new Object[]{typeCode})
                        .content(jsonPostValid)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }
    
    private ResultActions executeProfileAttributePut(String fileName, String typeCode, String attributeCode, String accessToken, ResultMatcher expected) throws Exception {
        InputStream isJsonPutValid = this.getClass().getResourceAsStream(fileName);
        String jsonPutValid = FileTextReader.getText(isJsonPutValid);
        ResultActions result = mockMvc
                .perform(put("/profileTypes/{profileTypeCode}/attribute/{attributeCode}", new Object[]{typeCode, attributeCode})
                        .content(jsonPutValid)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }
    
    private ResultActions executeProfileAttributeDelete(String typeCode,
            String attributeCode, String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(delete("/profileTypes/{profileTypeCode}/attribute/{attributeCode}", new Object[]{typeCode, attributeCode})
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }
    
    @Test
    void testGetUserProfileTypesStatus() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/profileTypesStatus")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload.size()", is(3)));
        result.andExpect(jsonPath("$.payload.ready", Matchers.hasSize(3)));
        result.andExpect(jsonPath("$.payload.toRefresh", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.payload.refreshing", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.metaData.size()", is(0)));
    }

    @Test
    void testRefreshUserProfileType_1() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(post("/profileTypesStatus")
                        .content("{\"profileTypeCodes\":[\"AAA\",\"BBB\"]}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));

        result = mockMvc
                .perform(post("/profileTypesStatus")
                        .content("{\"profileTypeCodes\":[\"PFL\"]}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload.size()", is(2)));
        result.andExpect(jsonPath("$.payload.result", is("success")));
        result.andExpect(jsonPath("$.payload.profileTypeCodes.size()", is(1)));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.metaData.size()", is(0)));
    }

    @Test
    void testRefreshUserProfileType_2() throws Exception {
        String typeCode = "TST";
        try {
            Assertions.assertNull(this.userProfileManager.getEntityPrototype(typeCode));
            Assertions.assertNull(this.userProfileManager.getEntityPrototype("XXX"));
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            this.executeProfileTypePost("2_POST_valid.json", accessToken, status().isOk());
            Assertions.assertNotNull(this.userProfileManager.getEntityPrototype(typeCode));

            ResultActions result1 = mockMvc
                    .perform(post("/profileTypes/refresh/{profileTypeCode}", new Object[]{typeCode})
                            .content("{}")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken));
            result1.andExpect(status().isOk());
            result1.andExpect(jsonPath("$.payload.profileTypeCode", is(typeCode)));
            result1.andExpect(jsonPath("$.payload.status", is("success")));
            result1.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result1.andExpect(jsonPath("$.metaData.size()", is(0)));

            ResultActions result2 = mockMvc
                    .perform(post("/profileTypes/refresh/{profileTypeCode}", new Object[]{"XXX"})
                            .content("{}")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken));
            result2.andExpect(status().isNotFound());
            result2.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            result2.andExpect(jsonPath("$.metaData.size()", is(0)));
            String x = result2.andReturn().getResponse().getContentAsString();
        } finally {
            if (null != this.userProfileManager.getEntityPrototype(typeCode)) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype(typeCode);
            }
        }
    }
    
    @Test
    void testRefreshUserProfileType_3() throws Exception {
        String typeCode = "TST";
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);
            
            this.checkStatus(3, 0, accessToken);
            Assertions.assertNull(this.userProfileManager.getEntityPrototype(typeCode));
            
            this.executeProfileTypePost("2_POST_valid.json", accessToken, status().isOk());
            Assertions.assertNotNull(this.userProfileManager.getEntityPrototype(typeCode));
            this.checkStatus(4, 0, accessToken);
            
            ResultActions result = this.executeProfileAttributePut("10_PUT_attribute_valid_1.json", typeCode, "DataAttribute", accessToken, status().isOk());
            this.checkStatus(4, 0, accessToken);
            
            result = this.executeProfileAttributePut("10_PUT_attribute_valid_2.json", typeCode, "DataAttribute", accessToken, status().isOk());
            this.checkStatus(3, 1, accessToken);
            
            ProfileTypeRefreshRequest request = new ProfileTypeRefreshRequest();
            request.getProfileTypeCodes().add(typeCode);
            ResultActions resultRefresh = mockMvc
                .perform(post("/profileTypesStatus")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
            resultRefresh.andExpect(status().isOk());
            synchronized (this) {
                this.wait(1000);
            }
            this.checkStatus(4, 0, accessToken);
            
            result = this.executeProfileAttributePut("10_PUT_attribute_valid_1.json", typeCode, "DataAttribute", accessToken, status().isOk());
            this.checkStatus(3, 1, accessToken);
            
            resultRefresh = mockMvc
                .perform(post("/profileTypes/refresh/{profileTypeCode}", new Object[]{typeCode})
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
            resultRefresh.andExpect(status().isOk());
            synchronized (this) {
                this.wait(1000);
            }
            this.checkStatus(4, 0, accessToken);
        } finally {
            if (null != this.userProfileManager.getEntityPrototype(typeCode)) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype(typeCode);
            }
        }
    }
    
    private void checkStatus(int expectedReady, int expectedToRefresh, String accessToken) throws Exception {
        ResultActions result = mockMvc
                .perform(get("/profileTypesStatus")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload.size()", is(3)));
        result.andExpect(jsonPath("$.payload.ready", Matchers.hasSize(expectedReady)));
        result.andExpect(jsonPath("$.payload.toRefresh", Matchers.hasSize(expectedToRefresh)));
        result.andExpect(jsonPath("$.payload.refreshing", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.metaData.size()", is(0)));
    }

    private ResultActions executeGetMyProfileType(UserDetails user, String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(get("/myProfileType")
                        .flashAttr("user", user)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andDo(print()).andExpect(expected);
        return result;
    }

    @Test
    void testGetUserProfileTypesWithAdminPermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/profileTypes")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }

    @Test
    void testGetUserProfileTypesWithoutPermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("normal_user", "0x24").build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/profileTypes")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isForbidden());
    }

    @Test
    void testGetUserProfileTypesWithEditUserProfilePermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("normal_user", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "admin", Permission.MANAGE_USER_PROFILES).build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/profileTypes")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }

    @Test
    void testGetUserProfileTypesWithViewUsersProfilePermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("normal_user", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "admin", Permission.VIEW_USERS).build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/profileTypes")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }

    @Test
    void testGetUserProfileTypesWithEditUsersProfilePermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("normal_user", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "admin", Permission.MANAGE_USERS).build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/profileTypes")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }

    @Test
    void testAddUpdateDeleteUserProfileAttributeWithRegex() throws Exception {
        String typeCode = "REX";
        try {
            Assertions.assertNull(this.userProfileManager.getEntityPrototype(typeCode));
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            InputStream isJsonPostValid = this.getClass().getResourceAsStream("8_POST_regex.json");
            String jsonPostValid = FileTextReader.getText(isJsonPostValid);
            ResultActions result = mockMvc
                    .perform(post("/profileTypes")
                            .content(jsonPostValid)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken));
            result.andExpect(status().isOk())
                    .andDo(resultPrint())
                    .andExpect(jsonPath("$.payload.attributes.size()", Matchers.is(4)))
                    .andExpect(jsonPath("$.payload.attributes[0].code", Matchers.is("text")))
                    .andExpect(jsonPath("$.payload.attributes[0].validationRules.minLength", Matchers.is(10)))
                    .andExpect(jsonPath("$.payload.attributes[0].validationRules.maxLength", Matchers.is(20)))
                    .andExpect(jsonPath("$.payload.attributes[0].validationRules.regex", Matchers.is("text regex")))
                    .andExpect(jsonPath("$.payload.attributes[1].code", Matchers.is("longtext")))
                    .andExpect(jsonPath("$.payload.attributes[1].validationRules.minLength", Matchers.is(10)))
                    .andExpect(jsonPath("$.payload.attributes[1].validationRules.maxLength", Matchers.is(20)))
                    .andExpect(jsonPath("$.payload.attributes[1].validationRules.regex", Matchers.is("longtext regex")))
                    .andExpect(jsonPath("$.payload.attributes[2].code", Matchers.is("monotext")))
                    .andExpect(jsonPath("$.payload.attributes[2].validationRules.minLength", Matchers.is(10)))
                    .andExpect(jsonPath("$.payload.attributes[2].validationRules.maxLength", Matchers.is(20)))
                    .andExpect(jsonPath("$.payload.attributes[2].validationRules.regex", Matchers.is("monotext regex")))
                    .andExpect(jsonPath("$.payload.attributes[3].code", Matchers.is("hypertext")))
                    .andExpect(jsonPath("$.payload.attributes[3].validationRules.minLength", Matchers.is(10)))
                    .andExpect(jsonPath("$.payload.attributes[3].validationRules.maxLength", Matchers.is(20)))
                    .andExpect(jsonPath("$.payload.attributes[3].validationRules.regex", Matchers.is("hypertext regex")));

            isJsonPostValid = this.getClass().getResourceAsStream("8_PUT_regex.json");
            jsonPostValid = FileTextReader.getText(isJsonPostValid);
            result = mockMvc
                    .perform(put("/profileTypes/{profileTypeCode}", new Object[]{typeCode})
                            .content(jsonPostValid)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken));
            result.andExpect(status().isOk())
                    .andDo(resultPrint())
                    .andExpect(jsonPath("$.payload.attributes.size()", Matchers.is(4)))
                    .andExpect(jsonPath("$.payload.attributes[0].code", Matchers.is("text")))
                    .andExpect(jsonPath("$.payload.attributes[0].validationRules.minLength", Matchers.is(11)))
                    .andExpect(jsonPath("$.payload.attributes[0].validationRules.maxLength", Matchers.is(21)))
                    .andExpect(jsonPath("$.payload.attributes[0].validationRules.regex", Matchers.is("text regex 2")))
                    .andExpect(jsonPath("$.payload.attributes[1].code", Matchers.is("longtext")))
                    .andExpect(jsonPath("$.payload.attributes[1].validationRules.minLength", Matchers.is(11)))
                    .andExpect(jsonPath("$.payload.attributes[1].validationRules.maxLength", Matchers.is(21)))
                    .andExpect(jsonPath("$.payload.attributes[1].validationRules.regex", Matchers.is("longtext regex 2")))
                    .andExpect(jsonPath("$.payload.attributes[2].code", Matchers.is("monotext")))
                    .andExpect(jsonPath("$.payload.attributes[2].validationRules.minLength", Matchers.is(11)))
                    .andExpect(jsonPath("$.payload.attributes[2].validationRules.maxLength", Matchers.is(21)))
                    .andExpect(jsonPath("$.payload.attributes[2].validationRules.regex", Matchers.is("monotext regex 2")))
                    .andExpect(jsonPath("$.payload.attributes[3].code", Matchers.is("hypertext")))
                    .andExpect(jsonPath("$.payload.attributes[3].validationRules.minLength", Matchers.is(11)))
                    .andExpect(jsonPath("$.payload.attributes[3].validationRules.maxLength", Matchers.is(21)))
                    .andExpect(jsonPath("$.payload.attributes[3].validationRules.regex", Matchers.is("hypertext regex 2")));

        } finally {
            if (null != this.userProfileManager.getEntityPrototype(typeCode)) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype(typeCode);
            }
        }
    }
    
}

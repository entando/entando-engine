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

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.IEntityTypesConfigurer;
import com.agiletec.aps.system.common.entity.model.attribute.ListAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.MonoTextAttribute;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.DateConverter;
import com.agiletec.aps.util.FileTextReader;
import org.entando.entando.aps.system.common.entity.model.attribute.EmailAttribute;
import org.entando.entando.aps.system.services.userprofile.IUserProfileManager;
import org.entando.entando.aps.system.services.userprofile.IUserProfileService;
import org.entando.entando.aps.system.services.userprofile.model.IUserProfile;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserProfileControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private IUserProfileService userProfileService;

    @Autowired
    private IUserProfileManager userProfileManager;

    @Autowired
    private IUserManager userManager;

    @Autowired
    private ProfileController controller;
    
    @Test
    void testGetUserProfileType() throws Exception {
        String accessToken = this.createAccessToken();
        ResultActions result = mockMvc
                .perform(get("/userProfiles/{username}", new Object[]{"editorCoach"})
                        .header("Authorization", "Bearer " + accessToken));
        System.out.println(result.andReturn().getResponse().getContentAsString());
        result.andExpect(status().isOk());
        testCors("/userProfiles/editorCoach");
    }

    @Test
    void testGetUserProfileTypePermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "manageUser", Permission.MANAGE_USERS)
                .build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/userProfiles/{username}", new Object[]{"editorCoach"})
                        .header("Authorization", "Bearer " + accessToken));
        result
                .andDo(resultPrint())
                .andExpect(status().isOk());
    }

    @Test
    void testGetInvalidUserProfileType() throws Exception {
        String accessToken = this.createAccessToken();
        ResultActions result = mockMvc
                .perform(get("/userProfiles/{username}", new Object[]{"xxxxx"})
                        .header("Authorization", "Bearer " + accessToken));
        System.out.println(result.andReturn().getResponse().getContentAsString());
        result.andExpect(status().isNotFound());
    }

    @Test
    void testGetValidUserProfileType() throws Exception {
        String accessToken = this.createAccessToken();
        ResultActions result = mockMvc
                .perform(get("/userProfiles/{username}", new Object[]{"editorCoach"})
                        .header("Authorization", "Bearer " + accessToken));
        System.out.println(result.andReturn().getResponse().getContentAsString());
        result.andExpect(status().isOk());
    }

    @Test
    void testAddUpdateUserProfile() throws Exception {
        try {
            Assertions.assertNull(this.userProfileManager.getEntityPrototype("TST"));
            String accessToken = this.createAccessToken();

            this.executeProfileTypePost("5_POST_type_valid.json", accessToken, status().isOk());

            Assertions.assertNull(this.userManager.getUser("new_user"));
            User user = new User();
            user.setUsername("new_user");
            user.setPassword("new_user");
            this.userManager.addUser(user);
            Assertions.assertNotNull(this.userProfileManager.getEntityPrototype("TST"));

            Assertions.assertNull(this.userProfileManager.getProfile("new_user"));
            ResultActions result = this.executeProfilePost("5_POST_invalid.json", accessToken, status().isBadRequest());
            result.andExpect(jsonPath("$.payload.size()", is(0)));
            result.andExpect(jsonPath("$.errors.size()", is(3)));
            result.andExpect(jsonPath("$.metaData.size()", is(0)));
            Assertions.assertNull(this.userProfileManager.getProfile("new_user"));

            Assertions.assertNull(this.userProfileManager.getProfile("new_user"));
            ResultActions result2 = this.executeProfilePost("5_POST_valid.json", accessToken, status().isOk());
            result2.andExpect(jsonPath("$.payload.id", is("new_user")));
            result2.andExpect(jsonPath("$.errors.size()", is(0)));
            result2.andExpect(jsonPath("$.metaData.size()", is(0)));
            IUserProfile profile = this.userProfileManager.getProfile("new_user");
            Assertions.assertNotNull(profile);
            Date date = (Date) profile.getAttribute("Date").getValue();
            Assertions.assertEquals("2017-09-21", DateConverter.getFormattedDate(date, "yyyy-MM-dd"));
            Boolean booleanValue = (Boolean) profile.getAttribute("Boolean").getValue();
            Assertions.assertTrue(booleanValue);
            Boolean threeState = (Boolean) profile.getAttribute("ThreeState").getValue();
            Assertions.assertNull(threeState);

            ResultActions result3 = this.executeProfilePut("5_PUT_valid.json", "invalid", accessToken, status().isConflict());
            result3.andExpect(jsonPath("$.payload.size()", is(0)));
            result3.andExpect(jsonPath("$.errors.size()", is(1)));
            result3.andExpect(jsonPath("$.errors[0].code", is("2")));
            result3.andExpect(jsonPath("$.metaData.size()", is(0)));

            mockOAuthInterceptor(new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                    .withAuthorization(Group.FREE_GROUP_NAME, "manageUser", Permission.MANAGE_USERS)
                    .build());

            ResultActions result4 = this.executeProfilePut("5_PUT_valid.json", "new_user", accessToken, status().isOk());
            result4.andExpect(jsonPath("$.payload.id", is("new_user")));
            result4.andExpect(jsonPath("$.errors.size()", is(0)));
            result4.andExpect(jsonPath("$.metaData.size()", is(0)));
            result4.andExpect(jsonPath("$.payload.attributes[0].code", is("Title")));
            result4.andExpect(jsonPath("$.payload.attributes[0].value", is("My title")));
            result4.andExpect(jsonPath("$.payload.attributes[0].values", Matchers.anything()));
            result4.andExpect(jsonPath("$.payload.attributes[0].elements.size()", is(0)));
            result4.andExpect(jsonPath("$.payload.attributes[0].compositeelements.size()", is(0)));
            result4.andExpect(jsonPath("$.payload.attributes[0].listelements", Matchers.anything()));
            profile = this.userProfileManager.getProfile("new_user");
            date = (Date) profile.getAttribute("Date").getValue();
            Assertions.assertEquals("2018-03-21", DateConverter.getFormattedDate(date, "yyyy-MM-dd"));
            booleanValue = (Boolean) profile.getAttribute("Boolean").getValue();
            Assertions.assertFalse(booleanValue);
            threeState = (Boolean) profile.getAttribute("ThreeState").getValue();
            Assertions.assertNotNull(threeState);
            Assertions.assertTrue(threeState);

            ListAttribute list = (ListAttribute) profile.getAttribute("multilist");
            Assertions.assertEquals(4, list.getAttributeList("en").size());
        } finally {
            this.userProfileManager.deleteProfile("new_user");
            this.userManager.removeUser("new_user");
            if (null != this.userProfileManager.getEntityPrototype("TST")) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype("TST");
            }
        }
    }
    
    @Test
    void testAddUserProfileWithEmail() throws Exception {
        try {
            String accessToken = this.createAccessToken();

            ResultActions result1 = this.executeProfilePost("9_POST_valid.json", accessToken, status().isOk());
            result1.andExpect(jsonPath("$.payload.id", is("new_user_2")));
            result1.andExpect(jsonPath("$.errors.size()", is(0)));
            result1.andExpect(jsonPath("$.metaData.size()", is(0)));
            IUserProfile profile = this.userProfileManager.getProfile("new_user_2");
            Assertions.assertNotNull(profile);
            EmailAttribute emailAttribute = (EmailAttribute) profile.getAttribute("email");
            Assertions.assertEquals("eric.brown@entando.com", emailAttribute.getText());
            
            ResultActions result2 = executeProfileGet("new_user_2", accessToken, status().isOk());
            result2.andExpect(jsonPath("$.payload.id", is("new_user_2")));
            result2.andExpect(jsonPath("$.payload.typeCode", is("OTH")));
            result2.andExpect(jsonPath("$.payload.attributes[0].value", is("Eric")));
            result2.andExpect(jsonPath("$.payload.attributes[1].value", is("Brown")));
            result2.andExpect(jsonPath("$.payload.attributes[2].value", is("eric.brown@entando.com")));
        } finally {
            this.userProfileManager.deleteProfile("new_user_2");
            Assertions.assertNull(this.userProfileManager.getProfile("new_user_2"));
        }
    }

    /* For an user created without profile, the profile has to be created the
       first time the "/userProfiles/{user}" endpoint is requested. */
    @Test
    void testGetProfileForNewUser() throws Exception {
        String username = "another_new_user";

        try {
            String accessToken = this.createAccessToken();

            Assertions.assertNull(this.userManager.getUser(username));
            User user = new User();
            user.setUsername(username);
            user.setPassword(username);
            this.userManager.addUser(user);

            ResultActions result = executeProfileGet(username, accessToken, status().isOk());

            result.andExpect(jsonPath("$.payload.id", is(username)));
            result.andExpect(jsonPath("$.payload.typeCode", is(SystemConstants.DEFAULT_PROFILE_TYPE_CODE)));
            // Checking mandatory attributes with empty values
            result.andExpect(jsonPath("$.payload.attributes[?(@.code == 'fullname')].value", is(Arrays.asList(""))));
            result.andExpect(jsonPath("$.payload.attributes[?(@.code == 'email')].value", is(Arrays.asList(""))));
        } finally {
            this.userProfileManager.deleteProfile(username);
            this.userManager.removeUser(username);
        }
    }

    @Test
    void testGetProfileForNewUserAndUpdateIt() throws Exception {
        try {
            Assertions.assertNull(this.userProfileManager.getEntityPrototype("TST"));
            Assertions.assertNull(this.userProfileManager.getEntityPrototype("TSU"));

            String accessToken = this.createAccessToken();

            this.executeProfileTypePost("5_POST_type_valid.json", accessToken, status().isOk());
            this.executeProfileTypePost("6_POST_type_valid.json", accessToken, status().isOk());
            Assertions.assertNotNull(this.userProfileManager.getEntityPrototype("TST"));
            Assertions.assertNotNull(this.userProfileManager.getEntityPrototype("TSU"));

            Assertions.assertNull(this.userManager.getUser("new_user"));
            User user = new User();
            user.setUsername("new_user");
            user.setPassword("new_user");
            this.userManager.addUser(user);

            Assertions.assertNull(this.userProfileManager.getProfile("new_user"));
            this.executeProfilePost("5_POST_valid.json", accessToken, status().isOk())
                    .andExpect(jsonPath("$.payload.id", is("new_user")))
                    .andExpect(jsonPath("$.payload.typeCode", is("TST")))
                    .andExpect(jsonPath("$.payload.typeDescription", is("Type for test")))
                    .andExpect(jsonPath("$.payload.description", is("Profile of user")))
                    .andExpect(jsonPath("$.payload.mainGroup", is("free")))
                    .andExpect(jsonPath("$.errors.size()", is(0)))
                    .andExpect(jsonPath("$.metaData.size()", is(0)))
                    .andExpect(jsonPath("$.payload.attributes.size()", is(10)))
                    .andExpect(jsonPath("$.payload.attributes[0].code", is("Title")))
                    .andExpect(jsonPath("$.payload.attributes[0].value", is("Mio titolo")))
                    .andExpect(jsonPath("$.payload.attributes[1].code", is("Subtitle")))
                    .andExpect(jsonPath("$.payload.attributes[1].values.en", is("multilingual EN")))
                    .andExpect(jsonPath("$.payload.attributes[1].values.it", is("multilingua IT")))
                    .andExpect(jsonPath("$.payload.attributes[2].code", is("Number")))
                    .andExpect(jsonPath("$.payload.attributes[2].value", is("2")))
                    .andExpect(jsonPath("$.payload.attributes[3].code", is("Boolean")))
                    .andExpect(jsonPath("$.payload.attributes[3].value", is(true)))
                    .andExpect(jsonPath("$.payload.attributes[4].code", is("ThreeState")))
                    .andExpect(jsonPath("$.payload.attributes[4].value", nullValue()))
                    .andExpect(jsonPath("$.payload.attributes[5].code", is("Date")))
                    .andExpect(jsonPath("$.payload.attributes[5].value", is("2017-09-21 21:24:18")))
                    .andExpect(jsonPath("$.payload.attributes[6].code", is("monolist")))
                    .andExpect(jsonPath("$.payload.attributes[6].elements.size()", is(2)))
                    .andExpect(jsonPath("$.payload.attributes[6].elements[0].code", is("monolist")))
                    .andExpect(jsonPath("$.payload.attributes[6].elements[0].values.en", is("multilingual EN_1")))
                    .andExpect(jsonPath("$.payload.attributes[6].elements[0].values.it", is("multilingua IT_1")))
                    .andExpect(jsonPath("$.payload.attributes[6].elements[1].code", is("monolist")))
                    .andExpect(jsonPath("$.payload.attributes[6].elements[1].values.en", is("multilingual EN_2")))
                    .andExpect(jsonPath("$.payload.attributes[6].elements[1].values.it", is("multilingua IT_2")))
                    .andExpect(jsonPath("$.payload.attributes[7].code", is("list")))
                    .andExpect(jsonPath("$.payload.attributes[7].value", nullValue()))
                    .andExpect(jsonPath("$.payload.attributes[8].code", is("multilist")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.en.size()", is(3)))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.en[0].code", is("multilist")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.en[0].value", is("value_en_1")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.en[1].code", is("multilist")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.en[1].value", is("value_en_2")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.en[2].code", is("multilist")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.en[2].value", is("value_en_3")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.it[0].code", is("multilist")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.it[0].value", is("value_it_1")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.it[1].code", is("multilist")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.it[1].value", is("value_it_2")))
                    .andExpect(jsonPath("$.payload.attributes[9].code", is("composite")))
                    .andExpect(jsonPath("$.payload.attributes[9].compositeelements.size()", is(2)))
                    .andExpect(jsonPath("$.payload.attributes[9].compositeelements[0].code", is("SubtitleSub")))
                    .andExpect(jsonPath("$.payload.attributes[9].compositeelements[0].values.en", is("multilingual EN")))
                    .andExpect(jsonPath("$.payload.attributes[9].compositeelements[0].values.it", is("multilingua IT")))
                    .andExpect(jsonPath("$.payload.attributes[9].compositeelements[1].code", is("TitleSub")))
                    .andExpect(jsonPath("$.payload.attributes[9].compositeelements[1].value", is("Mio titolo")));

            this.executeProfilePut("6_PUT_valid.json", "new_user", accessToken, status().isOk())
                    .andExpect(jsonPath("$.payload.id", is("new_user")))
                    .andExpect(jsonPath("$.payload.typeCode", is("TST")))
                    .andExpect(jsonPath("$.payload.typeDescription", is("Type for test TST")))
                    .andExpect(jsonPath("$.payload.description", is("Profile of user")))
                    .andExpect(jsonPath("$.payload.mainGroup", is("free")))
                    .andExpect(jsonPath("$.errors.size()", is(0)))
                    .andExpect(jsonPath("$.metaData.size()", is(0)))
                    .andExpect(jsonPath("$.payload.attributes.size()", is(10)))
                    .andExpect(jsonPath("$.payload.attributes[0].code", is("Title")))
                    .andExpect(jsonPath("$.payload.attributes[0].value", is("Mio titolo 2")))
                    .andExpect(jsonPath("$.payload.attributes[1].code", is("Subtitle")))
                    .andExpect(jsonPath("$.payload.attributes[1].values.en", is("multilingual EN 2")))
                    .andExpect(jsonPath("$.payload.attributes[1].values.it", is("multilingua IT 2")))
                    .andExpect(jsonPath("$.payload.attributes[2].code", is("Number")))
                    .andExpect(jsonPath("$.payload.attributes[2].value", is("3")))
                    .andExpect(jsonPath("$.payload.attributes[3].code", is("Boolean")))
                    .andExpect(jsonPath("$.payload.attributes[3].value", is(false)))
                    .andExpect(jsonPath("$.payload.attributes[4].code", is("ThreeState")))
                    .andExpect(jsonPath("$.payload.attributes[4].value", is(true)))
                    .andExpect(jsonPath("$.payload.attributes[5].code", is("Date")))
                    .andExpect(jsonPath("$.payload.attributes[5].value", is("2017-09-21 21:24:19")))
                    .andExpect(jsonPath("$.payload.attributes[6].code", is("monolist")))
                    .andExpect(jsonPath("$.payload.attributes[6].elements.size()", is(2)))
                    .andExpect(jsonPath("$.payload.attributes[6].elements[0].code", is("monolist")))
                    .andExpect(jsonPath("$.payload.attributes[6].elements[0].values.en", is("multilingual EN_12")))
                    .andExpect(jsonPath("$.payload.attributes[6].elements[0].values.it", is("multilingua IT_12")))
                    .andExpect(jsonPath("$.payload.attributes[6].elements[1].code", is("monolist")))
                    .andExpect(jsonPath("$.payload.attributes[6].elements[1].values.en", is("multilingual EN_22")))
                    .andExpect(jsonPath("$.payload.attributes[6].elements[1].values.it", is("multilingua IT_22")))
                    .andExpect(jsonPath("$.payload.attributes[7].code", is("list")))
                    .andExpect(jsonPath("$.payload.attributes[7].value", nullValue()))
                    .andExpect(jsonPath("$.payload.attributes[8].code", is("multilist")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.en.size()", is(3)))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.en[0].code", is("multilist")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.en[0].value", is("value_en_12")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.en[1].code", is("multilist")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.en[1].value", is("value_en_22")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.en[2].code", is("multilist")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.en[2].value", is("value_en_32")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.it[0].code", is("multilist")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.it[0].value", is("value_it_12")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.it[1].code", is("multilist")))
                    .andExpect(jsonPath("$.payload.attributes[8].listelements.it[1].value", is("value_it_22")))
                    .andExpect(jsonPath("$.payload.attributes[9].code", is("composite")))
                    .andExpect(jsonPath("$.payload.attributes[9].compositeelements.size()", is(2)))
                    .andExpect(jsonPath("$.payload.attributes[9].compositeelements[0].code", is("SubtitleSub")))
                    .andExpect(jsonPath("$.payload.attributes[9].compositeelements[0].values.en", is("multilingual EN 2")))
                    .andExpect(jsonPath("$.payload.attributes[9].compositeelements[0].values.it", is("multilingua IT 2")))
                    .andExpect(jsonPath("$.payload.attributes[9].compositeelements[1].code", is("TitleSub")))
                    .andExpect(jsonPath("$.payload.attributes[9].compositeelements[1].value", is("Mio titolo 2")));

            this.executeProfilePut("7_PUT_valid.json", "new_user", accessToken, status().isOk())
                    .andExpect(jsonPath("$.payload.id", is("new_user")))
                    .andExpect(jsonPath("$.payload.typeCode", is("TSU")))
                    .andExpect(jsonPath("$.payload.typeDescription", is("Type for test TSU")))
                    .andExpect(jsonPath("$.payload.description", is("Profile of user")))
                    .andExpect(jsonPath("$.payload.mainGroup", is("free")))
                    .andExpect(jsonPath("$.errors.size()", is(0)))
                    .andExpect(jsonPath("$.metaData.size()", is(0)))
                    .andExpect(jsonPath("$.payload.attributes.size()", is(3)))
                    .andExpect(jsonPath("$.payload.attributes[0].code", is("Title")))
                    .andExpect(jsonPath("$.payload.attributes[0].value", is("Mio titolo tsu")))
                    .andExpect(jsonPath("$.payload.attributes[1].code", is("Number")))
                    .andExpect(jsonPath("$.payload.attributes[1].value", is("7")))
                    .andExpect(jsonPath("$.payload.attributes[2].code", is("Boolean")))
                    .andExpect(jsonPath("$.payload.attributes[2].value", is(true)));

        } finally {
            this.userProfileManager.deleteProfile("new_user");
            this.userManager.removeUser("new_user");
            if (null != this.userProfileManager.getEntityPrototype("TST")) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype("TST");
            }
            if (null != this.userProfileManager.getEntityPrototype("TSU")) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype("TSU");
            }
        }
    }

    @Test
    void testPostMyProfileConflict() throws Exception {
        try {
            Assertions.assertNull(this.userProfileManager.getEntityPrototype("TST"));
            String accessToken = this.createAccessToken();

            this.executeProfileTypePost("11_POST_type_valid.json", accessToken, status().isOk());

            Assertions.assertNotNull(this.userProfileManager.getEntityPrototype("TST"));

            UserDetails userEditMyProfile = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                    .withAuthorization(Group.FREE_GROUP_NAME, "editor", Permission.ENTER_BACKEND)
                    .build();

            String userEditMyProfileToken =  mockOAuthInterceptor(userEditMyProfile);

            this.executePutUpdateMyProfile("11_PUT_invalid.json", userEditMyProfile, userEditMyProfileToken, status().isConflict())
            .andExpect(jsonPath("$.payload.size()", is(0)))
            .andExpect(jsonPath("$.errors.size()", is(1)));
        } finally {
            if (null != this.userProfileManager.getEntityPrototype("TST")) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype("TST");
            }
        }
    }

    @Test
    void testGetMyProfileOk() throws Exception {
        try {
            Assertions.assertNull(this.userProfileManager.getEntityPrototype("TST"));
            String accessTokenAdmin = createAccessToken();
            this.executeProfileTypePost("11_POST_type_valid.json", accessTokenAdmin, status().isOk());
            UserDetails loggedUser = new OAuth2TestUtils.UserBuilder("new_user", "0x24")
                    .withAuthorization(Group.FREE_GROUP_NAME, "editor", Permission.ENTER_BACKEND)
                    .build();
            String accessToken = mockOAuthInterceptor(loggedUser);
            Assertions.assertNotNull(this.userProfileManager.getEntityPrototype("TST"));
            Assertions.assertNull(this.userManager.getUser("new_user"));
            User user = new User();
            user.setUsername("new_user");
            user.setPassword("new_user");
            this.userManager.addUser(user);
            executeGetMyProfile(loggedUser, accessToken, status().isOk())
                    .andExpect(jsonPath("$.payload.id", is("new_user")))
                    .andExpect(jsonPath("$.metaData.size()", is(0)));

            Assertions.assertNotNull(this.userProfileManager.getProfile("new_user"));
        } finally {
            this.userProfileManager.deleteProfile("new_user");
            this.userManager.removeUser("new_user");
            if (null != this.userProfileManager.getEntityPrototype("TST")) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype("TST");
            }
        }
    }

    @Test
    void testGetMyProfileUserNotFound() throws Exception {
        try {
            Assertions.assertNull(this.userProfileManager.getEntityPrototype("TST"));
            String accessTokenAdmin = createAccessToken();
            this.executeProfileTypePost("11_POST_type_valid.json", accessTokenAdmin, status().isOk());
            UserDetails loggedUser = new OAuth2TestUtils.UserBuilder("new_user", "0x24")
                    .withAuthorization(Group.FREE_GROUP_NAME, "editor", Permission.ENTER_BACKEND)
                    .build();
            String accessToken = mockOAuthInterceptor(loggedUser);
            Assertions.assertNotNull(this.userProfileManager.getEntityPrototype("TST"));
            executeGetMyProfile(loggedUser, accessToken, status().isNotFound());
            Assertions.assertNull(this.userProfileManager.getProfile("new_user"));
        } finally {
            if (null != this.userProfileManager.getEntityPrototype("TST")) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype("TST");
            }
        }
    }

    @Test
    void testPostMyProfileOk() throws Exception {
        try {
            Assertions.assertNull(this.userProfileManager.getEntityPrototype("TST"));
            String accessToken = this.createAccessToken();

            this.executeProfileTypePost("11_POST_type_valid.json", accessToken, status().isOk());

            Assertions.assertNotNull(this.userProfileManager.getEntityPrototype("TST"));
            this.executeProfilePost("11_POST_valid.json", accessToken, status().isOk())
                .andExpect(jsonPath("$.payload.id", is("new_user")))
                .andExpect(jsonPath("$.metaData.size()", is(0)));

            Assertions.assertNotNull(this.userProfileManager.getProfile("new_user"));

            UserDetails userEditMyProfile = new OAuth2TestUtils.UserBuilder("new_user", "0x24")
                    .withAuthorization(Group.FREE_GROUP_NAME, "editor", Permission.ENTER_BACKEND)
                    .build();
            String userEditMyProfileToken =  mockOAuthInterceptor(userEditMyProfile);

            this.executePutUpdateMyProfile("11_PUT_valid.json", userEditMyProfile, userEditMyProfileToken, status().isOk())
                    .andExpect(jsonPath("$.payload.id", is("new_user")))
                    .andExpect(jsonPath("$.payload.typeCode", is("TST")))
                    .andExpect(jsonPath("$.payload.typeDescription", is("Type for test TST")));

        } finally {
            this.userManager.removeUser("new_user");
            if (null != this.userProfileManager.getEntityPrototype("TST")) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype("TST");
            }
        }
    }

    @Test
    void testAddUserProfileWithProfilePicture() throws Exception {
        try {
            String accessToken = this.createAccessToken();

            this.executeProfilePost("12_POST_valid.json", accessToken, status().isOk()).andDo(resultPrint())
                    .andExpect(jsonPath("$.payload.id", is("new_user_2")))
                    .andExpect(jsonPath("$.errors.size()", is(0)))
                    .andExpect(jsonPath("$.metaData.size()", is(0)));

            IUserProfile profile = this.userProfileManager.getProfile("new_user_2");
            Assertions.assertNotNull(profile);
            MonoTextAttribute profilePicture = (MonoTextAttribute) profile.getAttribute("profilepicture");
            Assertions.assertEquals("picture.png", profilePicture.getText());

            executeProfileGet("new_user_2", accessToken, status().isOk())
                    .andExpect(jsonPath("$.payload.id", is("new_user_2")))
                    .andExpect(jsonPath("$.payload.typeCode", is("OTH")))
                    .andExpect(jsonPath("$.payload.attributes[0].value", is("Eric")))
                    .andExpect(jsonPath("$.payload.attributes[1].value", is("Brown")))
                    .andExpect(jsonPath("$.payload.attributes[2].value", is("eric.brown@entando.com")))
                    .andExpect(jsonPath("$.payload.attributes[3].value", is("picture.png")));

            executeProfilePut("12_PUT_valid.json", "new_user_2", accessToken, status().isOk())
                    .andExpect(jsonPath("$.payload.id", is("new_user_2")))
                    .andExpect(jsonPath("$.payload.typeCode", is("OTH")))
                    .andExpect(jsonPath("$.payload.attributes[0].value", is("Eric")))
                    .andExpect(jsonPath("$.payload.attributes[1].value", is("Brown")))
                    .andExpect(jsonPath("$.payload.attributes[2].value", is("eric.brown@entando.com")))
                    .andExpect(jsonPath("$.payload.attributes[3].value", is("picture2.png")));
        } finally {
            this.userProfileManager.deleteProfile("new_user_2");
            Assertions.assertNull(this.userProfileManager.getProfile("new_user_2"));
        }
    }

    private String createAccessToken() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "manageUserProfile", Permission.MANAGE_USER_PROFILES)
                .build();
        return mockOAuthInterceptor(user);
    }

    private ResultActions executeProfileGet(String username, String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(get("/userProfiles/{username}", new Object[]{username})
                        .header("Authorization", "Bearer " + accessToken));
        result.andDo(resultPrint()).andExpect(expected);
        return result;
    }

    private ResultActions executeProfilePost(String fileName, String accessToken, ResultMatcher expected) throws Exception {
        InputStream isJsonPostValid = this.getClass().getResourceAsStream(fileName);
        String jsonPostValid = FileTextReader.getText(isJsonPostValid);
        ResultActions result = mockMvc
                .perform(post("/userProfiles")
                        .content(jsonPostValid)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andDo(resultPrint()).andExpect(expected);
        return result;
    }

    private ResultActions executeProfilePut(String fileName, String username, String accessToken, ResultMatcher expected) throws Exception {
        InputStream isJsonPostValid = this.getClass().getResourceAsStream(fileName);
        String jsonPostValid = FileTextReader.getText(isJsonPostValid);
        ResultActions result = mockMvc
                .perform(put("/userProfiles/{username}", new Object[]{username})
                        .content(jsonPostValid)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andDo(resultPrint()).andExpect(expected);
        return result;
    }

    private ResultActions executeGetMyProfile(UserDetails user, String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(get("/myUserProfile")
                        .flashAttr("user", user)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andDo(print()).andExpect(expected);
        return result;
    }

    private ResultActions executePutUpdateMyProfile(String fileName, UserDetails user, String accessToken, ResultMatcher expected) throws Exception {
        InputStream isJsonPostValid = this.getClass().getResourceAsStream(fileName);
        String jsonPostValid = FileTextReader.getText(isJsonPostValid);
        ResultActions result = mockMvc
                .perform(put("/myUserProfile")
                        .flashAttr("user", user)
                        .content(jsonPostValid)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andDo(print()).andExpect(expected);
        return result;
    }

    private ResultActions executeProfileTypePost(String fileName, String accessToken, ResultMatcher expected) throws Exception {
        InputStream isJsonPostValid = this.getClass().getResourceAsStream(fileName);
        String jsonPostValid = FileTextReader.getText(isJsonPostValid);
        ResultActions result = mockMvc
                .perform(post("/profileTypes")
                        .content(jsonPostValid)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andDo(resultPrint()).andExpect(expected);
        return result;
    }

}

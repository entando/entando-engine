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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.IEntityTypesConfigurer;
import com.agiletec.aps.system.common.entity.model.attribute.ListAttribute;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.DateConverter;
import com.agiletec.aps.util.FileTextReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.common.entity.model.attribute.EmailAttribute;
import org.entando.entando.aps.system.services.userprofile.IUserProfileManager;
import org.entando.entando.aps.system.services.userprofile.IUserProfileService;
import org.entando.entando.aps.system.services.userprofile.model.IUserProfile;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class UserProfileControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private IUserProfileService userProfileService;

    @Autowired
    private IUserProfileManager userProfileManager;

    @Autowired
    private IUserManager userManager;

    @Autowired
    private ProfileController controller;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    @Test
    public void testGetUserProfileType() throws Exception {
        String accessToken = this.createAccessToken();
        ResultActions result = mockMvc
                .perform(get("/userProfiles/{username}", new Object[]{"editorCoach"})
                        .header("Authorization", "Bearer " + accessToken));
        System.out.println(result.andReturn().getResponse().getContentAsString());
        result.andExpect(status().isOk());
        testCors("/userProfiles/editorCoach");
    }

    @Test
    public void testGetInvalidUserProfileType() throws Exception {
        String accessToken = this.createAccessToken();
        ResultActions result = mockMvc
                .perform(get("/userProfiles/{username}", new Object[]{"xxxxx"})
                        .header("Authorization", "Bearer " + accessToken));
        System.out.println(result.andReturn().getResponse().getContentAsString());
        result.andExpect(status().isNotFound());
    }

    @Test
    public void testGetValidUserProfileType() throws Exception {
        String accessToken = this.createAccessToken();
        ResultActions result = mockMvc
                .perform(get("/userProfiles/{username}", new Object[]{"editorCoach"})
                        .header("Authorization", "Bearer " + accessToken));
        System.out.println(result.andReturn().getResponse().getContentAsString());
        result.andExpect(status().isOk());
    }

    @Test
    public void testAddUpdateUserProfile() throws Exception {
        try {
            Assert.assertNull(this.userProfileManager.getEntityPrototype("TST"));
            String accessToken = this.createAccessToken();

            this.executeProfileTypePost("5_POST_type_valid.json", accessToken, status().isOk());

            Assert.assertNull(this.userManager.getUser("new_user"));
            User user = new User();
            user.setUsername("new_user");
            user.setPassword("new_user");
            this.userManager.addUser(user);
            Assert.assertNotNull(this.userProfileManager.getEntityPrototype("TST"));

            Assert.assertNull(this.userProfileManager.getProfile("new_user"));
            ResultActions result = this.executeProfilePost("5_POST_invalid.json", accessToken, status().isBadRequest());
            result.andExpect(jsonPath("$.payload.size()", is(0)));
            result.andExpect(jsonPath("$.errors.size()", is(3)));
            result.andExpect(jsonPath("$.metaData.size()", is(0)));
            Assert.assertNull(this.userProfileManager.getProfile("new_user"));

            Assert.assertNull(this.userProfileManager.getProfile("new_user"));
            ResultActions result2 = this.executeProfilePost("5_POST_valid.json", accessToken, status().isOk());
            result2.andExpect(jsonPath("$.payload.id", is("new_user")));
            result2.andExpect(jsonPath("$.errors.size()", is(0)));
            result2.andExpect(jsonPath("$.metaData.size()", is(0)));
            IUserProfile profile = this.userProfileManager.getProfile("new_user");
            Assert.assertNotNull(profile);
            Date date = (Date) profile.getAttribute("Date").getValue();
            Assert.assertEquals("2017-09-21", DateConverter.getFormattedDate(date, "yyyy-MM-dd"));
            Boolean booleanValue = (Boolean) profile.getAttribute("Boolean").getValue();
            Assert.assertTrue(booleanValue);
            Boolean threeState = (Boolean) profile.getAttribute("ThreeState").getValue();
            Assert.assertNull(threeState);

            ResultActions result3 = this.executeProfilePut("5_PUT_valid.json", "invalid", accessToken, status().isConflict());
            result3.andExpect(jsonPath("$.payload.size()", is(0)));
            result3.andExpect(jsonPath("$.errors.size()", is(1)));
            result3.andExpect(jsonPath("$.errors[0].code", is("2")));
            result3.andExpect(jsonPath("$.metaData.size()", is(0)));

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
            Assert.assertEquals("2018-03-21", DateConverter.getFormattedDate(date, "yyyy-MM-dd"));
            booleanValue = (Boolean) profile.getAttribute("Boolean").getValue();
            Assert.assertFalse(booleanValue);
            threeState = (Boolean) profile.getAttribute("ThreeState").getValue();
            Assert.assertNotNull(threeState);
            Assert.assertTrue(threeState);

            ListAttribute list = (ListAttribute) profile.getAttribute("multilist");
            Assert.assertEquals(4, list.getAttributeList("en").size());
        } finally {
            this.userProfileManager.deleteProfile("new_user");
            this.userManager.removeUser("new_user");
            if (null != this.userProfileManager.getEntityPrototype("TST")) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype("TST");
            }
        }
    }
    
    @Test
    public void testAddUserProfileWithEmail() throws Exception {
        try {
            String accessToken = this.createAccessToken();

            ResultActions result1 = this.executeProfilePost("9_POST_valid.json", accessToken, status().isOk());
            result1.andExpect(jsonPath("$.payload.id", is("new_user_2")));
            result1.andExpect(jsonPath("$.errors.size()", is(0)));
            result1.andExpect(jsonPath("$.metaData.size()", is(0)));
            IUserProfile profile = this.userProfileManager.getProfile("new_user_2");
            Assert.assertNotNull(profile);
            EmailAttribute emailAttribute = (EmailAttribute) profile.getAttribute("email");
            Assert.assertEquals("eric.brown@entando.com", emailAttribute.getText());
            
            ResultActions result2 = executeProfileGet("new_user_2", accessToken, status().isOk());
            result2.andExpect(jsonPath("$.payload.id", is("new_user_2")));
            result2.andExpect(jsonPath("$.payload.typeCode", is("OTH")));
            result2.andExpect(jsonPath("$.payload.attributes[0].value", is("Eric")));
            result2.andExpect(jsonPath("$.payload.attributes[1].value", is("Brown")));
            result2.andExpect(jsonPath("$.payload.attributes[2].value", is("eric.brown@entando.com")));
        } finally {
            this.userProfileManager.deleteProfile("new_user_2");
            Assert.assertNull(this.userProfileManager.getProfile("new_user_2"));
        }
    }

    /* For an user created without profile, the profile has to be created the
       first time the "/userProfiles/{user}" endpoint is requested. */
    @Test
    public void testGetProfileForNewUser() throws Exception {
        String username = "another_new_user";

        try {
            String accessToken = this.createAccessToken();

            Assert.assertNull(this.userManager.getUser(username));
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
    public void testGetProfileForNewUserAndUpdateIt() throws Exception {
        try {
            Assert.assertNull(this.userProfileManager.getEntityPrototype("TST"));
            Assert.assertNull(this.userProfileManager.getEntityPrototype("TSU"));

            String accessToken = this.createAccessToken();

            this.executeProfileTypePost("5_POST_type_valid.json", accessToken, status().isOk());
            this.executeProfileTypePost("6_POST_type_valid.json", accessToken, status().isOk());
            Assert.assertNotNull(this.userProfileManager.getEntityPrototype("TST"));
            Assert.assertNotNull(this.userProfileManager.getEntityPrototype("TSU"));

            Assert.assertNull(this.userManager.getUser("new_user"));
            User user = new User();
            user.setUsername("new_user");
            user.setPassword("new_user");
            this.userManager.addUser(user);

            Assert.assertNull(this.userProfileManager.getProfile("new_user"));
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
    public void testCreateEditDeleteProfilePicture() throws Exception {
        String accessToken = createAccessToken();
        String username = "admin";

        try {
            performCreateProfilePicture(username, accessToken, "application/jpeg")
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.username", is("admin")))
                    .andExpect(jsonPath("$.payload.versions.size()", is(4)))
                    .andExpect(jsonPath("$.payload.versions[0].dimensions", isEmptyOrNullString()))
                    .andExpect(jsonPath("$.payload.versions[0].size", is("2 Kb")))
                    .andExpect(jsonPath("$.payload.versions[0].path",
                            startsWith("/entando-de-app/engine/admin/profile/image_d0.jpg")))
                    .andExpect(jsonPath("$.payload.versions[1].dimensions", is("90x90 px")))
                    .andExpect(jsonPath("$.payload.versions[1].size", is("2 Kb")))
                    .andExpect(jsonPath("$.payload.versions[1].path",
                            startsWith("/entando-de-app/engine/admin/profile/image_d1.jpg")))
                    .andExpect(jsonPath("$.payload.versions[2].dimensions", is("130x130 px")))
                    .andExpect(jsonPath("$.payload.versions[2].size", is("2 Kb")))
                    .andExpect(jsonPath("$.payload.versions[2].path",
                            startsWith("/entando-de-app/engine/admin/profile/image_d2.jpg")))
                    .andExpect(jsonPath("$.payload.versions[3].dimensions", is("150x150 px")))
                    .andExpect(jsonPath("$.payload.versions[3].size", is("2 Kb")))
                    .andExpect(jsonPath("$.payload.versions[3].path",
                            startsWith("/entando-de-app/engine/admin/profile/image_d3.jpg")));

            performGetProfilePicture(username, accessToken)
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.username", is("admin")))
                    .andExpect(jsonPath("$.payload.versions.size()", is(4)))
                    .andExpect(jsonPath("$.payload.versions[0].dimensions", isEmptyOrNullString()))
                    .andExpect(jsonPath("$.payload.versions[0].size", is("2 Kb")))
                    .andExpect(jsonPath("$.payload.versions[0].path",
                            startsWith("/entando-de-app/engine/admin/profile/image_d0.jpg")))
                    .andExpect(jsonPath("$.payload.versions[1].dimensions", is("90x90 px")))
                    .andExpect(jsonPath("$.payload.versions[1].size", is("2 Kb")))
                    .andExpect(jsonPath("$.payload.versions[1].path",
                            startsWith("/entando-de-app/engine/admin/profile/image_d1.jpg")))
                    .andExpect(jsonPath("$.payload.versions[2].dimensions", is("130x130 px")))
                    .andExpect(jsonPath("$.payload.versions[2].size", is("2 Kb")))
                    .andExpect(jsonPath("$.payload.versions[2].path",
                            startsWith("/entando-de-app/engine/admin/profile/image_d2.jpg")))
                    .andExpect(jsonPath("$.payload.versions[3].dimensions", is("150x150 px")))
                    .andExpect(jsonPath("$.payload.versions[3].size", is("2 Kb")))
                    .andExpect(jsonPath("$.payload.versions[3].path",
                            startsWith("/entando-de-app/engine/admin/profile/image_d3.jpg")));

            performEditProfilePicture(username, accessToken)
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.username", is("admin")))
                    .andExpect(jsonPath("$.payload.versions.size()", is(4)))
                    .andExpect(jsonPath("$.payload.versions[0].dimensions", isEmptyOrNullString()))
                    .andExpect(jsonPath("$.payload.versions[0].size", is("2 Kb")))
                    .andExpect(jsonPath("$.payload.versions[0].path",
                            startsWith("/entando-de-app/engine/admin/profile/image_d0.jpg")))
                    .andExpect(jsonPath("$.payload.versions[1].dimensions", is("90x90 px")))
                    .andExpect(jsonPath("$.payload.versions[1].size", is("2 Kb")))
                    .andExpect(jsonPath("$.payload.versions[1].path",
                            startsWith("/entando-de-app/engine/admin/profile/image_d1.jpg")))
                    .andExpect(jsonPath("$.payload.versions[2].dimensions", is("130x130 px")))
                    .andExpect(jsonPath("$.payload.versions[2].size", is("2 Kb")))
                    .andExpect(jsonPath("$.payload.versions[2].path",
                            startsWith("/entando-de-app/engine/admin/profile/image_d2.jpg")))
                    .andExpect(jsonPath("$.payload.versions[3].dimensions", is("150x150 px")))
                    .andExpect(jsonPath("$.payload.versions[3].size", is("2 Kb")))
                    .andExpect(jsonPath("$.payload.versions[3].path",
                            startsWith("/entando-de-app/engine/admin/profile/image_d3.jpg")));
        } finally {
            performDeleteProfilePicture(username, accessToken)
                    .andDo(print())
                    .andExpect(status().isOk());

            performGetProfilePicture(username, accessToken)
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.versions.size()", is(4)));
        }
    }

    private ResultActions performCreateProfilePicture(String username, String accessToken, String mimeType) throws Exception {

        String contents = "some text very big so it has more than 1Kb size asdklasdhadsjakhdsjadjasdhjhjasd some garbage to make it bigger!!!" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x";

        String path = StringUtils.join("/userProfiles/", username, "/profilePicture");
        MockMultipartFile file = new MockMultipartFile("file", "image_test.jpeg", mimeType, contents.getBytes());

        MockHttpServletRequestBuilder request = multipart(path)
                .file(file)
                .header("Authorization", "Bearer " + accessToken);
        return mockMvc.perform(request);
    }

    private ResultActions performGetProfilePicture(String username, String accessToken) throws Exception {
        String path = StringUtils.join("/userProfiles/", username, "/profilePicture");
        return mockMvc.perform(
                get(path)
                        .header("Authorization", "Bearer " + accessToken));
    }

    private ResultActions performDeleteProfilePicture(String username, String accessToken) throws Exception {
        String path = StringUtils.join("/userProfiles/", username, "/profilePicture");
        return mockMvc.perform(
                delete(path)
                        .header("Authorization", "Bearer " + accessToken));
    }

    private ResultActions performEditProfilePicture(String username, String accessToken) throws Exception {

        String contents = "some text very big so it has more than 1Kb size asdklasdhadsjakhdsjadjasdhjhjasd some garbage to make it bigger!!!" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x";

        String path = StringUtils.join("/userProfiles/", username, "/profilePicture");

        ResultActions result = mockMvc
                .perform(multipart(path)
                        .file(new MockMultipartFile("file", "image_test.jpeg", "application/jpeg", contents.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));

        return result.andDo(print());
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

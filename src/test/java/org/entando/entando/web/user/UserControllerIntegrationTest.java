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
package org.entando.entando.web.user;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.common.entity.IEntityTypesConfigurer;
import com.agiletec.aps.system.services.authorization.Authorization;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupManager;
import com.agiletec.aps.system.services.role.IRoleManager;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.role.Role;
import com.agiletec.aps.system.services.user.IAuthenticationProviderManager;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.FileTextReader;
import com.jayway.jsonpath.JsonPath;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import org.entando.entando.aps.system.services.userprofile.IUserProfileManager;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.MockMvcHelper;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * @author paddeo
 */
class UserControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    IUserManager userManager;

    @Autowired
    IGroupManager groupManager;

    @Autowired
    IRoleManager roleManager;

    @Autowired
    private IAuthorizationManager authorizationManager;

    @Autowired
    private IAuthenticationProviderManager authenticationProviderManager;

    @Autowired
    private IUserProfileManager userProfileManager;

    private MockMvcHelper mockMvcHelper;

    @Test
    void testGetUsersDefaultSorting() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/users")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.metaData.pageSize", is(100)));
        result.andExpect(jsonPath("$.metaData.sort", is("username")));
        result.andExpect(jsonPath("$.metaData.page", is(1)));
    }

    @Test
    void testGetUsersWithProfile() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/users")
                        .param("withProfile", "1")
                        .header("Authorization", "Bearer " + accessToken));
        result.andDo(resultPrint())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload", Matchers.hasSize(Matchers.greaterThan(1))))
                .andExpect(jsonPath("$.metaData.additionalParams.withProfile", is("1")))
                .andExpect(jsonPath("$.payload[1].profileType.typeCode", is("PFL")))
                .andExpect(jsonPath("$.payload[1].profileType.typeDescription", is("Default user profile type")));
    }

    @Test
    void testGetUsersWithoutProfile() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/users")
                        .param("withProfile", "0")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        System.out.println("with no profile: " + result.andReturn().getResponse().getContentAsString());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.metaData.additionalParams.withProfile", is("0")));
    }

    @Test
    void testGetUsersWithProfileAndProfileAttributesFilters() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/users")
                        .param("withProfile", "1")
                        .param("filter[0].entityAttr", "fullname")
                        .param("filter[0].operator", "like")
                        .param("filter[0].value", "s")
                        .param("filter[1].attribute", "profileType")
                        .param("filter[1].operator", "eq")
                        .param("filter[1].value", "All")
                        .header("Authorization", "Bearer " + accessToken));
        result.andDo(resultPrint())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload", Matchers.hasSize(Matchers.greaterThan(0))))
                .andExpect(jsonPath("$.metaData.additionalParams.withProfile", is("1")))
                .andExpect(jsonPath("$.payload[0].profileType.typeCode", is("PFL")))
                .andExpect(jsonPath("$.payload[0].profileType.typeDescription", is("Default user profile type")))
                .andExpect(jsonPath("$.payload[0].profileAttributes.fullname", Matchers.containsString("s")));

        String username = JsonPath.read(result.andReturn().getResponse().getContentAsString(), "$.payload[0].username");

        result = mockMvc
                .perform(get("/users/" + username)
                .header("Authorization", "Bearer " + accessToken));

        result.andDo(resultPrint())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.username", is(username)))
                .andExpect(jsonPath("$.payload.profileType.typeCode", is("PFL")))
                .andExpect(jsonPath("$.payload.profileType.typeDescription", is("Default user profile type")));
    }

    @Test
    void testAddUserAuthorities_1() throws Exception {
        Group group = createGroup(1);
        Role role = createRole(1);
        UserDetails mockuser = this.createUser("mockuser");
        try {
            this.groupManager.addGroup(group);
            this.roleManager.addRole(role);
            this.userManager.addUser(mockuser);
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);
            String mockJson = "[{\"group\":\"group1\", \"role\":\"role1\"}]";
            ResultActions result1 = mockMvc.perform(
                    put("/users/{target}/authorities", "wrongUser")
                            .content(mockJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            result1.andExpect(status().isNotFound());
            ResultActions result2 = mockMvc.perform(
                    put("/users/{target}/authorities", "mockuser")
                            .content(mockJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            result2.andExpect(status().isOk());
            result2.andExpect(jsonPath("$.payload[0].group", is("group1")));

            ResultActions result3 = mockMvc.perform(
                    get("/users/{target}/authorities", "wrongUser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            result3.andExpect(status().isNotFound());

            ResultActions result4 = mockMvc.perform(
                    get("/users/{target}/authorities", "mockuser")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            result4.andExpect(status().isOk());
            result4.andExpect(jsonPath("$.payload", Matchers.hasSize(1)));
            result4.andExpect(jsonPath("$.payload[0].group", is("group1")));
            result4.andExpect(jsonPath("$.payload[0].role", is("role1")));
        } finally {
            this.authorizationManager.deleteUserAuthorizations("mockuser");
            this.groupManager.removeGroup(group);
            this.roleManager.removeRole(role);
            this.userManager.removeUser("mockuser");
        }
    }

    @Test
    void testAddUserAuthorities_2() throws Exception {
        Group group = createGroup(1);
        Role role = createRole(1);
        String username = "mockuser_1";
        UserDetails mockuser = this.createUser(username);
        try {
            this.groupManager.addGroup(group);
            this.roleManager.addRole(role);
            this.userManager.addUser(mockuser);
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);
            String mockJson1 = "[{\"group\":\"group1\", \"role\":\"role1\"}]";
            ResultActions result1 = mockMvc.perform(
                    post("/users/{target}/authorities", username)
                            .content(mockJson1).contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            result1.andExpect(status().isOk());
            result1.andExpect(jsonPath("$.payload[0].group", is("group1")));

            List<Authorization> auths = this.authorizationManager.getUserAuthorizations(username);
            Assertions.assertEquals(1, auths.size());

            String mockJson2 = "[{\"group\":\"customers\", \"role\":\"supervisor\"}]";
            ResultActions result2 = mockMvc.perform(
                    post("/users/{target}/authorities", username)
                            .content(mockJson2).contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            result2.andExpect(status().isOk());
            result2.andExpect(jsonPath("$.payload[0].group", is("customers")));

            auths = this.authorizationManager.getUserAuthorizations(username);
            Assertions.assertEquals(2, auths.size());

            ResultActions result3 = mockMvc.perform(
                    get("/users/{target}/authorities", username)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            result3.andExpect(status().isOk());
            result3.andExpect(jsonPath("$.payload", Matchers.hasSize(2)));

            String mockJson4 = "[{\"group\":\"helpdesk\", \"role\":\"pageManager\"}]";

            ResultActions result4 = mockMvc.perform(
                    put("/users/{target}/authorities", username)
                            .content(mockJson4).contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            result4.andExpect(status().isOk());
            result4.andExpect(jsonPath("$.payload[0].group", is("helpdesk")));

            auths = this.authorizationManager.getUserAuthorizations(username);
            Assertions.assertEquals(1, auths.size());
            Assertions.assertEquals("helpdesk", auths.get(0).getGroup().getName());

            String mockJson5 = "[{\"group\":\"wrong_group\", \"role\":\"pageManager\"}]";
            ResultActions result5 = mockMvc.perform(
                    put("/users/{target}/authorities", username)
                            .content(mockJson5).contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            result5.andExpect(status().isBadRequest());
            result5.andExpect(jsonPath("$.errors.size()", is(1)));
            result5.andExpect(jsonPath("$.errors[0].code", is("2")));

            auths = this.authorizationManager.getUserAuthorizations(username);
            Assertions.assertEquals(1, auths.size());
            Assertions.assertEquals("helpdesk", auths.get(0).getGroup().getName());

        } finally {
            this.authorizationManager.deleteUserAuthorizations(username);
            this.groupManager.removeGroup(group);
            this.roleManager.removeRole(role);
            this.userManager.removeUser(username);
        }
    }

    @Test
    void testAddUserAuthorities_3() throws Exception {
        Group group = this.createGroup(100);
        Role role = this.createRole(100);
        String username = "mockuser_2";
        UserDetails mockuser = this.createUser(username);
        try {
            this.groupManager.addGroup(group);
            this.roleManager.addRole(role);
            this.userManager.addUser(mockuser);
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);
            String mockJson1 = "[{\"group\":null, \"role\":\"role100\"}]";
            ResultActions result1 = mockMvc.perform(
                    post("/users/{target}/authorities", username)
                            .content(mockJson1).contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            result1.andExpect(status().isOk());
            result1.andExpect(jsonPath("$.payload[0].role", is("role100")));

            List<Authorization> auths = this.authorizationManager.getUserAuthorizations(username);
            Assertions.assertEquals(1, auths.size());

            String mockJson2 = "[{\"group\":\"customers\", \"role\":null}]";
            ResultActions result2 = mockMvc.perform(
                    post("/users/{target}/authorities", username)
                            .content(mockJson2).contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            result2.andExpect(status().isOk());
            result2.andExpect(jsonPath("$.payload[0].group", is("customers")));

            auths = this.authorizationManager.getUserAuthorizations(username);
            Assertions.assertEquals(2, auths.size());

            ResultActions result3 = mockMvc.perform(
                    get("/users/{target}/authorities", username)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            result3.andExpect(status().isOk());
            result3.andExpect(jsonPath("$.payload", Matchers.hasSize(2)));

            String mockJson4 = "[{\"group\":null, \"role\":null}]";

            ResultActions result4 = mockMvc.perform(
                    put("/users/{target}/authorities", username)
                            .content(mockJson4).contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            result4.andExpect(status().isBadRequest());
            result4.andExpect(jsonPath("$.errors.size()", is(1)));

            auths = this.authorizationManager.getUserAuthorizations(username);
            Assertions.assertEquals(2, auths.size());

        } finally {
            this.authorizationManager.deleteUserAuthorizations(username);
            this.groupManager.removeGroup(group);
            this.roleManager.removeRole(role);
            this.userManager.removeUser(username);
        }
    }

    @Test
    void testAddRemoveUser_1() throws Exception {
        String validUsername = "valid.username_ok";
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            String invalidBody1 = "{\"username\": \"$invalid%%\",\"status\": \"active\",\"password\": \"password\"," +
                    "\"passwordConfirm\": \"password\"}";
            ResultActions resultInvalid1 = this.executeUserPost(invalidBody1, accessToken, status().isBadRequest());
            resultInvalid1.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            resultInvalid1.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            resultInvalid1.andExpect(jsonPath("$.errors[0].code", is("2")));
            resultInvalid1.andExpect(jsonPath("$.metaData.size()", is(0)));

            String invalidBody2 = "{\"username\": \"usernamevelylong_.veryveryveryveryveryveryveryveryveryveryveryveryveryveryverylong\",\"status\": \"active\",\"password\": \"password\"}";
            ResultActions resultInvalid2 = this.executeUserPost(invalidBody2, accessToken, status().isBadRequest());
            resultInvalid2.andExpect(jsonPath("$.errors[0].code", is("2")));

            String invalidBody3 = "{\"username\": \"username with space\",\"status\": \"active\",\"password\": \"password\"," +
                    "\"passwordConfirm\": \"password\"}";
            ResultActions resultInvalid3 = this.executeUserPost(invalidBody3, accessToken, status().isBadRequest());
            resultInvalid3.andExpect(jsonPath("$.errors[0].code", is("2")));

            String mockJson = "{\"username\": \"" + validUsername + "\",\"status\": \"active\",\"password\": \"password\"," +
                    "\"passwordConfirm\": \"password\"}";
            ResultActions result = this.executeUserPost(mockJson, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.username", is(validUsername)));

            ResultActions result2 = this.executeUserPost(mockJson, accessToken, status().isConflict());
            result2.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            result2.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            result2.andExpect(jsonPath("$.errors[0].code", is("1")));
            result2.andExpect(jsonPath("$.metaData.size()", is(0)));

            ResultActions resultDelete = mockMvc.perform(
                    delete("/users/{username}", URLEncoder.encode(validUsername, "ISO-8859-1"))
                            .content(mockJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            resultDelete.andExpect(status().isOk());
            resultDelete.andExpect(jsonPath("$.payload.code", is(validUsername)));
        } catch (Throwable e) {
            this.userManager.removeUser(validUsername);
            throw e;
        } finally {
            UserDetails user = this.userManager.getUser(validUsername);
            assertNull(user);
        }
    }

    @Test
    void testAddRemoveUser_2() throws Exception {
        String validUsername = "valid.username_ok";
        String validPassword = "valid.123_ok";
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            String invalidBody1 = "{\"username\": \"" + validUsername + "\",\"status\": \"active\",\"password\": \"$invalid%%\","
                    + "\"passwordConfirm\": \"$invalid%%\"}";
            ResultActions resultInvalid1 = this.executeUserPost(invalidBody1, accessToken, status().isBadRequest());
            resultInvalid1.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            resultInvalid1.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            resultInvalid1.andExpect(jsonPath("$.errors[0].code", is("3")));
            resultInvalid1.andExpect(jsonPath("$.metaData.size()", is(0)));

            String invalidBody2 = "{\"username\": \"" + validUsername + "\",\"status\": \"active\",\"password\": \"upasswordvelylong_.veryverylong\","
                + "\"passwordConfirm\": \"upasswordvelylong_.veryverylong\"}";
            ResultActions resultInvalid2 = this.executeUserPost(invalidBody2, accessToken, status().isBadRequest());
            resultInvalid2.andExpect(jsonPath("$.errors[0].code", is("3")));

            String mockJson = "{\"username\": \"" + validUsername + "\",\"status\": \"active\",\"password\": \"" + validPassword + "\","
                    + "\"passwordConfirm\": \"" + validPassword + "\"}";
            ResultActions result = this.executeUserPost(mockJson, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.username", is(validUsername)));

            ResultActions resultDelete = mockMvc.perform(
                    delete("/users/{username}", URLEncoder.encode(validUsername, "ISO-8859-1"))
                            .content(mockJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            resultDelete.andExpect(status().isOk());
            resultDelete.andExpect(jsonPath("$.payload.code", is(validUsername)));
        } catch (Throwable e) {
            this.userManager.removeUser(validUsername);
            throw e;
        } finally {
            UserDetails user = this.userManager.getUser(validUsername);
            assertNull(user);
        }
    }

    @Test
    void testAddUserWithLongName() throws Exception {
        String validUsername = "valid.username_with_very_long_name_with_a_total_of_80_characters_maximum_allowed";
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            String mockJson = "{\"username\": \"" + validUsername + "\",\"status\": \"active\",\"password\": \"password\","
                + "\"passwordConfirm\": \"password\"}";
            ResultActions result = this.executeUserPost(mockJson, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.username", is(validUsername)));

            ResultActions result2 = this.executeUserPost(mockJson, accessToken, status().isConflict());
            result2.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            result2.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            result2.andExpect(jsonPath("$.errors[0].code", is("1")));
            result2.andExpect(jsonPath("$.metaData.size()", is(0)));

            ResultActions resultDelete = mockMvc.perform(
                    delete("/users/{username}", URLEncoder.encode(validUsername, "ISO-8859-1"))
                            .content(mockJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            resultDelete.andExpect(status().isOk());
            resultDelete.andExpect(jsonPath("$.payload.code", is(validUsername)));
        } catch (Throwable e) {
            this.userManager.removeUser(validUsername);
            throw e;
        } finally {
            UserDetails user = this.userManager.getUser(validUsername);
            assertNull(user);
        }
    }

    @Test
    void testAddUserWithNameTooLong() throws Exception {
        String invalidUsername = "invalid.username_with_too_many_characters_81_one_more_than_the_maximum_allowed_80";
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            String mockJson = "{\"username\": \"" + invalidUsername + "\",\"status\": \"active\",\"password\": \"password\"}";
            this.executeUserPost(mockJson, accessToken, status().isBadRequest());

        } catch (Throwable e) {
            this.userManager.removeUser(invalidUsername);
            throw e;
        } finally {
            UserDetails user = this.userManager.getUser(invalidUsername);
            assertNull(user);
        }
    }

    @Test
    void testAddUserUppercase() throws Exception {
        String invalidUsername = "Username";
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            String mockJson = "{\"username\": \"" + invalidUsername + "\",\"status\": \"active\",\"password\": \"password\"}";
            this.executeUserPost(mockJson, accessToken, status().isBadRequest());

        } catch (Throwable e) {
            this.userManager.removeUser(invalidUsername);
            throw e;
        } finally {
            UserDetails user = this.userManager.getUser(invalidUsername);
            assertNull(user);
        }
    }


    @Test
    void testUpdateUser() throws Exception {
        String validUsername = "test_test";
        String validPassword = "password";
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            String mockJson = "{\"username\": \"" + validUsername + "\",\"status\": \"active\",\"password\": \"" + validPassword + "\","
                    + "\"passwordConfirm\": \"" + validPassword + "\"}";
            this.executeUserPost(mockJson, accessToken, status().isOk());

            String invalidBody1 = "{\"username\": \"" + validUsername + "\",\"status\": \"active\",\"password\": \"$invalid%%\"}";
            ResultActions resultInvalid1 = this.executeUserPut(invalidBody1, validUsername, accessToken, status().isBadRequest());
            System.out.println(resultInvalid1.andReturn().getResponse().getContentAsString());
            resultInvalid1.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            resultInvalid1.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            resultInvalid1.andExpect(jsonPath("$.errors[0].code", is("3")));
            resultInvalid1.andExpect(jsonPath("$.metaData.size()", is(0)));

            String invalidBody2 = "{\"username\": \"" + validUsername + "\",\"status\": \"active\",\"password\": \"upasswordvelylong_.veryverylong\"}";
            ResultActions resultInvalid2 = this.executeUserPut(invalidBody2, validUsername, accessToken, status().isBadRequest());
            resultInvalid2.andExpect(jsonPath("$.errors[0].code", is("3")));

            String invalidBody3 = "{\"username\": \"" + validUsername + "\",\"status\": \"active\",\"password\": \"" + validPassword + "\"}";
            ResultActions resultInvalid3 = this.executeUserPut(invalidBody3, "invalidUsername", accessToken, status().isConflict());
            resultInvalid3.andExpect(jsonPath("$.errors[0].code", is("2")));

            String valid1 = "{\"username\": \"" + validUsername + "\",\"status\": \"active\",\"reset\": true}";
            ResultActions resultValid1 = this.executeUserPut(valid1, validUsername, accessToken, status().isOk());
            String responseValid1 = resultValid1.andReturn().getResponse().getContentAsString();
            System.out.println("resp:" + responseValid1);
            resultValid1.andExpect(jsonPath("$.payload.username", is(validUsername)));
            UserDetails authUser = this.authenticationProviderManager.getUser(validUsername, validPassword);
            Assertions.assertNotNull(authUser);

            String valid2 = "{\"username\": \"" + validUsername + "\",\"status\": \"active\",\"password\": \"12345678\",\"reset\": true}";
            ResultActions resultValid2 = this.executeUserPut(valid2, validUsername, accessToken, status().isOk());
            String responseValid2 = resultValid2.andReturn().getResponse().getContentAsString();
            System.out.println("resp:" + responseValid2);
            resultValid2.andExpect(jsonPath("$.payload.username", is(validUsername)));
            authUser = this.authenticationProviderManager.getUser(validUsername, validPassword);
            Assertions.assertNull(authUser);
            authUser = this.authenticationProviderManager.getUser(validUsername, "12345678");
            Assertions.assertNotNull(authUser);
        } catch (Throwable e) {
            throw e;
        } finally {
            this.userManager.removeUser(validUsername);
        }
    }

    @Test
    void testUpdatePassword_1() throws Exception {
        String validUsername = "valid.username_ok";
        String validPassword = "valid.123_ok";
        String newValidPassword = "valid.1234_ok";
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            String mockJson = "{\"username\": \"" + validUsername + "\",\"status\": \"active\",\"password\": \"" + validPassword + "\","
                    + "\"passwordConfirm\": \"" + validPassword + "\"}";
            this.executeUserPost(mockJson, accessToken, status().isOk());

            String invalidBody1 = "{\"username\": \"" + validUsername + "\",\"oldPassword\": \"" + validPassword + "\",\"newPassword\": \"$invalid%%\"}";
            ResultActions resultInvalid1 = this.executeUpdatePassword(invalidBody1, validUsername, accessToken, status().isBadRequest());
            resultInvalid1.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            resultInvalid1.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            resultInvalid1.andExpect(jsonPath("$.errors[0].code", is("3")));
            resultInvalid1.andExpect(jsonPath("$.metaData.size()", is(0)));

            String invalidBody2 = "{\"username\": \"" + validUsername + "\",\"oldPassword\": \"" + validPassword + "\",\"newPassword\": \"upasswordvelylong_.veryverylong\"}";
            ResultActions resultInvalid2 = this.executeUpdatePassword(invalidBody2, validUsername, accessToken, status().isBadRequest());
            resultInvalid2.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            resultInvalid2.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            resultInvalid2.andExpect(jsonPath("$.errors[0].code", is("3")));
            resultInvalid2.andExpect(jsonPath("$.metaData.size()", is(0)));

            String invalidBody3 = "{\"username\": \"" + validUsername + "\",\"oldPassword\": \"invalid\",\"newPassword\": \"" + newValidPassword + "\"}";
            ResultActions resultInvalid3 = this.executeUpdatePassword(invalidBody3, validUsername, accessToken, status().isBadRequest());
            resultInvalid3.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            resultInvalid3.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            resultInvalid3.andExpect(jsonPath("$.errors[0].code", is("4")));
            resultInvalid3.andExpect(jsonPath("$.metaData.size()", is(0)));

            String invalidBody4 = "{\"username\": \"" + validUsername + "\",\"oldPassword\": \"invalid\",\"newPassword\": \"\"}";
            ResultActions resultInvalid4 = this.executeUpdatePassword(invalidBody4, validUsername, accessToken, status().isBadRequest());
            resultInvalid4.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            resultInvalid4.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            resultInvalid4.andExpect(jsonPath("$.errors[0].code", is("52")));
            resultInvalid4.andExpect(jsonPath("$.metaData.size()", is(0)));

            String validBody = "{\"username\": \"" + validUsername + "\",\"oldPassword\": \"" + validPassword + "\",\"newPassword\": \"" + newValidPassword + "\"}";
            ResultActions resultValid = this.executeUpdatePassword(validBody, validUsername, accessToken, status().isOk());
            resultValid.andExpect(jsonPath("$.payload.username", is(validUsername)));
            resultValid.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            resultValid.andExpect(jsonPath("$.metaData.size()", is(0)));

        } catch (Throwable e) {
            throw e;
        } finally {
            this.userManager.removeUser(validUsername);
        }
    }

    @Test
    void testUpdatePassword_2() throws Exception {
        String validUsername = "valid_ok.2";
        String validPassword = "valid.123_ok";
        String newValidPassword = "valid.1234_ok";
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            String mockJson = "{\"username\": \"" + validUsername + "\",\"status\": \"active\",\"password\": \"" + validPassword + "\","
                    + "\"passwordConfirm\": \"" + validPassword + "\"}";
            this.executeUserPost(mockJson, accessToken, status().isOk());

            String invalidBody1 = "{\"username\": \"" + validUsername + "\",\"oldPassword\": \"" + validPassword + "\",\"newPassword\": \"" + newValidPassword + "\"}";
            ResultActions resultInvalid1 = this.executeUpdatePassword(invalidBody1, "no_same_username", accessToken, status().isConflict());
            resultInvalid1.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            resultInvalid1.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            resultInvalid1.andExpect(jsonPath("$.errors[0].code", is("2")));
            resultInvalid1.andExpect(jsonPath("$.metaData.size()", is(0)));

            String noExistingUser = "test12345";
            String invalidBody2 = "{\"username\": \"" + noExistingUser + "\",\"oldPassword\": \"" + validPassword + "\",\"newPassword\": \"" + newValidPassword + "\"}";
            ResultActions resultInvalid2 = this.executeUpdatePassword(invalidBody2, noExistingUser, accessToken, status().isNotFound());
            resultInvalid2.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            resultInvalid2.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            resultInvalid2.andExpect(jsonPath("$.errors[0].code", is("1")));
            resultInvalid2.andExpect(jsonPath("$.metaData.size()", is(0)));

            String validBody = "{\"username\": \"" + validUsername + "\",\"oldPassword\": \"" + validPassword + "\",\"newPassword\": \"" + newValidPassword + "\"}";
            ResultActions resultValid = this.executeUpdatePassword(validBody, validUsername, accessToken, status().isOk());
            resultValid.andExpect(jsonPath("$.payload.username", is(validUsername)));
            resultValid.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            resultValid.andExpect(jsonPath("$.metaData.size()", is(0)));

        } catch (Throwable e) {
            throw e;
        } finally {
            this.userManager.removeUser(validUsername);
        }
    }

    @Test
    void testUpdatePasswordInactiveUser() throws Exception {
        String validUsername = "user1";
        String validPassword = "password1";
        String newValidPassword = "password2";
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            String mockJson = "{\"username\": \"" + validUsername + "\",\"status\": \"inactive\",\"password\": \"" + validPassword + "\","
                    + "\"passwordConfirm\": \"" + validPassword + "\"}";
            this.executeUserPost(mockJson, accessToken, status().isOk());

            String body = "{\"username\": \"" + validUsername + "\",\"oldPassword\": \"" + validPassword + "\",\"newPassword\": \"" + newValidPassword + "\"}";
            ResultActions resultValid = this.executeUpdatePassword(body, validUsername, accessToken, status().isOk());
            resultValid.andExpect(jsonPath("$.payload.username", is(validUsername)));
            resultValid.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            resultValid.andExpect(jsonPath("$.metaData.size()", is(0)));

        } catch (Throwable e) {
            throw e;
        } finally {
            this.userManager.removeUser(validUsername);
        }
    }

    @Test
    void testUserPagination() throws Exception {
        String userPrefix = "test_pager_";
        for (int i = 0; i < 20; i++) {
            UserDetails user = this.createUser(userPrefix + i);
            this.userManager.addUser(user);
        }
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);
            ResultActions result = mockMvc
                    .perform(get("/users").contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken));
            result.andExpect(status().isOk());
            result.andExpect(jsonPath("$.payload", Matchers.hasSize(28)));
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.metaData.size()", is(8)));
            result.andExpect(jsonPath("$.metaData.page", is(1)));
            result.andExpect(jsonPath("$.metaData.pageSize", is(100)));
            result.andExpect(jsonPath("$.metaData.totalItems", is(28)));

            result = mockMvc
                    .perform(get("/users")
                            .param("pageSize", "10").param("page", "2")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken));
            result.andExpect(status().isOk());
            result.andExpect(jsonPath("$.payload", Matchers.hasSize(10)));
            result.andExpect(jsonPath("$.payload[0].username", is("test_pager_10")));
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.metaData.size()", is(8)));
            result.andExpect(jsonPath("$.metaData.page", is(2)));
            result.andExpect(jsonPath("$.metaData.pageSize", is(10)));
            result.andExpect(jsonPath("$.metaData.totalItems", is(28)));

            result = mockMvc
                    .perform(get("/users")
                            .param("pageSize", "10").param("page", "2")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken));
            result.andExpect(status().isOk());
            result.andExpect(jsonPath("$.payload", Matchers.hasSize(10)));
            result.andExpect(jsonPath("$.payload[0].username", is("test_pager_10")));
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.metaData.size()", is(8)));
            result.andExpect(jsonPath("$.metaData.page", is(2)));
            result.andExpect(jsonPath("$.metaData.pageSize", is(10)));
            result.andExpect(jsonPath("$.metaData.lastPage", is(3)));
            result.andExpect(jsonPath("$.metaData.totalItems", is(28)));

            result = mockMvc
                    .perform(get("/users")
                            .param("pageSize", "5").param("page", "4")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken));
            result.andExpect(status().isOk());
            result.andExpect(jsonPath("$.payload", Matchers.hasSize(5)));
            result.andExpect(jsonPath("$.payload[0].username", is("test_pager_15")));
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.metaData.size()", is(8)));
            result.andExpect(jsonPath("$.metaData.page", is(4)));
            result.andExpect(jsonPath("$.metaData.pageSize", is(5)));
            result.andExpect(jsonPath("$.metaData.lastPage", is(6)));
            result.andExpect(jsonPath("$.metaData.totalItems", is(28)));

        } catch (Throwable e) {
            throw e;
        } finally {
            for (int i = 0; i < 20; i++) {
                this.userManager.removeUser(userPrefix + i);
            }
        }
    }

    @Test
    void testGetUsersWithAdminPermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/users")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }

    @Test
    void testGetUsersWithoutPermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("normal_user", "0x24").build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/users")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isForbidden());
    }

    @Test
    void testGetUsersWithEnterBackendPermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("normal_user", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "admin", Permission.ENTER_BACKEND).build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/users")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }

    @Test
    void testGetUsersWithManagerUserProfilePermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("normal_user", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "admin", Permission.MANAGE_USER_PROFILES).build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/users/admin")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }

    @Test
    void testGetUsersWithManageUsersPermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("normal_user", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "admin", Permission.MANAGE_USERS).build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/users/admin")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }

    @Test
    void testGetUsersWithViewUsersPermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("normal_user", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "admin", Permission.VIEW_USERS).build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/users/admin")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }

    @Test
    void testAddUserWithDefaultProfile() throws Exception {
        String username = "user_with_default_profile";
        try {
            InputStream file = this.getClass().getResourceAsStream("1_POST_user_with_default_profile.json");
            String request = FileTextReader.getText(file);
            request = request.replace("**NAME**", username);

            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            mockMvc
                    .perform(post("/users")
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.username", Matchers.is("user_with_default_profile")))
                    .andExpect(jsonPath("$.payload.profileType.typeCode", Matchers.is("PFL")))
                    .andExpect(jsonPath("$.payload.profileType.typeDescription", Matchers.is("Default user profile type")));

        } finally {
            this.userManager.removeUser(username);
            UserDetails user = this.userManager.getUser(username);
            assertNull(user);
        }
    }

    @Test
    void testAddUserWithProfile() throws Exception {
        String username = "user_with_profile";
        try {

            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            InputStream file = this.getClass().getResourceAsStream("1_POST_profile_type.json");
            String profileRequest = FileTextReader.getText(file);
            mockMvc
                    .perform(post("/profileTypes")
                            .content(profileRequest)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk());

            file = this.getClass().getResourceAsStream("1_POST_user_with_profile.json");
            String userRequest = FileTextReader.getText(file);
            userRequest = userRequest.replace("**NAME**", username);

            mockMvc
                    .perform(post("/users")
                            .content(userRequest)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.username", Matchers.is("user_with_profile")))
                    .andExpect(jsonPath("$.payload.profileType.typeCode", Matchers.is("AAA")))
                    .andExpect(jsonPath("$.payload.profileType.typeDescription", Matchers.is("Profile Type AAA")));

        } finally {
            this.userManager.removeUser(username);
            UserDetails user = this.userManager.getUser(username);
            assertNull(user);

            if (null != this.userProfileManager.getEntityPrototype("AAA")) {
                ((IEntityTypesConfigurer) this.userProfileManager).removeEntityPrototype("AAA");
            }
        }
    }

    @Test
    void testUpdateUserWithProfile() throws Exception {
        String username = "user_with_profile";
        try {
            InputStream file = this.getClass().getResourceAsStream("1_POST_user_with_default_profile.json");
            String request = FileTextReader.getText(file).replace("**NAME**", username);

            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            mockMvc
                    .perform(post("/users")
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.username", Matchers.is("user_with_profile")))
                    .andExpect(jsonPath("$.payload.profileType.typeCode", Matchers.is("PFL")))
                    .andExpect(jsonPath("$.payload.profileType.typeDescription", Matchers.is("Default user profile type")));

            file = this.getClass().getResourceAsStream("1_PUT_user_with_profile.json");
            request = FileTextReader.getText(file).replace("**NAME**", username);

            mockMvc
                    .perform(put("/users/{username}", new Object[]{username})
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.username", Matchers.is("user_with_profile")))
                    .andExpect(jsonPath("$.payload.profileType.typeCode", Matchers.is("PFL")))
                    .andExpect(jsonPath("$.payload.profileType.typeDescription", Matchers.is("Default user profile type")));

        } finally {
            this.userManager.removeUser(username);
            UserDetails user = this.userManager.getUser(username);
            assertNull(user);
        }
    }

    @Test
    void testGetMyGroups() throws Exception {
        UserDetails loggedUser = new OAuth2TestUtils.UserBuilder("new_user", "0x24")
                .withAuthorization("coach", "editor", Permission.ENTER_BACKEND)
                .build();
        mockMvc
                .perform(get("/users/myGroups", new Object[]{"editorCoach"})
                        .header("Authorization", "Bearer " + mockOAuthInterceptor(loggedUser)))
                .andDo(resultPrint())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", Matchers.is(1)));

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        mockMvc
                .perform(get("/users/myGroups", new Object[]{"editorCoach"})
                        .header("Authorization", "Bearer " + mockOAuthInterceptor(user)))
                .andDo(resultPrint())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", Matchers.is(6)));
    }

    @Test
    void testGetMyGroupsPermissions() throws Exception {
        UserDetails loggedUser = new OAuth2TestUtils.UserBuilder("new_user", "0x24")
                .withAuthorization("coach", Permission.ENTER_BACKEND)
                .withAuthorization(Group.FREE_GROUP_NAME, Permission.MANAGE_PAGES)
                .build();
        mockMvc
                .perform(get("/users/myGroupPermissions", new Object[]{"editorCoach"})
                        .header("Authorization", "Bearer " + mockOAuthInterceptor(loggedUser)))
                .andDo(resultPrint())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", Matchers.is(2)))
                .andExpect(jsonPath("$.payload[0].group", Matchers.is("coach")))
                .andExpect(jsonPath("$.payload[1].group", Matchers.is("free")));

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        mockMvc
                .perform(get("/users/myGroupPermissions", new Object[]{"editorCoach"})
                        .header("Authorization", "Bearer " + mockOAuthInterceptor(user)))
                .andDo(resultPrint())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", Matchers.is(1)))
                .andExpect(jsonPath("$.payload[0].group", Matchers.is("administrators")))
                .andExpect(jsonPath("$.payload[0].permissions.size()", Matchers.is(1)))
                .andExpect(jsonPath("$.payload[0].permissions[0]", Matchers.is("superuser")));
    }

    private ResultActions executeUserPost(String body, String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(post("/users")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }

    private ResultActions executeUserPut(String body, String username, String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(put("/users/{username}", new Object[]{username})
                        .content(body).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }

    private ResultActions executeUpdatePassword(String body, String username, String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(post("/users/{username}/password", new Object[]{username})
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }

    private Group createGroup(int i) {
        Group group = new Group();
        group.setDescription("descr" + i);
        group.setName("group" + i);
        return group;
    }

    private Role createRole(int i) {
        Role role = new Role();
        role.setDescription("descr" + i);
        role.setName("role" + i);
        return role;
    }

    private UserDetails createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setDisabled(false);
        user.setLastAccess(new Date());
        user.setLastPasswordChange(new Date());
        user.setMaxMonthsSinceLastAccess(2);
        user.setMaxMonthsSinceLastPasswordChange(1);
        user.setPassword("password");
        return user;
    }

}

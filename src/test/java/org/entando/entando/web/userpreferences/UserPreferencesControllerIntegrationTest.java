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
package org.entando.entando.web.userpreferences;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.FileTextReader;

import java.io.InputStream;
import java.util.Date;

import org.entando.entando.aps.system.services.userpreferences.IUserPreferencesManager;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

class UserPreferencesControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private IUserManager userManager;
    @Autowired
    private IUserPreferencesManager userPreferencesManager;

    @Test
    void testGetWithUnknownUser() throws Exception {
        String username = "unknown_user";
        UserDetails user = new OAuth2TestUtils.UserBuilder(username, "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        mockMvc.perform(
                get("/userPreferences/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(resultPrint())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors.size()", Matchers.is(1)))
                .andExpect(jsonPath("$.errors[0].code", Matchers.is("1")))
                .andExpect(jsonPath("$.errors[0].message",
                        Matchers.is("a User with unknown_user code could not be found")));
    }

    @Test
    void testGetUsersPreferencesWithAdminPrivileges() throws Exception {
        String username = "user_with_admin_privileges";

        try {
            userManager.addUser(createUser(username));
            UserDetails user = new OAuth2TestUtils.UserBuilder(username, "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            mockMvc.perform(
                    get("/userPreferences/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(true)));
        } finally {
            this.userManager.removeUser(username);
            this.userPreferencesManager.deleteUserPreferences(username);
        }
    }

    @Test
    void testGetUsersPreferencesWithoutPrivileges() throws Exception {
        String username = "user_without_privileges";

        try {
            userManager.addUser(createUser(username));
            UserDetails user = new OAuth2TestUtils.UserBuilder(username, "0x24").build();
            String accessToken = mockOAuthInterceptor(user);

            mockMvc.perform(
                    get("/userPreferences/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(true)));
        } finally {
            this.userManager.removeUser(username);
            this.userPreferencesManager.deleteUserPreferences(username);
        }
    }

    @Test
    void testUpdateWithUnknownUser() throws Exception {
        String username = "unknown_user";
        UserDetails user = new OAuth2TestUtils.UserBuilder(username, "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        InputStream file = this.getClass().getResourceAsStream("1_PUT_user_preferences.json");
        String bodyRequest = FileTextReader.getText(file);

        mockMvc.perform(
                put("/userPreferences/{username}", username)
                        .content(bodyRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(resultPrint())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors.size()", Matchers.is(1)))
                .andExpect(jsonPath("$.errors[0].code", Matchers.is("1")))
                .andExpect(jsonPath("$.errors[0].message",
                        Matchers.is("a User with unknown_user code could not be found")));
    }

    @Test
    void testUpdateUsersPreferencesWithAdminPrivileges() throws Exception {
        String username = "user_with_admin_privileges";

        try {
            userManager.addUser(createUser(username));
            UserDetails user = new OAuth2TestUtils.UserBuilder(username, "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);
            InputStream file = this.getClass().getResourceAsStream("1_PUT_user_preferences.json");
            String bodyRequest = FileTextReader.getText(file);

            mockMvc.perform(
                    put("/userPreferences/{username}", username)
                            .content(bodyRequest)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isNotFound());
        } finally {
            this.userManager.removeUser(username);
            this.userPreferencesManager.deleteUserPreferences(username);
        }
    }

    @Test
    void testUpdateUsersPreferencesWithoutPrivileges() throws Exception {
        String username = "user_without_privileges";

        try {
            userManager.addUser(createUser(username));
            UserDetails user = new OAuth2TestUtils.UserBuilder(username, "0x24").build();
            String accessToken = mockOAuthInterceptor(user);
            InputStream file = this.getClass().getResourceAsStream("1_PUT_user_preferences.json");
            String bodyRequest = FileTextReader.getText(file);

            mockMvc.perform(
                    put("/userPreferences/{username}", username)
                            .content(bodyRequest)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isNotFound());
        } finally {
            this.userManager.removeUser(username);
            this.userPreferencesManager.deleteUserPreferences(username);
        }
    }

    @Test
    void testUpdateUsersPreferencesComplete() throws Exception {
        String username = "user_complete_update";

        try {
            userManager.addUser(createUser(username));
            UserDetails user = new OAuth2TestUtils.UserBuilder(username, "0x24").build();
            String accessToken = mockOAuthInterceptor(user);

            mockMvc.perform(
                    get("/userPreferences/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.defaultPageOwnerGroup", Matchers.isEmptyOrNullString()))
                    .andExpect(jsonPath("$.payload.defaultPageJoinGroups", Matchers.isEmptyOrNullString()))
                    .andExpect(jsonPath("$.payload.defaultContentOwnerGroup", Matchers.isEmptyOrNullString()))
                    .andExpect(jsonPath("$.payload.defaultContentJoinGroups", Matchers.isEmptyOrNullString()))
                    .andExpect(jsonPath("$.payload.defaultWidgetOwnerGroup", Matchers.isEmptyOrNullString()))
                    .andExpect(jsonPath("$.payload.defaultWidgetJoinGroups", Matchers.isEmptyOrNullString()));

            InputStream file = this.getClass().getResourceAsStream("2_PUT_user_preferences.json");
            String bodyRequest = FileTextReader.getText(file);

            mockMvc.perform(
                    put("/userPreferences/{username}", username)
                            .content(bodyRequest)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.defaultPageOwnerGroup", Matchers.is("group1")))
                    .andExpect(jsonPath("$.payload.defaultPageJoinGroups.size()", Matchers.is(4)))
                    .andExpect(jsonPath("$.payload.defaultPageJoinGroups[0]", Matchers.is("group2")))
                    .andExpect(jsonPath("$.payload.defaultPageJoinGroups[1]", Matchers.is("group3")))
                    .andExpect(jsonPath("$.payload.defaultPageJoinGroups[2]", Matchers.is("group4")))
                    .andExpect(jsonPath("$.payload.defaultPageJoinGroups[3]", Matchers.is("group5")))
                    .andExpect(jsonPath("$.payload.defaultContentOwnerGroup", Matchers.is("group6")))
                    .andExpect(jsonPath("$.payload.defaultContentJoinGroups.size()", Matchers.is(4)))
                    .andExpect(jsonPath("$.payload.defaultContentJoinGroups[0]", Matchers.is("group7")))
                    .andExpect(jsonPath("$.payload.defaultContentJoinGroups[1]", Matchers.is("group8")))
                    .andExpect(jsonPath("$.payload.defaultContentJoinGroups[2]", Matchers.is("group9")))
                    .andExpect(jsonPath("$.payload.defaultContentJoinGroups[3]", Matchers.is("group10")))
                    .andExpect(jsonPath("$.payload.defaultWidgetOwnerGroup", Matchers.is("group2")))
                    .andExpect(jsonPath("$.payload.defaultWidgetJoinGroups.size()", Matchers.is(2)))
                    .andExpect(jsonPath("$.payload.defaultWidgetJoinGroups[0]", Matchers.is("group5")))
                    .andExpect(jsonPath("$.payload.defaultWidgetJoinGroups[1]", Matchers.is("group9")));

            mockMvc.perform(
                    get("/userPreferences/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.defaultPageOwnerGroup", Matchers.is("group1")))
                    .andExpect(jsonPath("$.payload.defaultPageJoinGroups.size()", Matchers.is(4)))
                    .andExpect(jsonPath("$.payload.defaultPageJoinGroups[0]", Matchers.is("group2")))
                    .andExpect(jsonPath("$.payload.defaultPageJoinGroups[1]", Matchers.is("group3")))
                    .andExpect(jsonPath("$.payload.defaultPageJoinGroups[2]", Matchers.is("group4")))
                    .andExpect(jsonPath("$.payload.defaultPageJoinGroups[3]", Matchers.is("group5")))
                    .andExpect(jsonPath("$.payload.defaultContentOwnerGroup", Matchers.is("group6")))
                    .andExpect(jsonPath("$.payload.defaultContentJoinGroups.size()", Matchers.is(4)))
                    .andExpect(jsonPath("$.payload.defaultContentJoinGroups[0]", Matchers.is("group7")))
                    .andExpect(jsonPath("$.payload.defaultContentJoinGroups[1]", Matchers.is("group8")))
                    .andExpect(jsonPath("$.payload.defaultContentJoinGroups[2]", Matchers.is("group9")))
                    .andExpect(jsonPath("$.payload.defaultContentJoinGroups[3]", Matchers.is("group10")))
                    .andExpect(jsonPath("$.payload.defaultWidgetOwnerGroup", Matchers.is("group2")))
                    .andExpect(jsonPath("$.payload.defaultWidgetJoinGroups.size()", Matchers.is(2)))
                    .andExpect(jsonPath("$.payload.defaultWidgetJoinGroups[0]", Matchers.is("group5")))
                    .andExpect(jsonPath("$.payload.defaultWidgetJoinGroups[1]", Matchers.is("group9")));

            file = this.getClass().getResourceAsStream("9_PUT_user_preferences.json");
            bodyRequest = FileTextReader.getText(file);

            mockMvc.perform(
                    put("/userPreferences/{username}", username)
                            .content(bodyRequest)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.defaultPageOwnerGroup", Matchers.is("group1")))
                    .andExpect(jsonPath("$.payload.defaultPageJoinGroups.size()", Matchers.is(0)))
                    .andExpect(jsonPath("$.payload.defaultContentOwnerGroup", Matchers.is("group6")))
                    .andExpect(jsonPath("$.payload.defaultContentJoinGroups.size()", Matchers.is(0)))
                    .andExpect(jsonPath("$.payload.defaultWidgetOwnerGroup", Matchers.is("group2")))
                    .andExpect(jsonPath("$.payload.defaultWidgetJoinGroups.size()", Matchers.is(0)));

            mockMvc.perform(
                    get("/userPreferences/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.defaultPageOwnerGroup", Matchers.is("group1")))
                    .andExpect(jsonPath("$.payload.defaultPageJoinGroups.size()", Matchers.is(0)))
                    .andExpect(jsonPath("$.payload.defaultContentOwnerGroup", Matchers.is("group6")))
                    .andExpect(jsonPath("$.payload.defaultContentJoinGroups.size()", Matchers.is(0)))
                    .andExpect(jsonPath("$.payload.defaultWidgetOwnerGroup", Matchers.is("group2")))
                    .andExpect(jsonPath("$.payload.defaultWidgetJoinGroups.size()", Matchers.is(0)));
        } finally {
            this.userManager.removeUser(username);
            this.userPreferencesManager.deleteUserPreferences(username);
        }
    }

    @Test
    void testUpdateUsersPreferencesPartial() throws Exception {
        String username = "user_complete_partial";

        try {
            userManager.addUser(createUser(username));
            UserDetails user = new OAuth2TestUtils.UserBuilder(username, "0x24").build();
            String accessToken = mockOAuthInterceptor(user);

            mockMvc.perform(
                    get("/userPreferences/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(true)));

            InputStream file = this.getClass().getResourceAsStream("3_PUT_user_preferences.json");
            String bodyRequest = FileTextReader.getText(file);

            mockMvc.perform(
                    put("/userPreferences/{username}", username)
                            .content(bodyRequest)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(true)));

            mockMvc.perform(
                    get("/userPreferences/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(true)));

            file = this.getClass().getResourceAsStream("4_PUT_user_preferences.json");
            bodyRequest = FileTextReader.getText(file);

            mockMvc.perform(
                    put("/userPreferences/{username}", username)
                            .content(bodyRequest)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(true)));

            mockMvc.perform(
                    get("/userPreferences/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(true)));

            file = this.getClass().getResourceAsStream("5_PUT_user_preferences.json");
            bodyRequest = FileTextReader.getText(file);

            mockMvc.perform(
                    put("/userPreferences/{username}", username)
                            .content(bodyRequest)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(false)));

            mockMvc.perform(
                    get("/userPreferences/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(false)));

            file = this.getClass().getResourceAsStream("6_PUT_user_preferences.json");
            bodyRequest = FileTextReader.getText(file);

            mockMvc.perform(
                    put("/userPreferences/{username}", username)
                            .content(bodyRequest)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(false)));

            mockMvc.perform(
                    get("/userPreferences/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(false)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(false)));

            file = this.getClass().getResourceAsStream("7_PUT_user_preferences.json");
            bodyRequest = FileTextReader.getText(file);

            mockMvc.perform(
                    put("/userPreferences/{username}", username)
                            .content(bodyRequest)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(false)));

            mockMvc.perform(
                    get("/userPreferences/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(false)));

            file = this.getClass().getResourceAsStream("8_PUT_user_preferences.json");
            bodyRequest = FileTextReader.getText(file);

            mockMvc.perform(
                    put("/userPreferences/{username}", username)
                            .content(bodyRequest)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(true)));

            mockMvc.perform(
                    get("/userPreferences/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.wizard", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.loadOnPageSelect", Matchers.is(true)))
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(true)));
        } finally {
            this.userManager.removeUser(username);
            this.userPreferencesManager.deleteUserPreferences(username);
        }
    }

    @Test
    void testGetOtherUserPreferencesShouldFail() throws Exception {

        String callerUsername = "reader";
        String preferencesUsername = "admin";
        UserDetails user = new OAuth2TestUtils.UserBuilder(callerUsername, "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        mockMvc.perform(
                        get("/userPreferences/{username}", preferencesUsername)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header("Authorization", "Bearer " + accessToken))
                .andDo(resultPrint())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors[0].code", Matchers.is("9")))
                .andExpect(jsonPath("$.errors[0].message", Matchers.is("Cannot update other users' preferences")));
    }

    @Test
    void testUpdateOtherUserPreferencesShouldFail() throws Exception {

        String callerUsername = "reader";
        String preferencesUsername = "admin";
        UserDetails user = new OAuth2TestUtils.UserBuilder(callerUsername, "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        String bodyRequest;
        try (InputStream file = this.getClass().getResourceAsStream("1_PUT_user_preferences.json")) {
            bodyRequest = FileTextReader.getText(file);
        }

        mockMvc.perform(
                        put("/userPreferences/{username}", preferencesUsername)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(bodyRequest)
                                .header("Authorization", "Bearer " + accessToken))
                .andDo(resultPrint())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors[0].code", Matchers.is("9")))
                .andExpect(jsonPath("$.errors[0].message", Matchers.is("Cannot update other users' preferences")));
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

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

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class UserPreferencesControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private IUserManager userManager;
    @Autowired
    private IUserPreferencesManager userPreferencesManager;

    @Test
    public void testGetWithUnknownUser() throws Exception {
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
    public void testGetUsersPreferencesWithAdminPrivileges() throws Exception {
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
    public void testGetUsersPreferencesWithoutPrivileges() throws Exception {
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
    public void testUpdateWithUnknownUser() throws Exception {
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
    public void testUpdateUsersPreferencesWithAdminPrivileges() throws Exception {
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
    public void testUpdateUsersPreferencesWithoutPrivileges() throws Exception {
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
    public void testUpdateUsersPreferencesComplete() throws Exception {
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
                    .andExpect(jsonPath("$.payload.translationWarning", Matchers.is(true)));

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
        } finally {
            this.userManager.removeUser(username);
            this.userPreferencesManager.deleteUserPreferences(username);
        }
    }

    @Test
    public void testUpdateUsersPreferencesPartial() throws Exception {
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

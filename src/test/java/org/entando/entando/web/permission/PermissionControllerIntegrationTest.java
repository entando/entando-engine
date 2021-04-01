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
package org.entando.entando.web.permission;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PermissionControllerIntegrationTest extends AbstractControllerIntegrationTest {


    @Test
    void testGetPermissions() throws Exception {

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                                      .perform(get("/permissions")
                                                            .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }


    @Test
    void testGetPermissionsFilterByCode() throws Exception {

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                                      .perform(get("/permissions")
                                                                  .param("filter[0].attribute", "code")
                                                                  .param("filter[0].value", "manage")
                                                                  .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload.length()", is(4)));
    }


    @Test
    void testGetPermissionsFilterByDescr() throws Exception {

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                                      .perform(get("/permissions")
                                                            .param("filter[0].attribute", "descr")
                                                            .param("filter[0].value", "Access")
                                                            .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload.length()", is(2)));
    }

    @Test
    void testGetPermissionsWithoutPermission() throws Exception {

        UserDetails user = new OAuth2TestUtils.UserBuilder("normal_user", "0x24").build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/permissions")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isForbidden());
    }

    @Test
    void testGetPermissionsWithEnterBackEndPermission() throws Exception {

        UserDetails user = new OAuth2TestUtils.UserBuilder("enter_backend_user", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "admin", Permission.ENTER_BACKEND).build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/permissions")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }

    @Test
    void testGetPermissionsDescriptions() throws Exception {

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/permissions")
                        .header("Authorization", "Bearer " + accessToken));
        result
                .andDo(resultPrint())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.length()", is(12)))
                .andExpect(jsonPath("$.payload[0].code", is("editContents")))
                .andExpect(jsonPath("$.payload[0].descr", is("Content Editing")))
                .andExpect(jsonPath("$.payload[1].code", is("editUserProfile")))
                .andExpect(jsonPath("$.payload[1].descr", is("User Profile Editing")))
                .andExpect(jsonPath("$.payload[2].code", is("editUsers")))
                .andExpect(jsonPath("$.payload[2].descr", is("User Management")))
                .andExpect(jsonPath("$.payload[3].code", is("enterBackend")))
                .andExpect(jsonPath("$.payload[3].descr", is("Access to Administration Area")))
                .andExpect(jsonPath("$.payload[4].code", is("enterECR")))
                .andExpect(jsonPath("$.payload[4].descr", is("ECR Access Permission")))
                .andExpect(jsonPath("$.payload[5].code", is("manageCategories")))
                .andExpect(jsonPath("$.payload[5].descr", is("Operations on Categories")))
                .andExpect(jsonPath("$.payload[6].code", is("managePages")))
                .andExpect(jsonPath("$.payload[6].descr", is("Operations on Pages")))
                .andExpect(jsonPath("$.payload[7].code", is("manageResources")))
                .andExpect(jsonPath("$.payload[7].descr", is("Asset Editing")))
                .andExpect(jsonPath("$.payload[8].code", is("manageReview")))
                .andExpect(jsonPath("$.payload[8].descr", is("Review Management")))
                .andExpect(jsonPath("$.payload[9].code", is("superuser")))
                .andExpect(jsonPath("$.payload[9].descr", is("All functions")))
                .andExpect(jsonPath("$.payload[10].code", is("validateContents")))
                .andExpect(jsonPath("$.payload[10].descr", is("Content Supervision")))
                .andExpect(jsonPath("$.payload[11].code", is("viewUsers")))
                .andExpect(jsonPath("$.payload[11].descr", is("View Users and Profiles")));
    }

}

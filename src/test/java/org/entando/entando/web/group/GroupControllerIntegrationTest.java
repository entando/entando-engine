/*
 * Copyright 2018-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.web.group;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupManager;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.services.group.GroupTestHelper;
import org.entando.entando.aps.system.services.group.IGroupService;
import org.entando.entando.aps.system.services.group.model.GroupDto;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.MockMvcHelper;
import org.entando.entando.web.analysis.AnalysisControllerDiffAnalysisEngineTestsStubs;
import org.entando.entando.web.group.model.GroupRequest;
import org.entando.entando.web.group.validator.GroupValidator;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class GroupControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private IGroupService groupService;

    @Autowired
    private IGroupManager groupManager;

    private MockMvcHelper mockMvcHelper;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        this.mockMvcHelper = new MockMvcHelper(mockMvc, mockOAuthInterceptor(user));
    }

    @Test
    void testGetGroupsPagination() throws Exception {
        List<Group> testGroups = new ArrayList<>();
        try {
            for (int i = 0; i < 25; i++) {
                String x = ("tmp_" + i);
                Group group = new Group();
                group.setDescription(x);
                group.setName(x);
                testGroups.add(group);
                this.groupManager.addGroup(group);
            }

            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            ResultActions result = mockMvc.perform(
                    get("/groups")
                            .param("pageSize", "5")
                            .header("Authorization", "Bearer " + accessToken));

            result.andExpect(status().isOk());

            result.andExpect(jsonPath("$.metaData.pageSize", is(5)));
            result.andExpect(jsonPath("$.metaData.totalItems", is(31)));
            result.andExpect(jsonPath("$.metaData.page", is(1)));
            result.andExpect(jsonPath("$.metaData.lastPage", is(7)));
            result.andExpect(jsonPath("$.payload[0].code", is("administrators")));

            //-------------
            result = mockMvc.perform(
                    get("/groups")
                            .param("pageSize", "5")
                            .param("page", "1")
                            .header("Authorization", "Bearer " + accessToken));

            result.andExpect(status().isOk());

            result.andExpect(jsonPath("$.metaData.pageSize", is(5)));
            result.andExpect(jsonPath("$.metaData.totalItems", is(31)));
            result.andExpect(jsonPath("$.metaData.page", is(1)));
            result.andExpect(jsonPath("$.metaData.lastPage", is(7)));
            result.andExpect(jsonPath("$.payload[0].code", is("administrators")));

            //-------------
            result = mockMvc.perform(
                    get("/groups")
                            .param("pageSize", "5")
                            .param("page", "7")
                            .header("Authorization", "Bearer " + accessToken));

            result.andExpect(status().isOk());

            result.andExpect(jsonPath("$.metaData.pageSize", is(5)));
            result.andExpect(jsonPath("$.metaData.totalItems", is(31)));
            result.andExpect(jsonPath("$.metaData.page", is(7)));
            result.andExpect(jsonPath("$.metaData.lastPage", is(7)));
            result.andExpect(jsonPath("$.payload[0].code", is("tmp_9")));

            //-------------
            result = mockMvc.perform(
                    get("/groups")
                            .param("pageSize", "0")
                            .param("page", "7")
                            .header("Authorization", "Bearer " + accessToken));

            result.andExpect(status().isNotFound());

            //-------------
            result = mockMvc.perform(
                    get("/groups")
                            .param("pageSize", "7")
                            .param("page", "0")
                            .header("Authorization", "Bearer " + accessToken));

            result.andExpect(status().isBadRequest());

            //-------------
            result = mockMvc.perform(
                    get("/groups")
                            .param("pageSize", "1")
                            .param("page", "7")
                            .header("Authorization", "Bearer " + accessToken));

            result.andExpect(status().isOk());

            result.andExpect(jsonPath("$.metaData.pageSize", is(1)));
            result.andExpect(jsonPath("$.metaData.totalItems", is(31)));
            result.andExpect(jsonPath("$.metaData.page", is(7)));
            result.andExpect(jsonPath("$.metaData.lastPage", is(31)));
            result.andExpect(jsonPath("$.payload[0].code", is("tmp_0")));

        } finally {
            for (Group group : testGroups) {
                this.groupManager.removeGroup(group);
            }
        }
    }

    @Test
    void testGetGroupsSort() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        ResultActions result = mockMvc.perform(
                get("/groups").param("page", "0")
                        .param("direction", "DESC")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isBadRequest());

        result = mockMvc.perform(
                get("/groups").param("page", "1")
                        .param("direction", "DESC")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload.[0].code", is("management")));

        result = mockMvc.perform(
                get("/groups").param("page", "1")
                        .param("pageSize", "4")
                        .param("direction", "ASC")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(resultPrint());
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload[0].code", is("administrators")));

    }


    @Test
    void testGetInvalidGroup() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        GroupDto group = this.groupService.getGroup(Group.FREE_GROUP_NAME);
        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setCode(group.getCode());
        groupRequest.setName(group.getName());

        ResultActions result = mockMvc.perform(
                get("/groups/{code}", "invalid_code")
                        .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.errors[0].code", is(GroupValidator.ERRCODE_GROUP_NOT_FOUND)));

    }

    @Test
    void testUpdateInvalidGroup() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setCode("invalid");
        groupRequest.setName("invalid");

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(groupRequest);

        ResultActions result = mockMvc.perform(
                put("/groups/{code}", groupRequest.getCode())
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.errors[0].code", is(GroupValidator.ERRCODE_GROUP_NOT_FOUND)));

    }

    @Test
    void testGetGroupDetails() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc.perform(
                get("/groups/{code}", Group.FREE_GROUP_NAME)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload.references.length()", is(3)));
        String[] managers = "PageManager,WidgetTypeManager,AuthorizationManager".split(",");
        for (String managerName : managers) {
            result = mockMvc.perform(
                    get("/groups/{code}/references/{manager}", Group.FREE_GROUP_NAME, managerName)
                            .param("page", "1").param("pageSize", "3")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken));
        }
    }

    @Test
    void testGetGroupDetailsWithEnterBackendPermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, Permission.ENTER_BACKEND, Permission.ENTER_BACKEND)
                .build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc.perform(
                get("/groups/{code}", Group.FREE_GROUP_NAME)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }

    @Test
    void shouldTestGetGroupUsage() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.ADMINS_GROUP_NAME, Permission.SUPERUSER, Permission.SUPERUSER)
                .build();
        String accessToken = mockOAuthInterceptor(user);
        String code = "coach";
        mockMvc.perform(get("/groups/{code}/usage", code)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.type", is(GroupController.COMPONENT_ID)))
                .andExpect(jsonPath("$.payload.code", is(code)))
                .andExpect(jsonPath("$.payload.usage", is(6)))
                .andReturn();
    }

    @Test
    void testParamSize() throws EntException, Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setCode(StringUtils.repeat("a", 21));
        groupRequest.setName(StringUtils.repeat("a", 51));

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(groupRequest);

        ResultActions result = mockMvc.perform(
                post("/groups")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isBadRequest());

    }

    @Test
    void testGetGroupsWithBackendPermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, Permission.ENTER_BACKEND, Permission.ENTER_BACKEND)
                .build();
        String accessToken = mockOAuthInterceptor(user);

        mockMvc.perform(get("/groups")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }


    @Test
    void addExistingGroupShouldReturn409() throws Exception {

        GroupRequest groupRequest = GroupTestHelper.stubTestGroupRequest();

        try {
            mockMvcHelper.postMockMvc("/groups", groupRequest).andExpect(status().is2xxSuccessful());
            mockMvcHelper.postMockMvc("/groups", groupRequest).andExpect(status().isConflict());
        } finally {
            mockMvcHelper.deleteMockMvc("/groups/" + groupRequest.getCode(), groupRequest).andExpect(status().is2xxSuccessful());
        }
    }

    @Test
    void addExistingGroupWithDifferentNameShouldReturn409() throws Exception {

        GroupRequest groupRequest = GroupTestHelper.stubTestGroupRequest();

        try {
            mockMvcHelper.postMockMvc("/groups", groupRequest).andExpect(status().is2xxSuccessful());

            // try adding new group with different name
            GroupRequest groupRequest2 = GroupTestHelper.stubTestGroupRequest();
            groupRequest2.setName("new_name");

            mockMvcHelper.postMockMvc("/groups", groupRequest2).andExpect(status().isConflict());

        } finally {
            mockMvcHelper.deleteMockMvc("/groups/" + groupRequest.getCode(), groupRequest).andExpect(status().is2xxSuccessful());
        }

    }

    @Test
    void testComponentExistenceAnalysis() throws Exception {

        AnalysisControllerDiffAnalysisEngineTestsStubs.testComponentEngineAnalysisResult(
                AnalysisControllerDiffAnalysisEngineTestsStubs.COMPONENT_GROUPS,
                "free",
                AnalysisControllerDiffAnalysisEngineTestsStubs.STATUS_DIFF,
                new ContextOfControllerTests(mockMvc, mapper)
        );

        // should return NEW for NON existing component
        AnalysisControllerDiffAnalysisEngineTestsStubs.testComponentEngineAnalysisResult(
                AnalysisControllerDiffAnalysisEngineTestsStubs.COMPONENT_GROUPS,
                "AN_NONEXISTENT_CODE",
                AnalysisControllerDiffAnalysisEngineTestsStubs.STATUS_NEW,
                new ContextOfControllerTests(mockMvc, mapper)
        );
    }

}

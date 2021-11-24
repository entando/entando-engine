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
package org.entando.entando.web.guifragment;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.services.user.UserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.entando.entando.aps.system.services.guifragment.GuiFragment;
import org.entando.entando.aps.system.services.guifragment.IGuiFragmentManager;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.analysis.AnalysisControllerDiffAnalysisEngineTestsStubs;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.guifragment.model.GuiFragmentRequestBody;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

class GuiFragmentControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private static final String CODE = "code";
    private static final String TEST_CODE = "test-code";
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private IGuiFragmentManager guiFragmentManager;

    @BeforeEach
    public void init() throws EntException {
        guiFragmentManager.deleteGuiFragment(CODE);
        guiFragmentManager.deleteGuiFragment(TEST_CODE);
    }

    @Test
    void testGetFragments_1() throws Exception {
        String accessToken = getAccessToken();
        ResultActions result = mockMvc
                .perform(get("/fragments")
                        .header("Authorization", "Bearer " + accessToken));
        result.andDo(resultPrint());
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(1)));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.metaData.page", is(1)));
        result.andExpect(jsonPath("$.metaData.pageSize", is(100)));
        result.andExpect(jsonPath("$.metaData.totalItems", is(1)));

        result.andExpect(jsonPath("$.payload[0].widgetType.code", is("login_form")));
        result.andExpect(jsonPath("$.payload[0].widgetType.title", is("Widget di Login")));

        RestListRequest restListReq = new RestListRequest();
        restListReq.setPage(1);
        restListReq.setPageSize(4);
        testCors("/fragments");
    }

    @Test
    void testGetFragments_2() throws Exception {
        String accessToken = getAccessToken();
        ResultActions result = mockMvc.perform(
                get("/fragments").param("page", "1")
                        .param("pageSize", "4")
                        .param("filter[0].attribute", CODE)
                        .param("filter[0].value", "gin")
                        .header("Authorization", "Bearer " + accessToken)
        );
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(1)));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.metaData.page", is(1)));
        result.andExpect(jsonPath("$.metaData.pageSize", is(4)));
        result.andExpect(jsonPath("$.metaData.totalItems", is(1)));

        result.andExpect(jsonPath("$.payload[0].widgetType.code", is("login_form")));
        result.andExpect(jsonPath("$.payload[0].widgetType.title", is("Widget di Login")));

        result = mockMvc.perform(
                get("/fragments").param("page", "1")
                        .param("pageSize", "10")
                        .param("filter[0].attribute", CODE)
                        .param("filter[0].value", "test")
                        .header("Authorization", "Bearer " + accessToken)
        );
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.metaData.page", is(1)));
        result.andExpect(jsonPath("$.metaData.pageSize", is(10)));
        result.andExpect(jsonPath("$.metaData.totalItems", is(0)));
    }

    @Test
    void testGetFragments_3() throws Exception {
        String accessToken = getAccessToken();
        ResultActions result = mockMvc.perform(
                get("/fragments").param("page", "1")
                        .param("pageSize", "4")
                        .param("filter[0].attribute", "widgetType.code")
                        .param("filter[0].value", "gin")
                        .header("Authorization", "Bearer " + accessToken)
        );
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(1)));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.metaData.page", is(1)));
        result.andExpect(jsonPath("$.metaData.pageSize", is(4)));
        result.andExpect(jsonPath("$.metaData.totalItems", is(1)));

        result.andExpect(jsonPath("$.payload[0].widgetType.code", is("login_form")));
        result.andExpect(jsonPath("$.payload[0].widgetType.title", is("Widget di Login")));

        result = mockMvc.perform(
                get("/fragments").param("page", "1")
                        .param("pageSize", "10")
                        .param("filter[0].attribute", "widgetType.code")
                        .param("filter[0].value", "test")
                        .header("Authorization", "Bearer " + accessToken)
        );
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.metaData.page", is(1)));
        result.andExpect(jsonPath("$.metaData.pageSize", is(10)));
        result.andExpect(jsonPath("$.metaData.totalItems", is(0)));
    }

    @Test
    void testGetFragmentUsage() throws Exception {
        String accessToken = getAccessToken();
        String code = "login_form";

        mockMvc.perform(get("/fragments/{code}/usage".replace("{code}", code))
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(resultPrint())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors", Matchers.hasSize(0)))
                .andExpect(jsonPath("$.payload.type", is(GuiFragmentController.COMPONENT_ID)))
                .andExpect(jsonPath("$.payload.code", is(code)))
                .andExpect(jsonPath("$.payload.usage", is(0)))
                .andReturn();
    }

    @Test
    void testComponentExistenceAnalysis() throws Exception {
        // should return DIFF for existing component
        AnalysisControllerDiffAnalysisEngineTestsStubs.testComponentEngineAnalysisResult(
                AnalysisControllerDiffAnalysisEngineTestsStubs.COMPONENT_FRAGMENTS,
                "login_form",
                AnalysisControllerDiffAnalysisEngineTestsStubs.STATUS_DIFF,
                new ContextOfControllerTests(mockMvc, mapper)
        );

        // should return NEW for NON existing component
        AnalysisControllerDiffAnalysisEngineTestsStubs.testComponentEngineAnalysisResult(
                AnalysisControllerDiffAnalysisEngineTestsStubs.COMPONENT_FRAGMENTS,
                "AN_NONEXISTENT_CODE",
                AnalysisControllerDiffAnalysisEngineTestsStubs.STATUS_NEW,
                new ContextOfControllerTests(mockMvc, mapper)
        );
    }

    @Test
    void shouldUpdateFragmentWithoutCodeInBody() throws Exception {
        String accessToken = getAccessToken();
        GuiFragmentRequestBody requestBody = new GuiFragmentRequestBody();
        requestBody.setCode(CODE);
        requestBody.setGuiCode("<div>code</div>");

        mockMvc.perform(post("/fragments")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestBody)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        requestBody.setCode(null);
        requestBody.setGuiCode("<div>change</div>");
        mockMvc.perform(put("/fragments/" + CODE)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestBody)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotValidateCodeFormatForUpdate() throws Exception {
        String accessToken = getAccessToken();
        GuiFragment guiFragment = new GuiFragment();
        guiFragment.setCode(TEST_CODE);
        guiFragment.setGui("<div>test</div>");
        guiFragmentManager.addGuiFragment(guiFragment);

        GuiFragmentRequestBody requestBody = new GuiFragmentRequestBody();
        requestBody.setCode(TEST_CODE);
        requestBody.setGuiCode("<div>code</div>");
        mockMvc.perform(put("/fragments/" + TEST_CODE)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestBody)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    private String getAccessToken() {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .grantedToRoleAdmin().build();
        return mockOAuthInterceptor(user);
    }
}

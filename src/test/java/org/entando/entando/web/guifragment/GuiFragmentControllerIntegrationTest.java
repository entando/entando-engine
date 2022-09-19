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
import com.agiletec.aps.util.ApsProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.entando.entando.aps.system.services.guifragment.GuiFragment;
import org.entando.entando.aps.system.services.guifragment.IGuiFragmentManager;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
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

    @Autowired
    private IWidgetTypeManager widgetTypeManager;

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
    void testGetFragments_4() throws Exception {
        String accessToken = getAccessToken();

        String widgetTypeCode1 = "testWidgetType1";
        String widgetTypeCode2 = "testWidgetType";
        String guiFragmentCode1 = "test_search_1";
        String guiFragmentCode2 = "test_search_2";

        String widgetCategory = "test";
        ApsProperties titles = new ApsProperties();
        titles.put("it", "Titolo in ITA");
        titles.put("en", "Title in ENG");
        ApsProperties config = new ApsProperties();

        WidgetType widgetType1 = new WidgetType();
        widgetType1.setCode(widgetTypeCode1);
        widgetType1.setTitles(titles);
        widgetType1.setWidgetCategory(widgetCategory);

        WidgetType widgetType2 = new WidgetType();
        widgetType2.setCode(widgetTypeCode2);
        widgetType2.setTitles(titles);
        widgetType2.setWidgetCategory(widgetCategory);

        widgetTypeManager.addWidgetType(widgetType1);
        widgetTypeManager.addWidgetType(widgetType2);

        GuiFragment guiFragment1 = new GuiFragment();
        guiFragment1.setCode(guiFragmentCode1);
        guiFragment1.setWidgetTypeCode(widgetTypeCode1);
        guiFragment1.setGui("<div>test</div>");

        GuiFragment guiFragment2 = new GuiFragment();
        guiFragment2.setCode(guiFragmentCode2);
        guiFragment2.setWidgetTypeCode(widgetTypeCode2);
        guiFragment2.setGui("<div>test</div>");

        guiFragmentManager.addGuiFragment(guiFragment1);
        guiFragmentManager.addGuiFragment(guiFragment2);

        try {

            ResultActions result = mockMvc.perform(
                    get("/fragments").param("page", "1")
                            .param("pageSize", "4")
                            .param("filter[0].attribute", "widgetType.code")
                            .param("filter[0].operator", "eq")
                            .param("filter[0].value", widgetTypeCode1)
                            .header("Authorization", "Bearer " + accessToken)
            );
            result.andExpect(status().isOk());
            result.andExpect(jsonPath("$.payload", Matchers.hasSize(1)));
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.metaData.page", is(1)));
            result.andExpect(jsonPath("$.metaData.totalItems", is(1)));
            result.andExpect(jsonPath("$.payload[0].widgetType.code", is(widgetTypeCode1)));

            result = mockMvc.perform(
                    get("/fragments").param("page", "1")
                            .param("pageSize", "10")
                            .param("filter[0].attribute", "widgetType.code")
                            .param("filter[0].value", widgetTypeCode2)
                            .header("Authorization", "Bearer " + accessToken)
            );
            result.andExpect(status().isOk());
            result.andExpect(jsonPath("$.payload", Matchers.hasSize(2)));
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.metaData.page", is(1)));
            result.andExpect(jsonPath("$.metaData.totalItems", is(2)));

        } catch (Throwable t){
            throw new RuntimeException(t);
        }
        finally{
            guiFragmentManager.deleteGuiFragment(guiFragmentCode1);
            guiFragmentManager.deleteGuiFragment(guiFragmentCode2);
            widgetTypeManager.deleteWidgetType(widgetTypeCode1);
            widgetTypeManager.deleteWidgetType(widgetTypeCode2);
        }
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

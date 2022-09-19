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
package org.entando.entando.web.pagemodel;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.entando.entando.aps.system.services.pagemodel.PageModelTestUtil.validPageModelRequest;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.services.pagemodel.FrameSketch;
import com.agiletec.aps.util.FileTextReader;
import org.entando.entando.ent.exception.EntException;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import com.agiletec.aps.system.services.pagemodel.PageModelManager;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.aps.system.services.user.UserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.entando.entando.aps.system.services.pagemodel.PageModelTestUtil;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.analysis.AnalysisControllerDiffAnalysisEngineTestsStubs;
import org.entando.entando.web.pagemodel.model.PageModelConfigurationRequest;
import org.entando.entando.web.pagemodel.model.PageModelFrameReq;
import org.entando.entando.web.pagemodel.model.PageModelRequest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

class PageModelControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private static final String USERNAME = "jack_bauer";
    private static final String PASSWORD = "0x24";
    private static final String PAGE_MODEL_CODE = "testPM";
    private static final String PAGE_MODEL_WITH_DOT_CODE = "test.PM";
    private static final String NONEXISTENT_PAGE_MODEL = "nonexistentPageModel";
    private static final String PAGE_MODEL_DESCR = "descr";

    private String accessToken;

    private ObjectMapper jsonMapper = new ObjectMapper().setSerializationInclusion(NON_NULL);

    @Autowired
    private PageModelManager pageModelManager;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        this.setupAuthenticationDetails();
        this.deletePageModelsFromPreviousTests();
    }

    @Test
    void testComponentExistenceAnalysis() throws Exception {
        // should return DIFF for existing component
        AnalysisControllerDiffAnalysisEngineTestsStubs.testComponentEngineAnalysisResult(
                AnalysisControllerDiffAnalysisEngineTestsStubs.COMPONENT_PAGE_TEMPLATES,
                "home",
                AnalysisControllerDiffAnalysisEngineTestsStubs.STATUS_DIFF,
                new ContextOfControllerTests(mockMvc, jsonMapper)
        );

        // should return NEW for NON existing component
        AnalysisControllerDiffAnalysisEngineTestsStubs.testComponentEngineAnalysisResult(
                AnalysisControllerDiffAnalysisEngineTestsStubs.COMPONENT_PAGE_TEMPLATES,
                "AN_NONEXISTENT_CODE",
                AnalysisControllerDiffAnalysisEngineTestsStubs.STATUS_NEW,
                new ContextOfControllerTests(mockMvc, jsonMapper)
        );
    }

    private void setupAuthenticationDetails() {
        User user = new OAuth2TestUtils.UserBuilder(USERNAME, PASSWORD)
                .withAuthorization(Group.FREE_GROUP_NAME, Permission.MANAGE_PAGES, Permission.MANAGE_PAGES)
                .build();
        accessToken = mockOAuthInterceptor(user);
    }

    private String getAdminAuthenticationToken() {
        User admin = new OAuth2TestUtils.UserBuilder(USERNAME, PASSWORD).grantedToRoleAdmin()
                .build();
        return mockOAuthInterceptor(admin);
    }
    private String getUserAuthenticationToken() {
        User user = new OAuth2TestUtils.UserBuilder(USERNAME, PASSWORD)
                .withAuthorization(Group.FREE_GROUP_NAME, Permission.MANAGE_PAGES, Permission.MANAGE_PAGES)
                .build();
        return mockOAuthInterceptor(user);
    }


    private void deletePageModelsFromPreviousTests() throws EntException {
        pageModelManager.deletePageModel(PAGE_MODEL_CODE);
    }

    @Test
    void getAllPageModelsReturnOK() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/pageModels")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }

    @Test
    void getPageModelReturnOK() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/pageModels/{code}", "home")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload.references.length()", is(1)));
    }

    @Test
    void getPageModelsReference1() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/pageModels/{code}/references/{manager}", "home", "PageManager")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.metaData.totalItems", is(23)));
    }

    @Test
    void getPageModelsReference2() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/pageModels/{code}/references/{manager}", "service", "PageManager")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.metaData.totalItems", is(10)));

        result.andExpect(jsonPath("$.payload.size()", is(10)));
        result.andExpect(jsonPath("$.payload[0].code", is("service")));
        result.andExpect(jsonPath("$.payload[0].status", is("published")));
        result.andExpect(jsonPath("$.payload[0].onlineInstance", is(false)));
        result.andExpect(jsonPath("$.payload[0].titles.it", is("Nodo pagine di servizio")));

        result.andExpect(jsonPath("$.payload[5].code", is("service")));
        result.andExpect(jsonPath("$.payload[5].status", is("published")));
        result.andExpect(jsonPath("$.payload[5].onlineInstance", is(true)));
        result.andExpect(jsonPath("$.payload[5].titles.it", is("Nodo pagine di servizio")));
    }

    @Test
    void shouldTestGetPageModelUsage() throws Exception {
        String code = "home";
        mockMvc.perform(get("/pageModels/{code}/usage", code)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.type", is(PageModelController.COMPONENT_ID)))
                .andExpect(jsonPath("$.payload.code", is(code)))
                .andExpect(jsonPath("$.payload.usage", is(23)))
                .andReturn();
    }

    @Test
    void addRepeatedPageModelReturnConflict() throws Exception {
        String accessTokenAdmin = getAdminAuthenticationToken();

        // pageModel home always exists because it's created with DB.
        String payload = createPageModelPayload("home");
        ResultActions result = mockMvc.perform(
                post("/pageModels").content(payload)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessTokenAdmin));
        result.andExpect(status().isConflict());
    }

    @Test
    void addPageModelReturnOK() throws Exception {
        String accessTokenAdmin = getAdminAuthenticationToken();

        String payload = createPageModelPayload(PAGE_MODEL_CODE);
        ResultActions result = mockMvc.perform(
                post("/pageModels")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessTokenAdmin));
        result.andExpect(status().isOk());
    }
    @Test
    void addPageModelReturnForbiddenIfNonAdmin() throws Exception {
        String payload = createPageModelPayload(PAGE_MODEL_CODE);
        ResultActions result = mockMvc.perform(
                post("/pageModels")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isForbidden());
    }

    @Test
    void updatePageModelReturnForbiddenIfNonAdmin() throws Exception {
        String accessTokenAdmin = getAdminAuthenticationToken();

        String payload = createPageModelPayload(PAGE_MODEL_CODE);
        ResultActions result = mockMvc.perform(
                post("/pageModels")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessTokenAdmin));
        result.andExpect(status().isOk());

        String accessTokenUser = getUserAuthenticationToken();

        result = mockMvc.perform(
                put("/pageModels/{code}", PAGE_MODEL_CODE)
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessTokenUser));
        result.andExpect(status().isForbidden());
    }

    private String createPageModelPayload(String pageModelCode) throws JsonProcessingException {
        return createPageModelPayload(pageModelCode, PAGE_MODEL_DESCR);
    }

    private String createPageModelPayload(String pageModelCode, String pageModelDescr) throws JsonProcessingException {
        PageModelRequest pageModelRequest = validPageModelRequest();
        pageModelRequest.setCode(pageModelCode);
        pageModelRequest.setDescr(pageModelDescr);
        PageModelConfigurationRequest configuration = new PageModelConfigurationRequest();
        List<PageModelFrameReq> frames = new ArrayList<>();

        FrameSketch frameSkatch = new FrameSketch();
        frameSkatch.setCoords(0, 1, 2, 1);

        final PageModelFrameReq pageModelFrameReq = new PageModelFrameReq(0, "Position 0");
        pageModelFrameReq.setSketch(frameSkatch);
        frames.add(pageModelFrameReq);
        configuration.setFrames(frames);
        pageModelRequest.setConfiguration(configuration);
        return createJson(pageModelRequest);
    }

    private String createJson(PageModelRequest pageModelRequest) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(pageModelRequest);
    }

    @Test
    void getNonexistentPageModelReturnNotFound() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/pageModels/{code}", NONEXISTENT_PAGE_MODEL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isNotFound());
    }

    @Test
    void deletePageModelReturnOK() throws Exception {
        String accessTokenAdmin = getAdminAuthenticationToken();
        PageModel pageModel = new PageModel();
        pageModel.setCode(PAGE_MODEL_CODE);
        pageModel.setDescription(PAGE_MODEL_CODE);
        this.pageModelManager.addPageModel(pageModel);
        ResultActions result = mockMvc.perform(
                delete("/pageModels/{code}", PAGE_MODEL_CODE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessTokenAdmin));
        result.andExpect(status().isOk());
    }

    @Test
    void deletePageModelReturnForbiddenIfNotAdmin() throws Exception {
        PageModel pageModel = new PageModel();
        pageModel.setCode(PAGE_MODEL_CODE);
        pageModel.setDescription(PAGE_MODEL_CODE);
        this.pageModelManager.addPageModel(pageModel);
        ResultActions result = mockMvc.perform(
                delete("/pageModels/{code}", PAGE_MODEL_CODE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isForbidden());
    }

    @Test
    void deletePageModelNonexistentCodeReturnOK() throws Exception {
        String accessTokenAdmin = getAdminAuthenticationToken();

        ResultActions result = mockMvc.perform(
                delete("/pageModels/{code}", NONEXISTENT_PAGE_MODEL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessTokenAdmin));
        result.andExpect(status().isOk());
    }

    @Test
    void addPageModelWithDotReturnOK() throws Exception {
        String accessTokenAdmin = getAdminAuthenticationToken();

        try {
            PageModelRequest pageModelRequest = PageModelTestUtil.validPageModelRequest();
            pageModelRequest.setCode(PAGE_MODEL_WITH_DOT_CODE);
            ResultActions result = mockMvc.perform(
                    post("/pageModels")
                            .content(createJson(pageModelRequest))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.code", is(PAGE_MODEL_WITH_DOT_CODE)))
                    .andExpect(jsonPath("$.payload.descr", is("description")))
                    .andExpect(jsonPath("$.payload.configuration.frames[0].defaultWidget.code", is("leftmenu")))
                    .andExpect(jsonPath("$.payload.configuration.frames[0].defaultWidget.properties.navSpec", is("code(homepage).subtree(5)")))
                    .andExpect(jsonPath("$.payload.configuration.frames[1].defaultWidget", CoreMatchers.nullValue()));
            PageModel pageModel = this.pageModelManager.getPageModel(PAGE_MODEL_WITH_DOT_CODE);
            Assertions.assertNotNull(pageModel);
            Assertions.assertEquals(3, pageModel.getFrames().length);
            Assertions.assertEquals(3, pageModel.getFramesConfig().length);
            Assertions.assertNotNull(pageModel.getFramesConfig()[0].getDefaultWidget());
            Assertions.assertEquals("leftmenu", pageModel.getFramesConfig()[0].getDefaultWidget().getType().getCode());
            Assertions.assertEquals(1, pageModel.getFramesConfig()[0].getDefaultWidget().getConfig().size());
            Assertions.assertEquals("code(homepage).subtree(5)", pageModel.getFramesConfig()[0].getDefaultWidget().getConfig().getProperty("navSpec"));
            pageModelRequest.setDescr("description2");
            result = mockMvc.perform(
                    put("/pageModels/{code}", PAGE_MODEL_WITH_DOT_CODE)
                            .content(createJson(pageModelRequest))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.code", is(PAGE_MODEL_WITH_DOT_CODE)))
                    .andExpect(jsonPath("$.payload.descr", is("description2")));
            result = mockMvc.perform(
                    get("/pageModels/{code}", PAGE_MODEL_WITH_DOT_CODE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.code", is(PAGE_MODEL_WITH_DOT_CODE)))
                    .andExpect(jsonPath("$.payload.descr", is("description2")));
        } finally {
            ResultActions result = mockMvc.perform(
                    delete("/pageModels/{code}", PAGE_MODEL_WITH_DOT_CODE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andDo(resultPrint())
                    .andExpect(status().isOk());
        }
    }

    @Test
    void addPageModelWithErrors() throws Exception {
        String accessTokenAdmin = getAdminAuthenticationToken();

        try {
            PageModelRequest pageModelRequest = PageModelTestUtil.validPageModelRequest();

            FrameSketch newSketch = new FrameSketch();
            newSketch.setCoords(0, 3, 11, 3);
            final PageModelFrameReq newFrames = new PageModelFrameReq(3, "Position 3");
            newFrames.setSketch(newSketch);


            newFrames.getDefaultWidget().setCode("invalid_widget");
            pageModelRequest.getConfiguration().getFrames().add(newFrames);

            pageModelRequest.setCode(PAGE_MODEL_CODE);
            ResultActions result = mockMvc.perform(
                    post("/pageModels")
                            .content(createJson(pageModelRequest))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));

            result.andExpect(status().isBadRequest());
            result.andExpect(jsonPath("$.payload.size()", is(0)));
            result.andExpect(jsonPath("$.errors.size()", is(1)));
            result.andExpect(jsonPath("$.errors[0].code", is("6")));
            result.andExpect(jsonPath("$.metaData.size()", is(0)));
            PageModel pageModel = this.pageModelManager.getPageModel(PAGE_MODEL_CODE);
            Assertions.assertNull(pageModel);

            newFrames.getDefaultWidget().setCode("leftmenu");
            newFrames.getDefaultWidget().getProperties().put("wrongParam", "code(homepage).subtree(8)");

            result = mockMvc.perform(
                    post("/pageModels")
                            .content(createJson(pageModelRequest))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));

            result.andExpect(status().isBadRequest());
            result.andExpect(jsonPath("$.payload.size()", is(0)));
            result.andExpect(jsonPath("$.errors.size()", is(1)));
            result.andExpect(jsonPath("$.errors[0].code", is("7")));
            result.andExpect(jsonPath("$.metaData.size()", is(0)));
            pageModel = this.pageModelManager.getPageModel(PAGE_MODEL_CODE);
            Assertions.assertNull(pageModel);

            newFrames.getDefaultWidget().getProperties().remove("wrongParam");
            PageModelFrameReq newWrongFrames = new PageModelFrameReq(7, "Position 7");
            pageModelRequest.getConfiguration().getFrames().add(newWrongFrames);

            result = mockMvc.perform(
                    post("/pageModels")
                            .content(createJson(pageModelRequest))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));

            result.andExpect(status().isBadRequest());
            result.andExpect(jsonPath("$.payload.size()", is(0)));
            result.andExpect(jsonPath("$.errors.size()", is(1)));
            result.andExpect(jsonPath("$.errors[0].code", is("5")));
            result.andExpect(jsonPath("$.metaData.size()", is(0)));
            pageModel = this.pageModelManager.getPageModel(PAGE_MODEL_CODE);
            Assertions.assertNull(pageModel);

        } catch (Exception e) {
            throw e;
        } finally {
            this.pageModelManager.deletePageModel(PAGE_MODEL_CODE);
        }
    }


    @Test
    void addPageModelWithoutConfigurationReturnBadRequest() throws Exception {
        String accessTokenAdmin = getAdminAuthenticationToken();

        try {
            PageModelRequest pageModelRequest = PageModelTestUtil.validPageModelRequest();
            pageModelRequest.setConfiguration(null);

            pageModelRequest.setCode(PAGE_MODEL_CODE);
            ResultActions result = mockMvc.perform(
                    post("/pageModels")
                            .content(createJson(pageModelRequest))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());
            result.andExpect(jsonPath("$.payload.size()", is(0)));
            result.andExpect(jsonPath("$.errors.size()", is(1)));
            result.andExpect(jsonPath("$.errors[0].code", is("53")));
            result.andExpect(jsonPath("$.metaData.size()", is(0)));

        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    void updatePageModelWithErrors() throws Exception {
        String accessTokenAdmin = getAdminAuthenticationToken();

        try {

            PageModelRequest pageModelRequest = PageModelTestUtil.validPageModelRequest();
            pageModelRequest.setCode(PAGE_MODEL_CODE);
            ResultActions result = mockMvc.perform(
                    post("/pageModels")
                            .content(createJson(pageModelRequest))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken));
            result.andDo(resultPrint()).andExpect(status().isOk());

            PageModel pageModel = this.pageModelManager.getPageModel(PAGE_MODEL_CODE);
            Assertions.assertNotNull(pageModel);
            Assertions.assertEquals(3, pageModel.getFrames().length);


            PageModelFrameReq newFrames = new PageModelFrameReq(3, "Position 3");
            newFrames.getDefaultWidget().setCode("invalid_widget");

            FrameSketch newSketch = new FrameSketch();
            newSketch.setCoords(0, 3, 11, 3);

            newFrames.setSketch(newSketch);

            pageModelRequest.getConfiguration().getFrames().add(newFrames);

            result = mockMvc.perform(
                    post("/pageModels")
                            .content(createJson(pageModelRequest))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isConflict());
            result.andExpect(jsonPath("$.payload.size()", is(0)));
            result.andExpect(jsonPath("$.errors.size()", is(1)));
            result.andExpect(jsonPath("$.errors[0].code", is("2")));
            result.andExpect(jsonPath("$.metaData.size()", is(0)));
            pageModel = this.pageModelManager.getPageModel(PAGE_MODEL_CODE);
            Assertions.assertNotNull(pageModel);
            Assertions.assertEquals(3, pageModel.getFrames().length);

            result = mockMvc.perform(
                    put("/pageModels/{code}", PAGE_MODEL_CODE)
                            .content(createJson(pageModelRequest))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());
            result.andExpect(jsonPath("$.payload.size()", is(0)));
            result.andExpect(jsonPath("$.errors.size()", is(1)));
            result.andExpect(jsonPath("$.errors[0].code", is("6")));
            result.andExpect(jsonPath("$.metaData.size()", is(0)));
            pageModel = this.pageModelManager.getPageModel(PAGE_MODEL_CODE);
            Assertions.assertNotNull(pageModel);
            Assertions.assertEquals(3, pageModel.getFrames().length);

            pageModelRequest.setCode(NONEXISTENT_PAGE_MODEL);
            result = mockMvc.perform(
                    put("/pageModels/{code}", NONEXISTENT_PAGE_MODEL)
                            .content(createJson(pageModelRequest))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isNotFound());
            result.andExpect(jsonPath("$.payload.size()", is(0)));
            result.andExpect(jsonPath("$.errors.size()", is(1)));
            result.andExpect(jsonPath("$.errors[0].code", is("1")));
            result.andExpect(jsonPath("$.metaData.size()", is(0)));
            pageModel = this.pageModelManager.getPageModel(PAGE_MODEL_CODE);
            Assertions.assertNotNull(pageModel);
            Assertions.assertEquals(3, pageModel.getFrames().length);
        } catch (Exception e) {
            throw e;
        } finally {
            this.pageModelManager.deletePageModel(PAGE_MODEL_CODE);
        }
    }

    @Test
    void testGetPageModelWithAdminPermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        ResultActions result = mockMvc.perform(
                get("/pageModels/{code}", "home")
                        .header("Authorization", "Bearer " + mockOAuthInterceptor(user)));
        result.andExpect(status().isOk());
    }

    @Test
    void testGetPageModelWithoutPermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("normal_user", "0x24").build();
        ResultActions result = mockMvc.perform(
                get("/pageModels/{code}", "home")
                        .header("Authorization", "Bearer " + mockOAuthInterceptor(user)));
        result.andExpect(status().isForbidden());
    }

    @Test
    void testGetPageModelWithManagePagesPermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("normal_user", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "admin", Permission.MANAGE_PAGES).build();
        ResultActions result = mockMvc.perform(
                get("/pageModels/{code}", "home")
                        .header("Authorization", "Bearer " + mockOAuthInterceptor(user)));
        result.andExpect(status().isOk());
    }

    @Test
    void testPostPageTemplateValidations() throws Exception {
        String accessTokenAdmin = getAdminAuthenticationToken();

        try {
            // Null PageCode

            String payloadNullCode = createPageModelPayload(null);
            ResultActions result = mockMvc.perform(
                    post("/pageModels")
                            .content(payloadNullCode)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());


            // Blank PageCode

            String payloadBlankCode = createPageModelPayload("");
            result = mockMvc.perform(
                    post("/pageModels")
                            .content(payloadBlankCode)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());

            // Null Descr

            String payloadNullDescr = createPageModelPayload(PAGE_MODEL_CODE, null);
            result = mockMvc.perform(
                    post("/pageModels")
                            .content(payloadNullDescr)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());

            // Blank Descr

            String payloadBlankDescr = createPageModelPayload(PAGE_MODEL_CODE, "");
            result = mockMvc.perform(
                    post("/pageModels")
                            .content(payloadBlankDescr)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());

            // x1 with negative value

            result = mockMvc.perform(
                    post("/pageModels")
                            .content(getJsonRequest("invalid_Y1Y2_frames_1.json"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());

            // y1 with negative value

            result = mockMvc.perform(
                    post("/pageModels")
                            .content(getJsonRequest("invalid_Y1Y2_frames_2.json"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());

            // x2 with negative value

            result = mockMvc.perform(
                    post("/pageModels")
                            .content(getJsonRequest("invalid_Y1Y2_frames_3.json"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());

            // y2 with negative value

            result = mockMvc.perform(
                    post("/pageModels")
                            .content(getJsonRequest("invalid_Y1Y2_frames_4.json"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());

            // x2 < x1

            result = mockMvc.perform(
                    post("/pageModels")
                            .content(getJsonRequest("invalid_Y1Y2_frames_5.json"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());


            // y2 < y1

            result = mockMvc.perform(
                    post("/pageModels")
                            .content(getJsonRequest("invalid_Y1Y2_frames_6.json"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andDo(print()).andExpect(status().isBadRequest());


            // overlapping frames  test 1

            result = mockMvc.perform(
                    post("/pageModels")
                            .content(getJsonRequest("overlapping_frames_1.json"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());


            // overlapping frames test 2

            result = mockMvc.perform(
                    post("/pageModels")
                            .content(getJsonRequest("overlapping_frames_2.json"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());

            // post a valid template
            result = mockMvc.perform(
                    post("/pageModels")
                            .content(getJsonRequest("1_POST_valid_frames.json"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isOk());

        } catch (Exception e) {
            throw e;
        } finally {
            this.pageModelManager.deletePageModel("valid_page_model_1-2x2-1-column");
        }
    }

    @Test
    void testPutPageTemplateValidations() throws Exception {
        String accessTokenAdmin = getAdminAuthenticationToken();

        try {
            final String PUT_PAGE_TEMPLATE_CODE = "test-template";
            ResultActions result = mockMvc.perform(
                    post("/pageModels/")
                            .content(getJsonRequest("2_POST_valid_frames.json"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isOk());


            // x1 with negative value
            result = mockMvc.perform(
                    put("/pageModels/{code}", PUT_PAGE_TEMPLATE_CODE)
                            .content(getJsonRequest("invalid_Y1Y2_frames_1.json"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());
            // y1 with negative value

            result = mockMvc.perform(
                    put("/pageModels/{code}", PUT_PAGE_TEMPLATE_CODE)
                            .content(getJsonRequest("invalid_Y1Y2_frames_2.json"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());

            // x2 with negative value

            result = mockMvc.perform(
                    put("/pageModels/{code}", PUT_PAGE_TEMPLATE_CODE)
                            .content(getJsonRequest("invalid_Y1Y2_frames_3.json"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());

            // y2 with negative value

            result = mockMvc.perform(
                    put("/pageModels/{code}", PUT_PAGE_TEMPLATE_CODE)
                            .content(getJsonRequest("invalid_Y1Y2_frames_4.json"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());

            // x2 < x1

            result = mockMvc.perform(
                    put("/pageModels/{code}", PUT_PAGE_TEMPLATE_CODE)
                            .content(getJsonRequest("invalid_Y1Y2_frames_5.json"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());

            // y2 < y1

            result = mockMvc.perform(
                    put("/pageModels/{code}", PUT_PAGE_TEMPLATE_CODE)
                            .content(getJsonRequest("invalid_Y1Y2_frames_6.json"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());


            // overlapping frames test 1

            result = mockMvc.perform(
                    put("/pageModels/{code}", PUT_PAGE_TEMPLATE_CODE)
                            .content(getJsonRequest("overlapping_frames_1.json"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());


            // overlapping frames test 2

            result = mockMvc.perform(
                    put("/pageModels/{code}", PUT_PAGE_TEMPLATE_CODE)
                            .content(getJsonRequest("overlapping_frames_2.json"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessTokenAdmin));
            result.andExpect(status().isBadRequest());


        } catch (Exception e) {
            throw e;
        } finally {
            this.pageModelManager.deletePageModel("test-template");
        }

    }

    @Test
    void shouldSortWithoutApplyingFilterToTheSortingAttribute() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/pageModels?sort=pluginCode")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.metaData.totalItems", is(3)));
    }

    private String getJsonRequest(String filename) throws Exception {
        InputStream isJsonPostValid = this.getClass().getResourceAsStream(filename);
        String result = FileTextReader.getText(isJsonPostValid);
        return result;
    }
}

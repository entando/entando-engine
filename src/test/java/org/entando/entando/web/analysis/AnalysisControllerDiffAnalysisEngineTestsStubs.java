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
package org.entando.entando.web.analysis;

import com.agiletec.aps.system.services.user.UserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.entando.entando.web.AbstractControllerIntegrationTest.ContextOfControllerTests;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Stubs of test for the analysis controller differential analysis
 * <pre>
 * Please note that this class in ot an full test but only contains test code
 * meant to be called in the context of other integrations tests
 * </pre>
 */
public class AnalysisControllerDiffAnalysisEngineTestsStubs {

    public static final String STATUS_NEW = "NEW";
    public static final String STATUS_DIFF = "DIFF";

    public static final String COMPONENT_WIDGETS = "widgets";
    public static final String COMPONENT_FRAGMENTS = "fragments";
    public static final String COMPONENT_PAGES = "pages";
    public static final String COMPONENT_PAGE_TEMPLATES = "pageTemplates";
    public static final String COMPONENT_CONTENTS = "contents";
    public static final String COMPONENT_CONTENT_TEMPLATES = "contentTemplates";
    public static final String COMPONENT_CONTENT_TYPES = "contentTypes";
    public static final String COMPONENT_ASSETS = "assets";
    public static final String COMPONENT_DIRECTORIES = "directories";
    public static final String COMPONENT_RESOURCES = "resources";
    public static final String COMPONENT_CATEGORIES = "categories";
    public static final String COMPONENT_GROUPS = "groups";
    public static final String COMPONENT_LABELS = "labels";
    public static final String COMPONENT_LANGUAGES = "languages";

    /**
     * executes the request asking for analysis report of the desired component
     *
     * @param componentType
     * @param code
     * @param expectedValue
     * @param ctx
     * @throws Exception
     */
    public static void testComponentEngineAnalysisResult(
            String componentType,
            String code,
            String expectedValue,
            ContextOfControllerTests ctx
    ) throws Exception {
        ResultActions result = invokeDiffAnalysis(componentType, code, ctx, false);
        doAssert(result, componentType, code, expectedValue);
    }


    /**
     * executes the request asking for analysis report of the desired component
     *
     * @param componentType
     * @param code
     * @param expectedValue
     * @param ctx
     * @throws Exception
     */
    public static void testComponentCmsAnalysisResult(
            String componentType,
            String code,
            String expectedValue,
            ContextOfControllerTests ctx
    ) throws Exception {
        ResultActions result = invokeDiffAnalysis(componentType, code, ctx, true);
        doAssert(result, componentType, code, expectedValue);
    }


    /**
     * do assertions
     * @param result
     * @param componentType
     * @param code
     * @param expectedValue
     * @throws Exception
     */
    private static void doAssert(
            ResultActions result,
            String componentType,
            String code,
            String expectedValue) throws Exception {

        result.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.errors.length()", is(0)))
                .andExpect(jsonPath("$.payload." + componentType + ".length()", is(1)))
                .andExpect(jsonPath("$.payload." + componentType + "." + code, equalTo(expectedValue)));
    }


    /**
     * invoke the endpoint
     *
     * @param componentType
     * @param code
     * @param ctx
     * @return
     * @throws Exception
     */
    private static ResultActions invokeDiffAnalysis(String componentType, String code, ContextOfControllerTests ctx,
            boolean cmsApi)
            throws Exception {

        MockMvc mockMvc = ctx.mockMvc;
        ObjectMapper jsonMapper = ctx.jsonMapper;
        String accessToken = mkSuperuserAccessToken(ctx);

        Map<String, List<String>> request = ImmutableMap.<String, List<String>>builder()
                .putAll(
                        ImmutableMap.of(
                                componentType, ImmutableList.of(code)
                        )
                ).build();

        String path = "/analysis/"
                + (cmsApi ? "cms/" : "")
                + "components/diff";

        return mockMvc.perform(
                post(String.format(path))
                        .content(jsonMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
    }

    /**
     * create a valid access token
     *
     * @param ctx
     * @return
     */
    private static String mkSuperuserAccessToken(ContextOfControllerTests ctx) {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .grantedToRoleAdmin().build();
        return OAuth2TestUtils.mockOAuthInterceptor(
                ctx.apiOAuth2TokenManager,
                ctx.authenticationProviderManager,
                ctx.authorizationManager, user);
    }
}

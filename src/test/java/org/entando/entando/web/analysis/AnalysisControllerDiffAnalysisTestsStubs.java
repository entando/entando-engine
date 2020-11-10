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
public class AnalysisControllerDiffAnalysisTestsStubs {

    public static void testDiffResult(
            String code,
            ContextOfControllerTests ctx
    ) throws Exception {
        //-
        ResultActions result = invokeDiffAnalysis(code, ctx.mockMvc, ctx.jsonMapper, mkSuperuserAccessToken(ctx));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.errors.length()", is(0)));
        result.andExpect(jsonPath("$.payload.pageTemplates.length()", is(1)));
        result.andExpect(jsonPath("$.payload.pageTemplates." + code, equalTo("DIFF")));
    }

    public static void testNotFoundResult(
            String code,
            ContextOfControllerTests ctx
    ) throws Exception {
        //-
        //{"payload":{"pageTemplates":{"AN_NONEXISTENT_CODE":"DIFF"}},"metaData":{},"errors":[]}
        ResultActions result = invokeDiffAnalysis(code, ctx.mockMvc, ctx.jsonMapper, mkSuperuserAccessToken(ctx));
        result.andDo(print()).andExpect(status().isOk());
        result.andExpect(jsonPath("$.errors.length()", is(0)));
        result.andExpect(jsonPath("$.payload.pageTemplates.length()", is(1)));
        result.andExpect(jsonPath("$.payload.pageTemplates." + code, equalTo("NEW")));
    }

    private static ResultActions invokeDiffAnalysis(String code, MockMvc mockMvc, ObjectMapper jsonMapper, String accessToken) throws Exception {
        Map<String, List<String>> request = ImmutableMap.<String, List<String>>builder()
                .putAll(
                        ImmutableMap.of(
                                "pageTemplates", ImmutableList.of(code)
                        )
                ).build();

        return mockMvc.perform(
                post("/analysis/components/diff")
                        .content(jsonMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
    }

    private static String mkSuperuserAccessToken(ContextOfControllerTests ctx) {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .grantedToRoleAdmin().build();
        return OAuth2TestUtils.mockOAuthInterceptor(
                ctx.apiOAuth2TokenManager,
                ctx.authenticationProviderManager,
                ctx.authorizationManager, user);
    }
}

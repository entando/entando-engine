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
package org.entando.entando.web.language;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.entando.entando.aps.system.services.language.ILanguageService;
import org.entando.entando.aps.system.services.language.LanguageDto;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.analysis.AnalysisControllerDiffAnalysisEngineTestsStubs;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LanguageControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private ILanguageService languageService;

    @Autowired
    private ILangManager langManager;

    @Autowired
    @InjectMocks
    private LanguageController controller;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    void testGetLangsWithBackendPermission() throws Exception {
        UserDetails user = createUserWithPermission();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc.perform(get("/languages")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        testCors("/languages");
        System.out.println(result.andReturn().getResponse().getContentAsString());
    }

    @Test
    void testGetLangs() throws Exception {
        UserDetails user = createUserWithPermission();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/languages")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        testCors("/languages");
        System.out.println(result.andReturn().getResponse().getContentAsString());
    }

    @Test
    void testGetLangsByActive() throws Exception {
        UserDetails user = createUserWithPermission();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/languages")
                        .param("filter[0].attribute", "isActive")
                        .param("filter[0].value", "true")
                        .header("Authorization", "Bearer " + accessToken));
        System.out.println(result.andReturn().getResponse().getContentAsString());
        result.andExpect(status().isOk());

    }

    @Test
    void testGetLangValid() throws Exception {
        UserDetails user = createUserWithPermission();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/languages/{code}", new Object[]{"en"})
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());

    }

    @Test
    void testGetLangValidAssignable() throws Exception {
        UserDetails user = createUserWithPermission();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/languages/{code}", new Object[]{"de"})
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }

    @Test
    void testGetLangInvalid() throws Exception {
        UserDetails user = createUserWithPermission();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/languages/{code}", new Object[]{"xx"})
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isNotFound());
    }

    @Test
    void testActivateDeactivateLang() throws Exception {
        String langCode = "de";
        try {
            LanguageDto lang = this.languageService.getLanguage(langCode);
            assertThat(lang, is(not(nullValue())));
            assertThat(lang.isActive(), is(false));

            UserDetails user = createAdminUser();
            String accessToken = mockOAuthInterceptor(user);

            String payload = "{\"isActive\": true}";
            ResultActions result = mockMvc
                    .perform(put("/languages/{code}", new Object[]{langCode})
                            .content(payload)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken));
            result.andExpect(status().isOk());
            LanguageDto updatedLang = this.languageService.getLanguage(langCode);
            assertThat(updatedLang, is(not(nullValue())));
            assertThat(updatedLang.isActive(), is(true));

            //--
            payload = "{\"isActive\": false}";
            result = mockMvc
                    .perform(put("/languages/{code}", new Object[]{langCode})
                            .content(payload)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken));
            result.andExpect(status().isOk());

            updatedLang = this.languageService.getLanguage(langCode);
            assertThat(updatedLang, is(not(nullValue())));
            assertThat(updatedLang.isActive(), is(false));

        } finally {
            this.languageService.updateLanguage(langCode, false);
        }
    }

    @Test
    void testDeactivateDefaultLang() throws Exception {
        String langCode = this.langManager.getDefaultLang().getCode();
        try {
            LanguageDto lang = this.languageService.getLanguage(langCode);
            assertThat(lang, is(not(nullValue())));
            assertThat(lang.isActive(), is(true));

            UserDetails user = createAdminUser();
            String accessToken = mockOAuthInterceptor(user);

            String payload = "{\"isActive\": true}";
            ResultActions result = mockMvc
                    .perform(put("/languages/{code}", new Object[]{langCode})
                            .content(payload)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken));
            result.andExpect(status().isOk());
            LanguageDto updatedLang = this.languageService.getLanguage(langCode);
            assertThat(updatedLang, is(not(nullValue())));
            assertThat(updatedLang.isActive(), is(true));

            //--
            payload = "{\"isActive\": false}";
            result = mockMvc
                    .perform(put("/languages/{code}", new Object[]{langCode})
                            .content(payload)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + accessToken));
            result.andExpect(status().isConflict());
            result.andExpect(jsonPath("$.errors[0].code", is("1")));

            updatedLang = this.languageService.getLanguage(langCode);
            assertThat(updatedLang, is(not(nullValue())));
            assertThat(updatedLang.isActive(), is(true));

        } finally {
            this.languageService.updateLanguage(langCode, true);
        }
    }

    @Test
    void testDeactivateDefaultLangUnexistingCode() throws Exception {
        String langCode = "xx";

        UserDetails user = createAdminUser();
        String accessToken = mockOAuthInterceptor(user);

        String payload = "{\"isActive\": true}";
        ResultActions result = mockMvc
                .perform(put("/languages/{code}", new Object[]{langCode})
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isNotFound());

        result.andExpect(jsonPath("$.errors[0].code", is("2")));

    }

    @Test
    void testComponentExistenceAnalysis() throws Exception {

        AnalysisControllerDiffAnalysisEngineTestsStubs.testComponentEngineAnalysisResult(
                AnalysisControllerDiffAnalysisEngineTestsStubs.COMPONENT_LANGUAGES,
                "en",
                AnalysisControllerDiffAnalysisEngineTestsStubs.STATUS_DIFF,
                new ContextOfControllerTests(mockMvc, mapper)
        );

        // should return NEW for NON existing component
        AnalysisControllerDiffAnalysisEngineTestsStubs.testComponentEngineAnalysisResult(
                AnalysisControllerDiffAnalysisEngineTestsStubs.COMPONENT_LANGUAGES,
                "AN_NONEXISTENT_CODE",
                AnalysisControllerDiffAnalysisEngineTestsStubs.STATUS_NEW,
                new ContextOfControllerTests(mockMvc, mapper)
        );
    }


    private UserDetails createUserWithPermission() {
        return new OAuth2TestUtils.UserBuilder("user", "0x01")
                .withAuthorization(Group.FREE_GROUP_NAME, Permission.ENTER_BACKEND, Permission.ENTER_BACKEND)
                .build();
    }

    private UserDetails createAdminUser() {
        return new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
    }

}

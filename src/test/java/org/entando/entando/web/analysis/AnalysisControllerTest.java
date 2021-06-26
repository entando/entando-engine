/*
 * Copyright 2020-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
import org.entando.entando.aps.system.services.category.CategoryService;
import org.entando.entando.aps.system.services.category.model.CategoryDto;
import org.entando.entando.aps.system.services.group.GroupService;
import org.entando.entando.aps.system.services.group.model.GroupDto;
import org.entando.entando.aps.system.services.guifragment.GuiFragmentService;
import org.entando.entando.aps.system.services.guifragment.model.GuiFragmentDto;
import org.entando.entando.aps.system.services.label.LabelService;
import org.entando.entando.aps.system.services.label.model.LabelDto;
import org.entando.entando.aps.system.services.language.LanguageDto;
import org.entando.entando.aps.system.services.language.LanguageService;
import org.entando.entando.aps.system.services.page.PageService;
import org.entando.entando.aps.system.services.page.model.PageDto;
import org.entando.entando.aps.system.services.pagemodel.PageModelService;
import org.entando.entando.aps.system.services.pagemodel.model.PageModelDto;
import org.entando.entando.aps.system.services.storage.IFileBrowserService;
import org.entando.entando.aps.system.services.widgettype.WidgetService;
import org.entando.entando.aps.system.services.widgettype.model.WidgetDto;
import org.entando.entando.web.AbstractControllerTest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnalysisControllerTest extends AbstractControllerTest {

    @Test
    void testRunAnalysis() throws Exception {
        String accessToken = mockAccessToken();

        Map<String, List<String>> request = ImmutableMap.<String, List<String>>builder()
                .putAll(
                        ImmutableMap.of(
                                "widgets", ImmutableList.of("1", "2", "3"),
                                "fragments", ImmutableList.of("1", "2", "3"),
                                "pages", ImmutableList.of("1", "2", "3"),
                                "pageTemplates", ImmutableList.of("1", "2", "3")
                        )
                ).putAll(
                        ImmutableMap.of(
                                "categories", ImmutableList.of("1", "2", "3"),
                                "groups", ImmutableList.of("1", "2", "3"),
                                "labels", ImmutableList.of("1", "2", "3"),
                                "languages", ImmutableList.of("1", "2", "3"),
                                "resources", ImmutableList.of("1", "2", "3")
                        )
                ).build();

        // WIDGETS
        Mockito.doReturn(true).when(widgetService).exists("1");
        Mockito.doReturn(true).when(widgetService).exists("2");

        // GUI FRAGMENT
        Mockito.doReturn(true).when(guiFragmentService).exists("1");
        Mockito.doReturn(true).when(guiFragmentService).exists("2");

        // PAGES
        Mockito.doReturn(true).when(pageService).exists("1");
        Mockito.doReturn(true).when(pageService).exists("2");

        // PAGES MODELS
        Mockito.doReturn(true).when(pageModelService).exists("1");
        Mockito.doReturn(true).when(pageModelService).exists("2");

        // CATEGORIES
        Mockito.doReturn(true).when(categoryService).exists("1");
        Mockito.doReturn(true).when(categoryService).exists("2");

        // GROUPS
        Mockito.doReturn(true).when(groupService).exists("1");
        Mockito.doReturn(true).when(groupService).exists("2");

        // LABELS
        Mockito.doReturn(true).when(labelService).exists("1");
        Mockito.doReturn(true).when(labelService).exists("2");

        // LANGUAGE
        Mockito.doReturn(true).when(languageService).exists("1");
        Mockito.doReturn(true).when(languageService).exists("2");

        // RESOURCES (FILE SYSTEM OBJECTS)
        Mockito.doReturn(true).when(fileBrowserService).exists("1");
        Mockito.doReturn(true).when(fileBrowserService).exists("2");

        ResultActions result = mockMvc.perform(
                post("/analysis/components/diff")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isOk());
        result.andDo(MockMvcResultHandlers.print());
        result.andExpect(content().contentType("application/json"));
        checkByComponentType(result, "widgets");
        checkByComponentType(result, "fragments");
        checkByComponentType(result, "pages");
        checkByComponentType(result, "pageTemplates");
        checkByComponentType(result, "categories");
        checkByComponentType(result, "groups");
        checkByComponentType(result, "labels");
        checkByComponentType(result, "languages");
        checkByComponentType(result, "resources");
    }

    private void checkByComponentType(ResultActions result, String componentType) throws Exception {
        result.andExpect(jsonPath("$.payload." + componentType + ".1", Matchers.equalTo("DIFF")));
        result.andExpect(jsonPath("$.payload." + componentType + ".2", Matchers.equalTo("DIFF")));
        result.andExpect(jsonPath("$.payload." + componentType + ".3", Matchers.equalTo("NEW")));
    }

    @Test
    void testRunAnalysisWrongObjectType() throws Exception {
        String accessToken = mockAccessToken();

        Map<String, List<String>> request = ImmutableMap.of(
                "nonexistent-object-type", ImmutableList.of("1", "2", "3")
        );

        // WIDGETS
        Mockito.lenient().doReturn(true).when(widgetService).exists("1");
        Mockito.lenient().doReturn(true).when(widgetService).exists("2");

        ResultActions result = mockMvc.perform(
                post("/analysis/components/diff")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().is(400));
    }

    private String mockAccessToken() {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        return mockOAuthInterceptor(user);
    }

    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private AnalysisController controller;

    @Mock
    WidgetService widgetService;
    @Mock
    GuiFragmentService guiFragmentService;
    @Mock
    PageService pageService;
    @Mock
    PageModelService pageModelService;
    @Mock
    CategoryService categoryService;
    @Mock
    GroupService groupService;
    @Mock
    LabelService labelService;
    @Mock
    LanguageService languageService;
    @Mock
    IFileBrowserService fileBrowserService;

    @Mock
    WidgetDto widget;
    @Mock
    GuiFragmentDto guiFragment;
    @Mock
    PageDto page;
    @Mock
    PageModelDto pageModel;
    @Mock
    CategoryDto category;
    @Mock
    GroupDto group;
    @Mock
    LabelDto label;
    @Mock
    LanguageDto language;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .addInterceptors(entandoOauth2Interceptor)
                .setHandlerExceptionResolvers(createHandlerExceptionResolver())
                .build();
    }
}
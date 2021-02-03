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
package org.entando.entando.aps.system.services.analysis.component_existence;

import com.google.common.collect.ImmutableList;
import org.entando.entando.aps.system.services.analysis.component_existence.ComponentExistenceAnalysis.ComponentExistenceAnalysisResult;
import org.entando.entando.aps.system.services.analysis.component_existence.ComponentExistenceAnalysis.ServiceParams;
import org.entando.entando.aps.system.services.analysis.component_existence.ComponentExistenceAnalysis.Status;
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
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ComponentExistenceAnalysisTest {

    @Test
    void testRunAnalysis() throws EntException {
        ComponentExistenceAnalysis oea = new ComponentExistenceAnalysis();
        ComponentExistenceAnalysisResult result = new ComponentExistenceAnalysisResult();

        // WIDGETS
        Mockito.doReturn(true).when(widgetService).exists("w1");
        Mockito.doReturn(true).when(widgetService).exists("wx");
        Mockito.doReturn(true).when(widgetService).exists("wx1");

        // GUI FRAGMENT
        Mockito.doReturn(true).when(guiFragmentService).exists("f1");
        Mockito.doReturn(true).when(guiFragmentService).exists("fx");
        Mockito.doReturn(true).when(guiFragmentService).exists("fx1");

        // PAGES
        Mockito.doReturn(true).when(pageService).exists("p1");
        Mockito.doReturn(true).when(pageService).exists("px");
        Mockito.doReturn(true).when(pageService).exists("px1");

        // PAGES MODELS
        Mockito.doReturn(true).when(pageModelService).exists("pm1");
        Mockito.doReturn(true).when(pageModelService).exists("pmx");
        Mockito.doReturn(true).when(pageModelService).exists("pmx1");

        // CATEGORIES
        Mockito.doReturn(true).when(categoryService).exists("cat1");
        Mockito.doReturn(true).when(categoryService).exists("catx");
        Mockito.doReturn(true).when(categoryService).exists("catx1");

        // GROUPS
        Mockito.doReturn(true).when(groupService).exists("g1");
        Mockito.doReturn(true).when(groupService).exists("gx");
        Mockito.doReturn(true).when(groupService).exists("gx1");

        // LABELS
        Mockito.doReturn(true).when(labelService).exists("lab1");
        Mockito.doReturn(true).when(labelService).exists("labx");
        Mockito.doReturn(true).when(labelService).exists("labx1");

        // LANGUAGE
        Mockito.doReturn(true).when(languageService).exists("lan1");
        Mockito.doReturn(true).when(languageService).exists("lanx");
        Mockito.doReturn(true).when(languageService).exists("lanx1");

        // RESOURCES (FILE SYSTEM OBJECTS)
        Mockito.doReturn(true).when(fileBrowserService).exists("fb1");
        Mockito.doReturn(true).when(fileBrowserService).exists("fbx");
        Mockito.doReturn(true).when(fileBrowserService).exists("fbx1");

        result = oea.run(
                result,
                ImmutableList.of(
                        new ServiceParams(widgetService, ImmutableList.of("w1", "wx", "wx1", "wy")),
                        new ServiceParams(guiFragmentService, ImmutableList.of("f1", "fx", "fx1", "fy")),
                        new ServiceParams(pageService, ImmutableList.of("p1", "px", "px1", "py")),
                        new ServiceParams(pageModelService, ImmutableList.of("pm1", "pmx", "pmx1", "pmy")),
                        new ServiceParams(categoryService, ImmutableList.of("cat1", "catx", "catx1", "caty")),
                        new ServiceParams(groupService, ImmutableList.of("g1", "gx", "gx1", "gy")),
                        new ServiceParams(labelService, ImmutableList.of("lab1", "labx", "labx1", "laby")),
                        new ServiceParams(languageService, ImmutableList.of("lan1", "lanx", "lanx1", "lany")),
                        new ServiceParams(fileBrowserService, ImmutableList.of("fb1", "fbx", "fbx1", "fby"))
                )
        );

        assertServiceResults(result, widgetService, "w");
        assertServiceResults(result, guiFragmentService, "f");
        assertServiceResults(result, pageService, "p");
        assertServiceResults(result, pageModelService, "pm");
        assertServiceResults(result, categoryService, "cat");
        assertServiceResults(result, groupService, "g");
        assertServiceResults(result, labelService, "lab");
        assertServiceResults(result, languageService, "lan");
        assertServiceResults(result, fileBrowserService, "fb");
    }

    private void assertServiceResults(ComponentExistenceAnalysisResult result, Object service, String prefix) {
        Map<String, Status> map = result.map.get(service).map;
        assertSame(result.map, result.getMap());
        assertSame(map, result.map.get(service).getMap());
        assertThat(map.get(prefix + "1"), equalTo(Status.DIFF));
        assertThat(map.get(prefix + "x"), equalTo(Status.DIFF));
        assertThat(map.get(prefix + "x1"), equalTo(Status.DIFF));
        assertThat(map.get(prefix + "y"), equalTo(Status.NEW));
    }

    @Test
    void testRunAnalysisWithException() throws EntException {
        Assertions.assertThrows(EntRuntimeException.class, () -> {
            Mockito.doThrow(new EntException("xxx")).when(fileBrowserService).exists("fb1");
            ComponentExistenceAnalysis oea = new ComponentExistenceAnalysis();
            ComponentExistenceAnalysisResult result = new ComponentExistenceAnalysisResult();
            oea.run(result, ImmutableList.of(
                    new ServiceParams(fileBrowserService, ImmutableList.of("fb1", "fbx", "fbx1", "fby"))
            ));
        });
    }

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
}

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
package org.entando.entando.web.page;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.page.model.PageRequest;
import org.entando.entando.web.page.model.WidgetConfigurationRequest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.entando.entando.web.widget.model.WidgetRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import javax.ws.rs.core.HttpHeaders;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.Widget;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;

class PageConfigurationControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private IPageManager pageManager;

    @Autowired
    private IWidgetTypeManager widgetTypeManager;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testUpdateWidgetNotOverridable() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        String pageCode = "test_update_widget_page";
        String newWidgetCode = "test_update_widget_1";

        Assertions.assertNull(this.widgetTypeManager.getWidgetType(newWidgetCode));
        Assertions.assertNull(this.pageManager.getDraftPage(pageCode));
        try {

            WidgetRequest widgetRequestNotOverridable = getWidgetRequest(newWidgetCode, true);
            //Create the widget
            ResultActions result = this.executeWidgetPost(widgetRequestNotOverridable, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.code", is(newWidgetCode)))
                  .andExpect(jsonPath("$.payload.widgetCategory", Matchers.is("test")));
            Assertions.assertNotNull(this.widgetTypeManager.getWidgetType(newWidgetCode));

            //Create the page
            PageRequest pageRequest = getPageRequest(pageCode);
            executePostPage(pageRequest,  accessToken, status().isOk());

            //Get the widget type
            result = this.executeWidgetGet(newWidgetCode, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.used", is(0)))
                    .andExpect(jsonPath("$.payload.widgetCategory", Matchers.is("test")));

            // Update the Widget at frame 0 with a new configuration

            WidgetConfigurationRequest wcr = new WidgetConfigurationRequest();
            wcr.setCode(newWidgetCode);
            wcr.setConfig(Collections.singletonMap("key", "value_edited"));

            executePutPageFrameWidget(pageCode, wcr, accessToken, status().isBadRequest());

            result = this.executeWidgetGet(newWidgetCode, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.used", is(0)));

            // Test passing widget with a config

            WidgetConfigurationRequest wcr1 = new WidgetConfigurationRequest();
            wcr1.setCode(newWidgetCode);
            wcr1.setConfig(Collections.singletonMap("key", "value"));

            // Update the Widget at frame 0
            executePutPageFrameWidget(pageCode, wcr1, accessToken, status().isBadRequest());

            // Test passing a widget without config

            WidgetConfigurationRequest wcr2 = new WidgetConfigurationRequest();
            wcr2.setCode(newWidgetCode);
            wcr2.setConfig(null);
            // Update the Widget at frame 0
            executePutPageFrameWidget(pageCode, wcr2, accessToken, status().isOk());

            //Count widget usages

            result = this.executeWidgetGet(newWidgetCode, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.used", is(1)));

            //Get widget at position 0

            result = this.executeGetPageFrameWidget(pageCode, accessToken, status().isOk());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.code", Matchers.is(newWidgetCode)))
                    .andExpect(jsonPath("$.payload.config.key", Matchers.is("value")));


        } catch (Exception e) {
            throw e;
        } finally {
            this.pageManager.deletePage(pageCode);
            Assertions.assertNull(this.pageManager.getDraftPage(pageCode));
            this.widgetTypeManager.deleteWidgetType(newWidgetCode);
            Assertions.assertNull(this.widgetTypeManager.getWidgetType(newWidgetCode));
        }
    }

    @Test
    void testUpdateWidgetOverridable() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        String pageCode = "test_update_widget_page";
        String newWidgetCode = "test_update_widget_1";

        Assertions.assertNull(this.widgetTypeManager.getWidgetType(newWidgetCode));
        Assertions.assertNull(this.pageManager.getDraftPage(pageCode));
        try {

            WidgetRequest widgetRequestOverridable = getWidgetRequest(newWidgetCode, false);

            //Create the widget
            ResultActions result = this.executeWidgetPost(widgetRequestOverridable, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.code", is(newWidgetCode)))
                    .andExpect(jsonPath("$.payload.config.key", Matchers.is("value")));

            Assertions.assertNotNull(this.widgetTypeManager.getWidgetType(newWidgetCode));

            //Create the page
            PageRequest pageRequest = getPageRequest(pageCode);
            executePostPage(pageRequest, accessToken, status().isOk());

            //Get the widget type and count widget usages
            result = this.executeWidgetGet(newWidgetCode, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.used", is(0)));

            // Update the Widget at frame 0 with a new configuration
            WidgetConfigurationRequest wcr = new WidgetConfigurationRequest();
            wcr.setCode(newWidgetCode);
            wcr.setConfig(Collections.singletonMap("key", "value_edited"));
            result = executePutPageFrameWidget(pageCode, wcr, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.code", Matchers.is(newWidgetCode)))
                    .andExpect(jsonPath("$.payload.config.key", Matchers.is("value_edited")));

            //Count widget usages
            result = this.executeWidgetGet(newWidgetCode, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.used", is(1)));

            //Get widget at position 0
            result = this.executeGetPageFrameWidget(pageCode, accessToken, status().isOk());
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.code", Matchers.is(newWidgetCode)))
                    .andExpect(jsonPath("$.payload.config.key", Matchers.is("value_edited")));
        } catch (Exception e) {
            throw e;
        } finally {
            this.pageManager.deletePage(pageCode);
            Assertions.assertNull(this.pageManager.getDraftPage(pageCode));
            this.widgetTypeManager.deleteWidgetType(newWidgetCode);
            Assertions.assertNull(this.widgetTypeManager.getWidgetType(newWidgetCode));
        }
    }

    @Test
    void testAddWidgetParallel_1() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        String pageCode = "test_page_parallel_1";
        String widgetCode = "login_form";
        try {
            Assertions.assertNotNull(this.widgetTypeManager.getWidgetType(widgetCode));
            PageRequest pageRequest = getPageRequest(pageCode);
            this.executePostPage(pageRequest, accessToken, status().isOk());
            IPage addedPage = this.pageManager.getDraftPage(pageCode);
            Assertions.assertNotNull(addedPage);
            WidgetConfigurationRequest wcr = new WidgetConfigurationRequest();
            wcr.setCode(widgetCode);
            Assertions.assertEquals(6, addedPage.getWidgets().length);
            IntStream.range(0, addedPage.getWidgets().length).parallel().forEach(i -> {
                try {
                    ResultActions result = executePutPageFrameWidget(pageCode, i, wcr, accessToken, status().isOk());
                    result.andExpect(jsonPath("$.payload.code", Matchers.is(widgetCode)));
                } catch (Exception e) {
                    Assertions.fail("Error configuring widget into frame " + i);
                }
            });
            addedPage = this.pageManager.getDraftPage(pageCode);
            Assertions.assertNotNull(addedPage);
            Widget[] widgets = addedPage.getWidgets();
            Assertions.assertEquals(6, widgets.length);
            for (int i = 0; i < widgets.length; i++) {
                Widget widget = widgets[i];
                Assertions.assertNotNull(widget);
                Assertions.assertEquals(widgetCode, widget.getType().getCode());
            }
            
            IntStream.range(0, addedPage.getWidgets().length).parallel().forEach(i -> {
                try {
                    ResultActions result = executeDeletePageFrameWidget(pageCode, i, accessToken, status().isOk());
                    result.andExpect(jsonPath("$.payload.code", Matchers.is(String.valueOf(i))));
                } catch (Exception e) {
                    Assertions.fail("Error removing widget from frame " + i);
                }
            });
            addedPage = this.pageManager.getDraftPage(pageCode);
            Assertions.assertNotNull(addedPage);
            widgets = addedPage.getWidgets();
            Assertions.assertEquals(6, widgets.length);
            for (int i = 0; i < widgets.length; i++) {
                Widget widget = widgets[i];
                Assertions.assertNull(widget);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.pageManager.deletePage(pageCode);
            Assertions.assertNull(this.pageManager.getDraftPage(pageCode));
        }
    }
    
    @Test
    void testAddWidgetParallel_2() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        String pageCode_prefix = "test_page_parallel";
        String widgetCode = "login_form";
        try {
            Assertions.assertNotNull(this.widgetTypeManager.getWidgetType(widgetCode));
            List<PageRequest> pageRequests = IntStream.range(1, 20).boxed().map(i -> this.getPageRequest(pageCode_prefix + i)).collect(Collectors.toList());
            pageRequests.parallelStream().forEach(pr -> {
                try {
                    this.executePostPage(pr, accessToken, status().isOk());
                    IPage addedPage = this.pageManager.getDraftPage(pr.getCode());
                    Assertions.assertNotNull(addedPage);
                } catch (Exception e) {
                    Assertions.fail("Error adding page " + pr.getCode());
                }
            });
            WidgetConfigurationRequest wcr = new WidgetConfigurationRequest();
            wcr.setCode(widgetCode);
            IntStream.range(1, 20).parallel().forEach(i -> {
                String pageCode = pageCode_prefix + i;
                try {
                    ResultActions result = executePutPageFrameWidget(pageCode, 0, wcr, accessToken, status().isOk());
                    result.andExpect(jsonPath("$.payload.code", Matchers.is(widgetCode)));
                } catch (Exception e) {
                    Assertions.fail("Error configuring widget into frame " + i);
                }
            });
            IntStream.range(1, 20).parallel().forEach(i -> {
                String pageCode = pageCode_prefix + i;
                IPage addedPage = this.pageManager.getDraftPage(pageCode);
                Assertions.assertNotNull(addedPage);
                Widget[] widgets = addedPage.getWidgets();
                Assertions.assertEquals(6, widgets.length);
                for (int j = 0; j < widgets.length; j++) {
                    Widget widget = widgets[j];
                    if (j == 0) {
                        Assertions.assertNotNull(widget);
                        Assertions.assertEquals(widgetCode, widget.getType().getCode());
                    } else {
                        Assertions.assertNull(widget);
                    }
                }
            });
        } catch (Exception e) {
            throw e;
        } finally {
            IntStream.range(1, 20).parallel().forEach(i -> {
                String pageCode = pageCode_prefix + i;
                try {
                    this.pageManager.deletePage(pageCode);
                    Assertions.assertNull(this.pageManager.getDraftPage(pageCode));
                } catch (Exception e) {
                    Assertions.fail("Error removing page " + pageCode);
                }
            });
        }
    }
    
    private PageRequest getPageRequest(String pageCode) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setCode(pageCode);
        pageRequest.setPageModel("home");
        pageRequest.setOwnerGroup(Group.FREE_GROUP_NAME);
        Map<String, String> pageTitles = new HashMap<>();
        pageTitles.put("it", pageCode);
        pageTitles.put("en", pageCode);
        pageRequest.setTitles(pageTitles);
        pageRequest.setParentCode("service");
        return pageRequest;
    }

    private WidgetRequest getWidgetRequest(String newWidgetCode, boolean readonlyPageWidgetConfig) {
        WidgetRequest request = new WidgetRequest();
        request.setCode(newWidgetCode);
        request.setParentType("parent_widget");
        request.setGroup(Group.FREE_GROUP_NAME);
        Map<String, String> titles = new HashMap<>();
        titles.put("it", "Titolo");
        titles.put("en", "Title");
        request.setTitles(titles);
        request.setConfig(Collections.singletonMap("key", "value"));
        request.setCustomUi("<h1>Test</h1>");
        request.setGroup(Group.FREE_GROUP_NAME);
        request.setReadonlyPageWidgetConfig(readonlyPageWidgetConfig);
        request.setWidgetCategory("test");
        return request;
    }

    private ResultActions executeWidgetGet(String widgetTypeCode, String accessToken, ResultMatcher expected)
            throws Exception {
        ResultActions result = mockMvc
                .perform(get("/widgets/{code}", widgetTypeCode)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }

    private ResultActions executePostPage(PageRequest pageRequest, String accessToken, ResultMatcher expected)  throws Exception {
        ResultActions result = mockMvc
            .perform(post("/pages")
                            .content(mapper.writeValueAsString(pageRequest))
            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }

    private ResultActions executePutPageFrameWidget(String pageCode,
            WidgetConfigurationRequest widgetConfigurationRequest, String accessToken, ResultMatcher expected)
            throws Exception {
        return this.executePutPageFrameWidget(pageCode, 0, widgetConfigurationRequest, accessToken, expected);
    }

    private ResultActions executePutPageFrameWidget(String pageCode, int frame,
            WidgetConfigurationRequest widgetConfigurationRequest, String accessToken, ResultMatcher expected)
            throws Exception {
        ResultActions result = mockMvc
                .perform(put("/pages/{pageCode}/widgets/{frameId}", pageCode, frame)
                        .content(mapper.writeValueAsString(widgetConfigurationRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }
    
    private ResultActions executeDeletePageFrameWidget(String pageCode, int frame, 
            String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(delete("/pages/{pageCode}/widgets/{frameId}", pageCode, frame)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }

    private ResultActions executeGetPageFrameWidget(String pageCode, String accessToken,
            ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(get("/pages/{pageCode}/widgets/{frameId}", pageCode, 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }

    private ResultActions executeWidgetPost(WidgetRequest request, String accessToken, ResultMatcher expected)
            throws Exception {
        ResultActions result = mockMvc
                .perform(post("/widgets")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }

}

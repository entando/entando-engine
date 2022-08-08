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
package org.entando.entando.web.widget;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.entando.entando.aps.system.services.guifragment.IGuiFragmentManager;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.page.model.PageRequest;
import org.entando.entando.web.page.model.WidgetConfigurationRequest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.entando.entando.web.widget.model.WidgetRequest;
import org.entando.entando.web.widget.validator.WidgetValidator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import javax.ws.rs.core.HttpHeaders;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.util.ApsProperties;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.entando.entando.web.analysis.AnalysisControllerDiffAnalysisEngineTestsStubs;
import org.entando.entando.web.widget.model.WidgetRequest.WidgetParameter;

class WidgetControllerIntegrationTest extends AbstractControllerIntegrationTest {
    
    @Autowired
    private IPageManager pageManager;

    @Autowired
    private IWidgetTypeManager widgetTypeManager;

    @Autowired
    private IGuiFragmentManager guiFragmentManager;

    private ObjectMapper mapper = new ObjectMapper();
    
    @Test
    void testGetWidgets() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES)
                .build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/widgets")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
        result.andExpect(status().isOk());
        testCors("/widgets");
    }
    
    @Test
    void testGetWidget_1() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES)
                .build();
        String accessToken = mockOAuthInterceptor(user);
        // @formatter:off
        ResultActions result = this.executeWidgetGet("1", accessToken, status().isNotFound());
        String response = result.andReturn().getResponse().getContentAsString();
        assertNotNull(response);
    }
    
    @Test
    void testGetWidget_2() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES)
                .build();
        String accessToken = mockOAuthInterceptor(user);
        // @formatter:off
        ResultActions result = this.executeWidgetGet("login_form", accessToken, status().isOk());
        String response = result.andReturn().getResponse().getContentAsString();
        assertNotNull(response);
        result.andExpect(jsonPath("$.payload.code", is("login_form")));
        result.andExpect(jsonPath("$.payload.parameters.size()", is(0)));
    }
    
    @Test
    void testGetWidget_3() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES)
                .build();
        String accessToken = mockOAuthInterceptor(user);
        // @formatter:off
        ResultActions result = this.executeWidgetGet("formAction", accessToken, status().isOk());
        String response = result.andReturn().getResponse().getContentAsString();
        assertNotNull(response);
        result.andExpect(jsonPath("$.payload.code", is("formAction")));        
        result.andExpect(jsonPath("$.payload.action", is("configSimpleParameter")));
        result.andExpect(jsonPath("$.payload.parameters.size()", is(1)));
        result.andExpect(jsonPath("$.payload.parameters[0].code", is("actionPath")));
        result.andExpect(jsonPath("$.payload.parameters[0].description", is("Path relativo di una action o una Jsp")));
    }

    @Test
    void testGetWidgetUsage() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        String code = "login_form";

        // @formatter:off
        executeWidgetUsage(code, accessToken, status().isOk())
                .andDo(resultPrint())
                .andExpect(jsonPath("$.payload.type", is(WidgetController.COMPONENT_ID)))
                .andExpect(jsonPath("$.payload.code", is(code)))
                .andExpect(jsonPath("$.payload.usage", is(1)))
                .andReturn();
    }

    @Test
    void testGetWidgetInfo() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        // @formatter:off
        ResultActions result = mockMvc.perform(
                get("/widgets/login_form/info")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
        String response = result.andReturn().getResponse().getContentAsString();
        assertNotNull(response);
        result.andExpect(jsonPath("$.payload.publishedUtilizers", Matchers.hasSize(1)));
    }

    @Test
    void testGetWidgetList_1() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES)
                .build();
        String accessToken = mockOAuthInterceptor(user);
        // @formatter:off
        ResultActions result = mockMvc.perform(
                get("/widgets").param("pageSize", "100")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(7)));
        result.andExpect(jsonPath("$.metaData.pageSize", is(100)));
        result.andExpect(jsonPath("$.metaData.totalItems", is(7)));
        String response = result.andReturn().getResponse().getContentAsString();
        assertNotNull(response);
    }

    @Test
    void testGetWidgetList_2() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES)
                .build();
        String accessToken = mockOAuthInterceptor(user);
        // @formatter:off
        ResultActions result = mockMvc.perform(
                get("/widgets").param("pageSize", "5")
                        .param("sort", "code").param("direction", "DESC")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(5)));
        result.andExpect(jsonPath("$.metaData.pageSize", is(5)));
        result.andExpect(jsonPath("$.metaData.totalItems", is(7)));
        String response = result.andReturn().getResponse().getContentAsString();
        result.andExpect(jsonPath("$.payload[0].code", is("parent_widget")));
        assertNotNull(response);
    }

    @Test
    void testGetWidgetList_3() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES)
                .build();
        String accessToken = mockOAuthInterceptor(user);
        // @formatter:off
        ResultActions result = mockMvc.perform(
                get("/widgets").param("pageSize", "5")
                        .param("sort", "code").param("direction", "DESC")
                        .param("filters[0].attribute", "typology").param("filters[0].value", "oc")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(4)));
        result.andExpect(jsonPath("$.metaData.pageSize", is(5)));
        result.andExpect(jsonPath("$.metaData.totalItems", is(4)));
        result.andExpect(jsonPath("$.payload[0].code", is("messages_system")));
        String response = result.andReturn().getResponse().getContentAsString();
        assertNotNull(response);
    }

    @Test
    void testAddUpdateWidget_1() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        String newCode = "test_new_type_1";
        Assertions.assertNull(this.widgetTypeManager.getWidgetType(newCode));
        try {
            WidgetRequest request = new WidgetRequest();
            request.setCode(newCode);
            request.setGroup(Group.FREE_GROUP_NAME);
            Map<String, String> titles = new HashMap<>();
            titles.put("it", "Titolo ITA");
            titles.put("en", "Title EN");
            request.setTitles(titles);
            request.setCustomUi("<h1>Custom UI</h1>");
            request.setGroup(Group.FREE_GROUP_NAME);
            request.setReadonlyPageWidgetConfig(true);
            ResultActions result = this.executeWidgetPost(request, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.code", is(newCode)));
            WidgetType widgetType = this.widgetTypeManager.getWidgetType(newCode);
            Assertions.assertNotNull(widgetType);
            Assertions.assertEquals("Title EN", widgetType.getTitles().getProperty("en"));
            
            request.setGroup("invalid");
            titles.put("en", "Title EN modified");
            result = this.executeWidgetPut(request, newCode, accessToken, status().isBadRequest());
            result.andExpect(jsonPath("$.errors[0].code", is(WidgetValidator.ERRCODE_WIDGET_GROUP_INVALID)));
            
            request.setGroup("helpdesk");
            request.setCustomUi("");
            result = this.executeWidgetPut(request, newCode, accessToken, status().isBadRequest());
            result.andExpect(jsonPath("$.errors[0].code", is(WidgetValidator.ERRCODE_NOT_BLANK)));
            
            titles.put("en", "Title EN modified");
            request.setCustomUi("New Custom Ui");
            result = this.executeWidgetPut(request, newCode, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.group", is("helpdesk")));
            widgetType = this.widgetTypeManager.getWidgetType(newCode);
            Assertions.assertNotNull(widgetType);
            Assertions.assertEquals("Title EN modified", widgetType.getTitles().getProperty("en"));
            Assertions.assertEquals("helpdesk", widgetType.getMainGroup());
            
            result = this.executeWidgetDelete(newCode, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.code", is(newCode)));
            widgetType = this.widgetTypeManager.getWidgetType(newCode);
            Assertions.assertNull(widgetType);
        } catch (Exception e) {
            throw e;
        } finally {
            this.widgetTypeManager.deleteWidgetType(newCode);
        }
    }
    
    @Test
    void testParallelAddDeleteWidgetType() throws Throwable {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        String newCode_prefix = "test_widgetType";
        try {
            List<WidgetRequest> types = IntStream.range(1, 20).boxed().map(i -> {
                String code = newCode_prefix + "_" + i;
                WidgetRequest request = new WidgetRequest();
                request.setCode(code);
                request.setGroup(Group.FREE_GROUP_NAME);
                Map<String, String> titles = new HashMap<>();
                titles.put("it", "Titolo ITA " + i);
                titles.put("en", "Title EN " + i);
                request.setTitles(titles);
                request.setCustomUi("<h1>Custom UI " + i + "</h1>");
                request.setGroup(Group.FREE_GROUP_NAME);
                request.setReadonlyPageWidgetConfig(true);
                return request;
            }).collect(Collectors.toList());
            types.parallelStream().forEach(request -> {
                try {
                    ResultActions result = this.executeWidgetPost(request, accessToken, status().isOk());
                    result.andExpect(jsonPath("$.payload.code", is(request.getCode())));
                    result.andExpect(jsonPath("$.payload.parentType", nullValue()));
                    result.andExpect(jsonPath("$.payload.guiFragments.size()", is(1)));
                    result.andExpect(jsonPath("$.payload.hasConfig", is(false)));
                    result.andExpect(jsonPath("$.payload.parameters.size()", is(0)));
                } catch (Exception e) {
                    Assertions.fail("Error adding widgetType " + request.getCode());
                }
            });
            IntStream.range(1, 20).parallel().forEach(i -> {
                String code = newCode_prefix + "_" + i;
                assertNotNull(this.widgetTypeManager.getWidgetType(code));
            });
        } catch (Exception e) {
            throw e;
        } finally {
            IntStream.range(1, 20).parallel().forEach(i -> {
                String code = newCode_prefix + "_" + i;
                try {
                    ResultActions result = this.executeWidgetDelete(code, accessToken, status().isOk());
                    result.andExpect(jsonPath("$.payload.code", is(code)));
                } catch (Exception e) {
                    Assertions.fail("Error deleting widgetType " + code);
                }
                assertNull(this.widgetTypeManager.getWidgetType(code));
            });
        }
    }
    
    @Test
    void testAddUpdateWidget_2() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        String newCode = "test_new_type_2";
        Assertions.assertNull(this.widgetTypeManager.getWidgetType(newCode));
        try {
            WidgetRequest request = new WidgetRequest();
            request.setCode(newCode);
            request.setGroup(Group.FREE_GROUP_NAME);
            Map<String, String> titles = new HashMap<>();
            titles.put("it", "Titolo ITA 2");
            titles.put("en", "Title EN 2");
            request.setTitles(titles);
            request.setCustomUi("");
            request.setGroup(Group.FREE_GROUP_NAME);
            request.setReadonlyPageWidgetConfig(true);

            ResultActions result = this.executeWidgetPost(request, accessToken, status().isBadRequest());
            result.andExpect(jsonPath("$.errors[0].code", is(WidgetValidator.ERRCODE_NOT_BLANK)));
            
            titles.put("en", "");
            request.setCustomUi("Custom UI");
            result = this.executeWidgetPost(request, accessToken, status().isBadRequest());
            result.andExpect(jsonPath("$.errors[0].code", is(WidgetValidator.ERRCODE_MISSING_TITLE)));
            
            titles.put("en", "Title EN 2 bis");
            result = this.executeWidgetPut(request, newCode, accessToken, status().isNotFound());
            result.andExpect(jsonPath("$.errors[0].code", is(WidgetValidator.ERRCODE_WIDGET_NOT_FOUND)));

            result = this.executeWidgetPost(request, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.group", is(Group.FREE_GROUP_NAME)));
            WidgetType widgetType = this.widgetTypeManager.getWidgetType(newCode);
            Assertions.assertNotNull(widgetType);
            Assertions.assertEquals("Title EN 2 bis", widgetType.getTitles().getProperty("en"));
            
            titles.put("it", "");
            result = this.executeWidgetPut(request, newCode, accessToken, status().isBadRequest());
            result.andExpect(jsonPath("$.errors[0].code", is(WidgetValidator.ERRCODE_MISSING_TITLE)));
            
            titles.put("it", "new IT Title");
            request.setCode("wrongCode");
            result = this.executeWidgetPut(request, newCode, accessToken, status().isBadRequest());
            result.andExpect(jsonPath("$.errors[0].code", is(WidgetValidator.ERRCODE_URINAME_MISMATCH)));
            
            request.setCode(newCode);
            result = this.executeWidgetPut(request, newCode, accessToken, status().isOk());
            result.andExpect(jsonPath("$.errors.size()", is(0)));
            
            WidgetType newType = this.widgetTypeManager.getWidgetType(newCode);
            Assertions.assertNotNull(newType);
            Assertions.assertEquals("new IT Title", newType.getTitles().get("it"));
        } catch (Exception e) {
            throw e;
        } finally {
            this.widgetTypeManager.deleteWidgetType(newCode);
            Assertions.assertNull(this.widgetTypeManager.getWidgetType(newCode));
        }
    }
    
    @Test
    void testAddUpdateWidget_3() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        String pageCode = "test_add_delete_widget";
        String newWidgetCode = "test_new_type_3";
        Assertions.assertNull(this.widgetTypeManager.getWidgetType(newWidgetCode));
        try {
            WidgetRequest request = getWidgetRequest(newWidgetCode);
            request.setReadonlyPageWidgetConfig(true);
            ResultActions result0 = this.executeWidgetPost(request, accessToken, status().isOk());
            result0.andExpect(jsonPath("$.payload.code", is(newWidgetCode)));
            Assertions.assertNotNull(this.widgetTypeManager.getWidgetType(newWidgetCode));

            PageRequest pageRequest = new PageRequest();
            pageRequest.setCode(pageCode);
            pageRequest.setPageModel("home");

            pageRequest.setOwnerGroup(Group.FREE_GROUP_NAME);
            Map<String, String> pageTitles = new HashMap<>();
            pageTitles.put("it", pageCode);
            pageTitles.put("en", pageCode);
            pageRequest.setTitles(pageTitles);
            pageRequest.setParentCode("service");
            this.addPage(accessToken, pageRequest);
            
            ResultActions result1 = this.executeWidgetGet(newWidgetCode, accessToken, status().isOk());
            result1.andExpect(jsonPath("$.payload.used", is(0)));
            
            WidgetConfigurationRequest wcr = new WidgetConfigurationRequest();
            wcr.setCode(newWidgetCode);
            ResultActions resultPutWidget = mockMvc
                .perform(put("/pages/{pageCode}/widgets/{frameId}", new Object[]{pageCode, 1})
                        .content(mapper.writeValueAsString(wcr))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
            resultPutWidget.andExpect(status().isOk());
            
            ResultActions result3 = this.executeWidgetGet(newWidgetCode, accessToken, status().isOk());
            result3.andExpect(jsonPath("$.payload.used", is(1)));
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
    void testAddUpdateWidgetWithParentAndParameters() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        String code = "parametrized_widget";
        String mainCustomUi = "<h1>Main Custom UI</h1>";
        String childCustomUi = "<h1>Child Custom UI</h1>";
        String childCode = "test_new_type_2";
        WidgetType widgetType = this.widgetTypeManager.getWidgetType(code);
        Assertions.assertNull(widgetType);
        try {
            WidgetRequest request = new WidgetRequest();
            request.setCode(code);
            request.setGroup(Group.FREE_GROUP_NAME);
            Map<String, String> titles = new HashMap<>();
            titles.put("it", "Titolo ITA");
            titles.put("en", "Title EN");
            request.setTitles(titles);
            request.setReadonlyPageWidgetConfig(true);

            request.setCustomUi(mainCustomUi);
            request.setGroup(Group.FREE_GROUP_NAME);
            request.getParams().add(new WidgetParameter("param1", "Description of parameter 1"));
            request.getParams().add(new WidgetParameter("param2", "Description of parameter 2"));
            request.getParams().add(new WidgetParameter("param3", "Description of parameter 3"));
            request.getParams().add(new WidgetParameter("param4", null));
            request.setConfigUiName("configAction");
            ResultActions resultMaster = executeWidgetPost(request, accessToken, status().isOk());
            
            resultMaster.andExpect(jsonPath("$.payload.code", is(code)));
            resultMaster.andExpect(jsonPath("$.payload.code", is(code)));
            resultMaster.andExpect(jsonPath("$.payload.parentType", nullValue()));
            resultMaster.andExpect(jsonPath("$.payload.guiFragments.size()", is(1)));
            resultMaster.andExpect(jsonPath("$.payload.guiFragments[0].customUi", is(mainCustomUi)));
            resultMaster.andExpect(jsonPath("$.payload.hasConfig", is(true)));
            resultMaster.andExpect(jsonPath("$.payload.parameters.size()", is(4)));
            resultMaster.andExpect(jsonPath("$.payload.parameters[0].code", is("param1")));
            resultMaster.andExpect(jsonPath("$.payload.parameters[0].description", is("Description of parameter 1")));
            resultMaster.andExpect(jsonPath("$.payload.parameters[1].code", is("param2")));
            resultMaster.andExpect(jsonPath("$.payload.parameters[1].description", is("Description of parameter 2")));
            resultMaster.andExpect(jsonPath("$.payload.parameters[2].code", is("param3")));
            resultMaster.andExpect(jsonPath("$.payload.parameters[2].description", is("Description of parameter 3")));
            resultMaster.andExpect(jsonPath("$.payload.parameters[3].code", is("param4")));
            resultMaster.andExpect(jsonPath("$.payload.parameters[3].description", nullValue()));
            resultMaster.andExpect(jsonPath("$.payload.action", is("configAction")));
            
            widgetType = this.widgetTypeManager.getWidgetType(code);
            Assertions.assertNotNull(widgetType);
            
            WidgetRequest requestChild = new WidgetRequest();
            requestChild.setCode(childCode);
            requestChild.setGroup(Group.FREE_GROUP_NAME);
            Map<String, String> titlesChild = new HashMap<>();
            titlesChild.put("it", "Titolo ITA child");
            titlesChild.put("en", "Title EN child");
            requestChild.setTitles(titlesChild);
            requestChild.setParentCode(code);
            Map<String, String> config = new HashMap<>();
            config.put("param1", "Value1");
            requestChild.setParamsDefaults(config);
            
            ResultActions resultSlave = this.executeWidgetPost(requestChild, accessToken, status().isOk());
            
            resultSlave.andExpect(jsonPath("$.payload.code", is(childCode)))
                    .andExpect(jsonPath("$.payload.parentType", is(code)))
                    .andExpect(jsonPath("$.payload.guiFragments.size()", is(0)))
                    .andExpect(jsonPath("$.payload.parameters.size()", is(0)))
                    .andExpect(jsonPath("$.payload.hasConfig", is(false)))
                    .andExpect(jsonPath("$.payload.config.size()", is(1)))
                    .andExpect(jsonPath("$.payload.config.param1", is("Value1")));
            
            WidgetType childWidgetType = this.widgetTypeManager.getWidgetType(childCode);
            Assertions.assertNotNull(childWidgetType);
            Assertions.assertEquals(code, childWidgetType.getParentType().getCode());
            Assertions.assertNull(childWidgetType.getTypeParameters());
            Assertions.assertNull(childWidgetType.getAction());
            Assertions.assertEquals(1, childWidgetType.getConfig().size());
            Assertions.assertEquals("Value1", childWidgetType.getConfig().get("param1"));
            Assertions.assertEquals("Titolo ITA child", childWidgetType.getTitles().get("it"));
            Assertions.assertEquals("Title EN child", childWidgetType.getTitles().get("en"));
            
            titlesChild.put("en", "Title EN child modified");
            config.put("param3", "Value3");
            requestChild.setCustomUi(childCustomUi);
            
            //When updating, request parameters override everything
            executeWidgetPut(requestChild, childCode, accessToken, status().isOk())
                    .andDo(resultPrint())
                    .andExpect(jsonPath("$.payload.code", is(childCode)))
                    .andExpect(jsonPath("$.payload.parentType", is(code)))
                    .andExpect(jsonPath("$.payload.guiFragments.size()", is(0)))
                    .andExpect(jsonPath("$.payload.parameters.size()", is(0)))
                    .andExpect(jsonPath("$.payload.hasConfig", is(false)))
                    .andExpect(jsonPath("$.payload.config.size()", is(2)))
                    .andExpect(jsonPath("$.payload.config.param1", is("Value1")))
                    .andExpect(jsonPath("$.payload.config.param3", is("Value3")));
            
            //Update a child widget
            requestChild.setCustomUi(mainCustomUi);
            requestChild.setParamsDefaults(Collections.singletonMap("param2", "ValueX"));

            //When updating, request parameters override everything
            executeWidgetPut(requestChild, childCode, accessToken, status().isOk())
                    .andDo(resultPrint())
                    .andExpect(jsonPath("$.payload.code", is(childCode)))
                    .andExpect(jsonPath("$.payload.parentType", is(code)))
                    .andExpect(jsonPath("$.payload.guiFragments.size()", is(0)))
                    .andExpect(jsonPath("$.payload.parameters.size()", is(0)))
                    .andExpect(jsonPath("$.payload.hasConfig", is(false)))
                    .andExpect(jsonPath("$.payload.config.size()", is(1)))
                    .andExpect(jsonPath("$.payload.config.param2", is("ValueX")));

            //Parent should remain unchanged
            executeWidgetGet(code, accessToken, status().isOk())
                    .andDo(resultPrint())
                    .andExpect(jsonPath("$.payload.code", is(code)))
                    .andExpect(jsonPath("$.payload.parameters.size()", is(4)))
                    .andExpect(jsonPath("$.payload.parameters[0].code", is("param1")))
                    .andExpect(jsonPath("$.payload.parameters[0].description", is("Description of parameter 1")));
            
        } catch (Exception e) {
            throw e;
        } finally {
            List<String> codesOfWidgetToDelete = Arrays.asList(childCode, code);
            for (int j = 0; j < codesOfWidgetToDelete.size(); j++) {
                String codeOfWidgetToDelete = codesOfWidgetToDelete.get(j);
                List<String> fragmentCodes = this.guiFragmentManager.getGuiFragmentCodesByWidgetType(codeOfWidgetToDelete);
                for (int i = 0; i < fragmentCodes.size(); i++) {
                    this.guiFragmentManager.deleteGuiFragment(fragmentCodes.get(i));
                }
                this.widgetTypeManager.deleteWidgetType(codeOfWidgetToDelete);
            }
        }
    }
    
    @Test
    void testMoveWidgetToAnotherFrame() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        String pageCode = "test_move_widget_page";
        String newWidgetCode = "test_move_widget_1";
        Assertions.assertNull(this.widgetTypeManager.getWidgetType(newWidgetCode));
        Assertions.assertNull(this.pageManager.getDraftPage(pageCode));
        try {
            WidgetRequest request = getWidgetRequest(newWidgetCode);
            ResultActions result = this.executeWidgetPost(request, accessToken, status().isOk());
            result.andDo(resultPrint())
                    .andExpect(jsonPath("$.payload.code", is(newWidgetCode)));
            Assertions.assertNotNull(this.widgetTypeManager.getWidgetType(newWidgetCode));

            PageRequest pageRequest = getPageRequest(pageCode);

            result = mockMvc
                    .perform(post("/pages")
                            .content(mapper.writeValueAsString(pageRequest))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            result.andDo(resultPrint())
                    .andExpect(status().isOk());

            result = this.executeWidgetGet(newWidgetCode, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.used", is(0)));

            WidgetConfigurationRequest wcr = new WidgetConfigurationRequest();
            wcr.setCode(newWidgetCode);
            result = mockMvc
                    .perform(put("/pages/{pageCode}/widgets/{frameId}", new Object[]{pageCode, 0})
                            .content(mapper.writeValueAsString(wcr))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
            result.andDo(resultPrint())
                    .andExpect(status().isOk());

            result = this.executeWidgetGet(newWidgetCode, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.used", is(1)));

            result = mockMvc
                    .perform(get("/pages/{pageCode}/widgets", pageCode)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            result.andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload[0].code", is(newWidgetCode)))
                    .andExpect(jsonPath("$.payload[1]", Matchers.isEmptyOrNullString()))
                    .andExpect(jsonPath("$.payload[2]", Matchers.isEmptyOrNullString()))
                    .andExpect(jsonPath("$.payload[3]", Matchers.isEmptyOrNullString()));

            result = mockMvc
                    .perform(delete("/pages/{pageCode}/widgets/{frameId}", new Object[]{pageCode, 0})
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
            result.andDo(resultPrint())
                    .andExpect(status().isOk());

            result = mockMvc
                    .perform(get("/pages/{pageCode}/widgets", pageCode)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            result.andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload[0]", Matchers.isEmptyOrNullString()))
                    .andExpect(jsonPath("$.payload[1]", Matchers.isEmptyOrNullString()))
                    .andExpect(jsonPath("$.payload[2]", Matchers.isEmptyOrNullString()))
                    .andExpect(jsonPath("$.payload[3]", Matchers.isEmptyOrNullString()));

            result = mockMvc
                    .perform(put("/pages/{pageCode}/widgets/{frameId}", new Object[]{pageCode, 2})
                            .content(mapper.writeValueAsString(wcr))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
            result.andDo(resultPrint())
                    .andExpect(status().isOk());

            result = this.executeWidgetGet(newWidgetCode, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.used", is(1)));

            result = mockMvc
                    .perform(get("/pages/{pageCode}/widgets", pageCode)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken));
            result.andDo(resultPrint())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload[0]", Matchers.isEmptyOrNullString()))
                    .andExpect(jsonPath("$.payload[1]", Matchers.isEmptyOrNullString()))
                    .andExpect(jsonPath("$.payload[2].code", is(newWidgetCode)))
                    .andExpect(jsonPath("$.payload[3]", Matchers.isEmptyOrNullString()));

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
    void shouldUpdateWidgetWithoutCodeInBody() throws Exception { // TODO to clarify
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        String newCode = "test_new_type_1";
        Assertions.assertNull(this.widgetTypeManager.getWidgetType(newCode));
        try {
            WidgetRequest request = new WidgetRequest();
            request.setCode(newCode);
            request.setGroup(Group.FREE_GROUP_NAME);
            Map<String, String> titles = new HashMap<>();
            titles.put("it", "Titolo ITA");
            titles.put("en", "Title EN");
            request.setTitles(titles);
            request.setCustomUi("<h1>Custom UI</h1>");
            request.setGroup(Group.FREE_GROUP_NAME);
            request.setReadonlyPageWidgetConfig(true);
            ResultActions result = this.executeWidgetPost(request, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.code", is(newCode)));
            WidgetType widgetType = this.widgetTypeManager.getWidgetType(newCode);
            Assertions.assertNotNull(widgetType);
            Assertions.assertEquals("Title EN", widgetType.getTitles().getProperty("en"));

            titles.put("en", "Title EN modified");
            request.setCode(null);
            
            //this.executeWidgetPut(request, newCode, accessToken, status().isOk()); // TODO to clarify
            this.executeWidgetPut(request, newCode, accessToken, status().isBadRequest());
            
            request.setCode("wrongCode");
            this.executeWidgetPut(request, newCode, accessToken, status().isBadRequest());
            
            request.setCode(newCode);
            this.executeWidgetPut(request, newCode, accessToken, status().isOk());
        } finally {
            this.widgetTypeManager.deleteWidgetType(newCode);
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

    private WidgetRequest getWidgetRequest(String newWidgetCode) {
        WidgetRequest request = new WidgetRequest();
        request.setCode(newWidgetCode);
        request.setGroup(Group.FREE_GROUP_NAME);
        Map<String, String> titles = new HashMap<>();
        titles.put("it", "Titolo ITA 3");
        titles.put("en", "Title EN 3");
        request.setTitles(titles);
        request.setCustomUi("<h1>Test</h1>");
        request.setGroup(Group.FREE_GROUP_NAME);
        request.setReadonlyPageWidgetConfig(true);
        return request;
    }

    private void addPage(String accessToken, PageRequest pageRequest) throws Exception {
        ResultActions result = mockMvc
                .perform(post("/pages")
                        .content(mapper.writeValueAsString(pageRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }
    
    @Test
    void testUpdateStockLocked() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        String code = "login_form";
        WidgetType widgetType = this.widgetTypeManager.getWidgetType(code);
        WidgetRequest request = new WidgetRequest();
        request.setCode(code);
        request.setGroup(Group.FREE_GROUP_NAME);
        request.setTitles((Map) widgetType.getTitles());
        request.setWidgetCategory(widgetType.getWidgetCategory());
        request.setIcon(widgetType.getIcon());
        request.setReadonlyPageWidgetConfig(true);
        ResultActions result = this.executeWidgetPut(request, code, accessToken, status().isOk());
        result.andExpect(jsonPath("$.payload.code", is("login_form")));
    }
    
    @Test
    void testDeleteWidgetLocked() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        String code = "login_form";
        ResultActions result = this.executeWidgetDelete(code, accessToken, status().isBadRequest());
        result.andExpect(jsonPath("$.errors[0].code", is(WidgetValidator.ERRCODE_OPERATION_FORBIDDEN_LOCKED)));
    }

    @Test
    void testGetWidgetsWithAdminPermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/widgets")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }

    @Test
    void testGetWidgetsWithoutPermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("normal_user", "0x24").build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/widgets")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
        result.andExpect(status().isForbidden());
    }

    @Test
    void testGetWidgetsWithManagePagesPermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("enter_backend_user", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "admin", Permission.MANAGE_PAGES).build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/widgets")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }

    @Test
    void testEditLockedConfigWidget() throws Throwable {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        try {
            //Try to update the config of locked widget with a null config as previous value

            String widgetTypeCode = "login_form";
            
            WidgetRequest request = new WidgetRequest();
            request.setCode(widgetTypeCode);
            request.setGroup(Group.FREE_GROUP_NAME);
            Map<String, String> titles = new HashMap<>();
            titles.put("it", "Titolo ITA");
            titles.put("en", "Title EN");
            request.setTitles(titles);
            request.setCustomUi("<h1>Custom UI</h1>");
            request.setGroup(Group.FREE_GROUP_NAME);

            request.setConfig(Collections.singletonMap("k","v"));
            ResultActions result = this.executeWidgetPut(request, widgetTypeCode, accessToken, status().isBadRequest());
            result.andExpect(jsonPath("$.errors[0].code", is(WidgetValidator.ERRCODE_OPERATION_FORBIDDEN_LOCKED)));


            //Try to update the config of locked widget with a not null config as previous value

            widgetTypeCode = "formAction";
            request.setCode(widgetTypeCode);
            result = this.executeWidgetPut(request, widgetTypeCode, accessToken, status().isBadRequest());
            result.andDo(resultPrint());
            result.andExpect(jsonPath("$.errors[0].code", is(WidgetValidator.ERRCODE_OPERATION_FORBIDDEN_LOCKED)));

            //Try to update the config of locked widget with a not null config as previous value using a null config

            widgetTypeCode = "entando_apis";
            request.setCode(widgetTypeCode);
            request.setConfig(null);
            result = this.executeWidgetPut(request, widgetTypeCode, accessToken, status().isBadRequest());
            result.andDo(resultPrint());

            result.andExpect(jsonPath("$.errors[0].code", is(WidgetValidator.ERRCODE_OPERATION_FORBIDDEN_LOCKED)));

            //Try to update the config of locked widget with the old value

            final ApsProperties config = widgetTypeManager.getWidgetType(widgetTypeCode).getConfig();

            titles = new HashMap<>();
            titles.put("it", "APIs");
            titles.put("en", "APIs");

            request.setTitles(titles);

            request.setCode(widgetTypeCode);
            Map<String, String> configMap = (Map) config;

            request.setConfig(configMap);
            this.executeWidgetPut(request, widgetTypeCode, accessToken, status().isOk());

        } catch (Exception e) {
            throw e;
        } finally {
            List<String> codes = this.guiFragmentManager.getGuiFragmentCodesByWidgetType("entando_apis");
            for (int i = 0; i < codes.size(); i++) {
                this.guiFragmentManager.deleteGuiFragment(codes.get(i));
            }
        }
    }
    
    @Test
    void testComponentExistenceAnalysis() throws Exception {
        // should return DIFF for existing component
        AnalysisControllerDiffAnalysisEngineTestsStubs.testComponentEngineAnalysisResult(
                AnalysisControllerDiffAnalysisEngineTestsStubs.COMPONENT_WIDGETS,
                "login_form",
                AnalysisControllerDiffAnalysisEngineTestsStubs.STATUS_DIFF,
                new ContextOfControllerTests(mockMvc, mapper)
        );

        // should return NEW for NON existing component
        AnalysisControllerDiffAnalysisEngineTestsStubs.testComponentEngineAnalysisResult(
                AnalysisControllerDiffAnalysisEngineTestsStubs.COMPONENT_WIDGETS,
                "AN_NONEXISTENT_CODE",
                AnalysisControllerDiffAnalysisEngineTestsStubs.STATUS_NEW,
                new ContextOfControllerTests(mockMvc, mapper)
        );
    }
    
    private ResultActions executeWidgetGet(String widgetTypeCode, String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(get("/widgets/{code}", new Object[]{widgetTypeCode})
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }

    private ResultActions executeWidgetUsage(String widgetTypeCode, String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(get("/widgets/{code}/usage", new Object[]{widgetTypeCode})
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }
    
    private ResultActions executeWidgetPost(WidgetRequest request, String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(post("/widgets")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }
    
    private ResultActions executeWidgetPut(WidgetRequest request, String widgetTypeCode, String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(put("/widgets/{code}", new Object[]{widgetTypeCode})
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }
    
    private ResultActions executeWidgetDelete(String widgetTypeCode, String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc.perform(
                delete("/widgets/{code}", new Object[]{widgetTypeCode})
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }
    
}

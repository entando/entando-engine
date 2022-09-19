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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import java.util.HashMap;
import java.util.Map;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.aps.system.services.widgettype.WidgetService;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.aps.system.services.widgettype.model.WidgetDto;
import org.entando.entando.web.AbstractControllerTest;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.entando.entando.web.widget.model.WidgetRequest;
import org.entando.entando.web.widget.validator.WidgetValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class WidgetControllerTest extends AbstractControllerTest {

    @Mock
    private WidgetService widgetService;

    @Mock
    private WidgetType widgetType;

    @Mock
    private IWidgetTypeManager widgetTypeManager;

    @Spy @InjectMocks
    private WidgetValidator widgetValidator = Mockito.spy(WidgetValidator.class);

    @InjectMocks
    private WidgetController controller;

    @BeforeEach
    public void setUp() throws Exception {
        //MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .addInterceptors(entandoOauth2Interceptor)
                .setHandlerExceptionResolvers(createHandlerExceptionResolver())
                .build();
    }

    @Test
    void testGetWidget() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES)
                .build();

        String accessToken = mockOAuthInterceptor(user);
        // @formatter:off
        ResultActions result = mockMvc.perform(
                get("/widgets/1")
                        .header("Authorization", "Bearer " + accessToken)
        );
        String response = result.andReturn().getResponse().getContentAsString();
        assertNotNull(response);
    }

    @Test
    void testGetWidgetInfo() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        // @formatter:off
        ResultActions result = mockMvc.perform(
                get("/widgets/1/info")
                        .header("Authorization", "Bearer " + accessToken)
        );
        String response = result.andReturn().getResponse().getContentAsString();
        assertNotNull(response);
    }

    @Test
    void testGetWidgetList() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES)
                .build();
        String accessToken = mockOAuthInterceptor(user);
        PagedMetadata<WidgetDto> pagedDto = new PagedMetadata<>();
        when(widgetService.getWidgets(any())).thenReturn(pagedDto);
        // @formatter:off
        ResultActions result = mockMvc.perform(
                get("/widgets")
                        .header("Authorization", "Bearer " + accessToken)
        );
        result.andExpect(status().isOk());
        String response = result.andReturn().getResponse().getContentAsString();
        assertNotNull(response);
    }

    @Test
    void testRemoveWidget() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        // @formatter:off
        ResultActions result = mockMvc.perform(
                delete("/widgets/1")
                        .header("Authorization", "Bearer " + accessToken)
        );
        result.andExpect(status().isOk());
        String response = result.andReturn().getResponse().getContentAsString();
        assertNotNull(response);
    }

    @Test
    void testAddWidget() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        this.controller.setWidgetValidator(this.widgetValidator);
        Mockito.lenient().when(widgetTypeManager.getWidgetType(Mockito.anyString())).thenReturn(this.widgetType);
        
        // @formatter:off
        ResultActions result = mockMvc.perform(
                post("/widgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonBytes(createMockRequest()))
                        .header("Authorization", "Bearer " + accessToken)
        );
        String response = result.andReturn().getResponse().getContentAsString();
        result.andExpect(status().isOk());
        assertNotNull(response);
        Mockito.verify(widgetService, Mockito.times(1)).addWidget(Mockito.any(WidgetRequest.class));
    }

    @Test
    void failTestAddWidget_1() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        this.controller.setWidgetValidator(this.widgetValidator);
        WidgetRequest mockRequest = this.createMockRequest();
        mockRequest.setParentType("parentCode");
        Mockito.lenient().when(widgetTypeManager.getWidgetType("parentCode")).thenReturn(null);
        // @formatter:off
        ResultActions result = mockMvc.perform(
                post("/widgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonBytes(mockRequest))
                        .header("Authorization", "Bearer " + accessToken)
        );
        String response = result.andReturn().getResponse().getContentAsString();
        result.andExpect(status().isNotFound());
        assertNotNull(response);
        Mockito.verify(widgetService, Mockito.times(0)).addWidget(Mockito.any(WidgetRequest.class));
    }

    @Test
    void testUpdateWidget() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        this.controller.setWidgetValidator(this.widgetValidator);
        Mockito.lenient().when(widgetTypeManager.getWidgetType(Mockito.anyString())).thenReturn(this.widgetType);
        when(widgetService.updateWidget(any(), any())).thenReturn(new WidgetDto());
        // @formatter:off
        ResultActions result = mockMvc.perform(
                put("/widgets/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonBytes(createMockRequest()))
                        .header("Authorization", "Bearer " + accessToken)
        );
        String response = result.andReturn().getResponse().getContentAsString();
        result.andExpect(status().isOk());
        assertNotNull(response);
        Mockito.verify(widgetService, Mockito.times(1)).updateWidget(Mockito.anyString(), Mockito.any(WidgetRequest.class));
    }

    @Test
    void testUnauthorized() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withGroup(Group.FREE_GROUP_NAME)
                .build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc.perform(
                get("/widgets")
                        .header("Authorization", "Bearer " + accessToken)
        );
        result.andExpect(status().isForbidden());
    }

    private WidgetRequest createMockRequest() {
        Map<String, String> titles = new HashMap<>();
        titles.put("it", "il titolo");
        titles.put("en", "the test title");
        WidgetRequest req = new WidgetRequest();
        req.setCode("test");
        req.setGroup("test");
        req.setCustomUi("<h1>UI Code</h1>");
        req.setTitles(titles);
        return req;
    }

}

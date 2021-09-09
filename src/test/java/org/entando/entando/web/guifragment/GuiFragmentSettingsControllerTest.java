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

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.user.UserDetails;
import java.util.Map;
import org.entando.entando.aps.system.services.guifragment.IGuiFragmentManager;
import org.entando.entando.web.AbstractControllerTest;
import org.entando.entando.web.utils.OAuth2TestUtils;

import static org.hamcrest.CoreMatchers.is;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;

import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GuiFragmentSettingsControllerTest extends AbstractControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IGuiFragmentManager guiFragmentManager;

    @InjectMocks
    private GuiFragmentSettingsController controller;

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .addInterceptors(entandoOauth2Interceptor)
                .setHandlerExceptionResolvers(createHandlerExceptionResolver())
                .build();
    }

    @Test
    void getSetting_1() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        when(this.guiFragmentManager.getConfig(IGuiFragmentManager.CONFIG_PARAM_EDIT_EMPTY_FRAGMENT_ENABLED)).thenReturn("true");
        ResultActions result = mockMvc.perform(
                get("/fragmentsSettings")
                .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.payload." + GuiFragmentSettingsController.RESULT_PARAM_NAME, is(true)));
    }

    @Test
    void getSetting_2() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        when(this.guiFragmentManager.getConfig(IGuiFragmentManager.CONFIG_PARAM_EDIT_EMPTY_FRAGMENT_ENABLED)).thenReturn("invalid");
        ResultActions result = mockMvc.perform(
                get("/fragmentsSettings").header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.payload." + GuiFragmentSettingsController.RESULT_PARAM_NAME, is(false)));
    }

    @Test
    void updateWithSuccess() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        String payload = "{\"enableEditingWhenEmptyDefaultGui\":true}";
        ResultActions result = this.executePut(payload, accessToken, status().isOk());
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.payload." + GuiFragmentSettingsController.RESULT_PARAM_NAME, is(true)));
        Mockito.verify(this.guiFragmentManager, Mockito.times(1)).updateParams(Mockito.any(Map.class));
    }

    @Test
    void updateWithFailure() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        String payload = "{}";
        ResultActions result = this.executePut(payload, accessToken, status().isBadRequest());
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
        Mockito.verify(this.guiFragmentManager, Mockito.times(0)).updateParams(Mockito.any(Map.class));
    }

    private ResultActions executePut(String body, String accessToken, ResultMatcher rm) throws Exception {
        ResultActions result = mockMvc
                .perform(put("/fragmentsSettings").content(body)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(rm);
        return result;
    }

}

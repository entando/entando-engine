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
package org.entando.entando.web;

import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.user.IAuthenticationProviderManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.entando.entando.TestEntandoJndiUtils;
import org.entando.entando.aps.system.services.oauth2.IApiOAuth2TokenManager;
import org.entando.entando.web.common.interceptor.EntandoOauth2Interceptor;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.Resource;
import javax.servlet.Filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {
    "classpath*:spring/testpropertyPlaceholder.xml",
    "classpath*:spring/baseSystemConfig.xml",
    "classpath*:spring/aps/**/**.xml",
    "classpath*:spring/apsadmin/**/**.xml",
    "classpath*:spring/plugins/**/aps/**/**.xml",
    "classpath*:spring/plugins/**/apsadmin/**/**.xml",
    "classpath*:spring/web/**.xml"
})
@WebAppConfiguration(value = "")
public class AbstractControllerIntegrationTest {

    protected MockMvc mockMvc;

    @Resource
    protected WebApplicationContext webApplicationContext;

    @Autowired
    private Filter springSecurityFilterChain;

    protected IApiOAuth2TokenManager apiOAuth2TokenManager;

    protected IAuthenticationProviderManager authenticationProviderManager;

    protected IAuthorizationManager authorizationManager;
    
    @Autowired
    protected EntandoOauth2Interceptor entandoOauth2Interceptor;

    @Autowired
    protected CorsFilter corsFilter;

    @BeforeAll
    public static void setup() throws Exception {
        TestEntandoJndiUtils.setupJndi();
    }

    @BeforeEach
    public void setUp() throws Exception {
        this.apiOAuth2TokenManager = Mockito.mock(IApiOAuth2TokenManager.class);
        this.authenticationProviderManager = Mockito.mock(IAuthenticationProviderManager.class);
        this.authorizationManager = Mockito.mock(IAuthorizationManager.class);
        this.entandoOauth2Interceptor.setAuthenticationProviderManager(this.authenticationProviderManager);
        this.entandoOauth2Interceptor.setAuthorizationManager(this.authorizationManager);
        this.entandoOauth2Interceptor.setoAuth2TokenManager(this.apiOAuth2TokenManager);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .dispatchOptions(true)
                .addFilters(springSecurityFilterChain, corsFilter)
                .build();
        accessToken = null;
    }

    protected String mockOAuthInterceptor(UserDetails user) {
        return OAuth2TestUtils.mockOAuthInterceptor(apiOAuth2TokenManager, authenticationProviderManager, authorizationManager, user);
    }
    
    protected AuthRequestBuilder createAuthRequest(MockHttpServletRequestBuilder requestBuilder) {
        return new AuthRequestBuilder(mockMvc, getAccessToken(), requestBuilder);
    }

    private String accessToken;

    private String getAccessToken() {
        if (this.accessToken == null) {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            this.accessToken = OAuth2TestUtils.mockOAuthInterceptor(apiOAuth2TokenManager, authenticationProviderManager, authorizationManager, user);
        }
        return this.accessToken;
    }

    public void testCors(String endpoint, HttpMethod method) throws Exception {
        ResultActions result = mockMvc
                .perform(options(endpoint)
                        .header(HttpHeaders.ORIGIN, "http://www.someurl.com/")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, method.name())
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Content-Type", "Authorization")
                );

        result.andExpect(status().isOk());
        result.andExpect(header().string("Access-Control-Allow-Origin", "*"));
        result.andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,PATCH"));
        result.andExpect(header().string("Access-Control-Allow-Headers", "Content-Type, Authorization"));
        result.andExpect(header().doesNotExist("Access-Control-Allow-Credentials"));
        result.andExpect(header().string("Access-Control-Max-Age", "3600"));

    }

    public void testCors(String endpoint) throws Exception {
        ResultActions result = mockMvc
                .perform(options(endpoint)
                        .header(HttpHeaders.ORIGIN, "http://www.someurl.com/")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Content-Type", "Authorization")
                );

        result.andExpect(status().isOk());
        result.andExpect(header().string("Access-Control-Allow-Origin", "*"));
        result.andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,PATCH"));
        result.andExpect(header().string("Access-Control-Allow-Headers", "Content-Type, Authorization"));
        result.andExpect(header().doesNotExist("Access-Control-Allow-Credentials"));
        result.andExpect(header().string("Access-Control-Max-Age", "3600"));

    }

    public class ContextOfControllerTests {
        public final MockMvc mockMvc;
        public final ObjectMapper jsonMapper;
        public final IApiOAuth2TokenManager apiOAuth2TokenManager = AbstractControllerIntegrationTest.this.apiOAuth2TokenManager;
        public final IAuthenticationProviderManager authenticationProviderManager = AbstractControllerIntegrationTest.this.authenticationProviderManager;
        public final IAuthorizationManager authorizationManager = AbstractControllerIntegrationTest.this.authorizationManager;

        public ContextOfControllerTests(
                MockMvc mockMvc, ObjectMapper jsonMapper) {
            this.mockMvc = mockMvc;
            this.jsonMapper = jsonMapper;
        }
    }

    private static class NopResultHandler implements ResultHandler {
        private NopResultHandler() {
        }

        public void handle(MvcResult result) throws Exception {
        }
    }

    boolean restResultPrintIsEnabled = ("" + System.getProperty("org.entando.test.enableResultPrint")).equals("true");

    public ResultHandler resultPrint() {
        return (restResultPrintIsEnabled)
                ? MockMvcResultHandlers.print()
                : new NopResultHandler();
    }

}

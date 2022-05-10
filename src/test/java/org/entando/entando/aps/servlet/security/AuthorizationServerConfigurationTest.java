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
package org.entando.entando.aps.servlet.security;

import com.jayway.jsonpath.JsonPath;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.services.oauth2.IApiOAuth2TokenManager;
import org.springframework.security.crypto.codec.Base64;
import org.entando.entando.web.AbstractControllerIntegrationTest;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class AuthorizationServerConfigurationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private IApiOAuth2TokenManager apiOAuth2TokenManager;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.removeTokens("admin", "mainEditor", "supervisorCustomers");
    }

    @Test
    void obtainAccessToken() throws Exception {
        this.obtainAccessToken("admin", "admin", true);
        this.obtainAccessToken("mainEditor", "mainEditor", true);
        this.obtainAccessToken("supervisorCustomers", "supervisorCustomers", true);
    }

    private OAuth2AccessToken obtainAccessToken(String username, String password, boolean remove) throws Exception {
        OAuth2AccessToken oauthToken = null;
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "password");
            params.add("username", username);
            params.add("password", password);
            String hash = new String(Base64.encode("test1_consumer:secret".getBytes()));
            ResultActions result
                    = mockMvc.perform(post("/oauth/token")
                            .params(params)
                            .header("Authorization", "Basic " + hash)
                            .accept("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"));
            String resultString = result.andReturn().getResponse().getContentAsString();
            System.out.println(resultString);
            Assertions.assertTrue(StringUtils.isNotBlank(resultString));
            String token = JsonPath.parse(resultString).read("$.access_token");
            Assertions.assertTrue(StringUtils.isNotBlank(token));
            Collection<OAuth2AccessToken> oauthTokens = apiOAuth2TokenManager.findTokensByUserName(username);
            Assertions.assertEquals(1, oauthTokens.size());
            oauthToken = oauthTokens.stream().findFirst().get();
            Assertions.assertEquals(token, oauthToken.getValue());
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != oauthToken && remove) {
                this.apiOAuth2TokenManager.removeAccessToken(oauthToken);
            }
        }
        return oauthToken;
    }

    @Test
    void refreshAccessToken() throws Exception {
        OAuth2AccessToken accessToken = this.obtainAccessToken("admin", "admin", false);
        this.refreshAccessToken(accessToken, "admin");
        accessToken = this.obtainAccessToken("mainEditor", "mainEditor", false);
        this.refreshAccessToken(accessToken, "mainEditor");
        accessToken = this.obtainAccessToken("supervisorCustomers", "supervisorCustomers", false);
        this.refreshAccessToken(accessToken, "supervisorCustomers");
    }

    private void refreshAccessToken(OAuth2AccessToken accessToken, String username) throws Exception {
        String refreshToken = accessToken.getRefreshToken().getValue();
        try {
            Assertions.assertNotNull(this.apiOAuth2TokenManager.readRefreshToken(refreshToken));
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "refresh_token");
            params.add("refresh_token", refreshToken);
            String hash = new String(Base64.encode("test1_consumer:secret".getBytes()));
            ResultActions result
                    = mockMvc.perform(post("/oauth/token")
                            .params(params)
                            .header("Authorization", "Basic " + hash)
                            .accept("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"));
            String resultString = result.andReturn().getResponse().getContentAsString();
            System.out.println(resultString);
            Assertions.assertTrue(StringUtils.isNotBlank(resultString));
            String newAccesstoken = JsonPath.parse(resultString).read("$.access_token");
            Assertions.assertFalse(newAccesstoken.equals(accessToken.getValue()));
            String newRefreshtoken = JsonPath.parse(resultString).read("$.refresh_token");
            Assertions.assertNotEquals(newRefreshtoken, refreshToken);
            Collection<OAuth2AccessToken> oauthTokens = this.apiOAuth2TokenManager.findTokensByUserName(username);
            Assertions.assertEquals(1, oauthTokens.size());
            OAuth2AccessToken newOauthToken = oauthTokens.stream().findFirst().get();
            Assertions.assertEquals(newAccesstoken, newOauthToken.getValue());
            Assertions.assertEquals(newRefreshtoken, newOauthToken.getRefreshToken().getValue());
            Assertions.assertNull(this.apiOAuth2TokenManager.readRefreshToken(refreshToken));
        } catch (Exception e) {
            throw e;
        } finally {
            Collection<OAuth2AccessToken> tokens = this.apiOAuth2TokenManager.findTokensByUserName(username);
            for (OAuth2AccessToken token : tokens) {
                this.apiOAuth2TokenManager.removeAccessToken(token);
            }
        }
    }

    @Test
    void authenticationFailed() throws Exception {
        this.authenticationFailed("admin", "adminxx");
        this.authenticationFailed("admin", "");
        this.authenticationFailed("", "admin");
        this.authenticationFailed("mainEditor", "mainEditorxx");
        this.authenticationFailed("supervisorCustomers", "supervisorCustomersxx");
    }

    private void authenticationFailed(String username, String password) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", username);
        params.add("password", password);
        String hash = new String(Base64.encode("test1_consumer:secret".getBytes()));
        ResultActions result
                = mockMvc.perform(post("/oauth/token")
                        .params(params)
                        .header("Authorization", "Basic " + hash)
                        .accept("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
        String resultString = result.andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.isNotBlank(resultString));
        result.andExpect(jsonPath("$.error", is("invalid_grant")));
        result.andExpect(jsonPath("$.error_description", anything()));
        if (!StringUtils.isEmpty(username)) {
            Collection<OAuth2AccessToken> oauthTokens = apiOAuth2TokenManager.findTokensByUserName(username);
            Assertions.assertEquals(0, oauthTokens.size());
        }
    }

    @Test
    void invalidClient() throws Exception {
        this.invalidClient("mainEditor", "mainEditor", "test2_consumer", "secret", "password");
    }

    private void invalidClient(String username, String password, String clientId, String secret, String grantType) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("username", username);
        params.add("password", password);
        String hash = new String(Base64.encode((clientId + ":" + secret).getBytes()));
        ResultActions result
                = mockMvc.perform(post("/oauth/token")
                        .params(params)
                        .header("Authorization", "Basic " + hash)
                        .accept("application/json;charset=UTF-8"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
        String resultString = result.andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.isNotBlank(resultString));
        result.andExpect(jsonPath("$.error", is("invalid_client")));
        String expectedMessage = "Unauthorized grant type";
        result.andExpect(jsonPath("$.error_description", is(expectedMessage)));
        Collection<OAuth2AccessToken> oauthTokens = apiOAuth2TokenManager.findTokensByUserName(username);
        Assertions.assertEquals(0, oauthTokens.size());
    }

    @Test
    void missingGrant() throws Exception {
        this.missingGrant("admin", "admin", "test1_consumer", "secret", "");
        this.missingGrant("admin", "adminxx", "test1_consumer", "secret", "");
        this.missingGrant("mainEditor", "mainEditor", "test1_consumer", "secret", "");
        this.missingGrant("mainEditor", "mainEditorxx", "test1_consumer", "secret", "");
        this.missingGrant("mainEditor", "mainEditor", "test1_consumer", "secret", null);
        this.missingGrant("mainEditor", "mainEditor", "test2_consumer", "secret", "");
    }

    private void missingGrant(String username, String password, String clientId, String secret, String grantType) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("username", username);
        params.add("password", password);
        String hash = new String(Base64.encode((clientId + ":" + secret).getBytes()));
        ResultActions result
                = mockMvc.perform(post("/oauth/token")
                        .params(params)
                        .header("Authorization", "Basic " + hash)
                        .accept("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
        String resultString = result.andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.isNotBlank(resultString));
        result.andExpect(jsonPath("$.error", is("invalid_request")));
        result.andExpect(jsonPath("$.error_description", is("Missing grant type")));
        Collection<OAuth2AccessToken> oauthTokens = apiOAuth2TokenManager.findTokensByUserName(username);
        Assertions.assertEquals(0, oauthTokens.size());
    }

    @Test
    void unauthorized() throws Exception {
        this.unauthorized("admin", "admin", "test1_consumer", "secretwrong");
        this.unauthorized("admin", "admin", "", "secret");
        this.unauthorized("admin", "admin", "test1_consumer", "");
        this.unauthorized("admin", "admin", null, "secret");
        this.unauthorized("admin", "admin", "test1_consumer", null);
    }

    private void unauthorized(String username, String password, String clientId, String secret) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", username);
        params.add("password", password);
        String hash = new String(Base64.encode((clientId + ":" + secret).getBytes()));
        ResultActions result
                = mockMvc.perform(post("/oauth/token")
                        .params(params)
                        .header("Authorization", "Basic " + hash)
                        .accept("application/json;charset=UTF-8"))
                .andExpect(status().isUnauthorized());
        String resultString = result.andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.isBlank(resultString));
        if (!StringUtils.isEmpty(username)) {
            Collection<OAuth2AccessToken> oauthTokens = apiOAuth2TokenManager.findTokensByUserName(username);
            Assertions.assertEquals(0, oauthTokens.size());
        }
    }

    private void removeTokens(String... usernames) {
        for (String username : usernames) {
            Collection<OAuth2AccessToken> oauthTokens = apiOAuth2TokenManager.findTokensByUserName(username);
            oauthTokens.stream().forEach(oaat -> apiOAuth2TokenManager.removeAccessToken(oaat));
        }
    }

}

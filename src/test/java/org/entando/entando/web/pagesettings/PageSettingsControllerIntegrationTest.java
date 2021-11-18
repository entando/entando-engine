/*
 * Copyright 2021-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.web.pagesettings;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.pagesettings.model.PageSettingsRequest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

class PageSettingsControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private IPageManager pageManager;

    @Test
    void testGetPageSettings() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = this.executeGetPageSettings(accessToken, status().isOk());
        Map<String, String> parameters = this.pageManager.getParams();
        result.andExpect(jsonPath("$.payload.size()", is(parameters.size())));
        Iterator<Map.Entry<String, String>> iter = parameters.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            result.andExpect(jsonPath("$.payload." + entry.getKey(), is(entry.getValue())));
        }
    }
    
    @Test
    void testUpdatePageSettings() throws Exception {
        Map<String, String> initialParameters = this.pageManager.getParams();
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        Map<String, String> modifiedParameters = new HashMap<>();
        modifiedParameters.put(IPageManager.CONFIG_PARAM_URL_STYLE, IPageManager.CONFIG_PARAM_URL_STYLE_BREADCRUMBS);
        modifiedParameters.put(IPageManager.CONFIG_PARAM_ERROR_PAGE_CODE, "xxxx");
        modifiedParameters.put("addedParameter", "new value");
        try {
            ResultActions result = this.executeUpdatePageSettings(modifiedParameters, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.size()", is(initialParameters.size())));
            Iterator<Map.Entry<String, String>> iter = initialParameters.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                if (entry.getKey().equals(IPageManager.CONFIG_PARAM_URL_STYLE)) {
                    result.andExpect(jsonPath("$.payload." + entry.getKey(), is(IPageManager.CONFIG_PARAM_URL_STYLE_BREADCRUMBS)));
                } else if (entry.getKey().equals(IPageManager.CONFIG_PARAM_ERROR_PAGE_CODE)) {
                    result.andExpect(jsonPath("$.payload." + entry.getKey(), is("xxxx")));
                } else {
                    result.andExpect(jsonPath("$.payload." + entry.getKey(), is(entry.getValue())));
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.pageManager.updateParams(initialParameters);
            Map<String, String> restoredParameters = this.pageManager.getParams();
            Iterator<Map.Entry<String, String>> iter = restoredParameters.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                Assertions.assertEquals(initialParameters.get(entry.getKey()), entry.getValue());
            }
        }
    }

    private ResultActions executeGetPageSettings(String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(get("/pageSettings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }

    private ResultActions executeUpdatePageSettings(Map<String, String> params, String accessToken, ResultMatcher expected) throws Exception {
        PageSettingsRequest request = new PageSettingsRequest();
        if (null != params) {
            params.entrySet().stream().forEach(e -> request.put(e.getKey(), e.getValue()));
        }
        ResultActions result = mockMvc
                .perform(put("/pageSettings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonBytes(request))
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }

    protected byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

}

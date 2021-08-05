/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.pagesettings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.services.page.IPageManager;
import org.entando.entando.aps.system.services.pagesettings.model.PageSettingsDto;
import org.entando.entando.web.pagesettings.model.PageSettingsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author paddeo
 */
class PageSettingsServiceIntegrationTest extends BaseTestCase {

    private IPageSettingsService pageSettingsService;

    @BeforeEach
    private void init() throws Exception {
        try {
            pageSettingsService = (IPageSettingsService) this.getApplicationContext().getBean(IPageSettingsService.BEAN_NAME);
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    void testGetSettings() {
        PageSettingsDto settings = pageSettingsService.getPageSettings();
        assertNotNull(settings);
        assertTrue(settings.size() > 0);
        assertTrue(settings.keySet().stream().anyMatch(param -> param.equals(IPageManager.CONFIG_PARAM_BASE_URL)));
        assertEquals("request", settings.get(settings.keySet().stream().filter(param -> param.equals(IPageManager.CONFIG_PARAM_BASE_URL)).findFirst().get()));
    }

    @Test
    void testUpdateSettings() {
        PageSettingsDto settings = pageSettingsService.getPageSettings();
        assertNotNull(settings);
        assertTrue(settings.size() > 1);
        assertTrue(settings.keySet().stream().anyMatch(param -> param.equals(IPageManager.CONFIG_PARAM_BASE_URL_CONTEXT)));
        assertEquals("true", settings.get(settings.keySet().stream().filter(param -> param.equals(IPageManager.CONFIG_PARAM_BASE_URL_CONTEXT)).findFirst().get()));
        
        PageSettingsRequest request = new PageSettingsRequest();
        request.put(IPageManager.CONFIG_PARAM_BASE_URL_CONTEXT, "false");
        settings = pageSettingsService.updatePageSettings(request);
        assertNotNull(settings);
        assertTrue(settings.size() > 1);
        assertTrue(settings.keySet().stream().anyMatch(param -> param.equals(IPageManager.CONFIG_PARAM_BASE_URL_CONTEXT)));
        assertEquals("false", settings.get(settings.keySet().stream().filter(param -> param.equals(IPageManager.CONFIG_PARAM_BASE_URL_CONTEXT)).findFirst().get()));

        settings = pageSettingsService.getPageSettings();
        assertNotNull(settings);
        assertTrue(settings.size() > 1);
        assertTrue(settings.keySet().stream().anyMatch(param -> param.equals(IPageManager.CONFIG_PARAM_BASE_URL_CONTEXT)));
        assertEquals("false", settings.get(settings.keySet().stream().filter(param -> param.equals(IPageManager.CONFIG_PARAM_BASE_URL_CONTEXT)).findFirst().get()));
        
        request.put(IPageManager.CONFIG_PARAM_BASE_URL_CONTEXT, "true");
        settings = pageSettingsService.updatePageSettings(request);
        assertNotNull(settings);
        assertTrue(settings.size() > 1);
        assertTrue(settings.keySet().stream().anyMatch(param -> param.equals(IPageManager.CONFIG_PARAM_BASE_URL_CONTEXT)));
        assertEquals("true", settings.get(settings.keySet().stream().filter(param -> param.equals(IPageManager.CONFIG_PARAM_BASE_URL_CONTEXT)).findFirst().get()));
    }

}

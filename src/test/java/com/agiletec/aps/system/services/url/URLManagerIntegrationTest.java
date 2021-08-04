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
package com.agiletec.aps.system.services.url;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.baseconfig.SystemParamsUtils;
import com.agiletec.aps.system.services.page.IPageManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class URLManagerIntegrationTest extends BaseTestCase {

    private ConfigInterface configManager = null;
    private IURLManager urlManager = null;

    @Test
    void testGetURLString_1() throws Throwable {
        RequestContext reqCtx = this.getRequestContext();
        PageURL pageURL = urlManager.createURL(reqCtx);
        pageURL.setLangCode("it");
        pageURL.setPageCode("homepage");
        try {
            String url = this.urlManager.getURLString(pageURL, reqCtx);
            assertEquals("http://www.entando.com/Entando/it/homepage.page", url);
            this.changeUrlStyle(IPageManager.CONFIG_PARAM_URL_STYLE_BREADCRUMBS);
            url = this.urlManager.getURLString(pageURL, reqCtx);
            assertEquals("http://www.entando.com/Entando/pages/it/homepage/", url);
        } catch (Throwable t) {
            throw t;
        } finally {
            this.changeUrlStyle(IPageManager.CONFIG_PARAM_URL_STYLE_CLASSIC);
        }
    }

    @Test
    void testGetURLString_2() throws Throwable {
        RequestContext reqCtx = this.getRequestContext();
        PageURL pageURL = urlManager.createURL(reqCtx);
        pageURL.setLangCode("en");
        pageURL.setPageCode("pagina_11");
        try {
            String url = this.urlManager.getURLString(pageURL, reqCtx);
            assertEquals("http://www.entando.com/Entando/en/pagina_11.page", url);
            this.changeUrlStyle(IPageManager.CONFIG_PARAM_URL_STYLE_BREADCRUMBS);
            url = this.urlManager.getURLString(pageURL, reqCtx);
            assertEquals("http://www.entando.com/Entando/pages/en/homepage/pagina_1/pagina_11/", url);
        } catch (Throwable t) {
            throw t;
        } finally {
            this.changeUrlStyle(IPageManager.CONFIG_PARAM_URL_STYLE_CLASSIC);
        }
    }

    private void changeUrlStyle(String styleType) throws Throwable {
        try {
            String xmlParams = this.configManager.getConfigItem(SystemConstants.CONFIG_ITEM_PARAMS);
            Map<String, String> systemParams = SystemParamsUtils.getParams(xmlParams);
            systemParams.put(IPageManager.CONFIG_PARAM_URL_STYLE, styleType);
            String newXmlParams = SystemParamsUtils.getNewXmlParams(xmlParams, systemParams);
            this.configManager.updateConfigItem(SystemConstants.CONFIG_ITEM_PARAMS, newXmlParams);
        } catch (Throwable t) {
            throw t;
        }
    }

    @BeforeEach
    private void init() {
        this.urlManager = (IURLManager) this.getService(SystemConstants.URL_MANAGER);
        this.configManager = (ConfigInterface) this.getService(SystemConstants.BASE_CONFIG_MANAGER);
    }

}

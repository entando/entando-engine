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
package com.agiletec.aps.system.services.url;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class URLManagerTest {

    @InjectMocks
    private URLManager urlManager;
    
    @Mock
    private ConfigInterface configManager;
    @Mock
    private IPageManager pageManager;
    @Mock
    private ILangManager langManager;
    
    private MockHttpServletRequest request = new MockHttpServletRequest();;
    
    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Lang defaultLang = mock(Lang.class);
        Mockito.lenient().when(defaultLang.getCode()).thenReturn("en");
        Mockito.lenient().when(langManager.getDefaultLang()).thenReturn(defaultLang);
        IPage page = mock(IPage.class);
        Mockito.lenient().when(pageManager.getOnlinePage(Mockito.anyString())).thenReturn(page);
        Mockito.lenient().when(page.getCode()).thenReturn("homepage");
        Mockito.lenient().when(configManager.getParam(SystemConstants.PAR_APPL_BASE_URL)).thenReturn("http://www.entando.com/Entando");
    }
    
    @Test
    void testStaticBaseUrl() throws Throwable {
        String expectedUrl = "http://www.entando.com/Entando/it/homepage.page";
        RequestContext reqCtx = this.createRequestContext();
        Lang requestedLang = new Lang();
        requestedLang.setCode("it");
        requestedLang.setDescr("Italiano");
        Mockito.lenient().when(langManager.getLang("it")).thenReturn(requestedLang);
        PageURL pageURL = urlManager.createURL(reqCtx);
        pageURL.setLangCode("it");
        pageURL.setPageCode("homepage");
        String url = this.urlManager.getURLString(pageURL, reqCtx);
        assertEquals(expectedUrl, url);
        
        pageURL.setBaseUrlMode("current");
        url = this.urlManager.getURLString(pageURL, reqCtx);
        assertEquals(expectedUrl, url);
        
        pageURL.setBaseUrlMode("requestIfRelative");
        url = this.urlManager.getURLString(pageURL, reqCtx);
        assertEquals(expectedUrl, url);
        
        Mockito.lenient().when(pageManager.getConfig(IPageManager.CONFIG_PARAM_BASE_URL)).thenReturn(IPageManager.CONFIG_PARAM_BASE_URL_FROM_REQUEST);
        pageURL.setBaseUrlMode(IPageManager.CONFIG_PARAM_BASE_URL_STATIC);
        url = this.urlManager.getURLString(pageURL, reqCtx);
        assertEquals(expectedUrl, url);
    }
    
    @Test
    void testRelativeBaseUrl() throws Throwable {
        String expectedUrl = "/en/homepage.page";
        RequestContext reqCtx = this.createRequestContext();
        PageURL pageURL = urlManager.createURL(reqCtx);
        pageURL.setLangCode("en");
        pageURL.setPageCode("homepage");
        Mockito.lenient().when(pageManager.getConfig(IPageManager.CONFIG_PARAM_BASE_URL)).thenReturn(IPageManager.CONFIG_PARAM_BASE_URL_RELATIVE);
        
        String url = this.urlManager.getURLString(pageURL, reqCtx);
        assertEquals(expectedUrl, url);
        
        pageURL.setBaseUrlMode("current");
        url = this.urlManager.getURLString(pageURL, reqCtx);
        assertEquals(expectedUrl, url);
        
        pageURL.setBaseUrlMode(IPageManager.SPECIAL_PARAM_BASE_URL_REQUEST_IF_RELATIVE);
        url = this.urlManager.getURLString(pageURL, reqCtx);
        assertEquals("http://www.entando.org/en/homepage.page", url);
        
        Mockito.lenient().when(pageManager.getConfig(IPageManager.CONFIG_PARAM_BASE_URL_CONTEXT)).thenReturn("true");
        url = this.urlManager.getURLString(pageURL, reqCtx);
        assertEquals("http://www.entando.org/entando/en/homepage.page", url);
        
        request.addHeader("X-Forwarded-Proto", "https");
        url = this.urlManager.getURLString(pageURL, reqCtx);
        assertEquals("https://www.entando.org/entando/en/homepage.page", url);
        
        pageURL.setBaseUrlMode("current");
        url = this.urlManager.getURLString(pageURL, reqCtx);
        assertEquals("/entando" + expectedUrl, url);
        
        Mockito.lenient().when(pageManager.getConfig(IPageManager.CONFIG_PARAM_BASE_URL)).thenReturn(IPageManager.CONFIG_PARAM_BASE_URL_FROM_REQUEST);
        pageURL.setBaseUrlMode(IPageManager.CONFIG_PARAM_BASE_URL_RELATIVE);
        url = this.urlManager.getURLString(pageURL, reqCtx);
        assertEquals("/entando" + expectedUrl, url);
        
        Mockito.lenient().when(pageManager.getConfig(IPageManager.CONFIG_PARAM_BASE_URL_CONTEXT)).thenReturn("false");
        url = this.urlManager.getURLString(pageURL, reqCtx);
        assertEquals(expectedUrl, url);
    }
    
    @Test
    void testRequestBaseUrl() throws Throwable {
        String expectedUrl = "www.entando.org/en/homepage.page";
        String expectedUrlWithContext = "www.entando.org/entando/en/homepage.page";
        RequestContext reqCtx = this.createRequestContext();
        PageURL pageURL = urlManager.createURL(reqCtx);
        pageURL.setLangCode("en");
        pageURL.setPageCode("homepage");
        Mockito.lenient().when(pageManager.getConfig(IPageManager.CONFIG_PARAM_BASE_URL)).thenReturn(IPageManager.CONFIG_PARAM_BASE_URL_FROM_REQUEST);
        
        String url = this.urlManager.getURLString(pageURL, reqCtx);
        assertEquals("http://"+expectedUrl, url);
        
        Mockito.lenient().when(pageManager.getConfig(IPageManager.CONFIG_PARAM_BASE_URL_CONTEXT)).thenReturn("true");
        
        url = this.urlManager.getURLString(pageURL, reqCtx);
        assertEquals("http://"+expectedUrlWithContext, url);
        
        Mockito.lenient().when(pageManager.getConfig(IPageManager.CONFIG_PARAM_BASE_URL)).thenReturn(IPageManager.CONFIG_PARAM_BASE_URL_STATIC);
        pageURL.setBaseUrlMode(IPageManager.CONFIG_PARAM_BASE_URL_FROM_REQUEST);
        url = this.urlManager.getURLString(pageURL, reqCtx);
        assertEquals("http://"+expectedUrlWithContext, url);
        
        Mockito.lenient().when(pageManager.getConfig(IPageManager.CONFIG_PARAM_BASE_URL_CONTEXT)).thenReturn("false");
        url = this.urlManager.getURLString(pageURL, reqCtx);
        assertEquals("http://"+expectedUrl, url);
        
        request.addHeader("X-Forwarded-Proto", "https");
        url = this.urlManager.getURLString(pageURL, reqCtx);
        assertEquals("https://"+expectedUrl, url);
    }
    
    public RequestContext createRequestContext() {
        RequestContext reqCtx = new RequestContext();
        request.setScheme("http");
        request.setServerName("www.entando.org");
        request.addHeader("Host", "www.entando.org");
        request.setContextPath("/entando");
        request.setAttribute(RequestContext.REQCTX, reqCtx);
        MockHttpServletResponse response = new MockHttpServletResponse();
        reqCtx.setRequest(request);
        reqCtx.setResponse(response);
        Lang defaultLang = new Lang();
        defaultLang.setCode("en");
        defaultLang.setDescr("English");
        reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, defaultLang);
        Mockito.lenient().when(langManager.getLang("en")).thenReturn(defaultLang);
        return reqCtx;
    }
    
}

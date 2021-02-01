/*
 * Copyright 2020-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.tags;

import static javax.servlet.jsp.tagext.Tag.SKIP_BODY;
import static org.mockito.Mockito.when;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import java.io.IOException;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CspNonceTagTest {
    
    @Mock
    private PageContext pageContext;
    
    @Mock
    private ServletRequest servletRequest;
    
    @Mock
    private RequestContext reqCtx;
    
    @Mock
    private JspWriter writer;

    @InjectMocks
    private CspNonceTag nonceTag;
    
    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(pageContext.getRequest()).thenReturn(this.servletRequest);
        when(servletRequest.getAttribute(RequestContext.REQCTX)).thenReturn(this.reqCtx);
        Mockito.lenient().when(pageContext.getOut()).thenReturn(this.writer);
        this.nonceTag.release();
    }
    
    @Test
    void getNullToken() throws Throwable {
        when(reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CSP_NONCE_TOKEN)).thenReturn(null);
        int result = this.nonceTag.doStartTag();
        Assertions.assertEquals(SKIP_BODY, result);
        Mockito.verify(pageContext, Mockito.times(0)).getOut();
        Mockito.verify(pageContext, Mockito.times(0)).setAttribute(Mockito.anyString(), Mockito.anyString());
    }
    
    @Test
    void getNotNullToken() throws Throwable {
        when(reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CSP_NONCE_TOKEN)).thenReturn("mytoken");
        int result = this.nonceTag.doStartTag();
        Assertions.assertEquals(SKIP_BODY, result);
        Mockito.verify(pageContext, Mockito.times(1)).getOut();
        Mockito.verify(pageContext, Mockito.times(0)).setAttribute(Mockito.anyString(), Mockito.anyString());
    }
    
    @Test
    void getNotNullTokenWithVar() throws Throwable {
        when(reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CSP_NONCE_TOKEN)).thenReturn("mytoken");
        this.nonceTag.setVar("var");
        int result = this.nonceTag.doStartTag();
        Assertions.assertEquals(SKIP_BODY, result);
        Mockito.verify(pageContext, Mockito.times(0)).getOut();
        Mockito.verify(pageContext, Mockito.times(1)).setAttribute(Mockito.anyString(), Mockito.anyString());
    }
    
    @Test
    void getNotNullTokenWithVarAndNoEscape() throws Throwable {
        when(reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CSP_NONCE_TOKEN)).thenReturn("mytoken");
        int result = this.nonceTag.doStartTag();
        this.nonceTag.setEscapeXml(false);
        this.nonceTag.setVar("var");
        Assertions.assertEquals(SKIP_BODY, result);
        Mockito.verify(pageContext, Mockito.times(1)).getOut();
        Mockito.verify(pageContext, Mockito.times(0)).setAttribute(Mockito.anyString(), Mockito.anyString());
    }
    
    @Test
    void getNotNullTokenWithNoVarAndNoEscape() throws Throwable {
        when(reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CSP_NONCE_TOKEN)).thenReturn("mytoken");
        int result = this.nonceTag.doStartTag();
        this.nonceTag.setEscapeXml(false);
        Assertions.assertEquals(SKIP_BODY, result);
        Mockito.verify(pageContext, Mockito.times(1)).getOut();
        Mockito.verify(pageContext, Mockito.times(0)).setAttribute(Mockito.anyString(), Mockito.anyString());
    }
    
    @Test
    void testJspException() throws Exception {
        Assertions.assertThrows(JspException.class, () -> {
            when(reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CSP_NONCE_TOKEN)).thenReturn("mytoken");
            Mockito.doThrow(IOException.class).when(this.writer).write(Mockito.anyString());
            this.nonceTag.doStartTag();
        });
    }
    
}

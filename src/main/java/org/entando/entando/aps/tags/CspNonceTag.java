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

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.tags.InfoTag;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author E.Santoboni
 */
public class CspNonceTag extends ExtendedTagSupport {

    private static final Logger logger = LoggerFactory.getLogger(InfoTag.class);

    private String var;

    @Override
    public int doStartTag() throws JspException {
        ServletRequest request = this.pageContext.getRequest();
        try {
            RequestContext reqCtx = (RequestContext) request.getAttribute(RequestContext.REQCTX);
            String currentToken = (null!= reqCtx) ? (String) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CSP_NONCE_TOKEN) : null;
            if (null != currentToken) {
                if (StringUtils.isBlank(this.getVar())) {
                    if (this.getEscapeXml()) {
                        out(this.pageContext, this.getEscapeXml(), currentToken);
                    } else {
                        this.pageContext.getOut().print(currentToken);
                    }
                } else {
                    this.pageContext.setAttribute(this.getVar(), currentToken);
                }
            }
        } catch (Exception t) {
            String msg = "Error detected during tag preprocessing";
            logger.error(msg, t);
            throw new JspException(msg, t);
        }
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public void release() {
        super.release();
        this.var = null;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

}

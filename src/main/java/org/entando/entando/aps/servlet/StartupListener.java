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
package org.entando.entando.aps.servlet;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import org.entando.entando.aps.system.exception.CSRFProtectionException;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * Init the system when the web application is started
 *
 * @author E.Santoboni
 */
public class StartupListener extends org.springframework.web.context.ContextLoaderListener {

    private static final EntLogger LOGGER = EntLogFactory.getSanitizedLogger(StartupListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext svCtx = event.getServletContext();
        String msg = this.getClass().getName() + ": INIT " + svCtx.getServletContextName();
        ApsSystemUtils.directStdoutTrace(msg, true);
        super.contextInitialized(event);
        msg = this.getClass().getName() + ": INIT DONE " + svCtx.getServletContextName();
        ApsSystemUtils.directStdoutTrace(msg, true);

        boolean isActive = Objects.nonNull(System.getenv(SystemConstants.ENTANDO_CSRF_PROTECTION));
        String whiteList = System.getenv(SystemConstants.ENTANDO_CSRF_ALLOWED_DOMAINS);

        if (!isActive || !SystemConstants.CSRF_BASIC_PROTECTION.equals(System.getenv(SystemConstants.ENTANDO_CSRF_PROTECTION))) {
            LOGGER.warn("CSRF protection is not enabled");
        } else if (SystemConstants.CSRF_BASIC_PROTECTION.equals(System.getenv(SystemConstants.ENTANDO_CSRF_PROTECTION))) {
            if (whiteList != null && !whiteList.equals("")) {
                String message = "CSRF protection is enabled Domains --> ".concat(whiteList);
                LOGGER.info(message);
            } else {
                LOGGER.error("CSRF protection is enabled but the domains are initialized. Please initialize the domains");
                throw new CSRFProtectionException("CSRF protection is enabled but the domains are not initialized. Please initialize the domains");
            }
        }
        
        String cspEnabled = System.getenv(SystemConstants.CSP_HEADER_ENABLED);
        if (StringUtils.isEmpty(cspEnabled) || Boolean.TRUE.toString().equalsIgnoreCase(cspEnabled)) {
            LOGGER.info("Content Security Policy (CSP) header is enabled");
            String cspExtraConfig = System.getenv(SystemConstants.CSP_HEADER_EXTRACONFIG);
            if (!StringUtils.isEmpty(cspExtraConfig)) {
                LOGGER.info("Content Security Policy (CSP) extra-config set to: " + cspExtraConfig);
            }
        } else {
            LOGGER.warn("Content Security Policy (CSP) header is not enabled");
        }
    }

}

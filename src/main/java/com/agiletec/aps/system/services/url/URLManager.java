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

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.ent.exception.EntException;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.PageUtils;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Servizio di gestione degli url; crea un URL completo ad una pagina del
 * portale a partire da informazioni essenziali.
 *
 * @author M.Diana - E.Santoboni
 */
public class URLManager extends AbstractURLManager {

    private static final EntLogger _logger = EntLogFactory.getSanitizedLogger(URLManager.class);

    @Override
    public void init() throws Exception {
        _logger.debug("{} ready", this.getClass().getName());
    }

    /**
     * Crea un URL completo ad una pagina del portale a partire dalle
     * informazioni essenziali contenute nell'oggetto pageUrl passato come
     * parametro.<br>
     * In questa implementazione, l'URL è costruito come concatenazione dei
     * seguenti elementi:
     * <ul>
     * <li>parametro di configurazione PAR_APPL_BASE_URL, che rappresenta l'URL
     * base della web application così come viene visto dall'esterno; deve
     * comprendere la stringa "http://" e deve terminare con "/";</li>
     * <li>codice della lingua impostata nell'oggetto pageUrl, oppure la lingua
     * corrente, oppure la lingua di default;</li>
     * <li>se il parametro "urlStyle" è settato a "classic", codice della pagina
     * corrente impostata nell'oggetto pageUrl seguito dal suffisso ".page",
     * altrimenti, se il parametro "urlStyle" è settato a "breadcrumbs",
     * "/pages/" seguito dal'insieme del codici pagina dalla root alla pagina
     * corrente separati da "/";</li>
     * <li>eventuale query string se sull'oggetto pageUrl sono stati impostati
     * parametri.</li>
     * </ul>
     *
     * @param pageUrl L'oggetto contenente le informazioni da tradurre in URL.
     * @param reqCtx Il contesto della richiesta.
     * @return La Stringa contenente l'URL.
     * @see com.agiletec.aps.system.services.url.AbstractURLManager#getURLString(com.agiletec.aps.system.services.url.PageURL,
     * com.agiletec.aps.system.RequestContext)
     */
    @Override
    public String getURLString(PageURL pageUrl, RequestContext reqCtx) {
        try {
            String langCode = pageUrl.getLangCode();
            Lang lang = this.getLangManager().getLang(langCode);
            if (lang == null && null != reqCtx) {
                lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
            }
            if (lang == null) {
                lang = this.getLangManager().getDefaultLang();
            }
            String pageCode = pageUrl.getPageCode();
            IPage page = this.getPageManager().getOnlinePage(pageCode);
            if (page == null && null != reqCtx) {
                page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
            }
            if (page == null) {
                page = this.getPageManager().getOnlineRoot();
            }
            HttpServletRequest request = (null != reqCtx) ? reqCtx.getRequest() : null;
            String url = this.createURL(page, lang, pageUrl.getParams(), pageUrl.isEscapeAmp(), pageUrl.getBaseUrlMode(), request);
            if (null != reqCtx && this.useJsessionId()) {
                HttpServletResponse resp = reqCtx.getResponse();
                String encUrl = resp.encodeURL(url.toString());
                return encUrl;
            } else {
                return url;
            }
        } catch (Throwable t) {
            _logger.error("Error creating url", t);
            throw new RuntimeException("Error creating url", t);
        }
    }

    /**
     * Create and return url by required page, lang and request params.
     *
     * @param requiredPage The required page.
     * @param requiredLang The required lang.
     * @param params A map of params. It can be null.
     * @return The url.
     */
    @Override
    public String createURL(IPage requiredPage, Lang requiredLang, Map<String, String> params) {
        try {
            return this.createURL(requiredPage, requiredLang, params, false, null);
        } catch (EntException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String createURL(IPage requiredPage, Lang requiredLang, Map<String, String> params, boolean escapeAmp) {
        try {
            return this.createURL(requiredPage, requiredLang, params, escapeAmp, null);
        } catch (EntException ex) {
            throw new RuntimeException("Error creating url", ex);
        }
    }

    @Override
    public String createURL(IPage requiredPage, Lang requiredLang, Map<String, String> params, boolean escapeAmp,
            HttpServletRequest request) throws EntException {
        return this.createURL(requiredPage, requiredLang, params, escapeAmp, null, request);
    }

    public String createURL(IPage requiredPage, Lang requiredLang, Map<String, String> params, boolean escapeAmp,
            String forcedBaseUrlMode, HttpServletRequest request) throws EntException {
        StringBuilder url = null;
        try {
            url = new StringBuilder(this.getApplicationBaseURL(forcedBaseUrlMode, request));
            if (!this.isUrlStyleBreadcrumbs()) {
                url.append(requiredLang.getCode()).append('/');
                url.append(requiredPage.getCode()).append(".page");
            } else {
                url.append("pages/");
                url.append(requiredLang.getCode()).append('/');
                StringBuffer fullPath = PageUtils.getFullPath(this.getPageManager(), requiredPage, "/");
                url.append(fullPath.append("/"));
            }
            String queryString = this.createQueryString(params, escapeAmp);
            url.append(queryString);
        } catch (Throwable t) {
            _logger.error("Error creating url", t);
            throw new EntException("Error creating url", t);
        }
        return url.toString();
    }

    @Override
    public String getApplicationBaseURL(HttpServletRequest request) throws EntException {
        return this.getApplicationBaseURL(null, request);
    }

    public String getApplicationBaseURL(String forcedBaseUrlMode, HttpServletRequest request) throws EntException {
        StringBuilder baseUrl = new StringBuilder();
        this.addBaseURL(baseUrl, forcedBaseUrlMode, request);
        if (!baseUrl.toString().endsWith("/")) {
            baseUrl.append("/");
        }
        return baseUrl.toString();
    }

    protected void addBaseURL(StringBuilder link, HttpServletRequest request) {
        this.addBaseURL(link, null, request);
    }

    protected void addBaseURL(StringBuilder link, String forcedBaseUrlMode, HttpServletRequest request) {
        if (null == request) {
            link.append(this.getConfigManager().getParam(SystemConstants.PAR_APPL_BASE_URL));
            return;
        }
        String baseUrlMode = this.calculateBaseUrlMode(forcedBaseUrlMode, request);
        if (this.isForceAddSchemeHost(baseUrlMode)) {
            String reqScheme = request.getHeader("X-Forwarded-Proto");
            if (StringUtils.isBlank(reqScheme)) {
                reqScheme = request.getScheme();
            }
            link.append(reqScheme);
            link.append("://");
            String serverName = request.getServerName();
            link.append(serverName);
            boolean checkPort = false;
            String hostName = request.getHeader("Host");
            if (null != hostName && hostName.startsWith(serverName)) {
                checkPort = true;
                if (hostName.length() > serverName.length()) {
                    String encodedHostName = org.owasp.encoder.Encode.forHtmlContent(hostName);
                    link.append(encodedHostName.substring(serverName.length()));
                }
            }
            if (!checkPort) {
                link.append(":").append(request.getServerPort());
            }
            if (this.addContextName()) {
                link.append(request.getContextPath());
            }
        } else if (this.isRelativeBaseUrl(baseUrlMode)) {
            if (this.addContextName()) {
                link.append(request.getContextPath());
            }
        } else {
            link.append(this.getConfigManager().getParam(SystemConstants.PAR_APPL_BASE_URL));
        }
    }

    protected String calculateBaseUrlMode(String forcedBaseUrlMode, HttpServletRequest request) {
        if (null == request) {
            return IPageManager.CONFIG_PARAM_BASE_URL_STATIC;
        }
        String param = this.getBaseUrlStrategy();
        if (StringUtils.isBlank(forcedBaseUrlMode)) {
            return param;
        }
        boolean isDefaultRelative = this.isRelativeBaseUrl(param);
        if (IPageManager.CONFIG_PARAM_BASE_URL_FROM_REQUEST.equalsIgnoreCase(forcedBaseUrlMode)
                || (isDefaultRelative && IPageManager.SPECIAL_PARAM_BASE_URL_REQUEST_IF_RELATIVE.equalsIgnoreCase(forcedBaseUrlMode))) {
            return IPageManager.CONFIG_PARAM_BASE_URL_FROM_REQUEST;
        } else if (IPageManager.CONFIG_PARAM_BASE_URL_STATIC.equalsIgnoreCase(forcedBaseUrlMode)) {
            return IPageManager.CONFIG_PARAM_BASE_URL_STATIC;
        } else if (IPageManager.CONFIG_PARAM_BASE_URL_RELATIVE.equalsIgnoreCase(forcedBaseUrlMode)) {
            return IPageManager.CONFIG_PARAM_BASE_URL_RELATIVE;
        }
        return param;
    }

    protected String getBaseUrlStrategy() {
        return this.getPageManager().getConfig(IPageManager.CONFIG_PARAM_BASE_URL);
    }

    protected boolean isForceAddSchemeHost() {
        return this.isForceAddSchemeHost(this.getBaseUrlStrategy());
    }

    protected boolean isForceAddSchemeHost(String param) {
        return (IPageManager.CONFIG_PARAM_BASE_URL_FROM_REQUEST.equals(param));
    }

    protected boolean isRelativeBaseUrl() {
        return this.isRelativeBaseUrl(this.getBaseUrlStrategy());
    }

    protected boolean isRelativeBaseUrl(String param) {
        return (IPageManager.CONFIG_PARAM_BASE_URL_RELATIVE.equals(param));
    }

    protected boolean isStaticBaseUrl() {
        String param = this.getBaseUrlStrategy();
        return (StringUtils.isBlank(param) || IPageManager.CONFIG_PARAM_BASE_URL_STATIC.equals(param));
    }

    protected boolean addContextName() {
        String param = this.getPageManager().getConfig(IPageManager.CONFIG_PARAM_BASE_URL_CONTEXT);
        return (null != param && Boolean.parseBoolean(param));
    }

    protected boolean isUrlStyleBreadcrumbs() {
        String param = this.getPageManager().getConfig(IPageManager.CONFIG_PARAM_URL_STYLE);
        return (param != null && param.trim().equals(IPageManager.CONFIG_PARAM_URL_STYLE_BREADCRUMBS));
    }

    protected boolean useJsessionId() {
        String param = this.getPageManager().getConfig(IPageManager.CONFIG_PARAM_USE_JSESSIONID);
        return (param != null && Boolean.parseBoolean(param));
    }

    protected ConfigInterface getConfigManager() {
        return _configManager;
    }

    public void setConfigManager(ConfigInterface configManager) {
        this._configManager = configManager;
    }

    protected ILangManager getLangManager() {
        return _langManager;
    }

    public void setLangManager(ILangManager langManager) {
        this._langManager = langManager;
    }

    protected IPageManager getPageManager() {
        return _pageManager;
    }

    public void setPageManager(IPageManager pageManager) {
        this._pageManager = pageManager;
    }

    private ConfigInterface _configManager;
    private IPageManager _pageManager;
    private ILangManager _langManager;

    protected static final int DEFAULT_HTTP_PORT = 80;
    protected static final int DEFAULT_HTTPS_PORT = 443;

}

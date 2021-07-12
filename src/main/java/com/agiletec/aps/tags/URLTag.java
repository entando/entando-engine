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
package com.agiletec.aps.tags;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.collections.CollectionUtils;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.aps.tags.util.IParameterParentTag;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Generates the URL to a portal page. The URL is either displayed or placed in a variable.
 * The URL depends on the page attributes and the given language otherwise the current values
 * are used.
 * Use the sub-tag "ParameterTag" to insert parameters in the query string.
 */
public class URLTag extends TagSupport implements IParameterParentTag {

	private static final EntLogger _logger = EntLogFactory.getSanitizedLogger(URLTag.class);

	/**
	 * Prepares a PageURL object; this object may comprehend several sub-tags
	 */
	@Override
	public int doStartTag() throws JspException {
		ServletRequest request =  this.pageContext.getRequest();
		RequestContext reqCtx = (RequestContext) request.getAttribute(RequestContext.REQCTX);
		try {
			IURLManager urlManager = 
					(IURLManager) ApsWebApplicationUtils.getBean(SystemConstants.URL_MANAGER, this.pageContext);
			this._pageUrl = urlManager.createURL(reqCtx);
			if (_pageCode != null) {
				_pageUrl.setPageCode(_pageCode);
			}
			if (_langCode != null) {
				_pageUrl.setLangCode(_langCode);
			}
            this._pageUrl.setEscapeAmp(this.isEscapeAmp());
			if (this.isParamRepeat()) {
				List<String> exclusion = this.getParametersToExclude();
				_pageUrl.setParamRepeat(exclusion);
			}
			this._pageUrl.setBaseUrlMode(this.getBaseUrlMode());
		} catch (Throwable t) {
			_logger.error("Error during tag initialization", t);
			throw new JspException("Error during tag initialization", t);
		}
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * Completes the URL generation making it available for immediate
	 * output or placing it in a variable
	 */
	@Override
	public int doEndTag() throws JspException {
		String url = _pageUrl.getURL();
		if (this.getVar() != null) {
			this.pageContext.setAttribute(this.getVar(), url);
		} else {
			try {
				String encodedUrl = org.owasp.encoder.Encode.forHtmlContent(url);
				this.pageContext.getOut().print(encodedUrl);
			} catch (Throwable t) {
				_logger.error("Error closing tag", t);
				throw new JspException("Error closing tag", t);
			}
		}
		return EVAL_PAGE;
	}

	@Override
	public void addParameter(String name, String value) {
		this._pageUrl.addParam(name, value);
	}
    
	protected List<String> getParametersToExclude() {
		List<String> parameters = new ArrayList<>();
		String csv = this.getExcludeParameters();
		if (null != csv && csv.trim().length() > 0) {
			CollectionUtils.addAll(parameters, csv.split(","));
		}
		parameters.add(SystemConstants.LOGIN_PASSWORD_PARAM_NAME);
		return parameters;
	}

	@Override
	public void release() {
		this._langCode = null;
		this._pageCode = null;
		this._varName = null;
		this._paramRepeat = false;
		this.escapeAmp = true;
		this._pageUrl = null;
		this._excludeParameters = null;
		this.baseUrlMode = null;
	}

	/**
	 * Return the language code
	 * @return The literal code
	 */
	public String getLang() {
		return _langCode;
	}

	/**
	 * Set the language code
	 * @param lang the literal code
	 */
	public void setLang(String lang) {
		this._langCode = lang;
	}

	/**
	 * Return the page code
	 * @return The page code
	 */
	public String getPage() {
		return _pageCode;
	}

	/**
	 * Set the page code
	 * @param page The page code
	 */
	public void setPage(String page) {
		this._pageCode = page;
	}

	/**
	 * Return the name of the variable containing the generated URL.
	 * @return The name of the variable
	 */
	public String getVar() {
		return _varName;
	}

	/**
	 * Set the name of the variable containing the generated URL.
	 * @param var The name of the variable
	 */
	public void setVar(String var) {
		this._varName = var;
	}

	/**
	 * Repeats the parameters of the previous request when true, false otherwise.
	 * @return Returns the parRepeat.
	 */
	public boolean isParamRepeat() {
		return _paramRepeat;
	}

	/**
	 * Toggles the repetition of the previous query string parameters
	 * @param paramRepeat True enables the repetition, false otherwise.
	 */
	public void setParamRepeat(boolean paramRepeat) {
		this._paramRepeat = paramRepeat;
	}

    public boolean isEscapeAmp() {
        return escapeAmp;
    }

    public void setEscapeAmp(boolean escapeAmp) {
        this.escapeAmp = escapeAmp;
    }
    
	/**
	 * Gets list of parameter names (comma separated) to exclude from repeating.
	 * By default, this attribute excludes only the password parameter of the login form.
	 * @return the exclude list.
	 */
	public String getExcludeParameters() {
		return _excludeParameters;
	}

	/**
	 * Sets the list of parameter names (comma separated) to exclude from repeating.
	 * By default, this attribute excludes only the password parameter of the login form.
	 * @param excludeParameters the excludes list (comma separated).
	 */
	public void setExcludeParameters(String excludeParameters) {
		this._excludeParameters = excludeParameters;
	}

	public String getBaseUrlMode() {
		return baseUrlMode;
	}

	public void setBaseUrlMode(String baseUrlMode) {
		this.baseUrlMode = baseUrlMode;
	}

	private String _langCode;
	private String _pageCode;
	private String _varName;
	private boolean _paramRepeat;
	private boolean escapeAmp = true;
	private PageURL _pageUrl;
	private String _excludeParameters;
	private String baseUrlMode;

}

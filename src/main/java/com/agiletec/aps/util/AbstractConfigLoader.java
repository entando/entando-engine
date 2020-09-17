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
package com.agiletec.aps.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.entando.entando.ent.exception.EntException;

/**
 * Abstract config Loader Class.
 * @author E.Santoboni
 */
public abstract class AbstractConfigLoader {

	private static final Logger _logger = LoggerFactory.getLogger(AbstractConfigLoader.class);
	
	protected List<String> loadDefinitionPaths() throws Throwable {
		List<String> filenames = new ArrayList<String>();
		try {
			StringTokenizer tokenizer = new StringTokenizer(this.getDefinitionConfig(), ",");
			while (tokenizer.hasMoreTokens()) {
				String currentFilename = tokenizer.nextToken().trim();
				int index = currentFilename.indexOf(AXTER);
				if (-1 == index) {
					filenames.add(currentFilename);
				} else {
					List<String> confFiles = new ArrayList<String>();
					String rootInspectionDir = currentFilename.substring(0, index);
					this.inspectResources(currentFilename, rootInspectionDir, confFiles);
					filenames.addAll(confFiles);
				}
			}
		} catch (Throwable t) {
			_logger.error("Error loading definition paths", t);
			//ApsSystemUtils.logThrowable(t, this, "loadDefinitionPaths", "Error loading definition paths");
			throw new EntException("Error loading definition paths", t);
		}
		return filenames;
	}
	
	@SuppressWarnings("unchecked")
	private void inspectResources(String currentFileName, String rootInspectionDir, List<String> confFiles) throws Throwable {
		Set<String> resourcesPath = this.getServletContext().getResourcePaths(rootInspectionDir);
		if (null == resourcesPath) return;
		Iterator<String> it = resourcesPath.iterator();
		while (it.hasNext()) {
			String current = it.next();
			if (!current.endsWith("/") && this.isConfResource(current, currentFileName)){
				confFiles.add(current);
			} else {
				this.inspectResources(currentFileName, current, confFiles);
			}
		}		
	}
	
	private boolean isConfResource(String current, String currentFilename) {
		String regExp = currentFilename.replaceAll(AXTER_REG_EXP, REG_EXP);
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(current);
		return m.matches();
	}
	
	protected ServletContext getServletContext() {
		return this._servletContext;
	}
	protected void setServletContext(ServletContext servletContext) {
		this._servletContext = servletContext;
	}
	
	protected String getDefinitionConfig() {
		return _definitionConfig;
	}
	protected void setDefinitionConfig(String definitionConfig) {
		this._definitionConfig = definitionConfig;
	}
	
	private ServletContext _servletContext;
	private String _definitionConfig;
	
	private final static String REG_EXP = "[\\\\w,\\\\-,_]*";
	private final static String AXTER = "**";
	private final static String AXTER_REG_EXP = "\\*\\*";
	
}
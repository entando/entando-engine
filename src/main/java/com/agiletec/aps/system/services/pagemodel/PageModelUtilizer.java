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
package com.agiletec.aps.system.services.pagemodel;

import java.util.List;

import org.entando.entando.ent.exception.EntException;

/**
 * Base interface for the services whose elements can directly reference the page templates
 * @author E.Santoboni
 */
public interface PageModelUtilizer {
	
	/**
	 * Return the id of the utilizer service.
	 * @return The id of the utilizer
	 */
	public String getName();
	
	/**
	 * Return the list of the objects which reference the given page template.
	 * @param pageModelCode The code of the page
	 * @return The list of the object referencing the given page template
	 * @throws EntException In case of error
	 */
	public List getPageModelUtilizers(String pageModelCode) throws EntException;
	
}

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

import com.agiletec.aps.system.common.FieldSearchFilter;
import com.agiletec.aps.system.common.model.dao.SearcherDaoPaginatedResult;
import org.entando.entando.ent.exception.EntException;

import java.util.*;

/**
 * Interface of the page templates manager.
 */
public interface IPageModelManager {

	/**
	 * Return a Page Template by the code.
	 *
	 * @param code The code of the Page Template
	 * @return The required Page Template
	 */
	PageModel getPageModel(String code);

	/**
	 * Return the collection of defined Page Templates
	 *
	 * @return The collection of defined Page Templates
	 */
	Collection<PageModel> getPageModels();

	void addPageModel(PageModel pageModel) throws EntException;

	void updatePageModel(PageModel pageModel) throws EntException;

	void deletePageModel(String code) throws EntException;

    SearcherDaoPaginatedResult<PageModel> searchPageModels(List<FieldSearchFilter> filters) throws EntException;

}

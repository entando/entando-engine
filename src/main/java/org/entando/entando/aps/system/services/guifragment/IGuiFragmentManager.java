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
package org.entando.entando.aps.system.services.guifragment;

import com.agiletec.aps.system.common.IParameterizableManager;
import java.util.List;

import org.entando.entando.ent.exception.EntException;

import com.agiletec.aps.system.common.FieldSearchFilter;
import com.agiletec.aps.system.common.model.dao.SearcherDaoPaginatedResult;

/**
 * @author E.Santoboni
 */
public interface IGuiFragmentManager extends IParameterizableManager {

	public static final String CONFIG_PARAM_EDIT_EMPTY_FRAGMENT_ENABLED = "editEmptyFragmentEnabled";

	public GuiFragment getGuiFragment(String code) throws EntException;

	public List<String> getGuiFragments() throws EntException;

	public List<String> searchGuiFragments(FieldSearchFilter filters[]) throws EntException;

	public void addGuiFragment(GuiFragment guiFragment) throws EntException;

	public void updateGuiFragment(GuiFragment guiFragment) throws EntException;

	public void deleteGuiFragment(String code) throws EntException;

	public GuiFragment getUniqueGuiFragmentByWidgetType(String widgetTypeCode) throws EntException;

	public List<String> getGuiFragmentCodesByWidgetType(String widgetTypeCode) throws EntException;

	public List<String> loadGuiFragmentPluginCodes() throws EntException;

	public SearcherDaoPaginatedResult<GuiFragment> getGuiFragments(List<FieldSearchFilter> fieldSearchFilters) throws EntException;

}

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

import com.agiletec.aps.system.common.FieldSearchFilter;
import com.agiletec.aps.system.common.model.dao.SearcherDaoPaginatedResult;
import com.agiletec.aps.system.exception.ApsSystemException;
import java.util.List;

/**
 * @author E.Santoboni
 */
public interface IGuiFragmentManager {

    GuiFragment getGuiFragment(String code) throws ApsSystemException;

    List<String> getGuiFragments() throws ApsSystemException;

    List<String> searchGuiFragments(FieldSearchFilter[] filters) throws ApsSystemException;

    void addGuiFragment(GuiFragment guiFragment) throws ApsSystemException;

    void updateGuiFragment(GuiFragment guiFragment) throws ApsSystemException;

    void deleteGuiFragment(String code) throws ApsSystemException;

    GuiFragment getUniqueGuiFragmentByWidgetType(String widgetTypeCode) throws ApsSystemException;

    List<String> getGuiFragmentCodesByWidgetType(String widgetTypeCode) throws ApsSystemException;

    List<String> loadGuiFragmentPluginCodes() throws ApsSystemException;

    SearcherDaoPaginatedResult<GuiFragment> getGuiFragments(List<FieldSearchFilter> fieldSearchFilters) throws ApsSystemException;

}

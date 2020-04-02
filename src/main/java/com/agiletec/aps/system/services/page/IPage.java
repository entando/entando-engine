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

package com.agiletec.aps.system.services.page;

import com.agiletec.aps.system.common.tree.ITreeNode;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import com.agiletec.aps.util.ApsProperties;
import java.io.Serializable;
import java.util.Set;

/**
 * This class describes a pages of the portal.
 *
 * @author E.Santoboni
 */
public interface IPage extends ITreeNode, Serializable {

    IPage clone();

    /**
     * Returns a properties with the titles of the page, where the keys are the codes of language.
     *
     * @return The page titles
     */
    @Override
    ApsProperties getTitles();

    /**
     * Returns the title, of the page in the specified language.
     *
     * @param langCode The code of the language.
     * @return The title, of the page.
     */
    @Override
    String getTitle(String langCode);

    /**
     * Return the model of the associated page
     *
     * @return The page model
     */
    PageModel getModel();

    /**
     * Return the set of codes of the additional groups authorized to view the page in the front-end.
     *
     * @return The set of codes belonging to the additional group authorized to access the page in the front-end.
     */
    Set<String> getExtraGroups();

    /**
     * Add a group (code) authorized to view/access the page in the Front-end.
     *
     * @param groupName The group to add.
     */
    @Deprecated
    void addExtraGroup(String groupName);

    /**
     * Remove a group (code) authorized to view/access the page in the Front-end.
     *
     * @param groupName The group to remove.
     */
    @Deprecated
    void removeExtraGroup(String groupName);

    PageMetadata getMetadata();

    void setMetadata(PageMetadata metadata);

    boolean isOnline();

    boolean isChanged();

    boolean isOnlineInstance();

    /**
     * This returns a boolean values indicating whether the page is displayed in the menus or similar.
     *
     * @return true if the page must be shown in the menu, false otherwise.
     */
    boolean isShowable();

    /**
     * This returns a boolean values indicating whether the page use the extra titles extracted from Request Context parameter
     * EXTRAPAR_EXTRA_PAGE_TITLES.
     *
     * @return true if the page must use the extra titles, false otherwise.
     */
    boolean isUseExtraTitles();

    /**
     * Return the widgets configured in this page.
     *
     * @return all the widgets of the current page
     */
    Widget[] getWidgets();

    void setWidgets(Widget[] widgets);

    /**
     * Return the mimetype configured for this page.
     *
     * @return the mimetype configured for this page.
     */
    String getMimeType();

    /**
     * Return the charset configured for this page.
     *
     * @return the charset configured for this page.
     */
    String getCharset();
}

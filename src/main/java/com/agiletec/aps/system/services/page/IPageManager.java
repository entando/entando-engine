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

import com.agiletec.aps.system.common.IParameterizableManager;
import java.util.List;

import com.agiletec.aps.system.common.tree.ITreeNode;
import com.agiletec.aps.system.common.tree.ITreeNodeManager;
import org.entando.entando.ent.exception.EntException;

/**
 * Basic interface for the page manager services.
 *
 * @author M.Diana
 */
public interface IPageManager extends ITreeNodeManager, IParameterizableManager {

    public static final String CONFIG_PARAM_URL_STYLE = "urlStyle";

    public static final String CONFIG_PARAM_URL_STYLE_CLASSIC = "classic";
    public static final String CONFIG_PARAM_URL_STYLE_BREADCRUMBS = "breadcrumbs";

    public static final String CONFIG_PARAM_TREE_STYLE_PAGE = "treeStyle_page";

    public static final String CONFIG_PARAM_USE_JSESSIONID = "useJsessionId";

    public static final String CONFIG_PARAM_BASE_URL = "baseUrl";

    public static final String CONFIG_PARAM_BASE_URL_RELATIVE = "relative";
    public static final String CONFIG_PARAM_BASE_URL_FROM_REQUEST = "request";
    public static final String CONFIG_PARAM_BASE_URL_STATIC = "static";

    public static final String SPECIAL_PARAM_BASE_URL_REQUEST_IF_RELATIVE = "requestIfRelative";

    public static final String CONFIG_PARAM_BASE_URL_CONTEXT = "baseUrlContext";

    public static final String CONFIG_PARAM_START_LANG_FROM_BROWSER = "startLangFromBrowser";

    public static final String CONFIG_PARAM_HOMEPAGE_PAGE_CODE = "homePageCode";

    public static final String CONFIG_PARAM_NOT_FOUND_PAGE_CODE = "notFoundPageCode";

    public static final String CONFIG_PARAM_ERROR_PAGE_CODE = "errorPageCode";

    public static final String CONFIG_PARAM_LOGIN_PAGE_CODE = "loginPageCode";

    @Deprecated
    public ITreeNode getRoot();

    /**
     * Delete a page and eventually the association with the showlets.
     *
     * @param pageCode the code of the page to delete
     * @throws EntException In case of database access error.
     */
    public void deletePage(String pageCode) throws EntException;

    /**
     * Add a new page to the database.
     *
     * @param page The page to add
     * @throws EntException In case of database access error.
     */
    public void addPage(IPage page) throws EntException;

    /**
     * Update a page record in the database, in draft
     *
     * @param page The modified page.
     * @throws EntException In case of database access error.
     */
    public void updatePage(IPage page) throws EntException;

    /**
     * Update a page as online.
     *
     * @param pageCode The code of the page to be setted online.
     * @throws EntException In case of error.
     */
    public void setPageOnline(String pageCode) throws EntException;

    /**
     * Update a page as offline.
     *
     * @param pageCode The code of the page to be setted offline.
     * @throws EntException In case of error.
     */
    public void setPageOffline(String pageCode) throws EntException;

    /**
     * Move a page.
     *
     * @param pageCode The code of the page to move.
     * @param moveUp When true the page is moved to a higher level of the tree,
     * otherwise to a lower level.
     * @return The result of the operation: false if the move request could not
     * be satisfied, true otherwise.
     * @throws EntException In case of database access error.
     */
    public boolean movePage(String pageCode, boolean moveUp) throws EntException;

    /**
     * Move a widget.
     *
     * @param pageCode The code of the page to configure.
     * @param frameToMove the frame position to move .
     * @param destFrame the frame final position .
     * @return The result of the operation: false if the move request could not
     * be satisfied, true otherwise.
     * @throws EntException In case of database access error.
     */
    public boolean moveWidget(String pageCode, Integer frameToMove, Integer destFrame) throws EntException;

    /**
     * @deprecated Use {@link #joinWidget(String, Widget, int)} instead
     */
    @Deprecated
    public void joinShowlet(String pageCode, Widget widget, int pos) throws EntException;

    /**
     * Set the showlet -including its configuration- in the given page in the
     * desidered position. If the position is already occupied by another
     * showlet this will be substituted with the new one.
     *
     * @param pageCode the code of the page where to set the showlet
     * @param widget The showlet to set
     * @param pos The position where to place the showlet in
     * @throws EntException In case of error.
     */
    public void joinWidget(String pageCode, Widget widget, int pos) throws EntException;

    /**
     * @deprecated Use {@link #removeWidget(String, int)} instead
     */
    @Deprecated
    public void removeShowlet(String pageCode, int pos) throws EntException;

    /**
     * Remove a widget from the given page.
     *
     * @param pageCode the code of the widget to remove from the page
     * @param pos The position in the page to free
     * @throws EntException In case of error
     */
    public void removeWidget(String pageCode, int pos) throws EntException;

    public IPage getOnlineRoot();

    public IPage getDraftRoot();

    public IPage getOnlinePage(String pageCode);

    public IPage getDraftPage(String pageCode);

    /**
     * Search pages by a token of its code.
     *
     * @param pageCodeToken The token containing to be looked up across the
     * pages.
     * @param allowedGroups The codes of allowed page groups.
     * @return A list of candidates containing the given token. If the
     * pageCodeToken is null then this method will return a set containing all
     * the pages.
     * @throws EntException in case of error.
     */
    public List<IPage> searchPages(String pageCodeToken, String title, List<String> allowedGroups) throws EntException;

    public List<IPage> searchOnlinePages(String pageCodeToken, String title, List<String> allowedGroups) throws EntException;

    public List<String> getOnlineWidgetUtilizerCodes(String widgetTypeCode) throws EntException;

    public List<IPage> getOnlineWidgetUtilizers(String widgetTypeCode) throws EntException;

    public List<String> getDraftWidgetUtilizerCodes(String widgetTypeCode) throws EntException;

    public List<IPage> getDraftWidgetUtilizers(String widgetTypeCode) throws EntException;

    public boolean movePage(IPage currentPage, IPage newParent) throws EntException;

    public boolean movePage(String pageCode, String newParentCode) throws EntException;

    /**
     * Extract page statistics
     *
     * @return a PagesStatus pojo
     */
    public PagesStatus getPagesStatus();

    public List<IPage> loadLastUpdatedPages(int size) throws EntException;

}

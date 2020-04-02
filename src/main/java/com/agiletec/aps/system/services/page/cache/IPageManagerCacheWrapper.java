/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
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

package com.agiletec.aps.system.services.page.cache;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageDAO;
import com.agiletec.aps.system.services.page.PagesStatus;
import java.util.List;

/**
 * @author E.Santoboni
 */
public interface IPageManagerCacheWrapper {

    String PAGE_MANAGER_CACHE_NAME = "Entando_PageManager";
    String ONLINE_PAGE_CACHE_NAME_PREFIX = "PageManager_onLine_";
    String DRAFT_PAGE_CACHE_NAME_PREFIX = "PageManager_draft_";
    String ONLINE_ROOT_CACHE_NAME = "PageManager_onLineRoot";
    String DRAFT_ROOT_CACHE_NAME = "PageManager_draftRoot";
    String PAGE_STATUS_CACHE_NAME = "PageManager_pagesStatus";
    String DRAFT_PAGE_CODES_CACHE_NAME = "PageManager_draftCodes";
    String ONLINE_PAGE_CODES_CACHE_NAME = "PageManager_onlineCodes";

    String ONLINE_WIDGET_UTILIZER_CACHE_NAME_PREFIX = "PageManager_onlineUtilizer_";
    String DRAFT_WIDGET_UTILIZER_CACHE_NAME_PREFIX = "PageManager_draftUtilizer_";

    void initCache(IPageDAO pageDao) throws ApsSystemException;

    PagesStatus getPagesStatus();

    IPage getOnlinePage(String pageCode);

    IPage getDraftPage(String pageCode);

    IPage getOnlineRoot();

    IPage getDraftRoot();

    List<String> getOnlineWidgetUtilizers(String widgetTypeCode) throws ApsSystemException;

    List<String> getDraftWidgetUtilizers(String widgetTypeCode) throws ApsSystemException;

    void deleteDraftPage(String pageCode);

    void addDraftPage(IPage page);

    void updateDraftPage(IPage page);

    void moveUpDown(String pageDown, String pageUp);

    void setPageOnline(String pageCode);

    void setPageOffline(String pageCode);

    void movePage(String pageCode, String newParentCode);

}

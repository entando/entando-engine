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
package org.entando.entando.aps.system.services.page;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collection;
import java.util.List;

import org.entando.entando.aps.system.services.IComponentExistsService;
import org.entando.entando.aps.system.services.IComponentUsageService;
import org.entando.entando.aps.system.services.page.model.PageConfigurationDto;
import org.entando.entando.aps.system.services.page.model.PageDto;
import org.entando.entando.aps.system.services.page.model.PagesStatusDto;
import org.entando.entando.aps.system.services.page.model.WidgetConfigurationDto;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.page.model.PageCloneRequest;
import org.entando.entando.web.page.model.PagePositionRequest;
import org.entando.entando.web.page.model.PageRequest;
import org.entando.entando.web.page.model.PageSearchRequest;
import org.entando.entando.web.page.model.WidgetConfigurationRequest;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;

/**
 *
 * @author paddeo
 */
public interface IPageService extends IComponentExistsService, IComponentUsageService {

    String BEAN_NAME = "PageService";
    String STATUS_ONLINE = "published";
    String STATUS_DRAFT = "draft";
    String STATUS_UNPUBLISHED = "unpublished";

    PageDto getPage(String pageCode, String status);

    PageDto addPage(PageRequest pageRequest);

    void removePage(String pageName);

    PageDto updatePage(String pageCode, PageRequest pageRequest);

    PageDto getPatchedPage(String pageCode, JsonNode patch);

    default List<PageDto> getPages(String parentCode) {
        return getPages(parentCode, null, null);
    }

    List<PageDto> getPages(String parentCode,
            @Nullable String forLinkingToOwnerGroup, @Nullable Collection<String> forLinkingToExtraGroups);

    PagedMetadata<PageDto> searchPages(PageSearchRequest request, List<String> allowedGroups);

    /**
     * Search against online pages
     *
     * @param request
     * @return
     */
    PagedMetadata<PageDto> searchOnlineFreePages(RestListRequest request);

    PageDto movePage(String pageCode, PagePositionRequest pageRequest);

    PageConfigurationDto getPageConfiguration(String pageCode, String status);

    PageConfigurationDto restorePageConfiguration(String pageCode);

    WidgetConfigurationDto getWidgetConfiguration(String pageCode, int frameId, String status);

    WidgetConfigurationDto updateWidgetConfiguration(String pageCode, int frameId, WidgetConfigurationRequest widget);

    void deleteWidgetConfiguration(String pageCode, int frameId);

    PageDto updatePageStatus(String pageCode, String status);

    PageConfigurationDto applyDefaultWidgets(String pageCode);

    PagedMetadata<?> getPageReferences(String pageCode, String manager, RestListRequest requestList);

    PagesStatusDto getPagesStatus();

    List<PageDto> listViewPages();

    PageDto clonePage(String pageCode, PageCloneRequest pageCloneRequest, BindingResult bindingResult);

}

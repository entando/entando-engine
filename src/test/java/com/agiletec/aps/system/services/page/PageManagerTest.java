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

import com.agiletec.aps.system.services.page.cache.IPageManagerCacheWrapper;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PageManagerTest {
 
    @Mock
    private IPageManagerCacheWrapper cacheWrapper;
    
    @InjectMocks
    private PageManager pageManager;

    @Test
    void searchPages_FilterByCodeOnAllowedGroup_ShouldReturnResult() throws Exception {
        testSearchPages("page_a", "group_a", "group_a", "page_a", null, "page_a");
    }
    
    @Test
    void searchPages_FilterByTitleOnAllowedGroup_ShouldReturnResult() throws Exception {
        testSearchPages("page_a", "group_a", "group_a", null, "page_a_title_en", "page_a");
    }
    
    @Test
    void searchPages_OnAllowedGroup_ShouldReturnResult() throws Exception {
        testSearchPages("page_a", "group_a", "group_a", null, null, "homepage", "page_a");
    }
    
    @Test
    void searchPages_FilterOnEmptyCodeOnAllowedGroup_ShouldReturnResult() throws Exception {
        testSearchPages("page_a", "group_a", "group_a", "", null, "homepage", "page_a");
    }

    @Test
    void searchPages_FilterOnWrongCodeOnAllowedGroup_ShouldNotReturnResult() throws Exception {
        testSearchPages("page_a", "group_a", "group_a", "fooCode", null);
    }

    @Test
    void searchPages_FilterOnWrongTitleOnAllowedGroup_ShouldNotReturnResult() throws Exception {
        testSearchPages("page_a", "group_a", "group_a", null, "fooTitle");
    }
    
    @Test
    void searchPages_FilterByCodeOnNotAllowedGroup_ShouldNotReturnResult() throws Exception {
        testSearchPages("page_a", "group_a", "group_b", "page_a", null);
    }
    
    @Test
    void searchPages_FilterByTitleOnNotAllowedGroup_ShouldNotReturnResult() throws Exception {
        testSearchPages("page_a", "group_a", "group_b", null, "page_a_title_en");
    }
    
    @Test
    void searchPages_OnNotAllowedGroup_ShouldNotReturnResult() throws Exception {
        testSearchPages("page_a", "group_a", "group_b", null, null, "homepage");
    }
    
    @Test
    void searchPages_FilterOnEmptyCodeOnNotAllowedGroup_ShouldNotReturnResult() throws Exception {
        testSearchPages("page_a", "group_a", "group_b", "", null, "homepage");
    }

    @Test
    void searchPages_UserWithNullGroupsList_ShouldNotReturnResult() throws Exception {
        List<IPage> onlinePages = pageManager.searchOnlinePages(null, null, null);
        List<IPage> draftPages = pageManager.searchPages(null, null, null);
        Assertions.assertTrue(onlinePages.isEmpty());
        Assertions.assertTrue(draftPages.isEmpty());
    }

    @Test
    void searchPages_UserWithEmptyGroupsList_ShouldNotReturnResult() throws Exception {
        List<IPage> onlinePages = pageManager.searchOnlinePages(null, null, List.of());
        List<IPage> draftPages = pageManager.searchPages(null, null, List.of());
        Assertions.assertTrue(onlinePages.isEmpty());
        Assertions.assertTrue(draftPages.isEmpty());
    }

    private void testSearchPages(String pageCode, String pageGroup, String userGroup, 
            String codeFilter, String titleFilter, String... expectedFound) throws Exception {

        Page onlineRoot = getPage("homepage", null, "free", true, pageCode);
        Page draftRoot = getPage("homepage", null, "free", false, pageCode);
        Mockito.when(cacheWrapper.getOnlineRoot()).thenReturn(onlineRoot);
        Mockito.when(cacheWrapper.getDraftRoot()).thenReturn(draftRoot);

        Page onlinePage = getPage(pageCode, "homepage", pageGroup, true);
        Page draftPage = getPage(pageCode, "homepage", pageGroup, false);
        Mockito.when(cacheWrapper.getOnlinePage(pageCode)).thenReturn(onlinePage);
        Mockito.when(cacheWrapper.getDraftPage(pageCode)).thenReturn(draftPage);

        List<IPage> onlinePages = pageManager.searchOnlinePages(codeFilter, titleFilter, List.of(userGroup));
        List<IPage> draftPages = pageManager.searchPages(codeFilter, titleFilter, List.of(userGroup));

        Assertions.assertEquals(expectedFound.length, onlinePages.size());
        Assertions.assertEquals(expectedFound.length, draftPages.size());

        for (int i = 0; i < expectedFound.length; i++) {
            Assertions.assertEquals(expectedFound[i], onlinePages.get(i).getCode());
            Assertions.assertEquals(expectedFound[i], draftPages.get(i).getCode());
        }
    }

    private Page getPage(String code, String parentCode, String ownerGroup, boolean online, String... children) {
        Page page = new Page();
        page.setCode(code);
        page.setParentCode(parentCode);
        page.setTitle("en", code + "_title_en");
        page.setTitle("it", code + "_title_it");
        page.setGroup(ownerGroup);
        page.setOnline(online);
        page.setChildrenCodes(children);
        return page;
    }
}

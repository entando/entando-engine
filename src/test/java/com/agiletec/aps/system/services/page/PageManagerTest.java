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

import com.agiletec.aps.system.common.notify.INotifyManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.cache.IPageManagerCacheWrapper;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import java.util.List;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.ent.exception.EntException;
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
    private IPageDAO pageDao;
    
    @Mock
    private INotifyManager notifyManager;
 
    @Mock
    private IPageManagerCacheWrapper cacheWrapper;
    
    @InjectMocks
    private PageManager pageManager;
    
    @Test
    void deletePageWithError() throws Exception {
        IPage mockPage = Mockito.mock(IPage.class);
        Mockito.when(mockPage.getChildrenCodes()).thenReturn(new String[]{});
        Mockito.when(cacheWrapper.getDraftPage(Mockito.anyString())).thenReturn(mockPage);
        Assertions.assertThrows(EntException.class, () -> {
            Mockito.doThrow(RuntimeException.class).when(this.pageDao).deletePage(mockPage);
            pageManager.deletePage("pageCode");
        });
        Mockito.verify(cacheWrapper, Mockito.times(0)).initCache(pageDao);
        Mockito.verifyNoInteractions(notifyManager);
    }
    
    @Test
    void addPageWithError() throws Exception {
        IPage mockPage = Mockito.mock(IPage.class);
        Mockito.when(mockPage.getParentCode()).thenReturn("parent");
        Mockito.when(cacheWrapper.getDraftPage("parent")).thenReturn(Mockito.mock(IPage.class));
        Assertions.assertThrows(EntException.class, () -> {
            Mockito.doThrow(RuntimeException.class).when(this.pageDao).addPage(mockPage);
            pageManager.addPage(mockPage);
        });
        Mockito.verify(mockPage, Mockito.times(1)).setPosition(Mockito.anyInt());
        Mockito.verify(cacheWrapper, Mockito.times(0)).addDraftPage(mockPage);
        Mockito.verifyNoInteractions(notifyManager);
    }
    
    @Test
    void updatePageWithError() throws Exception {
        IPage mockPage = Mockito.mock(IPage.class);
        Assertions.assertThrows(EntException.class, () -> {
            Mockito.doThrow(RuntimeException.class).when(this.pageDao).updatePage(mockPage);
            pageManager.updatePage(mockPage);
        });
        Mockito.verify(cacheWrapper, Mockito.times(0)).updateDraftPage(mockPage);
        Mockito.verifyNoInteractions(notifyManager);
    }
    
    @Test
    void setPageOnlineWithError() throws Exception {
        Assertions.assertThrows(EntException.class, () -> {
            Mockito.doThrow(RuntimeException.class).when(this.pageDao).setPageOnline(Mockito.anyString());
            pageManager.setPageOnline(Mockito.anyString());
        });
        Mockito.verify(cacheWrapper, Mockito.times(0)).setPageOnline(Mockito.anyString());
        Mockito.verifyNoInteractions(notifyManager);
    }
    
    @Test
    void setPageOfflineWithError() throws Exception {
        Assertions.assertThrows(EntException.class, () -> {
            Mockito.doThrow(RuntimeException.class).when(this.pageDao).setPageOffline(Mockito.anyString());
            pageManager.setPageOffline(Mockito.anyString());
        });
        Mockito.verify(cacheWrapper, Mockito.times(0)).setPageOffline(Mockito.anyString());
        Mockito.verifyNoInteractions(notifyManager);
    }
    
    @Test
    void movePageWithError() throws Exception {
        IPage mockPage = Mockito.mock(IPage.class);
        IPage otherMockPage = Mockito.mock(IPage.class);
        IPage parentMockPage = Mockito.mock(IPage.class);
        Mockito.when(parentMockPage.getChildrenCodes()).thenReturn(new String[]{"child1", "child2"});
        Mockito.when(cacheWrapper.getDraftPage("child1")).thenReturn(mockPage);
        Mockito.when(cacheWrapper.getDraftPage("child2")).thenReturn(otherMockPage);
        Mockito.when(mockPage.getCode()).thenReturn("child1");
        Mockito.when(mockPage.getParentCode()).thenReturn("parent");
        Mockito.when(otherMockPage.getParentCode()).thenReturn("parent");
        Mockito.when(cacheWrapper.getDraftPage("parent")).thenReturn(parentMockPage);
        Assertions.assertThrows(EntException.class, () -> {
            Mockito.doThrow(RuntimeException.class).when(this.pageDao).updatePosition(Mockito.anyString(), Mockito.anyString());
            pageManager.movePage("child1", false);
        });
        Mockito.verify(cacheWrapper, Mockito.times(1)).moveUpDown(Mockito.anyString(), Mockito.anyString());
        Mockito.verifyNoInteractions(notifyManager);
    }
    
    @Test
    void removeWidgetWithError() throws Exception {
        Page draftPage = getPage("page_code", "homepage", Group.FREE_GROUP_NAME, false);
        this.addMetadata(draftPage);
        Widget widget = new Widget();
        widget.setType(Mockito.mock(WidgetType.class));
        draftPage.setWidgets(new Widget[]{null, null, widget, null});
        Mockito.when(cacheWrapper.getDraftPage("page_code")).thenReturn(draftPage);
        Assertions.assertThrows(EntException.class, () -> {
            Mockito.doThrow(RuntimeException.class).when(this.pageDao).removeWidget(draftPage, 2);
            pageManager.removeWidget("page_code", 2);
        });
        Mockito.verify(cacheWrapper, Mockito.times(0)).setPageOffline(Mockito.anyString());
        Mockito.verifyNoInteractions(notifyManager);
    }
    
    @Test
    void joinWidgetWithError() throws Exception {
        Page draftPage = getPage("page_code", "homepage", Group.FREE_GROUP_NAME, false);
        this.addMetadata(draftPage);
        Widget widget = new Widget();
        widget.setType(Mockito.mock(WidgetType.class));
        Mockito.when(cacheWrapper.getDraftPage("page_code")).thenReturn(draftPage);
        Assertions.assertThrows(EntException.class, () -> {
            Mockito.doThrow(RuntimeException.class).when(this.pageDao).joinWidget(draftPage, widget, 2);
            pageManager.joinWidget("page_code", widget, 2);
        });
        Mockito.verify(cacheWrapper, Mockito.times(0)).updateDraftPage(Mockito.any(IPage.class));
        Mockito.verifyNoInteractions(notifyManager);
    }

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
    
    private void addMetadata(Page page) {
        PageMetadata metadata = new PageMetadata();
        PageModel model = Mockito.mock(PageModel.class);
        Mockito.when(model.getFrames()).thenReturn(new String[]{"pos0", "pos1", "pos2", "pos3"});
        metadata.setModel(model);
        page.setMetadata(metadata);
    }
    
}

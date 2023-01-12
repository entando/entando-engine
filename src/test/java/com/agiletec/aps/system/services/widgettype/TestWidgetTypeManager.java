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
package com.agiletec.aps.system.services.widgettype;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.services.mock.MockWidgetTypeDAO;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.IManager;
import com.agiletec.aps.system.services.pagemodel.IPageModelManager;
import com.agiletec.aps.util.ApsProperties;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.aps.system.services.widgettype.WidgetTypeParameter;
import org.entando.entando.ent.exception.EntException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.PageMetadata;
import com.agiletec.aps.system.services.page.PageTestUtil;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;

/**
 * @author M.Diana - E.Santoboni
 */
class TestWidgetTypeManager extends BaseTestCase {
    
    @Test
    void testGetWidgetTypes() throws EntException {
        List<WidgetType> list = this.widgetTypeManager.getWidgetTypes();
        Iterator<WidgetType> iter = list.iterator();
        Map<String, String> widgetTypes = new HashMap<>();
        while (iter.hasNext()) {
            WidgetType widgetType = iter.next();
            widgetTypes.put(widgetType.getCode(), widgetType.getTitles().getProperty("it"));
        }
        boolean containsKey = widgetTypes.containsKey("entando_apis");
        boolean containsValue = widgetTypes.containsValue("APIs");
        assertTrue(containsKey);
        assertTrue(containsValue);
        containsKey = widgetTypes.containsKey("leftmenu");
        containsValue = widgetTypes.containsValue("Menu di navigazione verticale");
        assertTrue(containsKey);
        assertTrue(containsValue);
    }

    @Test
    void testGetWidgetType_1() throws EntException {
        WidgetType widgetType = widgetTypeManager.getWidgetType("leftmenu");
        assertEquals("leftmenu", widgetType.getCode());
        assertEquals("Menu di navigazione verticale", widgetType.getTitles().get("it"));
        assertTrue(widgetType.isLocked());
        assertFalse(widgetType.isLogic());
        assertFalse(widgetType.isUserType());
        assertNull(widgetType.getParentType());
        assertNull(widgetType.getConfig());
        String action = widgetType.getAction();
        assertEquals(action, "navigatorConfig");
        List<WidgetTypeParameter> list = widgetType.getTypeParameters();
        Iterator<WidgetTypeParameter> iter = list.iterator();
        Map<String, String> parameters = new HashMap<>();
        while (iter.hasNext()) {
            WidgetTypeParameter parameter = (WidgetTypeParameter) iter.next();
            parameters.put(parameter.getName(), parameter.getDescr());
        }
        assertEquals(1, parameters.size());
        assertTrue(parameters.containsKey("navSpec"));
        assertTrue(parameters.containsValue("Rules for the Page List auto-generation"));
    }

    @Test
    void testGetWidgetType_2() throws EntException {
        WidgetType widgetType = widgetTypeManager.getWidgetType("entando_apis");
        assertEquals("entando_apis", widgetType.getCode());
        assertEquals("APIs", widgetType.getTitles().get("it"));
        assertTrue(widgetType.isLocked());
        assertTrue(widgetType.isLogic());
        assertFalse(widgetType.isUserType());
        assertNull(widgetType.getAction());
        assertNull(widgetType.getTypeParameters());
        assertNotNull(widgetType.getParentType());
        assertEquals("formAction", widgetType.getParentType().getCode());
        assertNotNull(widgetType.getConfig());
        String contentTypeParam = widgetType.getConfig().getProperty("actionPath");
        assertEquals("/ExtStr2/do/Front/Api/Resource/list.action", contentTypeParam);
    }

    @Test
    void testFailureDeleteWidgetType_1() throws Throwable {
        String widgetTypeCode = "formAction";
        assertNotNull(this.widgetTypeManager.getWidgetType(widgetTypeCode));
        try {
            this.widgetTypeManager.deleteWidgetType(widgetTypeCode);
        } catch (Throwable t) {
            throw t;
        }
        assertNotNull(this.widgetTypeManager.getWidgetType(widgetTypeCode));
    }

    @Test
    void testFailureDeleteWidgetType_2() throws Throwable {
        String widgetTypeCode = "test_widgetType";
        assertNull(this.widgetTypeManager.getWidgetType(widgetTypeCode));
        try {
            WidgetType type = this.createNewWidgetType(widgetTypeCode);
            type.setLocked(true);
            this.widgetTypeManager.addWidgetType(type);
            assertNotNull(this.widgetTypeManager.getWidgetType(widgetTypeCode));
            this.widgetTypeManager.deleteWidgetType(widgetTypeCode);
            assertNotNull(this.widgetTypeManager.getWidgetType(widgetTypeCode));
        } catch (Throwable t) {
            throw t;
        } finally {
            if (null != this.widgetTypeManager.getWidgetType(widgetTypeCode)) {
                this.mockWidgetTypeDAO.deleteWidgetType(widgetTypeCode);
            }
            ((IManager) this.widgetTypeManager).refresh();
            assertNull(this.widgetTypeManager.getWidgetType(widgetTypeCode));
        }
    }

    @Test
    void testAddDeleteWidgetType() throws Throwable {
        String widgetTypeCode = "test_widgetType";
        assertNull(this.widgetTypeManager.getWidgetType(widgetTypeCode));
        try {
            this.widgetTypeManager.deleteWidgetType(widgetTypeCode);
            WidgetType type = this.createNewWidgetType(widgetTypeCode);
            this.widgetTypeManager.addWidgetType(type);
            assertNotNull(this.widgetTypeManager.getWidgetType(widgetTypeCode));
        } catch (Throwable t) {
            throw t;
        } finally {
            if (null != this.widgetTypeManager.getWidgetType(widgetTypeCode)) {
                this.widgetTypeManager.deleteWidgetType(widgetTypeCode);
            }
            assertNull(this.widgetTypeManager.getWidgetType(widgetTypeCode));
        }
    }
    
    @Test
    void testParallelAddDeleteWidgetType() throws Throwable {
        String widgetTypeCode = "test_widgetType";
        assertNull(this.widgetTypeManager.getWidgetType(widgetTypeCode));
        try {
            List<WidgetType> types = IntStream.range(1, 20).boxed().map(i -> {
                String code = widgetTypeCode + "_" + i;
                return this.createNewWidgetType(code);
            }).collect(Collectors.toList());
            types.parallelStream().forEach(wt -> {
                try {
                    this.widgetTypeManager.addWidgetType(wt);
                } catch (Exception e) {
                    Assertions.fail("Error adding widgetType " + wt.getCode());
                }
            });
            IntStream.range(1, 20).parallel().forEach(i -> {
                String code = widgetTypeCode + "_" + i;
                assertNotNull(this.widgetTypeManager.getWidgetType(code));
            });
        } catch (Throwable t) {
            throw t;
        } finally {
            IntStream.range(1, 20).parallel().forEach(i -> {
                String code = widgetTypeCode + "_" + i;
                try {
                    this.widgetTypeManager.deleteWidgetType(code);
                } catch (Exception e) {
                    Assertions.fail("Error deleting widgetType " + code);
                }
                assertNull(this.widgetTypeManager.getWidgetType(code));
            });
        }
    }
    
    @Test
    void testUpdateTitles() throws Throwable {
        String widgetTypeCode = "test_widgetType";
        assertNull(this.widgetTypeManager.getWidgetType(widgetTypeCode));
        try {
            WidgetType type = this.createNewWidgetType(widgetTypeCode);
            this.widgetTypeManager.addWidgetType(type);
            WidgetType extracted = this.widgetTypeManager.getWidgetType(widgetTypeCode);
            assertNotNull(extracted);
            assertEquals("Titolo", extracted.getTitles().get("it"));
            assertEquals("Title", extracted.getTitles().get("en"));
            ApsProperties newTitles = new ApsProperties();
            newTitles.put("it", "Titolo modificato");
            newTitles.put("en", "Modified title");
            this.widgetTypeManager.updateWidgetType(widgetTypeCode, newTitles, type.getConfig(), type.getMainGroup(),
                    type.getConfigUi(), type.getBundleId(), type.isReadonlyPageWidgetConfig());
            extracted = this.widgetTypeManager.getWidgetType(widgetTypeCode);
            assertNotNull(extracted);
            assertEquals("Titolo modificato", extracted.getTitles().get("it"));
            assertEquals("Modified title", extracted.getTitles().get("en"));
        } catch (Throwable t) {
            throw t;
        } finally {
            if (null != this.widgetTypeManager.getWidgetType(widgetTypeCode)) {
                this.widgetTypeManager.deleteWidgetType(widgetTypeCode);
            }
            assertNull(this.widgetTypeManager.getWidgetType(widgetTypeCode));
        }
    }

    @Test
    void testUpdate() throws Throwable {
        String widgetTypeCode = "test_showletType";
        String icon= "font-awesome:fa-box";
        assertNull(this.widgetTypeManager.getWidgetType(widgetTypeCode));
        try {
            WidgetType type = this.createNewWidgetType(widgetTypeCode);
            this.widgetTypeManager.addWidgetType(type);
            WidgetType extracted = this.widgetTypeManager.getWidgetType(widgetTypeCode);
            assertNotNull(extracted);
            assertEquals("formAction", extracted.getParentType().getCode());
            assertEquals("/myNewJsp.jsp", extracted.getConfig().get("actionPath"));
            ApsProperties newProperties = new ApsProperties();
            this.widgetTypeManager.updateWidgetType(widgetTypeCode, extracted.getTitles(), newProperties, type.getMainGroup(),
                    type.getConfigUi(), type.getBundleId(), true, type.getWidgetCategory(),icon);
            extracted = this.widgetTypeManager.getWidgetType(widgetTypeCode);
            assertNotNull(extracted);
            assertNotNull(extracted.getConfig());
            assertEquals(0, extracted.getConfig().size());
            assertTrue(extracted.isReadonlyPageWidgetConfig());
            assertEquals(icon, extracted.getIcon());
            newProperties.put("contentId", "EVN103");
            this.widgetTypeManager.updateWidgetType(widgetTypeCode, extracted.getTitles(), newProperties, type.getMainGroup(),
                    type.getConfigUi(), type.getBundleId(), type.isReadonlyPageWidgetConfig(),type.getWidgetCategory(),icon);
            extracted = this.widgetTypeManager.getWidgetType(widgetTypeCode);
            assertNotNull(extracted);
            assertEquals("EVN103", extracted.getConfig().get("contentId"));
        } catch (Throwable t) {
            throw t;
        } finally {
            if (null != this.widgetTypeManager.getWidgetType(widgetTypeCode)) {
                this.widgetTypeManager.deleteWidgetType(widgetTypeCode);
            }
            assertNull(this.widgetTypeManager.getWidgetType(widgetTypeCode));
        }
    }

    @Test
    void testUpdateWithoutWidgetCategory() throws Throwable {
        String widgetTypeCode = "test_showletType";
        assertNull(this.widgetTypeManager.getWidgetType(widgetTypeCode));
        try {
            WidgetType type = this.createNewWidgetType(widgetTypeCode);
            this.widgetTypeManager.addWidgetType(type);
            WidgetType extracted = this.widgetTypeManager.getWidgetType(widgetTypeCode);
            assertNotNull(extracted);
            assertEquals("formAction", extracted.getParentType().getCode());
            assertEquals("/myNewJsp.jsp", extracted.getConfig().get("actionPath"));
            ApsProperties newProperties = new ApsProperties();
            this.widgetTypeManager.updateWidgetType(widgetTypeCode, extracted.getTitles(), newProperties, type.getMainGroup(),
                    type.getConfigUi(), type.getBundleId(), true);
            extracted = this.widgetTypeManager.getWidgetType(widgetTypeCode);
            assertNotNull(extracted);
            assertNotNull(extracted.getConfig());
            assertEquals("test",extracted.getWidgetCategory());
            assertEquals("iconTest", extracted.getIcon());
            assertEquals(0, extracted.getConfig().size());
            assertTrue(extracted.isReadonlyPageWidgetConfig());
            newProperties.put("contentId", "EVN103");
            this.widgetTypeManager.updateWidgetType(widgetTypeCode, extracted.getTitles(), newProperties, type.getMainGroup(),
                    type.getConfigUi(), type.getBundleId(), type.isReadonlyPageWidgetConfig(),type.getWidgetCategory());
            extracted = this.widgetTypeManager.getWidgetType(widgetTypeCode);
            assertNotNull(extracted);
            assertEquals("EVN103", extracted.getConfig().get("contentId"));
        } catch (Throwable t) {
            throw t;
        } finally {
            if (null != this.widgetTypeManager.getWidgetType(widgetTypeCode)) {
                this.widgetTypeManager.deleteWidgetType(widgetTypeCode);
            }
            assertNull(this.widgetTypeManager.getWidgetType(widgetTypeCode));
        }
    }

    @Test
    void testUpdateWithoutIcon() throws Throwable {
        String widgetTypeCode = "test_showletType";
        String icon= "font-awesome:fa-box";
        assertNull(this.widgetTypeManager.getWidgetType(widgetTypeCode));
        try {
            WidgetType type = this.createNewWidgetType(widgetTypeCode);
            this.widgetTypeManager.addWidgetType(type);
            WidgetType extracted = this.widgetTypeManager.getWidgetType(widgetTypeCode);
            assertNotNull(extracted);
            assertEquals("formAction", extracted.getParentType().getCode());
            assertEquals("/myNewJsp.jsp", extracted.getConfig().get("actionPath"));
            ApsProperties newProperties = new ApsProperties();
            this.widgetTypeManager.updateWidgetType(widgetTypeCode, extracted.getTitles(), newProperties, type.getMainGroup(),
                    type.getConfigUi(), type.getBundleId(), true, "test");
            extracted = this.widgetTypeManager.getWidgetType(widgetTypeCode);
            assertNotNull(extracted);
            assertNotNull(extracted.getConfig());
            assertEquals("test",extracted.getWidgetCategory());
            assertEquals("iconTest", extracted.getIcon());
            assertEquals(0, extracted.getConfig().size());
            assertTrue(extracted.isReadonlyPageWidgetConfig());
            newProperties.put("contentId", "EVN103");
            this.widgetTypeManager.updateWidgetType(widgetTypeCode, extracted.getTitles(), newProperties, type.getMainGroup(),
                    type.getConfigUi(), type.getBundleId(), type.isReadonlyPageWidgetConfig(),type.getWidgetCategory(), icon);
            extracted = this.widgetTypeManager.getWidgetType(widgetTypeCode);
            assertNotNull(extracted);
            assertEquals("EVN103", extracted.getConfig().get("contentId"));
        } catch (Throwable t) {
            throw t;
        } finally {
            if (null != this.widgetTypeManager.getWidgetType(widgetTypeCode)) {
                this.widgetTypeManager.deleteWidgetType(widgetTypeCode);
            }
            assertNull(this.widgetTypeManager.getWidgetType(widgetTypeCode));
        }
    }

    @Test
    void testUpdateReadOnlyPageConfigLockedWidget() throws Throwable {
        String widgetTypeCode = "entando_apis";
        WidgetType widgetType = widgetTypeManager.getWidgetType(widgetTypeCode);
        try {
            assertNotNull(widgetType);
            this.widgetTypeManager.updateWidgetType(widgetTypeCode, widgetType.getTitles(), widgetType.getConfig(), widgetType.getMainGroup(),
                    widgetType.getConfigUi(), widgetType.getBundleId(), false, "test", "test");
            WidgetType updated = this.widgetTypeManager.getWidgetType(widgetTypeCode);
            assertNotNull(updated);
            assertNotNull(updated.getConfig());
            assertTrue(updated.isReadonlyPageWidgetConfig());
            assertEquals(widgetType.getWidgetCategory(),updated.getWidgetCategory());
            assertEquals(widgetType.getIcon(),updated.getIcon());
        } catch (Throwable t) {
            throw t;
        }
    }
    
    private WidgetType createNewWidgetType(String code) {
        WidgetType type = new WidgetType();
        type.setCode(code);
        ApsProperties titles = new ApsProperties();
        titles.put("it", "Titolo");
        titles.put("en", "Title");
        type.setTitles(titles);
        WidgetType parent = this.widgetTypeManager.getWidgetType("formAction");
        assertNotNull(parent);
        type.setParentType(parent);
        type.setPluginCode(null);
        ApsProperties config = new ApsProperties();
        config.put("actionPath", "/myNewJsp.jsp");
        type.setConfig(config);
        type.setWidgetCategory("test");
        type.setIcon("iconTest");
        type.setReadonlyPageWidgetConfig(false);
        return type;
    }
    
    @Test
    void testWidgetEvent() throws Throwable {
        String widgetTypeCode = "test_widgetType";
        String testCode = "test_page_code";
        try {
            WidgetType type = this.createNewWidgetType(widgetTypeCode);
            type.setMainGroup(Group.FREE_GROUP_NAME);
            this.widgetTypeManager.addWidgetType(type);
            WidgetType extracted = this.widgetTypeManager.getWidgetType(widgetTypeCode);
            assertNotNull(extracted);
            assertEquals(Group.FREE_GROUP_NAME, extracted.getMainGroup());
            
            IPage page = createPageForTest(testCode, "service");
            Widget widgetOnPage = new Widget();
            widgetOnPage.setTypeCode(extracted.getCode());
            page.getWidgets()[3] = widgetOnPage;
            this.pageManager.addPage(page);
            
            IPage extractedPage = this.pageManager.getDraftPage(testCode);
            assertNotNull(extractedPage);
            assertNotNull(extractedPage.getWidgets()[3]);
            WidgetType widgetType = this.widgetTypeManager.getWidgetType(extractedPage.getWidgets()[3].getTypeCode());
            assertEquals(Group.FREE_GROUP_NAME, widgetType.getMainGroup());
            this.pageManager.setPageOnline(testCode);
            IPage extractedPublicPage = this.pageManager.getDraftPage(testCode);
            assertNotNull(extractedPublicPage);
            assertNotNull(extractedPublicPage.getWidgets()[3]);
            widgetType = this.widgetTypeManager.getWidgetType(extractedPublicPage.getWidgets()[3].getTypeCode());
            assertEquals(Group.FREE_GROUP_NAME, widgetType.getMainGroup());
            
            this.widgetTypeManager.updateWidgetType(widgetTypeCode,
                    extracted.getTitles(), extracted.getConfig(), Group.ADMINS_GROUP_NAME, 
                    extracted.getConfigUi(), null, Boolean.FALSE, null, null);
            extracted = this.widgetTypeManager.getWidgetType(widgetTypeCode);
            assertNotNull(extracted);
            assertEquals(Group.ADMINS_GROUP_NAME, extracted.getMainGroup());
            synchronized (this) {
                this.wait(1000);
            }
            extractedPage = this.pageManager.getDraftPage(testCode);
            assertNotNull(extractedPage);
            assertNotNull(extractedPage.getWidgets()[3]);
            widgetType = this.widgetTypeManager.getWidgetType(extractedPage.getWidgets()[3].getTypeCode());
            assertEquals(Group.ADMINS_GROUP_NAME, widgetType.getMainGroup());
            extractedPublicPage = this.pageManager.getDraftPage(testCode);
            assertNotNull(extractedPublicPage);
            assertNotNull(extractedPublicPage.getWidgets()[3]);
            widgetType = this.widgetTypeManager.getWidgetType(extractedPublicPage.getWidgets()[3].getTypeCode());
            assertEquals(Group.ADMINS_GROUP_NAME, widgetType.getMainGroup());
        } catch (Exception e) {
            throw e;
        } finally {
            this.pageManager.setPageOffline(testCode);
            this.pageManager.deletePage(testCode);
            this.widgetTypeManager.deleteWidgetType(widgetTypeCode);
        }
    }
    
    private IPage createPageForTest(String code, String parentCode) throws Throwable {
        IPage prototype = this.pageManager.getDraftPage("service");
        PageModel pageModel = this.pageModelManager.getPageModel(prototype.getMetadata().getModelCode());
        PageMetadata metadata = PageTestUtil.createPageMetadata(pageModel,
                true, "pagina temporanea", null, null, false, null, null);
        Widget[] widgets = new Widget[pageModel.getFrames().length];
        return PageTestUtil.createPage(code, parentCode, Group.FREE_GROUP_NAME, pageModel, metadata, widgets);
    }

    @BeforeEach
    private void init() {
        this.widgetTypeManager = (IWidgetTypeManager) this.getService(SystemConstants.WIDGET_TYPE_MANAGER);
        this.pageManager = (IPageManager) this.getService(SystemConstants.PAGE_MANAGER);
        this.pageModelManager = (IPageModelManager) this.getService(SystemConstants.PAGE_MODEL_MANAGER);
        DataSource dataSource = (DataSource) this.getApplicationContext().getBean("portDataSource");
        this.mockWidgetTypeDAO = new MockWidgetTypeDAO();
        this.mockWidgetTypeDAO.setDataSource(dataSource);
    }

    private IWidgetTypeManager widgetTypeManager = null;
    private IPageManager pageManager = null;
    private IPageModelManager pageModelManager;
    private MockWidgetTypeDAO mockWidgetTypeDAO;

}

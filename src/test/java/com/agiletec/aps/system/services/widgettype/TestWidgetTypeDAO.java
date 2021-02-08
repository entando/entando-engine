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
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.lang.ILangManager;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.aps.system.services.widgettype.WidgetTypeDAO;
import org.entando.entando.ent.exception.EntException;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author M.Diana
 */
class TestWidgetTypeDAO extends BaseTestCase {

    @Test
    void testLoadWidgetTypes() throws Throwable {
        DataSource dataSource = (DataSource) this.getApplicationContext().getBean("portDataSource");
        WidgetTypeDAO widgetTypeDao = new WidgetTypeDAO();
        widgetTypeDao.setDataSource(dataSource);
        ILangManager langManager = (ILangManager) this.getService(SystemConstants.LANGUAGE_MANAGER);
        widgetTypeDao.setLangManager(langManager);
        Map<String, WidgetType> types = null;
        try {
            types = widgetTypeDao.loadWidgetTypes();
        } catch (Throwable t) {
            throw t;
        }
        WidgetType widgetType = (WidgetType) types.get("formAction");
        assertNotNull(widgetType);
        widgetType = (WidgetType) types.get("login_form");
        assertNotNull(widgetType);
        widgetType = (WidgetType) types.get("content_viewer_list");
        assertNull(widgetType);
    }

    @Test
    void testGetWidgetType()  {
        String code="login_form";
        DataSource dataSource = (DataSource) this.getApplicationContext().getBean("portDataSource");
        WidgetTypeDAO widgetTypeDao = new WidgetTypeDAO();
        widgetTypeDao.setDataSource(dataSource);
        ILangManager langManager = (ILangManager) this.getService(SystemConstants.LANGUAGE_MANAGER);
        widgetTypeDao.setLangManager(langManager);
        WidgetType widgetType = null;
        try {
            widgetType = widgetTypeDao.getWidgetType(code);
        } catch (EntException e) {
            e.printStackTrace();
        }
        assertNotNull(widgetType);
        assertEquals("login_form",widgetType.getCode());
        assertTrue(widgetType.isReadonlyPageWidgetConfig());
        assertEquals("system",widgetType.getWidgetCategory());
        assertEquals("font-awesome:fa-sign-in",widgetType.getIcon());
    }
    
}

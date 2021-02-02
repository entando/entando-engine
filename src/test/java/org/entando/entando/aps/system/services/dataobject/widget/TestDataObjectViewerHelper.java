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
package org.entando.entando.aps.system.services.dataobject.widget;

import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.aps.system.services.widgettype.WidgetType;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.util.ApsProperties;

import static org.entando.entando.Jdk11CompatibleDateFormatter.formatMediumDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.agiletec.aps.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestDataObjectViewerHelper extends BaseTestCase {

    @Test
    void testGetRenderedDataObject() throws Throwable {
        try {
            String dataId = "ART1";
            String modelId = "3";
            String renderedDataObject = _helper.getRenderedDataObject(dataId, modelId, _requestContext);
            String expected = "------ RENDERING CONTENUTO: id = ART1; ---------\n"
                    + "ATTRIBUTI:\n"
                    + "  - AUTORI (Monolist-Monotext):\n"
                    + "         testo=Pippo;\n"
                    + "         testo=Paperino;\n"
                    + "         testo=Pluto;\n"
                    + "  - TITOLO (Text): testo=Il titolo;\n"
                    + "  - DATA (Date): data_media = "+formatMediumDate("10-mar-2004")+";\n"
                    + "------ END ------";
            assertEquals(replaceNewLine(expected.trim()), replaceNewLine(renderedDataObject.trim()));
        } catch (Throwable t) {
            throw t;
        }
    }

    private String replaceNewLine(String input) {
        input = input.replaceAll("\\n", "");
        input = input.replaceAll("\\r", "");
        return input;
    }

    @Test
    void testGetRenderedDataObjectNotApproved() throws Throwable {
        try {
            String dataId = "ART2";
            String modelId = "3";
            String renderedDataObject = _helper.getRenderedDataObject(
                    dataId, modelId, _requestContext);
            assertEquals("", renderedDataObject);
        } catch (Throwable t) {
            throw t;
        }
    }

    @Test
    void testGetRenderedDataObjectNotPresent() throws Throwable {
        try {
            String dataId = "ART3";
            String modelId = "3";
            String renderedDataObject = _helper.getRenderedDataObject(
                    dataId, modelId, _requestContext);
            assertEquals("", renderedDataObject);
        } catch (Throwable t) {
            throw t;
        }
    }

    @BeforeEach
    void init() {
        _requestContext = this.getRequestContext();
        Lang lang = new Lang();
        lang.setCode("it");
        lang.setDescr("italiano");
        _requestContext.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, lang);
        Widget widget = new Widget();
        IWidgetTypeManager showletTypeMan
                = (IWidgetTypeManager) this.getService(SystemConstants.WIDGET_TYPE_MANAGER);
        WidgetType showletType = showletTypeMan.getWidgetType("content_viewer");
        widget.setType(showletType);
        widget.setConfig(new ApsProperties());
        _requestContext.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_WIDGET, widget);
        this._helper = (IDataObjectViewerHelper) this.getApplicationContext().getBean("DataObjectViewerHelper");
    }

    private RequestContext _requestContext;
    private IDataObjectViewerHelper _helper;

}

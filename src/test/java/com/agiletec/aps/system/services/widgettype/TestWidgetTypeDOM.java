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

import java.util.ArrayList;
import java.util.List;

import org.entando.entando.aps.system.services.widgettype.WidgetTypeDOM;
import org.entando.entando.aps.system.services.widgettype.WidgetTypeParameter;

import com.agiletec.aps.BaseTestCase;
import org.entando.entando.ent.exception.EntException;

/**
 * @author M.Diana
 */
public class TestWidgetTypeDOM extends BaseTestCase {
	
    public void testParseConfig() throws EntException {
		String framesXml = "<config>" +
							"<parameter name=\"contentType\">" +
							"Tipo di contenuto (obbligatorio)" +
							"</parameter>" +
							"<parameter name=\"modelId\">" +
							"Modello di contenuto (obbligatorio)" +
							"</parameter>" +
							"<parameter name=\"filters\" />" +
							"<action name=\"listViewerConfig\"/>" +
							"</config>";
		WidgetTypeDOM showletTypeDOM = new WidgetTypeDOM(framesXml);
        String action = showletTypeDOM.getAction();
        assertTrue(action.equals("listViewerConfig"));
        List<WidgetTypeParameter> params = showletTypeDOM.getParameters();
        assertEquals(3, params.size());
	}
    
    public void testCreateConfig() throws EntException {
    	WidgetTypeParameter params1 = new WidgetTypeParameter();
    	params1.setName("param1");
    	params1.setDescr("Param1 Descr");
    	WidgetTypeParameter params2 = new WidgetTypeParameter();
    	params2.setName("param2");
    	params2.setDescr("Param2 Descr");
    	List<WidgetTypeParameter> params = new ArrayList<WidgetTypeParameter>();
    	params.add(params1);
    	params.add(params2);
    	WidgetTypeDOM showletTypeDOM = new WidgetTypeDOM(params, "customActionName");
    	String xml = showletTypeDOM.getXMLDocument();
    	
    	WidgetTypeDOM showletTypeDOM2 = new WidgetTypeDOM(xml);
    	assertEquals("customActionName", showletTypeDOM2.getAction());
    	List<WidgetTypeParameter> extractedParams = showletTypeDOM2.getParameters();
    	assertEquals(2, extractedParams.size());
    	assertEquals("param1", extractedParams.get(0).getName());
    	assertEquals("Param2 Descr", extractedParams.get(1).getDescr());
    }
			
}

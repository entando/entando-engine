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
package org.entando.entando.aps.system.services.dataobject.parse;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.agiletec.aps.BaseTestCase;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.aps.system.services.dataobject.model.DataObject;
import org.entando.entando.aps.system.services.dataobject.IDataObjectManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestDataObjectDOM extends BaseTestCase {

	@Test
    void testGetXMLDocument() throws EntException {
		DataObject dataObjectTest = this._dataObjectManager.createDataObject("ART");
		assertNotNull(dataObjectTest);
		dataObjectTest.addGroup("tempGroupName");
		String xml = dataObjectTest.getXML();
		int index = xml.indexOf("tempGroupName");
		assertTrue((index != -1));
	}

    @BeforeEach
	void init() throws Exception {
		try {
			_dataObjectManager = (IDataObjectManager) this.getService("DataObjectManager");
		} catch (Throwable t) {
			throw new Exception(t);
		}
	}

	private IDataObjectManager _dataObjectManager = null;

}

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
package com.agiletec.aps.system.common.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.ListableBeanFactory;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import org.junit.jupiter.api.Test;

/**
 * @author E.Santoboni
 */
class TestEntityManager extends BaseTestCase {
	
    @Test
	public void testGetAttributeTypes() throws Throwable {
		this.init();
        if (null == this._entityManager) return;
		Map<String, AttributeInterface> attributes = this._entityManager.getEntityAttributePrototypes();
		assertNotNull(attributes);
		assertNotNull(attributes.get("Text"));
	}
	
	@Test
	public void testGetEntityTypes() throws Throwable {
		this.init();
        if (null == this._entityManager) return;
		Map<String, AttributeInterface> attributes = this._entityManager.getEntityAttributePrototypes();
		String testTypeCode = "XXX";
		String testAttributeName = "testAttributeName";
		Map<String, IApsEntity> entityTypes = this._entityManager.getEntityPrototypes();
		try {
			assertNotNull(entityTypes);
			assertTrue(entityTypes.size()>0);
			assertNull(entityTypes.get(testTypeCode));
			IApsEntity entityPrototype = new ArrayList<IApsEntity>(entityTypes.values()).get(0);
			int initAttributeNumber = entityPrototype.getAttributeList().size();
			
			entityPrototype.setTypeCode(testTypeCode);
			entityPrototype.setTypeDescr("testDescription");
			assertNull(entityPrototype.getAttribute(testAttributeName));
			AttributeInterface newAttribute = attributes.get("Text");
			newAttribute.setName(testAttributeName);
			newAttribute.setRequired(true);
			entityPrototype.addAttribute(newAttribute);
			
			((IEntityTypesConfigurer) this._entityManager).addEntityPrototype(entityPrototype);
			
			entityTypes = this._entityManager.getEntityPrototypes();
			IApsEntity extractedEntityPrototype = entityTypes.get(testTypeCode);
			assertNotNull(extractedEntityPrototype);
			assertEquals(initAttributeNumber+1, extractedEntityPrototype.getAttributeList().size());
		} catch (Throwable t) {
			throw t;
		} finally {
			((IEntityTypesConfigurer) this._entityManager).removeEntityPrototype(testTypeCode);
			entityTypes = this._entityManager.getEntityPrototypes();
			assertNull(entityTypes.get(testTypeCode));
		}
	}
	
	private void init() {
		ListableBeanFactory factory = (ListableBeanFactory) this.getApplicationContext();
		String[] defNames = factory.getBeanNamesForType(IEntityManager.class);
		if (null != defNames && defNames.length>0) {
			this._entityManager = (IEntityManager) this.getApplicationContext().getBean(defNames[0]);
		}
	}
	
	private IEntityManager _entityManager;
	
}
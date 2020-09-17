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
package com.agiletec.aps.system.common.entity.model.attribute;

import java.util.List;
import java.util.Map;

import org.jdom.Element;

import com.agiletec.aps.system.common.entity.model.AttributeSearchInfo;
import org.entando.entando.ent.exception.EntException;
import com.agiletec.aps.system.services.lang.Lang;

/**
 * Abstract class used for the implementation of "Complex Attributes". Complex attributes are those 
 * constituted by the aggregation, in different ways, of "Simple Attributes".
 * @author E.Santoboni
 */
public abstract class AbstractComplexAttribute extends AbstractAttribute {
	
	@Override
	public boolean isSimple() {
		return false;
	}
	
	/**
	 * Return the list of "Simple Attributes" constituting the Complex Attribute
	 * @return The list of simple Attributes.
	 */
	public abstract List<AttributeInterface> getAttributes();
	
	/**
	 * Return the structure of attributes suitable for the rendering process.
	 * @return The structure of attributes suitable for rendering.
	 */
	public abstract Object getRenderingAttributes();
	
	/**
	 * This method overrides the one of the abstract class so that it always returns false.
	 * This happens because Complex Attributes can never be "searchable" by design.
	 * @return Return always false.
	 * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#isSearchable()
	 */
	@Override
	public boolean isSearchable() {
		return false;
	}
	
	@Override
	public boolean isSearchableOptionSupported() {
		return false;
	}
	
	@Override
	public List<AttributeSearchInfo> getSearchInfos(List<Lang> systemLangs) {
		return null;
	}
	
	/**
	 * Set up the configuration of the Complex Attributes.
	 * @param attributeElement The JDOM element of the attribute. 
	 * @param attrTypes The list of the attributes available. 
	 * @throws EntException in case of error.
	 */
	public abstract void setComplexAttributeConfig(Element attributeElement, Map<String, AttributeInterface> attrTypes) throws EntException;
	
}

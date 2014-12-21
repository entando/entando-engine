/*
 * Copyright 2013-Present Entando Corporation (http://www.entando.com) All rights reserved.
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
package com.agiletec.aps.system.common.entity.parse.attribute;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.CompositeAttribute;

/**
 * Handler class that interprets the XML defining a 'Composite Attribute'.
 * @author E.Santoboni
 */
public class CompositeAttributeHandler extends AbstractAttributeHandler {
	
	@Override
	public void startAttribute(Attributes attributes, String qName) throws SAXException {
		if (qName.equals("composite")) {
			startComposite(attributes, qName);
		} else {
			this.startAttributeElement(attributes, qName);
		}
	}
	
	private void startAttributeElement(Attributes attributes, String qName) throws SAXException {
		if (null == this._elementAttribute) {
			if (qName.equals("attribute")) {
				String attributename = extractAttribute(attributes, "name", qName, true);
				this._elementAttribute = ((CompositeAttribute) this.getCurrentAttr()).getAttribute(attributename);
			}
		} else {
			if (null == this._elementAttributeHandler) {
				this._elementAttributeHandler = this._elementAttribute.getHandler();
				this._elementAttributeHandler.setCurrentAttr(this._elementAttribute);
			}
			this._elementAttributeHandler.startAttribute(attributes, qName);
		}
	}
	
	private void startComposite(Attributes attributes, String qName) throws SAXException {
		//nothing to do
	}
	
	@Override
	public void endAttribute(String qName, StringBuffer textBuffer) {
		if (qName.equals("composite")) {
			endComposite();
		} else {
			if (null == _elementAttributeHandler || _elementAttributeHandler.isEndAttribute(qName)) {
				_elementAttribute = null;
				_elementAttributeHandler = null;
			} else {
				_elementAttributeHandler.endAttribute(qName, textBuffer);
			}
		}
	}
	
	private void endComposite() {
		_elementAttribute = null;
		_elementAttributeHandler = null;
	}
	
	@Override
	public boolean isEndAttribute(String qName) {
		return qName.equals("composite");
	}
	
	private AttributeInterface _elementAttribute;
	private AttributeHandlerInterface _elementAttributeHandler;

}
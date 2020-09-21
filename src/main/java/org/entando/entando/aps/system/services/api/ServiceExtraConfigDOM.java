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
package org.entando.entando.aps.system.services.api;

import java.io.StringReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

import org.entando.entando.ent.exception.EntException;

/**
 * @author E.Santoboni
 */
public class ServiceExtraConfigDOM {

	private static final EntLogger _logger =  EntLogFactory.getSanitizedLogger(ServiceExtraConfigDOM.class);
	
    protected ServiceExtraConfigDOM() {
        this._doc = new Document();
        Element elementRoot = new Element("config");
        this._doc.setRootElement(elementRoot);
    }
    
    protected ServiceExtraConfigDOM(String xml) throws EntException {
        if (null == xml || xml.trim().length() == 0) {
            return;
        }
        this.decodeDOM(xml);
    }
    
    protected String[] extractFreeParameters() {
        String[] freeParameters = null;
        Element freeParametersElement = this._doc.getRootElement().getChild(FREE_PARAMETERS_ELEMENT_NAME);
        if (null != freeParametersElement) {
            List<Element> freeParametersElements = freeParametersElement.getChildren(FREE_PARAMETER_ELEMENT_NAME);
            if (null != freeParametersElements && freeParametersElements.size() > 0) {
                freeParameters = new String[freeParametersElements.size()];
                for (int i = 0; i < freeParametersElements.size(); i++) {
                    freeParameters[i] = freeParametersElements.get(i).getAttributeValue(FREE_PARAMETER_ATTRIBUTE_NAME);
                }
            }
        }
        return freeParameters;
    }
    
    private void decodeDOM(String xml) throws EntException {
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        StringReader reader = new StringReader(xml);
        try {
            this._doc = builder.build(reader);
        } catch (Throwable t) {
            _logger.error("Error while parsing xml. {} ", xml, t);
            throw new EntException("Error detected while parsing the XML", t);
        }
    }
    
    public String extractXml(String[] freeParameters) {
        this.fillDocument(freeParameters);
        return this.getXMLDocument();
    }
    
    protected void fillDocument(String[] freeParameters) {
        if (null != freeParameters && freeParameters.length > 0) {
            Element freeParametersElement = new Element(FREE_PARAMETERS_ELEMENT_NAME);
            this._doc.getRootElement().addContent(freeParametersElement);
            for (int i = 0; i < freeParameters.length; i++) {
                String paramName = freeParameters[i];
                Element freeParameterElement = new Element(FREE_PARAMETER_ELEMENT_NAME);
                freeParameterElement.setAttribute(FREE_PARAMETER_ATTRIBUTE_NAME, paramName);
                freeParametersElement.addContent(freeParameterElement);
            }
        }
    }
    
    protected String getXMLDocument() {
        XMLOutputter out = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        out.setFormat(format);
        return out.outputString(this._doc);
    }
    
    private Document _doc;
    
    private static final String FREE_PARAMETERS_ELEMENT_NAME = "freeparameters";
    
    private static final String FREE_PARAMETER_ELEMENT_NAME = "parameter";
    
    private static final String FREE_PARAMETER_ATTRIBUTE_NAME = "name";
    
}

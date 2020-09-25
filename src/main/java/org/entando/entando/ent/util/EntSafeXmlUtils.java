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

package org.entando.entando.ent.util;

import org.entando.entando.ent.exception.EntRuntimeException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;

/**
 * Helpers to centralize creation of safe factories and parsers
 * <p>
 * Reference:
 * <p>
 * https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#schemafactory
 */
public class EntSafeXmlUtils {

    private static final String HTTP_XML_ORG_SAX_FEATURES_EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    private static final String HTTP_XML_ORG_SAX_FEATURES_EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    private static final String HTTP_APACHE_ORG_XML_FEATURES_NONVALIDATING_LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    public static final String XMLSCHEMA_FACTORY_CLASS = "com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory";

    private static SAXParserFactory newSaxParserFactory() {
        SAXParserFactory parseFactory = SAXParserFactory.newInstance();
        try {
            parseFactory.setFeature(HTTP_XML_ORG_SAX_FEATURES_EXTERNAL_GENERAL_ENTITIES, false);
            parseFactory.setFeature(HTTP_XML_ORG_SAX_FEATURES_EXTERNAL_PARAMETER_ENTITIES, false);
            parseFactory.setFeature(HTTP_APACHE_ORG_XML_FEATURES_NONVALIDATING_LOAD_EXTERNAL_DTD, false);
        } catch (SAXNotSupportedException | SAXNotRecognizedException | ParserConfigurationException e) {
            throw new EntRuntimeException("Unable to setup the SAXParserFactory in secure mode", e);
        }
        return parseFactory;
    }

    public static SAXParser newSafeSAXParser() throws ParserConfigurationException, SAXException {
        SAXParserFactory parseFactory = newSaxParserFactory();
        return parseFactory.newSAXParser();
    }

    public static SchemaFactory newSafeSchemaFactory(String schemaLanguage) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(schemaLanguage, XMLSCHEMA_FACTORY_CLASS, null);
        try {
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
            throw new EntRuntimeException("Unable to setup the SchemaFactory in secure mode", e);
        }
        return schemaFactory;
    }

    public static Schema newSafeSchema(String schemaLanguage, InputStream resourceAsStream) throws SAXException {
        SchemaFactory factory = EntSafeXmlUtils.newSafeSchemaFactory(schemaLanguage);
        StreamSource schemaSource = new StreamSource(resourceAsStream);
        return factory.newSchema(schemaSource);
    }

    private EntSafeXmlUtils() {
    }
}

package org.entando.entando.ent.util;

import com.agiletec.aps.system.common.entity.parse.EntityHandler;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

class EntSafeXmlUtilsTest {

    @Test
    void testNewSafeSAXParser() throws ParserConfigurationException, SAXException, IOException {
        SAXParser p = EntSafeXmlUtils.newSafeSAXParser();
        Assertions.assertNotNull(p);
        Assertions.assertFalse(p.getXMLReader().getFeature("http://xml.org/sax/features/external-general-entities"));
        Assertions.assertFalse(p.getXMLReader().getFeature("http://xml.org/sax/features/external-parameter-entities"));
        Assertions.assertFalse(p.getXMLReader().getFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd"));
        InputStream xmlIs = getResIs("components/test_component/component.xml");
        Assertions.assertNotNull(xmlIs);
        EntityHandler handler = new EntityHandler();
        p.parse(xmlIs, handler);
    }

    @Test
    void testNewSafeSchemaFactory() throws SAXException, IOException {
        InputStream schemaIs = getResIs("components/test_schema/componentDef-4.2.xsd");
        Assertions.assertNotNull(schemaIs);
        InputStream xmlIs = getResIs("components/test_component/component.xml");
        Assertions.assertNotNull(xmlIs);
        Schema schema = EntSafeXmlUtils.newSafeSchema(XMLConstants.W3C_XML_SCHEMA_NS_URI, schemaIs);
        Validator validator = schema.newValidator();
        Source source = new StreamSource(xmlIs);
        Assertions.assertNotNull(source);
        validator.validate(source);
    }

    private InputStream getResIs(String s) {
        return this.getClass().getClassLoader().getResourceAsStream(s);
    }
}
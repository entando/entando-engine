package org.entando.entando.ent.util;

import com.agiletec.aps.BaseTestCase;
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
import junit.framework.TestCase;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.xml.sax.SAXException;

public class EntSafeXmlUtilsTest {

    @Test
    public void testNewSafeSAXParser() throws ParserConfigurationException, SAXException, IOException {
        SAXParser p = EntSafeXmlUtils.newSafeSAXParser();
        assertNotNull(p);
        assertFalse(p.getXMLReader().getFeature("http://xml.org/sax/features/external-general-entities"));
        assertFalse(p.getXMLReader().getFeature("http://xml.org/sax/features/external-parameter-entities"));
        assertFalse(p.getXMLReader().getFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd"));
        InputStream xmlIs = getResIs("components/test_component/component.xml");
        assertNotNull(xmlIs);
        EntityHandler handler = new EntityHandler();
        p.parse(xmlIs, handler);
    }

    @Test
    public void testNewSafeSchemaFactory() throws SAXException, IOException {
        InputStream schemaIs = getResIs("components/test_schema/componentDef-4.2.xsd");
        assertNotNull(schemaIs);
        InputStream xmlIs = getResIs("components/test_component/component.xml");
        assertNotNull(xmlIs);
        Schema schema = EntSafeXmlUtils.newSafeSchema(XMLConstants.W3C_XML_SCHEMA_NS_URI, schemaIs);
        Validator validator = schema.newValidator();
        Source source = new StreamSource(xmlIs);
        assertNotNull(source);
        validator.validate(source);
    }

    private InputStream getResIs(String s) {
        return this.getClass().getClassLoader().getResourceAsStream(s);
    }
}
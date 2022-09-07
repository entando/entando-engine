package org.entando.entando.aps.system.services.page.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.agiletec.aps.util.ApsProperties;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.Test;

class WidgetConfigPropertiesSerializerTest {

    @Test
    void testSerializeEmptyContents() throws Exception {
        ApsProperties properties = new ApsProperties(new Properties());
        properties.put("contents", List.of());

        String json = testWidgetConfigPropertiesSerializer(properties);

        assertEquals("{\"contents\":[]}", json);
    }

    @Test
    void testSerializeContentsWithSimpleDescription() throws Exception {
        Properties contentProperties = new Properties();
        contentProperties.setProperty("contentId", "EVN21");
        contentProperties.setProperty("modelId", "list");
        contentProperties.setProperty("contentDescription", "Simple description");

        ApsProperties properties = new ApsProperties(new Properties());
        properties.put("contents", List.of(contentProperties));

        String json = testWidgetConfigPropertiesSerializer(properties);

        assertEquals("{\"contents\":[{\"modelId\":\"list\",\"contentDescription\":\"Simple description\",\"contentId\":\"EVN21\"}]}", json);
    }

    @Test
    void testSerializeContentsWithDescriptionContainingSpecialChars() throws Exception {
        Properties contentProperties = new Properties();
        contentProperties.setProperty("contentId", "EVN21");
        contentProperties.setProperty("modelId", "list");
        contentProperties.setProperty("contentDescription", "key1: value1, key2: value2");

        ApsProperties properties = new ApsProperties(new Properties());
        properties.put("contents", List.of(contentProperties));

        String json = testWidgetConfigPropertiesSerializer(properties);

        assertEquals("{\"contents\":[{\"modelId\":\"list\",\"contentDescription\":\"key1: value1, key2: value2\",\"contentId\":\"EVN21\"}]}", json);
    }

    private String testWidgetConfigPropertiesSerializer(ApsProperties properties) throws Exception {
        Writer jsonWriter = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
        SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();

        WidgetConfigPropertiesSerializer serializer = new WidgetConfigPropertiesSerializer();
        serializer.serialize(properties, jsonGenerator, serializerProvider);
        jsonGenerator.flush();

        return jsonWriter.toString();
    }
}

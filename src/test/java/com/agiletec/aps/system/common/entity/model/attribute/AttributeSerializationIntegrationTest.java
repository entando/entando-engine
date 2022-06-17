package com.agiletec.aps.system.common.entity.model.attribute;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.lang.ILangManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.context.ContextLoader;

class AttributeSerializationIntegrationTest extends BaseTestCase {

    private ILangManager langManager;

    @BeforeEach
    public void init() {
        this.langManager = (ILangManager) this.getService(SystemConstants.LANGUAGE_MANAGER);
    }

    @Test
    void testSerializeTextAttribute() throws Exception {
        TextAttribute attribute = new TextAttribute();
        attribute.setLangManager(langManager);
        attribute = testSerializeAndDeserialize(attribute);
        Assertions.assertNotNull(attribute.getLangManager());
        attribute = testSerializeAndDeserializeNullApplicationContext(attribute);
        Assertions.assertNull(attribute.getLangManager());
    }

    @Test
    void testSerializeEnumeratorAttribute() throws Exception {
        EnumeratorAttribute attribute = new EnumeratorAttribute();
        attribute.setBeanFactory(this.getApplicationContext());
        attribute.setLangManager(langManager);
        attribute = testSerializeAndDeserialize(attribute);
        Assertions.assertNotNull(attribute.getBeanFactory());
        Assertions.assertNotNull(attribute.getLangManager());
        attribute = testSerializeAndDeserializeNullApplicationContext(attribute);
        Assertions.assertNull(attribute.getBeanFactory());
        Assertions.assertNull(attribute.getLangManager());
    }

    private <T> T testSerializeAndDeserializeNullApplicationContext(T attribute) throws Exception {
        try (MockedStatic<ContextLoader> contextLoader = Mockito.mockStatic(ContextLoader.class)) {
            contextLoader.when(() -> ContextLoader.getCurrentWebApplicationContext()).thenReturn(null);
            return testSerializeAndDeserialize(attribute);
        }
    }

    private <T> T testSerializeAndDeserialize(T attribute) throws Exception {

        byte[] data;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(os)) {
            objectOutputStream.writeObject(attribute);
            data = os.toByteArray();
        }

        try (ByteArrayInputStream is = new ByteArrayInputStream(data);
                ObjectInputStream objectInputStream = new ObjectInputStream(is)) {
            return (T) objectInputStream.readObject();
        }
    }
}

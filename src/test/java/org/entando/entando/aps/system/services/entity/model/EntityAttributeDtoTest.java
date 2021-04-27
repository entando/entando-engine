package org.entando.entando.aps.system.services.entity.model;

import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.BooleanAttribute;
import org.entando.entando.ent.util.EntLogging;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EntityAttributeDtoTest {
    private final EntLogging.EntLogger logger = EntLogging.EntLogFactory.getSanitizedLogger(getClass());

    private EntityAttributeDto entityAttributeDto;

    @Test
    void testFillEntityAttributeBoolean() {
        AttributeInterface attribute = new BooleanAttribute();
        entityAttributeDto = new EntityAttributeDto(attribute);

        entityAttributeDto.setValue("true");
        entityAttributeDto.fillEntityAttribute(attribute, null);
        Assertions.assertTrue(((BooleanAttribute) attribute).getBooleanValue());

        entityAttributeDto.setValue("false");
        entityAttributeDto.fillEntityAttribute(attribute, null);
        Assertions.assertFalse(((BooleanAttribute) attribute).getBooleanValue());

        entityAttributeDto.setValue(true);
        entityAttributeDto.fillEntityAttribute(attribute, null);
        Assertions.assertTrue(((BooleanAttribute) attribute).getBooleanValue());

        entityAttributeDto.setValue(false);
        entityAttributeDto.fillEntityAttribute(attribute, null);
        Assertions.assertFalse(((BooleanAttribute) attribute).getBooleanValue());
    }
}

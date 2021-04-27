package org.entando.entando.aps.system.services.entity.model;

import com.agiletec.aps.system.common.entity.model.attribute.BooleanAttribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EntityAttributeDtoTest {

    @Test
    void testFillEntityAttributeBoolean() {
        EntityAttributeDto entityAttributeDto;

        BooleanAttribute attribute = new BooleanAttribute();
        entityAttributeDto = new EntityAttributeDto(attribute);

        entityAttributeDto.setValue("true");
        entityAttributeDto.fillEntityAttribute(attribute, null);
        Assertions.assertTrue(attribute.getBooleanValue());

        entityAttributeDto.setValue("false");
        entityAttributeDto.fillEntityAttribute(attribute, null);
        Assertions.assertFalse(attribute.getBooleanValue());

        entityAttributeDto.setValue(true);
        entityAttributeDto.fillEntityAttribute(attribute, null);
        Assertions.assertTrue(attribute.getBooleanValue());

        entityAttributeDto.setValue(false);
        entityAttributeDto.fillEntityAttribute(attribute, null);
        Assertions.assertFalse(attribute.getBooleanValue());
    }
}

package org.entando.entando.aps.system.common.entity.model.attribute;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumeratorMapAttributeTest {

    @Test
    void testDefaultSeparator() {

        EnumeratorMapAttribute attribute = new EnumeratorMapAttribute();
        attribute.setStaticItems("k1=v1,k2=v2");

        attribute.initItems();

        Assertions.assertEquals(2, attribute.getMapItems().length);
        Assertions.assertEquals("k1", attribute.getMapItems()[0].getKey());
        Assertions.assertEquals("v1", attribute.getMapItems()[0].getValue());
        Assertions.assertEquals("k2", attribute.getMapItems()[1].getKey());
        Assertions.assertEquals("v2", attribute.getMapItems()[1].getValue());
    }

    @Test
    void testCustomSeparator() {

        EnumeratorMapAttribute attribute = new EnumeratorMapAttribute();
        attribute.setCustomSeparator("|");
        attribute.setStaticItems("k1=v1|k2=v2");

        attribute.initItems();

        Assertions.assertEquals(2, attribute.getMapItems().length);
        Assertions.assertEquals("k1", attribute.getMapItems()[0].getKey());
        Assertions.assertEquals("v1", attribute.getMapItems()[0].getValue());
        Assertions.assertEquals("k2", attribute.getMapItems()[1].getKey());
        Assertions.assertEquals("v2", attribute.getMapItems()[1].getValue());
    }
}

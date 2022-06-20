package com.agiletec.aps.system.common.entity.model.attribute;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumeratorAttributeTest {

    @Test
    void testDefaultSeparator() {

        EnumeratorAttribute attribute = new EnumeratorAttribute();
        attribute.setStaticItems("A,B,C");

        attribute.initItems();

        Assertions.assertEquals(3, attribute.getItems().length);
        Assertions.assertEquals("A", attribute.getItems()[0]);
        Assertions.assertEquals("B", attribute.getItems()[1]);
        Assertions.assertEquals("C", attribute.getItems()[2]);
    }

    @Test
    void testCustomSeparator() {

        EnumeratorAttribute attribute = new EnumeratorAttribute();
        attribute.setCustomSeparator("|");
        attribute.setStaticItems("A|B|C");

        attribute.initItems();

        Assertions.assertEquals(3, attribute.getItems().length);
        Assertions.assertEquals("A", attribute.getItems()[0]);
        Assertions.assertEquals("B", attribute.getItems()[1]);
        Assertions.assertEquals("C", attribute.getItems()[2]);
    }
}

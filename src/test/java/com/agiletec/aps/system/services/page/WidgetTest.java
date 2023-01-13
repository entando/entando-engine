package com.agiletec.aps.system.services.page;

import com.agiletec.aps.util.ApsProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WidgetTest {

    @Test
    void testEqualsOnClonedObject() {
        Widget widget = new Widget();
        widget.setConfig(new ApsProperties());
        widget.setTypeCode("code1");
        Widget clonedWidget = widget.clone();
        Assertions.assertEquals(clonedWidget, widget);
        Assertions.assertEquals(widget.hashCode(), clonedWidget.hashCode());
    }

    @Test
    void testEqualsDifferentConfig() {
        Widget widget1 = new Widget();
        widget1.setConfig(new ApsProperties());
        Widget widget2 = new Widget();
        ApsProperties config2 = new ApsProperties();
        config2.setProperty("key", "value");
        widget2.setConfig(config2);
        Assertions.assertNotEquals(widget1, widget2);
    }

    @Test
    void testEqualsDifferentConfigAndFirstNull() {
        Widget widget1 = new Widget();
        Widget widget2 = new Widget();
        ApsProperties config2 = new ApsProperties();
        config2.setProperty("key", "value");
        widget2.setConfig(config2);
        Assertions.assertNotEquals(widget1, widget2);
    }

    @Test
    void testEqualsDifferentCode() {
        Widget widget1 = new Widget();
        widget1.setTypeCode("code1");
        Widget widget2 = new Widget();
        widget2.setTypeCode("code2");
        Assertions.assertNotEquals(widget1, widget2);
    }

    @Test
    void testEqualsDifferentCodeAndFirstNull() {
        Widget widget1 = new Widget();
        Widget widget2 = new Widget();
        widget2.setTypeCode("code2");
        Assertions.assertNotEquals(widget1, widget2);
    }
}

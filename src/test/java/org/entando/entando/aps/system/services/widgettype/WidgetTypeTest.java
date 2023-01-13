package org.entando.entando.aps.system.services.widgettype;

import com.agiletec.aps.util.ApsProperties;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WidgetTypeTest {

    @Test
    void testEqualsOnClonedObject() {
        WidgetType widgetType = new WidgetType();
        widgetType.setCode("my-widget");
        widgetType.setAction("action");
        ApsProperties config = new ApsProperties();
        config.setProperty("key", "value");
        widgetType.setConfig(config);
        widgetType.setMainGroup("group");
        widgetType.setTypeParameters(List.of(new WidgetTypeParameter("name", "description")));
        widgetType.setParentType(getParentType("parentType"));
        widgetType.setParentTypeCode("parentType");
        widgetType.setPluginCode("pluginCode");
        widgetType.setTitles(getTitles("title", "titolo"));
        widgetType.setWidgetCategory("category");
        widgetType.setIcon("icon");
        WidgetType clonedWidgetType = widgetType.clone();
        Assertions.assertEquals(clonedWidgetType, widgetType);
        Assertions.assertEquals(clonedWidgetType.hashCode(), widgetType.hashCode());
    }

    @Test
    void testEqualsDifferentActions() {
        WidgetType widgetType1 = new WidgetType();
        widgetType1.setAction("action1");
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setAction("action2");
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentActionsAndFirstNull() {
        WidgetType widgetType1 = new WidgetType();
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setAction("action2");
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentCode() {
        WidgetType widgetType1 = new WidgetType();
        widgetType1.setCode("code1");
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setCode("code2");
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentCodeAndFirstNull() {
        WidgetType widgetType1 = new WidgetType();
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setCode("code2");
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentConfig() {
        WidgetType widgetType1 = new WidgetType();
        widgetType1.setConfig(new ApsProperties());
        WidgetType widgetType2 = new WidgetType();
        ApsProperties config2 = new ApsProperties();
        config2.setProperty("key", "value");
        widgetType2.setConfig(config2);
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentConfigAndFirstNull() {
        WidgetType widgetType1 = new WidgetType();
        WidgetType widgetType2 = new WidgetType();
        ApsProperties config2 = new ApsProperties();
        config2.setProperty("key", "value");
        widgetType2.setConfig(config2);
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentLocked() {
        WidgetType widgetType1 = new WidgetType();
        WidgetType widgetType2 = new WidgetType();
        widgetType1.setLocked(true);
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentMainGroup() {
        WidgetType widgetType1 = new WidgetType();
        widgetType1.setMainGroup("group1");
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setMainGroup("group2");
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentMainGroupAndFirstNull() {
        WidgetType widgetType1 = new WidgetType();
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setMainGroup("group2");
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentWidgetTypeParameters() {
        WidgetType widgetType1 = new WidgetType();
        widgetType1.setTypeParameters(List.of(new WidgetTypeParameter("name1", "description1")));
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setTypeParameters(List.of(new WidgetTypeParameter("name2", "description2")));
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentWidgetTypeParametersAndFirstNull() {
        WidgetType widgetType1 = new WidgetType();
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setTypeParameters(List.of(new WidgetTypeParameter("name2", "description2")));
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentParentType() {
        WidgetType widgetType1 = new WidgetType();
        widgetType1.setParentType(getParentType("parentType1"));
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setParentType(getParentType("parentType2"));
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentParentTypeAndFirstNull() {
        WidgetType widgetType1 = new WidgetType();
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setParentType(getParentType("parentType2"));
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentParentTypeCode() {
        WidgetType widgetType1 = new WidgetType();
        widgetType1.setParentTypeCode("parentType1");
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setParentTypeCode("parentType2");
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentParentTypeCodeAndFirstNull() {
        WidgetType widgetType1 = new WidgetType();
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setParentTypeCode("parentType2");
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentPluginCode() {
        WidgetType widgetType1 = new WidgetType();
        widgetType1.setPluginCode("pluginCode1");
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setPluginCode("pluginCode2");
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentPluginCodeAndFirstNull() {
        WidgetType widgetType1 = new WidgetType();
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setPluginCode("pluginCode2");
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentTitles() {
        WidgetType widgetType1 = new WidgetType();
        widgetType1.setTitles(getTitles("title 1", "titolo 1"));
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setTitles(getTitles("title 2", "titolo 2"));
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentTitlesAndFirstNull() {
        WidgetType widgetType1 = new WidgetType();
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setTitles(getTitles("title 2", "titolo 2"));
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentReadonlyPageWidgetConfig() {
        WidgetType widgetType1 = new WidgetType();
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setReadonlyPageWidgetConfig(true);
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentReadonlyPageWidgetConfigAndFirstNull() {
        WidgetType widgetType1 = new WidgetType();
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setReadonlyPageWidgetConfig(true);
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentWidgetCategory() {
        WidgetType widgetType1 = new WidgetType();
        widgetType1.setWidgetCategory("category1");
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setWidgetCategory("category2");
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentWidgetCategoryAndFirstNull() {
        WidgetType widgetType1 = new WidgetType();
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setWidgetCategory("category2");
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentIcon() {
        WidgetType widgetType1 = new WidgetType();
        widgetType1.setIcon("icon1");
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setIcon("icon2");
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    @Test
    void testEqualsDifferentIconAndFirstNull() {
        WidgetType widgetType1 = new WidgetType();
        WidgetType widgetType2 = new WidgetType();
        widgetType2.setIcon("icon2");
        Assertions.assertNotEquals(widgetType1, widgetType2);
    }

    private WidgetType getParentType(String code) {
        WidgetType parentType = new WidgetType();
        parentType.setCode(code);
        return parentType;
    }

    private ApsProperties getTitles(String titleEn, String titleIt) {
        ApsProperties titles = new ApsProperties();
        titles.setProperty("en", titleEn);
        titles.setProperty("it", titleIt);
        return titles;
    }
}

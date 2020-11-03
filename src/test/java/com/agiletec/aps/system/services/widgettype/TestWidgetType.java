package com.agiletec.aps.system.services.widgettype;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.util.ApsProperties;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.ent.exception.EntException;
import org.junit.Test;

import javax.sql.DataSource;

public class TestWidgetType extends BaseTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testEqualsAndHashcode() throws EntException {
        String widgetTypeCode = "test_showletType";
        WidgetType widgetType1 = createWidgetType(widgetTypeCode);
        WidgetType widgetType2 = createWidgetType(widgetTypeCode);
        assertEquals(widgetType1, widgetType2);
        assertEquals(widgetType1.hashCode(),widgetType2.hashCode());
    }


    private WidgetType createWidgetType(String code) {
        WidgetType type = new WidgetType();
        type.setCode(code);
        ApsProperties titles = new ApsProperties();
        titles.put("it", "Titolo in ITA");
        titles.put("en", "Title in ENG");
        type.setTitles(titles);
        ApsProperties config = new ApsProperties();
        type.setConfig(config);
        type.setWidgetCategory("test");
        type.setReadonlyPageWidgetConfig(false);
        return type;
    }
}

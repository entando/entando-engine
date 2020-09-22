package org.entando.entando.aps.system.services.label;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;

import com.agiletec.aps.util.ApsProperties;
import org.entando.entando.aps.system.services.label.model.LabelDto;

public class LabelTestHelper {

    public static final String LABEL_KEY = "lab";
    public static final String EN_KEY = "EN";
    public static final String SOME_VALUE = "some_value";

    public static ApsProperties stubTestApsProperties() {
        ApsProperties apsProperties = new ApsProperties();
        apsProperties.put(LABEL_KEY, singletonMap(EN_KEY, SOME_VALUE));
        return apsProperties;
    }

    public static LabelDto stubTestLabelDto() {
        return new LabelDto(LABEL_KEY, singletonMap(EN_KEY, SOME_VALUE));
    }


    public static void assertLabelsDtoEquals(LabelDto expected, LabelDto actual) {
        assertEquals(expected.getKey(), actual.getKey());
        expected.getTitles().keySet()
                .forEach(key -> assertEquals(expected.getTitles().get(key), actual.getTitles().get(key)));
    }

}

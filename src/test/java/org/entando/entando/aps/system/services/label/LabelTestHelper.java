package org.entando.entando.aps.system.services.label;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.agiletec.aps.util.ApsProperties;
import java.util.HashMap;
import java.util.Map;
import org.entando.entando.aps.system.services.label.model.LabelDto;
import org.entando.entando.web.label.LabelRequest;
import org.springframework.test.web.servlet.ResultActions;

public class LabelTestHelper {

    public static final String LABEL_KEY = "thick";
    public static final String KEY = "it";
    public static final String SOME_VALUE = "some_value";

    public static ApsProperties stubTestApsProperties() {
        ApsProperties apsProperties = new ApsProperties();
        apsProperties.put(LABEL_KEY, stubTitles());
        return apsProperties;
    }

    public static LabelDto stubTestLabelDto() {
        return new LabelDto(LABEL_KEY, stubTitles());
    }

    public static LabelRequest stubTestLabelRequest() {
        LabelRequest labelRequest = new LabelRequest();
        labelRequest.setKey(LABEL_KEY);
        labelRequest.setTitles(stubTitles());
        return labelRequest;
    }

    private static Map<String, String> stubTitles() {
        Map<String, String> titles = new HashMap<>();
        titles.put(KEY, SOME_VALUE);
        return titles;
    }


    public static void assertLabelsDtoEquals(LabelDto expected, LabelDto actual) {
        assertEquals(expected.getKey(), actual.getKey());
        expected.getTitles().keySet()
                .forEach(key -> assertEquals(expected.getTitles().get(key), actual.getTitles().get(key)));
    }



    public static void assertLabels(LabelDto expected, ResultActions actual) throws Exception {

        actual.andExpect(jsonPath("$.payload.key", is(expected.getKey())))
                .andExpect(jsonPath("$.payload.titles", is(expected.getTitles())));
    }

}

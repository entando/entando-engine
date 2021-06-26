package org.entando.entando.aps.system.services.category;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.agiletec.aps.system.services.category.Category;
import com.agiletec.aps.util.ApsProperties;
import java.util.Collections;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.entando.entando.aps.system.services.category.model.CategoryDto;
import org.springframework.test.web.servlet.ResultActions;

public class CategoryTestHelper {

    public static final String CATEGORY_CODE = "NCAT";
    public static final String CATEGORY_PARENT_CODE = "PCAT";
    public static final String CATEGORY_CHILD = "child";
    public static final String CATEGORY_TITLE_KEY = "title";
    public static final String CATEGORY_TITLE_VALUE = "titlevalue";
    public static final String CATEGORY_FULLTITLE_KEY = "fulltitle";
    public static final String CATEGORY_FULLTITLE_VALUE = "fulltitlevalue";
    public static final String CATEGORY_REFERENCE_KEY = "ref";
    public static final boolean CATEGORY_REFERENCE_VALUE = true;
    public static final String EN_KEY = "EN";


    public static ApsProperties stubTestApsProperties() {
        ApsProperties apsProperties = new ApsProperties();
        apsProperties.put(EN_KEY, CATEGORY_TITLE_VALUE);
        return apsProperties;
    }

    public static CategoryDto stubTestCategoryDto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCode(CATEGORY_CODE);
        categoryDto.setParentCode(CATEGORY_PARENT_CODE);
        categoryDto.setTitles(Collections.singletonMap(EN_KEY, CATEGORY_TITLE_VALUE));
        return categoryDto;
    }

    public static Category stubTestCategory() {
        Category category = new Category();
        category.setCode(CATEGORY_CODE);
        category.setParentCode(CATEGORY_PARENT_CODE);
        category.setTitles(stubTestApsProperties());
        return category;
    }



    public static void assertCategoryDtoEquals(CategoryDto expected, CategoryDto actual) {
        assertEquals(expected.getCode(), actual.getCode());
        assertEquals(expected.getParentCode(), actual.getParentCode());
        assertEquals(expected.getTitles(), actual.getTitles());
        assertEquals(expected.getFullTitles(), actual.getFullTitles());
        assertEquals(expected.getChildren().stream().sorted().collect(Collectors.toList()), actual.getChildren().stream().sorted().collect(Collectors.toList()));
    }


    public static void assertCategories(CategoryDto expected, ResultActions actual) throws Exception {

        actual.andExpect(jsonPath("$.payload.parentCode", is(expected.getParentCode())))
                .andExpect(jsonPath("$.payload.code", is(expected.getCode())));

        expected.getTitles().forEach((key, value) -> {
            try {
                actual.andExpect(jsonPath(String.format("$.payload.titles.%s", key), is(value)));
            } catch (Exception e) {
                Assertions.fail("Titles don't match");
            }
        });
    }

}

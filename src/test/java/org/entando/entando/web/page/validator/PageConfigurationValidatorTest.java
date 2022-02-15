package org.entando.entando.web.page.validator;

import java.util.HashMap;
import java.util.Map;
import org.assertj.core.util.Maps;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.aps.system.services.widgettype.WidgetTypeManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;

@ExtendWith(MockitoExtension.class)
class PageConfigurationValidatorTest {

    private static final String WIDGET_CODE = "navigation_menu";
    private static final Map<String, Object> WIDGET_CONFIG = Maps.newHashMap("config_key", "config_value");

    @Mock
    private Errors errors;

    @Mock
    private WidgetType widgetType;

    @Mock
    private WidgetTypeManager widgetTypeManager;

    @InjectMocks
    private PageConfigurationValidator validator;

    @Test
    void testValidateWidgetConfigOverridableNullConfig() {
        Mockito.when(widgetTypeManager.getWidgetType(WIDGET_CODE)).thenReturn(widgetType);
        validator.validateWidgetConfigOverridable(WIDGET_CODE, null, errors);
        Mockito.verifyNoInteractions(errors);
    }

    @Test
    void testValidateWidgetConfigOverridableTypeNotFound() {
        validator.validateWidgetConfigOverridable(WIDGET_CODE, null, errors);
        Mockito.verifyNoInteractions(errors);
    }

    @Test
    void testValidateWidgetConfigOverridableTypeNotReadOnly() {
        Mockito.when(widgetTypeManager.getWidgetType(WIDGET_CODE)).thenReturn(widgetType);
        validator.validateWidgetConfigOverridable(WIDGET_CODE, WIDGET_CONFIG, errors);
        Mockito.verifyNoInteractions(errors);
    }

    @Test
    void testValidateWidgetConfigOverridableEmptyConfig() {
        Mockito.when(widgetTypeManager.getWidgetType(WIDGET_CODE)).thenReturn(widgetType);
        validator.validateWidgetConfigOverridable(WIDGET_CODE, new HashMap<>(), errors);
        Mockito.verifyNoInteractions(errors);
    }

    @Test
    void testValidateWidgetConfigOverridableTypeReadOnly() {
        Mockito.when(widgetType.isReadonlyPageWidgetConfig()).thenReturn(true);
        Mockito.when(widgetTypeManager.getWidgetType(WIDGET_CODE)).thenReturn(widgetType);
        validator.validateWidgetConfigOverridable(WIDGET_CODE, WIDGET_CONFIG, errors);
        Mockito.verify(errors).rejectValue(ArgumentMatchers.eq("code"),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}

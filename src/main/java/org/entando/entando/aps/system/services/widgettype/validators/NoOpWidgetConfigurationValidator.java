package org.entando.entando.aps.system.services.widgettype.validators;

import com.agiletec.aps.system.services.page.IPage;
import org.entando.entando.web.page.model.WidgetConfigurationRequest;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.springframework.validation.BeanPropertyBindingResult;

public class NoOpWidgetConfigurationValidator implements WidgetConfigurationValidator {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(getClass());

    @Override
    public boolean supports(String widgetCode) {
        return false;
    }

    @Override
    public BeanPropertyBindingResult validate(WidgetConfigurationRequest widget, IPage page) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(widget, widget.getClass().getSimpleName());
        logger.warn("no WidgetConfigurationValidator implementation found for widget {} ", widget.getCode());
        return bindingResult;
    }

}

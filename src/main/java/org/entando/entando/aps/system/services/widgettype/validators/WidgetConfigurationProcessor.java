package org.entando.entando.aps.system.services.widgettype.validators;

import com.agiletec.aps.util.ApsProperties;
import org.entando.entando.web.page.model.WidgetConfigurationRequest;

/**
 * Interface to be implemented by components that performs widget configuration processing
 *
 * @author spuddu
 */
public interface WidgetConfigurationProcessor {

    /**
     * Returns true is the implementation of this processor fits the provided widget code
     */
    boolean supports(String widgetCode);

    /**
     * Process the widgetConfiguration and returns it as the service layer expects
     */
    Object buildConfiguration(WidgetConfigurationRequest widget);

    /**
     * Process the configuration as provided by the service layer and and transforms it as the web layer expects
     */
    ApsProperties extractConfiguration(ApsProperties widgetProperties);

}

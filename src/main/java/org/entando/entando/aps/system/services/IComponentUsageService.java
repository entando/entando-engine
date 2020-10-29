package org.entando.entando.aps.system.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.component.ComponentAnalysisState;
import org.entando.entando.web.component.ComponentAnalysis;
import org.entando.entando.web.component.ComponentUsageEntity;

/**
 * this interface alloy every service that supply info about the usage and the analysis of its managed component
 */
public interface IComponentUsageService {

    Integer getComponentUsage(String componentCode);

    PagedMetadata<ComponentUsageEntity> getComponentUsageDetails(String componentCode, RestListRequest restListRequest);

    /**
     * for each code, checks if the relative component does exists or not
     *
     * @param codeList the list of the component codes to check
     * @return a ComponentAnalysis containing the requested conflict/no conflict info
     */
    // FIXME
//    ComponentAnalysis getComponentAnalysis(List<String> codeList);
    default ComponentAnalysis getComponentAnalysis(List<String> codeList) {
        // FIXME stub data
        Map<String, ComponentAnalysisState> components = new HashMap<>();
        components.put("code1", ComponentAnalysisState.CONFLICT);
        components.put("code2", ComponentAnalysisState.NO_CONFLICT);
        components.put("STUB_DATA", ComponentAnalysisState.NO_CONFLICT);
        return new ComponentAnalysis(components);
    }
}

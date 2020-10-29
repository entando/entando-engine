package org.entando.entando.aps.system.services.language;

import java.util.List;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.component.ComponentAnalysis;

public interface ILanguageService {

    String BEAN_NAME = "LanguageService";

    public PagedMetadata<LanguageDto> getLanguages(RestListRequest requestList);

    public LanguageDto getLanguage(String code);

    public LanguageDto updateLanguage(String code, boolean active);

    /**
     * for each code, checks if the relative component does exists or not
     *
     * @param codeList the list of the component codes to check
     * @return a ComponentAnalysis containing the requested conflict/no conflict info
     */
    // if we will implement the other methods of the interface IComponentUsageService, remove this method in favor of the one in the interface
    ComponentAnalysis getComponentAnalysis(List<String> codeList);
}


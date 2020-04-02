package org.entando.entando.aps.system.services.language;

import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;

public interface ILanguageService {

    String BEAN_NAME = "LanguageService";

    PagedMetadata<LanguageDto> getLanguages(RestListRequest requestList);

    LanguageDto getLanguage(String code);

    LanguageDto updateLanguage(String code, boolean active);

}


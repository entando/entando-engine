/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.entando.aps.system.services.pagesettings;

import com.agiletec.aps.system.services.page.IPageManager;
import java.util.HashMap;
import java.util.Map;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.aps.system.services.IDtoBuilder;
import org.entando.entando.aps.system.services.pagesettings.model.PageSettingsDto;
import org.entando.entando.web.pagesettings.model.PageSettingsRequest;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author paddeo
 */
public class PageSettingsService implements IPageSettingsService {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(getClass());

    @Autowired
    private IPageManager pageManager;

    @Autowired
    private IDtoBuilder<Map<String, String>, PageSettingsDto> dtoBuilder;

    public IPageManager getPageManager() {
        return pageManager;
    }

    public void setPageManager(IPageManager pageManager) {
        this.pageManager = pageManager;
    }

    public IDtoBuilder<Map<String, String>, PageSettingsDto> getDtoBuilder() {
        return dtoBuilder;
    }

    public void setDtoBuilder(IDtoBuilder<Map<String, String>, PageSettingsDto> dtoBuilder) {
        this.dtoBuilder = dtoBuilder;
    }

    @Override
    public PageSettingsDto getPageSettings() {
        try {
            Map<String, String> systemParams = this.getSystemParams();
            return this.getDtoBuilder().convert(systemParams);
        } catch (Throwable e) {
            logger.error("Error getting page settings", e);
            throw new RestServerError("Error getting page settings", e);
        }
    }

    @Override
    public PageSettingsDto updatePageSettings(PageSettingsRequest request) {
        try {
            Map<String, String> systemParams = new HashMap<>();
            request.keySet().forEach((param) -> {
                systemParams.put(param, request.get(param));
            });
            this.getPageManager().updateParams(systemParams);
            return this.getDtoBuilder().convert(this.getSystemParams());
        } catch (Throwable e) {
            logger.error("Error updating page settings", e);
            throw new RestServerError("Error updating page settings", e);
        }
    }

    private Map<String, String> getSystemParams() throws Throwable {
        return this.getPageManager().getParams();
    }

}

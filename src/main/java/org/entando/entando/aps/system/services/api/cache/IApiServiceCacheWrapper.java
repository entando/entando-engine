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

package org.entando.entando.aps.system.services.api.cache;

import com.agiletec.aps.system.exception.ApsSystemException;
import java.util.Map;
import org.entando.entando.aps.system.services.api.IApiCatalogDAO;
import org.entando.entando.aps.system.services.api.model.ApiResource;
import org.entando.entando.aps.system.services.api.model.ApiService;

public interface IApiServiceCacheWrapper {

    String APICATALOG_SERVICE_CACHE_NAME_PREFIX = "ApiCatalogManager_service_";

    String APICATALOG_SERVICES_CACHE_NAME = "ApiCatalogManager_services";

    void initCache(Map<String, ApiResource> resources, IApiCatalogDAO apiCatalogDAO) throws ApsSystemException;

    Map<String, ApiService> getMasterServices();

    void addService(ApiService apiService);

    void updateService(ApiService apiService);

    void removeService(String key);

}

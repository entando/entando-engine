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

package org.entando.entando.aps.system.services.api;

import java.util.List;
import java.util.Map;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.ApiResource;
import org.entando.entando.aps.system.services.api.model.ApiService;

/**
 * Interfrace for resource and service Api Objects
 *
 * @author E.Santoboni
 */
public interface IApiCatalogDAO {

    void loadApiStatus(Map<String, ApiResource> resources);

    void resetApiStatus(String resourceCode, ApiMethod.HttpMethod httpMethod);

    void saveApiStatus(ApiMethod method);

    @Deprecated
    Map<String, ApiService> loadServices(Map<String, ApiMethod> methods);

    Map<String, ApiService> loadServices(List<ApiMethod> methods);

    void addService(ApiService service);

    void updateService(ApiService service);

    void deleteService(String key);

}
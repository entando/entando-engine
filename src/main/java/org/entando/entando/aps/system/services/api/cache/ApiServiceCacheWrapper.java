/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
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

import java.util.List;
import java.util.Map;

import org.entando.entando.aps.system.services.api.IApiCatalogDAO;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.ApiResource;
import org.entando.entando.aps.system.services.api.model.ApiService;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.springframework.cache.Cache;

import com.agiletec.aps.system.common.AbstractGenericCacheWrapper;
import org.entando.entando.ent.exception.EntException;
import java.util.ArrayList;
import org.entando.entando.aps.system.services.api.IApiCatalogManager;

public class ApiServiceCacheWrapper extends AbstractGenericCacheWrapper<ApiService> implements IApiServiceCacheWrapper {

	private final EntLogger logger = EntLogFactory.getSanitizedLogger(getClass());

	@Override
	protected String getCacheName() {
		return IApiCatalogManager.API_CATALOG_CACHE_NAME;
	}

	@Override
	public void initCache(Map<String, ApiResource> resources, IApiCatalogDAO apiCatalogDAO) throws EntException {
		try {
			Cache cache = this.getCache();
			List<ApiMethod> apiGETMethods = buildApiGetMethods(resources);
			Map<String, ApiService> services = apiCatalogDAO.loadServices(apiGETMethods);
			this.insertAndCleanCache(cache, services);
		} catch (Throwable t) {
			logger.error("Error bootstrapping ApiCatalog cache", t);
			throw new EntException("Error bootstrapping ApiCatalog cache", t);
		}
	}

	protected List<ApiMethod> buildApiGetMethods(Map<String, ApiResource> resources) {
		List<ApiMethod> apiGETMethods = new ArrayList<>();
		List<ApiResource> resourceList = new ArrayList<>(resources.values());
		for (int i = 0; i < resourceList.size(); i++) {
			ApiResource apiResource = resourceList.get(i);
			if (null != apiResource.getGetMethod()) {
				apiGETMethods.add(apiResource.getGetMethod());
			}
		}
		return apiGETMethods;
	}

	@Override
	public Map<String, ApiService> getMasterServices() {
		return super.getObjectMap();
	}

	@Override
	public void addService(ApiService apiService) {
		this.manage(apiService.getKey(), apiService, Action.ADD);
	}

	@Override
	public void updateService(ApiService apiService) {
		this.manage(apiService.getKey(), apiService, Action.UPDATE);
	}

	@Override
	public void removeService(String key) {
		ApiService apiService = this.get(this.getCacheKeyPrefix() + key, ApiService.class);
		this.manage(key, apiService, Action.DELETE);
	}

	@Override
	protected String getCodesCacheKey() {
		return APICATALOG_SERVICES_CACHE_NAME;
	}

	@Override
	protected String getCacheKeyPrefix() {
		return APICATALOG_SERVICE_CACHE_NAME_PREFIX;
	}

}

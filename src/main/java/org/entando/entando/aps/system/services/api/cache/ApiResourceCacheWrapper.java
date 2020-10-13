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

import java.util.Map;

import org.entando.entando.aps.system.services.api.IApiCatalogDAO;
import org.entando.entando.aps.system.services.api.model.ApiResource;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.springframework.cache.Cache;

import com.agiletec.aps.system.common.AbstractGenericCacheWrapper;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.aps.system.services.api.IApiCatalogManager;

public class ApiResourceCacheWrapper extends AbstractGenericCacheWrapper<ApiResource> implements IApiResourceCacheWrapper {

	private final EntLogger logger = EntLogFactory.getSanitizedLogger(getClass());

	@Override
	protected String getCacheName() {
		return IApiCatalogManager.API_CATALOG_CACHE_NAME;
	}

	@Override
	public void initCache(Map<String, ApiResource> resources, IApiCatalogDAO apiCatalogDAO) throws EntException {
		try {
			Cache cache = this.getCache();
			apiCatalogDAO.loadApiStatus(resources);
			this.insertAndCleanCache(cache, resources);
		} catch (Throwable t) {
			logger.error("Error bootstrapping ApiCatalog cache", t);
			throw new EntException("Error bootstrapping ApiCatalog cache", t);
		}
	}

	@Override
	public ApiResource getMasterResource(String code) {
		return this.get(this.getCacheKeyPrefix() + code, ApiResource.class);
	}

	@Override
	public Map<String, ApiResource> getMasterResources() {
		return super.getObjectMap();
	}

	@Override
	public void updateResource(ApiResource resource) {
		super.manage(resource.getCode(), resource, Action.UPDATE);
	}

	@Override
	protected String getCodesCacheKey() {
		return APICATALOG_RESOURCES_CACHE_NAME;
	}

	@Override
	protected String getCacheKeyPrefix() {
		return APICATALOG_RESOURCE_CACHE_NAME_PREFIX;
	}

}

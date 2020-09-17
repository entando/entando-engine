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

import org.entando.entando.ent.exception.EntException;

/**
 * @author E.Santoboni
 */
public interface IApiCatalogManager {

	public static final String API_CATALOG_CACHE_NAME = "Entando_ApiCatalogManager";

	/**
	 * Return the api related whith the given widget, if exist.
	 *
	 * @param widgetCode The widget code.
	 * @return The api method related.
	 * @throws EntException In case of error.
	 */
	public ApiMethod getRelatedMethod(String widgetCode) throws EntException;

	@Deprecated
	public Map<String, ApiMethod> getRelatedShowletMethods() throws EntException;

	public Map<String, ApiMethod> getRelatedWidgetMethods() throws EntException;

	/**
	 * Return the map of GET methods indexed by api method name.
	 *
	 * @return The map of GET methods indexed by api method name.
	 * @throws EntException In case of error
	 * @deprecated use getMethods(ApiMethod.HttpMethod) method
	 */
	public Map<String, ApiMethod> getMethods() throws EntException;

	public List<ApiMethod> getMethods(ApiMethod.HttpMethod httpMethod) throws EntException;

	public Map<String, ApiResource> getResources() throws EntException;

	/**
	 * Return an API resource
	 *
	 * @param namespace The namespace.
	 * @param resourceName The resource name.
	 * @return The resource.
	 * @throws EntException In case of exception.
	 */
	public ApiResource getResource(String namespace, String resourceName) throws EntException;

	/**
	 * Return a GET methods by name.
	 *
	 * @param resourceName the resource name
	 * @return a GET methods.
	 * @throws EntException In case of error
	 * @deprecated use getMethod(ApiMethod.HttpMethod, resourceName) method
	 */
	public ApiMethod getMethod(String resourceName) throws EntException;

	public ApiMethod getMethod(ApiMethod.HttpMethod httpMethod, String resourceName) throws EntException;

	public ApiMethod getMethod(ApiMethod.HttpMethod httpMethod, String namespace, String resourceName) throws EntException;

	public Map<String, ApiService> getServices() throws EntException;

	public Map<String, ApiService> getServices(String tag/*, Boolean myentando*/) throws EntException;

	public ApiService getApiService(String key) throws EntException;

	public void updateMethodConfig(ApiMethod apiMethod) throws EntException;

	public void resetMethodConfig(ApiMethod apiMethod) throws EntException;

	public void saveService(ApiService service) throws EntException;

	public void deleteService(String key) throws EntException;

	public void updateService(ApiService service) throws EntException;

}

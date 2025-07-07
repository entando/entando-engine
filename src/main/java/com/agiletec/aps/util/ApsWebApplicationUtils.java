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
package com.agiletec.aps.util;

import com.agiletec.aps.system.ApsSystemUtils.ApsDeepDebug;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.common.RefreshableBean;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.springframework.core.io.Resource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Classe di utilità.
 * @author E.Santoboni
 */
public class ApsWebApplicationUtils {

	private static final AtomicBoolean isReloadInProgress = new AtomicBoolean(false);
	private static final AtomicInteger reloadProgress = new AtomicInteger();

    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(ApsWebApplicationUtils.class);
	
	/**
	 * Resolve the given location pattern into Resource objects. 
	 * @param locationPattern The location pattern to resolve.
	 * @param svCtx The Servlet Context
	 * @return The corresponding Resource objects.
	 * @throws IOException In case of exception
	 */
	public static Resource[] getResources(String locationPattern, ServletContext svCtx) throws IOException {
		WebApplicationContext wac = getWebApplicationContext(svCtx);
		return wac.getResources(locationPattern);
	}
	
	/**
	 * Resolve the given location pattern into Resource objects. 
	 * @param locationPattern The location pattern to resolve.
	 * @param pageContext The Page Context
	 * @return The corresponding Resource objects.
	 * @throws IOException In case of exception.
	 */
	public static Resource[] getResources(String locationPattern, PageContext pageContext) throws IOException {
		return getResources(locationPattern, pageContext.getServletContext());
	}
	
	/**
	 * Restituisce un servizio di sistema.
	 * @param serviceName Il nome del servizio richiesto.
	 * @param request La request.
	 * @return Il servizio richiesto.
	 * @deprecated use getBean
	 */
	public static AbstractService getService(String serviceName, HttpServletRequest request) {
		WebApplicationContext wac = getWebApplicationContext(request);
		return getService(serviceName, wac);
	}
	
	/**
	 * Restituisce un servizio di sistema.
	 * Il seguente metodo è in uso ai tag jsp del sistema.
	 * @param serviceName Il nome del servizio richiesto.
	 * @param pageContext Il Contesto di pagina,
	 * @return Il servizio richiesto.
	 * @deprecated use getBean
	 */
	public static AbstractService getService(String serviceName, PageContext pageContext) {
		WebApplicationContext wac = getWebApplicationContext(pageContext.getServletContext());
		return getService(serviceName, wac);
	}
	
	/**
	 * Restituisce un bean di sistema.
	 * Il seguente metodo è in uso ai tag jsp del sistema.
	 * @param beanName Il nome del servizio richiesto.
	 * @param request La request.
	 * @return Il servizio richiesto.
	 */
	public static Object getBean(String beanName, HttpServletRequest request) {
		WebApplicationContext wac = getWebApplicationContext(request);
		return wac.getBean(beanName);
	}
	
	/**
	 * Restituisce un bean di sistema.
	 * Il seguente metodo è in uso ai tag jsp del sistema.
	 * @param beanName Il nome del servizio richiesto.
	 * @param pageContext Il Contesto di pagina,
	 * @return Il servizio richiesto.
	 */
	public static Object getBean(String beanName, PageContext pageContext) {
		WebApplicationContext wac = getWebApplicationContext(pageContext.getServletContext());
		return wac.getBean(beanName);
	}
	
	/**
	 * Restituisce il WebApplicationContext del sistema.
	 * @param request La request.
	 * @return Il WebApplicationContext del sistema.
	 */
	public static WebApplicationContext getWebApplicationContext(HttpServletRequest request) {
		ServletContext svCtx = request.getSession().getServletContext();
		WebApplicationContext wac = getWebApplicationContext(svCtx);
		return wac;
	}
	
	private static WebApplicationContext getWebApplicationContext(ServletContext svCtx) {
		return WebApplicationContextUtils.getWebApplicationContext(svCtx);
	}
	
	private static AbstractService getService(String serviceName, WebApplicationContext wac) {
		return (AbstractService) wac.getBean(serviceName);
	}
	
	/**
	 * Esegue il refresh del sistema.
	 * @param request La request.
	 * @throws Throwable In caso di errori in fase di aggiornamento del sistema.
	 */
	public static void executeSystemRefresh(HttpServletRequest request) throws Throwable {
		WebApplicationContext wac = getWebApplicationContext(request);
		executeSystemRefresh(wac);
	}
	
	public static void executeSystemRefresh(ServletContext svCtx) throws Throwable {
		WebApplicationContext wac = getWebApplicationContext(svCtx);
		executeSystemRefresh(wac);
	}

	private static void executeSystemRefresh(WebApplicationContext wac) throws Throwable {
		if (!isReloadInProgress.compareAndSet(false, true)) {
			ApsDeepDebug.print("service-reload","!!! " + Thread.currentThread().getName() + " tried to reload system services but another reload is in progress, aborting!!!");
			logger.info("rejecting the reload of the configuration while still executing the previous one!");
			return;
		}
		final long startTime = System.currentTimeMillis();

		reloadProgress.set(0);
		try {
			final RefreshableBean configManager = (RefreshableBean) wac.getBean(SystemConstants.BASE_CONFIG_MANAGER);
			final String[] beansNames = wac.getBeanNamesForType(RefreshableBean.class);
			final int beansCount = beansNames.length;

			reloadRefreshableBean(configManager, SystemConstants.BASE_CONFIG_MANAGER,
                    (int) ( 100.0 / beansCount));
			for (int i = 0; i < beansCount; i++) {
				Object bean = null;

				try {
					if (beansNames[i].equals(SystemConstants.BASE_CONFIG_MANAGER)) {
						continue;
					}
					bean = wac.getBean(beansNames[i]);
					final int progress = (int)(((i + 1)  * 100.0) / beansCount);
					reloadProgress.set(progress);
					reloadRefreshableBean(bean, beansNames[i], progress);
				} catch (Exception t) {
					ApsDeepDebug.print("service-reload", "RELOADING " + beansNames[i] + " COMPLETED WITH ERRORS");
					logger.error("error in executeSystemRefresh", t);
				}
			}
		} finally {
			isReloadInProgress.set(false);
			reloadProgress.set(0);
			long endTime = System.currentTimeMillis();
			ApsDeepDebug.print("service-reload", "Tempo di esecuzione: " + (endTime - startTime) + " ms");
			logger.info("reload configuration completed in {} ms", (endTime - startTime));
		}
	}

	private static void reloadRefreshableBean(final Object bean, final String name, final int progress) throws Throwable {
		if (bean != null) {
			long currentServiceStart =0;
			long currentServiceEnd = 0;

			ApsDeepDebug.print("service-reload", "RELOADING " + name + " start...");
			currentServiceStart = System.currentTimeMillis();
			((RefreshableBean) bean).refresh();
			currentServiceEnd = System.currentTimeMillis();
			ApsDeepDebug.print("service-reload", "RELOADING " + name + " completed in " + (currentServiceEnd - currentServiceStart) + " ms, " + progress + "% completed");
		} else {
			ApsDeepDebug.print("service-reload", "THE BEAN WITH NAME " + name + " DOES NOT EXIST");
		}
	}

	public static boolean isReloadInProgress() {
		return isReloadInProgress.get();
	}

	public static int getReloadProgress() {
		return reloadProgress.get();
	}
}

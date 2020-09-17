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
package com.agiletec.aps.system.common.renderer;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agiletec.aps.system.common.AbstractService;
import org.entando.entando.ent.exception.EntException;

/**
 * Entities rendering service.
 * @author M.Diana - W.Ambu - E.Santoboni
 */
public class DefaultVelocityRenderer extends AbstractService implements LogChute, IVelocityRenderer {

	private static final Logger _logger = LoggerFactory.getLogger(DefaultVelocityRenderer.class);
	
	@Override
	public void init() throws Exception {
		try {
			Velocity.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this);
			Velocity.init();
		} catch (Throwable t) {
			_logger.error("Error initializing the VelocityEngine", t);
			//ApsSystemUtils.logThrowable(t, this, "init");
			throw new EntException("Error initializing the VelocityEngine", t);
		}
		_logger.debug("{} ready.", this.getName());
	}
	
	@Override
	public String render(Object object, String velocityTemplate) {
		String renderedObject = null;
		try {
			Context velocityContext = new VelocityContext();
			velocityContext.put(this.getWrapperContextName(), object);
			StringWriter stringWriter = new StringWriter();
			boolean isEvaluated = Velocity.evaluate(velocityContext, stringWriter, "render", velocityTemplate);
			if (!isEvaluated) {
				throw new EntException("Rendering error");
			}
			stringWriter.flush();
			renderedObject = stringWriter.toString();
		} catch (Throwable t) {
			_logger.error("Rendering error, class: {} - template: {}", object.getClass().getSimpleName(), velocityTemplate, t);
			//ApsSystemUtils.logThrowable(t, this, "render", "Rendering error");
			renderedObject = "";
		}
		return renderedObject;
	}
	
	@Override
	public void init(RuntimeServices rs) {
		//non fa nulla
	}
	
	@Override
	public boolean isLevelEnabled(int level) {
		return true;
	}
	
	@Override
	public void log(int level, String message) {
		this.log(level, message, null);
	}
	
	@Override
	public void log(int level, String message, Throwable t) {
		if (t == null) {
			switch (level) {
				case TRACE_ID:
					_logger.trace(message);
					break;
				case DEBUG_ID:
					_logger.debug(message);
					break;
				case INFO_ID:
					_logger.info(message);
					break;
				case WARN_ID:
					_logger.warn(message, t);
					break;
				case ERROR_ID:
					_logger.error(message, t);
					break;
				default:
					_logger.info(message);
					break;
			}
		} else {
			_logger.error(message, t);
		}
	}
	
	protected String getWrapperContextName() {
		if (null == this._wrapperContextName) {
			return DEFAULT_WRAPPER_CTX_NAME;
		}
		return _wrapperContextName;
	}
	
	public void setWrapperContextName(String wrapperContextName) {
		this._wrapperContextName = wrapperContextName;
	}
	
	private String _wrapperContextName;
	
	protected static final String DEFAULT_WRAPPER_CTX_NAME = "object";
	
}

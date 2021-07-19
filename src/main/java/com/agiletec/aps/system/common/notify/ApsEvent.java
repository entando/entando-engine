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
package com.agiletec.aps.system.common.notify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.springframework.context.ApplicationEvent;

import com.agiletec.aps.system.common.IManager;

/**
 * Base class for the implementation of the events to be notified to the event management
 * service.
 * The class that implements the event must exclusively know the interface through which the
 * observing service (the final destination of the notification) must implement to properly handle
 * the specific event.
 * @author M.Diana - E.Santoboni
 */
public abstract class ApsEvent extends ApplicationEvent {

	/**
	 * The default source event.
	 */
	public static final String LOCAL_EVENT = "localhost";

	private String channel;
	private String message;
	
	/**
	 * Event constructor
	 */
	public ApsEvent() {
		super(LOCAL_EVENT);
	}

	protected ApsEvent(String channel, Map<String, String> properties) {
		this();
		this.setChannel(channel);
		this.setMessage(properties);
	}

	public static Map<String, String> getProperties(String message) throws EntException {
		if (StringUtils.isBlank(message)) {
			return null;
		}
		try {
			JSONObject obj = new JSONObject(message);
			JSONObject includes = obj.getJSONObject("event");
			return new ObjectMapper().readValue(includes.toString(), HashMap.class);
		} catch (JsonProcessingException | JSONException e) {
			throw new EntException("Error parsing message", e);
		}
	}

	/**
	 * Notify the event to the observer service. This method must be invoked 
	 * inside the update() method of the observer service. 
	 * @param srv The listening service
	 */
	public abstract void notify(IManager srv);
	
	/**
	 * Return the object class of the interface that the observer service  have to implement
	 * to handle to event.
	 * @return The interface class which the observer must implement 
	 */
	public abstract Class getObserverInterface();

	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}

    public String getMessage() {
        return message;
    }
    public void setMessage(Map<String, String> properties) {
        try {
            JSONObject request = new JSONObject();
            if (null != properties) {
                request.put("event", properties);
            }
            this.message = request.toString();
        } catch (JSONException e) {
            throw new EntRuntimeException("Error creating message", e);
        }
    }
    public void setMessage(String message) {
        this.message = message;
    }
	
}
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
package org.entando.entando.aps.system.services.actionlog;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.entando.entando.aps.system.services.actionlog.model.ActivityStreamInfo;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

import org.entando.entando.ent.exception.EntException;

import java.nio.charset.StandardCharsets;

/**
 * @author E.Santoboni
 */
public class ActivityStreamInfoDOM {

	private static final EntLogger _logger = EntLogFactory.getSanitizedLogger(ActivityStreamInfoDOM.class);
	
	public static String marshalInfo(ActivityStreamInfo activityStreamInfo) throws EntException {
		StringWriter writer = new StringWriter();
		try {
			JAXBContext context = JAXBContext.newInstance(ActivityStreamInfo.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(activityStreamInfo, writer);
		} catch (Throwable t) {
			_logger.error("Error binding object", t);
			//ApsSystemUtils.logThrowable(t, ActivityStreamInfoDOM.class, "bindInfo", "Error binding object");
			throw new EntException("Error binding object", t);
		}
		return writer.toString();
	}
	
	public static ActivityStreamInfo unmarshalInfo(String xml) throws EntException {
		ActivityStreamInfo bodyObject = null;
		try {
			JAXBContext context = JAXBContext.newInstance(ActivityStreamInfo.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
			bodyObject = (ActivityStreamInfo) unmarshaller.unmarshal(is);
		} catch (Throwable t) {
			_logger.error("Error unmarshalling activity stream info config. xml: {}", xml, t);
			//ApsSystemUtils.logThrowable(t, UnmarshalUtils.class, "unmarshalInfo");
			throw new EntException("Error unmarshalling activity stream info config", t);
		}
		return bodyObject;
	}
	
}
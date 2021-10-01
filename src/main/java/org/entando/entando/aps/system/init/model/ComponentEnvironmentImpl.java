/*
 * Copyright 2019-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.init.model;

import org.entando.entando.ent.exception.EntException;
import java.io.Serializable;
import java.util.Map;
import org.jdom.Element;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

public class ComponentEnvironmentImpl extends AbstractComponentModule implements ComponentEnvironment, Serializable {

    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(ComponentEnvironment.class);

    private String code;

    public ComponentEnvironmentImpl(Element environmentElement, Map<String, String> postProcessClasses) throws Throwable {
        try {
            code = environmentElement.getAttributeValue("code");
            Element postProcessesElement = environmentElement.getChild("postProcesses");
            super.createPostProcesses(postProcessesElement, postProcessClasses);
        } catch (Throwable t) {
            logger.error("Error creating ComponentEnvironment", t);
            throw new EntException("Error creating ComponentEnvironment", t);
        }
    }

    @Override
    public String getCode() {
        return code;
    }

    protected void setCode(String code) {
        this.code = code;
    }
    
}

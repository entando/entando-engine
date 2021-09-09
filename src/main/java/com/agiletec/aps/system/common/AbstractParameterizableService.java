/*
 * Copyright 2021-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package com.agiletec.aps.system.common;

import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.entando.entando.ent.exception.EntException;

public abstract class AbstractParameterizableService extends AbstractService implements IParameterizableManager {

    private transient ConfigInterface configManager;

    @Override
    public String getConfig(String param) {
        return this.getConfigManager().getParam(param);
    }

    @Override
    public Map<String, String> getParams() {
        return this.getParameterNames().stream()
                .filter(p -> null != this.getConfig(p))
                .collect(Collectors.toMap(p -> p, this::getConfig));
    }

    @Override
    public synchronized void updateParams(Map<String, String> params) throws EntException {
        if (null == params) {
            return;
        }
        Map<String, String> paramsToUpdate = params.entrySet().stream()
                .filter(e -> this.getParameterNames().contains(e.getKey()))
                .collect(Collectors.toMap(Entry<String, String>::getKey, Entry<String, String>::getValue));
        this.getConfigManager().updateParams(paramsToUpdate, true);
    }

    protected abstract List<String> getParameterNames();

    protected ConfigInterface getConfigManager() {
        return configManager;
    }
    public void setConfigManager(ConfigInterface configManager) {
        this.configManager = configManager;
    }

}

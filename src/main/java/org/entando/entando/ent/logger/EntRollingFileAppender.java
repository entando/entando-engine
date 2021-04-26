/*
 * Copyright 2021-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.ent.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.filter.Filter;
import java.io.IOException;
import java.util.List;

/**
 * @author E.Santoboni
 */
public class EntRollingFileAppender<E> extends ch.qos.logback.core.rolling.RollingFileAppender<E>{

    @Override
    public void openFile(String fileName) throws IOException {
        List<Filter<E>> filters = super.getCopyOfAttachedFiltersList();
        for (int i = 0; i < filters.size(); i++) {
            Filter<E> filter = filters.get(i);
            if (filter instanceof EntThresholdFilter) {
                String level = ((EntThresholdFilter) filter).getLevel();
                if (level.equalsIgnoreCase(Level.OFF.toString())) {
                    this.addInfo("Log Level OFF - disabled file creation");
                    return;
                }
            }
        }
        super.openFile(fileName);
    }
    
}

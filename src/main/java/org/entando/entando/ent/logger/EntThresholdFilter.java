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

/**
 * @author E.Santoboni
 */
public class EntThresholdFilter extends ch.qos.logback.classic.filter.ThresholdFilter {
    
    private String levelString;

    @Override
    public void setLevel(String level) {
        this.levelString = level;
        super.setLevel(level);
    }
    
    public String getLevel() {
        return this.levelString;
    }
    
}

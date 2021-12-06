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
package com.agiletec.aps.system.services.page;

import com.agiletec.aps.util.ApsProperties;
import org.entando.entando.aps.system.services.widgettype.WidgetType;

import java.io.Serializable;

/**
 * This class represent an instance of a widget configured in a page frame. 
 * @author M.Diana - E.Santoboni
 */
public class Widget implements Serializable {
    
    @Override
    public Widget clone() {
        Widget clone = new Widget();
        if (null != this.getConfig()) {
            clone.setConfig(this.getConfig().clone());
        }
        clone.setTypeCode(this.getTypeCode());
        return clone;
    }

	/**
	 * Return the configuration of the widget
	 * @return The configuration properties
	 */
	public ApsProperties getConfig() {
		return _config;
	}

	/**
	 * Set the configuration of the widget.
	 * @param config The configuration properties to set
	 */
	public void setConfig(ApsProperties config) {
		this._config = config;
	}
	
    public String getTypeCode() {
        return typeCode;
    }
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_config == null) ? 0 : _config.hashCode());
		result = prime * result + ((typeCode == null) ? 0 : typeCode.hashCode());
		return result;
	}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Widget other = (Widget) obj;
        if (_config == null) {
            if (other._config != null) {
                return false;
            }
        } else if (!_config.equals(other._config)) {
            return false;
        }
        if (typeCode == null) {
            if (other.typeCode != null) {
                return false;
            }
        } else if (!typeCode.equals(other.typeCode)) {
            return false;
        }
        return true;
    }

	/**
	 * The type of the widget
	 */
	private String typeCode;
	
	/**
	 * The configuration properties; the configuration may be null
	 */
	private ApsProperties _config;


}

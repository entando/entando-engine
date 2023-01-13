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
package org.entando.entando.aps.system.services.widgettype;

import java.io.Serializable;
import java.util.Objects;

/**
 * Rappresenta un parametro di configurazione del widget.
 * @author E.Santoboni
 */
public class WidgetTypeParameter implements Serializable {

	public WidgetTypeParameter() {

	}

	public WidgetTypeParameter(String name, String descr) {
		this.name = name;
		this.descr = descr;
	}
	
	@Override
	public WidgetTypeParameter clone() {
		return new WidgetTypeParameter(this.getName(), this.getDescr());
	}
	
	/**
	 * Restituisce la descrizione del parametro.
	 * @return La descrizione del parametro.
	 */
	public String getDescr() {
		return descr;
	}

	/**
	 * Setta la descrizione del parametro.
	 * @param descr La descrizione del parametro.
	 */
	public void setDescr(String descr) {
		this.descr = descr;
	}

	/**
	 * Restituisce il nome del parametro.
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setta il nome del parametro.
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	private String name;
	private String descr;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		WidgetTypeParameter that = (WidgetTypeParameter) o;
		return Objects.equals(name, that.name) && Objects.equals(descr, that.descr);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, descr);
	}
}

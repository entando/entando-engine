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
package org.entando.entando.aps.system.services.userpreferences;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "userPreferences")
@XmlType(propOrder = {"username", "wizard", "loadOnPageSelect", "translationWarning"})
public class UserPreferences implements Serializable {

	@XmlElement(name = "username", required = true)
	public String getUsername() {
		return _username;
	}

	public void setUsername(String username) {
		this._username = username;
	}

	@XmlElement(name = "wizard", required = true)
	public boolean isWizard() {
		return _wizard;
	}

	public void setWizard(boolean wizard) {
		this._wizard = wizard;
	}

	@XmlElement(name = "loadOnPageSelect", required = true)
	public boolean isLoadOnPageSelect() {
		return _loadOnPageSelect;
	}

	public void setLoadOnPageSelect(boolean loadOnPageSelect) {
		this._loadOnPageSelect = loadOnPageSelect;
	}

	@XmlElement(name = "translationWarning", required = true)
	public boolean isTranslationWarning() {
		return _translationWarning;
	}

	public void setTranslationWarning(boolean translationWarning) {
		this._translationWarning = translationWarning;
	}

	private String _username;
	private boolean _wizard;
	private boolean _loadOnPageSelect;
	private boolean _translationWarning;

	@Override
	public String toString() {
		return "UserPreferences{" +
				"username='" + _username + '\'' +
				", wizard=" + _wizard +
				", loadOnPageSelect=" + _loadOnPageSelect +
				", translationWarning=" + _translationWarning +
				'}';
	}
}

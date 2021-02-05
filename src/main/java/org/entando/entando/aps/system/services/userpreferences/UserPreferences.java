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
@XmlType(propOrder = {"username", "wizard", "loadOnPageSelect", "translationWarning", "displayAttributes",
		"defaultOwnerGroup",
		"defaultJoinGroups"})
public class UserPreferences implements Serializable {

	private String username;
	private boolean wizard;
	private boolean loadOnPageSelect;
	private boolean translationWarning;
	private boolean displayAttributes;
	private String defaultOwnerGroup;
	private String defaultJoinGroups;

	@XmlElement(name = "username", required = true)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@XmlElement(name = "wizard", required = true)
	public boolean isWizard() {
		return wizard;
	}

	public void setWizard(boolean wizard) {
		this.wizard = wizard;
	}

	@XmlElement(name = "loadOnPageSelect", required = true)
	public boolean isLoadOnPageSelect() {
		return loadOnPageSelect;
	}

	public void setLoadOnPageSelect(boolean loadOnPageSelect) {
		this.loadOnPageSelect = loadOnPageSelect;
	}

	@XmlElement(name = "translationWarning", required = true)
	public boolean isTranslationWarning() {
		return translationWarning;
	}

	public void setTranslationWarning(boolean translationWarning) {
		this.translationWarning = translationWarning;
	}

	@XmlElement(name = "displayAttributes", required = true)
	public boolean isDisplayAttributes() {
		return displayAttributes;
	}

	public void setDisplayAttributes(boolean displayAttributes) {
		this.displayAttributes = displayAttributes;
	}

	@XmlElement(name = "defaultOwnerGroup")
	public String getDefaultOwnerGroup() {
		return defaultOwnerGroup;
	}

	public void setDefaultOwnerGroup(String defaultOwnerGroup) {
		this.defaultOwnerGroup = defaultOwnerGroup;
	}

	@XmlElement(name = "defaultJoinGroups")
	public String getDefaultJoinGroups() {
		return defaultJoinGroups;
	}

	public void setDefaultJoinGroups(String defaultJoinGroups) {
		this.defaultJoinGroups = defaultJoinGroups;
	}

	@Override
	public String toString() {
		return "UserPreferences{" +
				"username='" + username + '\'' +
				", wizard=" + wizard +
				", loadOnPageSelect=" + loadOnPageSelect +
				", translationWarning=" + translationWarning +
				", displayAttributes=" + displayAttributes +
				", defaultOwnerGroup='" + defaultOwnerGroup + '\'' +
				", defaultJoinGroups='" + defaultJoinGroups + '\'' +
				'}';
	}
}

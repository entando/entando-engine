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
@XmlType(propOrder = {"username", "wizard", "loadOnPageSelect", "translationWarning", "defaultPageOwnerGroup",
		"defaultPageJoinGroups", "defaultContentOwnerGroup", "defaultContentJoinGroups", "defaultWidgetOwnerGroup",
		"defaultWidgetJoinGroups"})
public class UserPreferences implements Serializable {

	private String username;
	private boolean wizard;
	private boolean loadOnPageSelect;
	private boolean translationWarning;
	private String defaultPageOwnerGroup;
	private String defaultPageJoinGroups;
	private String defaultContentOwnerGroup;
	private String defaultContentJoinGroups;
	private String defaultWidgetOwnerGroup;
	private String defaultWidgetJoinGroups;

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

	@XmlElement(name = "defaultPageOwnerGroup")
	public String getDefaultPageOwnerGroup() {
		return defaultPageOwnerGroup;
	}

	public void setDefaultPageOwnerGroup(String defaultPageOwnerGroup) {
		this.defaultPageOwnerGroup = defaultPageOwnerGroup;
	}

	@XmlElement(name = "defaultPageJoinGroups")
	public String getDefaultPageJoinGroups() {
		return defaultPageJoinGroups;
	}

	public void setDefaultPageJoinGroups(String defaultPageJoinGroups) {
		this.defaultPageJoinGroups = defaultPageJoinGroups;
	}

	@XmlElement(name = "defaultContentOwnerGroup")
	public String getDefaultContentOwnerGroup() {
		return defaultContentOwnerGroup;
	}

	public void setDefaultContentOwnerGroup(String defaultContentOwnerGroup) {
		this.defaultContentOwnerGroup = defaultContentOwnerGroup;
	}

	@XmlElement(name = "defaultContentJoinGroups")
	public String getDefaultContentJoinGroups() {
		return defaultContentJoinGroups;
	}

	public void setDefaultContentJoinGroups(String defaultContentJoinGroups) {
		this.defaultContentJoinGroups = defaultContentJoinGroups;
	}

	@XmlElement(name = "defaultWidgetOwnerGroup")
	public String getDefaultWidgetOwnerGroup() {
		return defaultWidgetOwnerGroup;
	}

	public void setDefaultWidgetOwnerGroup(String defaultWidgetOwnerGroup) {
		this.defaultWidgetOwnerGroup = defaultWidgetOwnerGroup;
	}

	@XmlElement(name = "defaultWidgetJoinGroups")
	public String getDefaultWidgetJoinGroups() {
		return defaultWidgetJoinGroups;
	}

	public void setDefaultWidgetJoinGroups(String defaultWidgetJoinGroups) {
		this.defaultWidgetJoinGroups = defaultWidgetJoinGroups;
	}

	@Override
	public String toString() {
		return "UserPreferences{" +
				"username='" + username + '\'' +
				", wizard=" + wizard +
				", loadOnPageSelect=" + loadOnPageSelect +
				", translationWarning=" + translationWarning +
				", defaultPageOwnerGroup='" + defaultPageOwnerGroup + '\'' +
				", defaultPageJoinGroups='" + defaultPageJoinGroups + '\'' +
				", defaultContentOwnerGroup='" + defaultContentOwnerGroup + '\'' +
				", defaultContentJoinGroups='" + defaultContentJoinGroups + '\'' +
				", defaultWidgetOwnerGroup='" + defaultWidgetOwnerGroup + '\'' +
				", defaultWidgetJoinGroups='" + defaultWidgetJoinGroups + '\'' +
				'}';
	}
}

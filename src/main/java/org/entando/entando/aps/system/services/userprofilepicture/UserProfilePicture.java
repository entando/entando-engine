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
package org.entando.entando.aps.system.services.userprofilepicture;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "userProfilePicture")
@XmlType(propOrder = {"username", "versions"})
public class UserProfilePicture implements Serializable {

	private String username;
	private List<UserProfilePictureVersion> versions = new ArrayList<>();

	@XmlElement(name = "username", required = true)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@XmlElement(name = "versions", required = true)
	public List<UserProfilePictureVersion> getVersions() {
		return versions;
	}

	public void setVersions(List<UserProfilePictureVersion> versions) {
		this.versions = versions;
	}

	@Override
	public String toString() {
		return "UserProfilePicture{" +
				"username='" + username + '\'' +
				", versions=" + versions +
				'}';
	}
}

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

import com.agiletec.aps.system.common.AbstractDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.entando.entando.ent.exception.EntException;

public class UserProfilePictureDAO extends AbstractDAO implements IUserProfilePictureDAO {
	
	private static final String LOAD_USER_PROFILE_PICTURE =
			"SELECT upp.username, uppv.username, uppv.dimensions, uppv.path, uppv.size "
					+ "FROM userprofilepicture upp "
					+ "LEFT JOIN userprofilepictureversion uppv ON "
					+ "upp.username = uppv.username WHERE "
					+ "upp.username = ? ";

	private static final String ADD_USER_PROFILE_PICTURE =
			"INSERT INTO userprofilepicture (username) VALUES ( ? )";

	private static final String ADD_USER_PROFILE_PICTURE_VERSION =
			"INSERT INTO userprofilepictureversion (username, dimensions, path, size) VALUES ( ?, ?, ?, ? )";

	private static final String DELETE_USER_PROFILE_PICTURE =
			"DELETE FROM userprofilepicture WHERE username = ? ";

	private static final String DELETE_USER_PROFILE_PICTURE_VERSION =
			"DELETE FROM userprofilepictureversion WHERE username = ? ";

	@Override
	public UserProfilePicture loadUserProfilePicture(String username) throws EntException {
		Connection conn = null;
		UserProfilePicture response = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(LOAD_USER_PROFILE_PICTURE);
			stat.setString(1, username);
			res = stat.executeQuery();
			while (res.next()) {
				if (response == null) {
					response = new UserProfilePicture();
					response.setUsername(res.getString(1));
				}
				UserProfilePictureVersion version = new UserProfilePictureVersion();
				version.setUsername(res.getString(2));
				version.setDimensions(res.getString(3));
				version.setPath(res.getString(4));
				version.setSize(res.getString(5));
				if (version.getUsername() != null) {
					response.getVersions().add(version);
				}
			}
		} catch (SQLException e) {
			throw new EntException("Error loading user preferences for user profile picture " + username, e);
		} finally {
			this.closeDaoResources(res, stat, conn);
		}
		return response;
	}

	@Override
	public void addUserProfilePicture(UserProfilePicture userProfilePicture) throws EntException {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			addUserProfilePicture(conn, userProfilePicture);
			addUserProfilePictureVersion(conn, userProfilePicture);
			conn.commit();
		} catch (SQLException | EntException e) {
			this.executeRollback(conn);
			throw new EntException(
					"Error while inserting user profile picture for user " + userProfilePicture.getUsername(), e);
		} finally {
			closeConnection(conn);
		}
	}

	private void addUserProfilePicture(Connection conn, UserProfilePicture userProfilePicture) throws EntException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(ADD_USER_PROFILE_PICTURE);
			stat.setString(1, userProfilePicture.getUsername());
			stat.executeUpdate();
		} catch (SQLException e) {
			throw new EntException(
					"Error while inserting user profile picture for user " + userProfilePicture.getUsername(), e);
		} finally {
			closeDaoResources(null, stat);
		}
	}

	private void addUserProfilePictureVersion(Connection conn, UserProfilePicture userProfilePicture)
			throws EntException {
		PreparedStatement stat = null;
		try {
			List<UserProfilePictureVersion> versions = userProfilePicture.getVersions();
			stat = conn.prepareStatement(ADD_USER_PROFILE_PICTURE_VERSION);
			for (UserProfilePictureVersion version : versions) {
				stat.setString(1, version.getUsername());
				stat.setString(2, version.getDimensions());
				stat.setString(3, version.getPath());
				stat.setString(4, version.getSize());
				stat.addBatch();
				stat.clearParameters();
			}
			stat.executeBatch();
		} catch (SQLException e) {
			throw new EntException(
					"Error while inserting user profile picture version for user " + userProfilePicture.getUsername(),
					e);
		} finally {
			closeDaoResources(null, stat);
		}
	}

	@Override
	public void updateUserProfilePicture(UserProfilePicture userProfilePicture) throws EntException {
		try {
			deleteUserProfilePicture(userProfilePicture.getUsername());
			addUserProfilePicture(userProfilePicture);
		} catch (EntException e) {
			throw new EntException(
					"Error detected while updating user profile picture for user " + userProfilePicture.getUsername(),
					e);
		}
	}

	@Override
	public void deleteUserProfilePicture(String username) throws EntException {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			deleteUserProfilePicture(conn, username);
			deleteUserProfilePictureVersion(conn, username);
			conn.commit();
		} catch (SQLException e) {
			this.executeRollback(conn);
			throw new EntException("Error detected while deleting user profile picture for user " + username, e);
		} finally {
			closeConnection(conn);
		}
	}

	private void deleteUserProfilePicture(Connection conn, String username) throws EntException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(DELETE_USER_PROFILE_PICTURE);
			stat.setString(1, username);
			stat.executeUpdate();
		} catch (SQLException e) {
			throw new EntException(
					"Error while deleting user profile picture for user " + username, e);
		} finally {
			closeDaoResources(null, stat);
		}
	}

	private void deleteUserProfilePictureVersion(Connection conn, String username) throws EntException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(DELETE_USER_PROFILE_PICTURE_VERSION);
			stat.setString(1, username);
			stat.executeUpdate();
		} catch (SQLException e) {
			throw new EntException(
					"Error while deleting user profile picture version for user " + username, e);
		} finally {
			closeDaoResources(null, stat);
		}
	}
}

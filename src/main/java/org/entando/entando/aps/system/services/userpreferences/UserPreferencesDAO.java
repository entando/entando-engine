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

import com.agiletec.aps.system.common.AbstractDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;

public class UserPreferencesDAO extends AbstractDAO implements IUserPreferencesDAO {
	
	private static final EntLogger _logger =  EntLogFactory.getSanitizedLogger(UserPreferencesDAO.class);

	private static final String LOAD_USER_PREFERENCES =
			"SELECT wizard, loadonpageselect, translationwarning FROM userpreferences WHERE username = ? ";

	private static final String ADD_USER_PREFERENCES =
			"INSERT INTO userpreferences (username, wizard, loadonpageselect, translationwarning) VALUES ( ? , ? , ? , ? )";

	private static final String UPDATE_USER_PREFERENCES =
			"UPDATE userpreferences SET wizard = ? , loadonpageselect = ? , translationwarning = ? WHERE username = ? ";

	private static final String DELETE_USER_PREFERENCES =
			"DELETE FROM userpreferences WHERE username = ? ";

	@Override
	public UserPreferences loadUserPreferences(String username) throws EntException {
		Connection conn = null;
		UserPreferences response = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(LOAD_USER_PREFERENCES);
			stat.setString(1, username);
			res = stat.executeQuery();
			if (res.next()) {
				response = new UserPreferences();
				response.setUsername(username);
				response.setWizard(res.getBoolean(1));
				response.setLoadOnPageSelect(res.getBoolean(2));
				response.setTranslationWarning(res.getBoolean(3));
			}
		} catch (SQLException e) {
			_logger.error("Error loading user preferences for user {}", username,  e);
			throw new EntException("Error loading user preferences for user " + username, e);
		} finally {
			this.closeDaoResources(res, stat, conn);
		}
		return response;
	}

	@Override
	public void addUserPreferences(UserPreferences userPreferences) throws EntException {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(ADD_USER_PREFERENCES);
			stat.setString(1, userPreferences.getUsername());
			stat.setBoolean(2, userPreferences.isWizard());
			stat.setBoolean(3, userPreferences.isLoadOnPageSelect());
			stat.setBoolean(4, userPreferences.isTranslationWarning());
			stat.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			this.executeRollback(conn);
			_logger.error("Error while inserting user preferences {}", userPreferences,  e);
			throw new EntException("Error while inserting user preferences", e);
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	@Override
	public void updateUserPreferences(UserPreferences userPreferences) throws EntException {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(UPDATE_USER_PREFERENCES);
			stat.setBoolean(1, userPreferences.isWizard());
			stat.setBoolean(2, userPreferences.isLoadOnPageSelect());
			stat.setBoolean(3, userPreferences.isTranslationWarning());
			stat.setString(4, userPreferences.getUsername());
			stat.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			this.executeRollback(conn);
			_logger.error("Error detected while updating user preferences",  userPreferences, e);
			throw new EntException("Error detected while updating user preferences", e);
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	@Override
	public void deleteUserPreferences(String username) throws EntException {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(DELETE_USER_PREFERENCES);
			stat.setString(1, username);
			stat.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			this.executeRollback(conn);
			_logger.error("Error detected while deleting user preferences for user '{}'", username,  e);
			throw new EntException("Error detected while deleting user preferences", e);
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}
}

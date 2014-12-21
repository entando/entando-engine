/*
 * Copyright 2013-Present Entando Corporation (http://www.entando.com) All rights reserved.
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
package com.agiletec.aps.system.services.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to change some date fields in the 'authuser' table, in order to test the behaviour of the privacy module
 * 
 * @author M. Minnai
 */
public class MockUserDAO extends UserDAO {

	private static final Logger _logger =  LoggerFactory.getLogger(MockUserDAO.class);
	
	public MockUserDAO(DataSource datasource) {
		this.setDataSource(datasource);
	}
	
	/**
	 * Change the last access date of the given user
	 * @param username the user which we want to set the access date
	 * @param accessDate the date to set, if null the current date will be used
	 */
	public void setLastAccessDate(String username, Date accessDate) {
		this.mockDate(username, accessDate, this.MOCK_ACCESS_DATE);
	}
	
	/**
	 * Change the date of the last change of password, for the given user
	 * @param username the user which we want to set the access date
	 * @param accessDate the date to set, if null the current date will be used
	 */
	public void setLastPasswordChange(String username, Date accessDate) {
		this.mockDate(username, accessDate, this.MOCK_LAST_PASSWORD_CHANGE);
	}
	
	/**
	 * Executes the given SQL so to mock some date in the authuser table for a particular user  
	 * @param username the user which we want to set the access date
	 * @param accessDate the date to set, if null the current date will be used
	 * @param the sql provided by the caller method
	 */
	private void mockDate(String username, Date accessDate, String sql) {
		Date date = new Date(); 
		Connection conn = null;
		PreparedStatement stat = null;
		if (null == accessDate) accessDate = date;
		try {
			conn = this.getConnection();
			stat = conn.prepareStatement(sql);
			stat.setDate(1, new java.sql.Date(accessDate.getTime()));
			stat.setString(2, username);
			stat.execute();
		} catch (Throwable t) {
			_logger.error("Error setting date. username: {} - accessDate:{} - sql:{}", username, accessDate, sql, t);
			throw new RuntimeException("Error setting date", t);
			//processDaoException(t, "Errore nel settare data", "mockDate");
		}  finally {
			this.closeDaoResources(null, stat, conn);
		}
	}

	private String MOCK_ACCESS_DATE = 
		"UPDATE authusers SET lastaccess = ? WHERE username = ? ";
	
	private String MOCK_LAST_PASSWORD_CHANGE = 
		"UPDATE authusers SET lastpasswordchange = ? WHERE username = ? ";
	
}

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
package com.agiletec.aps.system.services.role;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import com.agiletec.aps.BaseTestCase;
import org.junit.jupiter.api.Test;

/**
 * @author M.Diana
 */
class TestPermissionDAO extends BaseTestCase {
	
    @Test
    void testAddUpdateDeletePermission() throws Throwable {
    	DataSource dataSource = (DataSource) this.getApplicationContext().getBean("servDataSource");
		PermissionDAO permissionDao = new PermissionDAO();
		permissionDao.setDataSource(dataSource);
		try {
            permissionDao.deletePermission("temp");
        } catch (Throwable t) {
        	throw t;
        }
		Permission permission = new Permission();
		permission.setName("temp");
		permission.setDescription("temp");
        try {
        	permissionDao.addPermission(permission);
        } catch (Throwable t) {
        	throw t;
        }
		Map<String, Permission> permissions = null;
        try {
        	permissions = permissionDao.loadPermissions();
        } catch (Throwable t) {
        	throw t;
        }
        Iterator<Permission> iter = permissions.values().iterator();
        boolean contains = false;
        while (iter.hasNext()) {
			permission = iter.next();
			if (permission.getName().equals("temp")) {
				contains = true;
			}
		}
        assertTrue(contains);
        this.updatePermission(permissionDao);
        this.deletePermission(permissionDao);
	}
	
	private void updatePermission(PermissionDAO permissionDao) throws Throwable {
		Permission permission = new Permission();
		permission.setName("temp");
		permission.setDescription("temp1");
        try {
        	permissionDao.updatePermission(permission);
        } catch (Throwable t) {
        	throw t;
        }
        Map<String, Permission> permissions = null;
        try {
        	permissions = permissionDao.loadPermissions();
        } catch (Throwable t) {
        	throw t;
        }
        Iterator<Permission> iter = permissions.values().iterator();
        boolean contains = false;
        while (iter.hasNext()) {
			permission = iter.next();
			if (permission.getDescription().equals("temp1")) {
				contains = true;
			}
		}
        assertTrue(contains);
	}
		
	private void deletePermission(PermissionDAO permissionDao) throws Throwable {
		Permission permission = new Permission();
		permission.setName("temp");
        try {
        	permissionDao.deletePermission(permission);
        } catch (Throwable t) {
        	throw t;
        }
        Map<String, Permission> permissions = null;
        try {
        	permissions = permissionDao.loadPermissions();
        } catch (Throwable t) {
        	throw t;
        }
        Iterator<Permission> iter = permissions.values().iterator();
        boolean contains = false;
        while (iter.hasNext()) {
			permission = iter.next();
			if (permission.getName().equals("temp")) {
				contains = true;
			}
		}
        assertFalse(contains);    
	}		
    	
}

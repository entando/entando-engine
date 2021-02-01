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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author M.Casari
 */
class TestRoleManager extends BaseTestCase {
	
    private IRoleManager roleManager;
    
    @BeforeEach
    private void init() {
        this.roleManager = (IRoleManager) this.getService(SystemConstants.ROLE_MANAGER);
    }
    
    @Test
    void testAddUpdateDeletePermission() throws Throwable {
		Role role = new Role();
		role.setName("temp");
		try {
			this.roleManager.removeRole(role);
			this.roleManager.removePermission("temp");
		} catch (Throwable t) {
			throw t;
		}
		Permission permission = new Permission();
		permission.setName("temp");
		permission.setDescription("Permesso temporaneo");
		this.roleManager.addPermission(permission);		
		role = new Role();
		role.setName("temp");
		role.setDescription("Ruolo temporaneo");
		role.addPermission("temp");
		this.roleManager.addRole(role);
		
		this.getRolesAndPermissions();
		this.updateRoleAndPermission();
		this.getNewRolesAndPermissions();
		this.deleteRoleAndPermission();
		this.deletedRoleAndPermission();
	}
	
	private void getRolesAndPermissions() throws Throwable {
		Role role = this.roleManager.getRole("temp");
		assertNotNull(role);
		
		assertEquals("Ruolo temporaneo", role.getDescription());
		Iterator<Permission> iter = this.roleManager.getPermissions().iterator();
		boolean contains = false;
		while (iter.hasNext()) {
			Permission permission = (Permission) iter.next();
			if (permission.getName().equals("temp")) {
				contains = true;
			}
		}
		assertTrue(contains);
		Set<String> permissionSet = role.getPermissions();
		contains = permissionSet.contains("temp");
		assertTrue(contains);		
	}
	
	private void updateRoleAndPermission() throws Throwable {
		Role role = new Role();
		role.setName("temp");
		role.setDescription("Ruolo temporaneo 1");
		role.addPermission("temp");
		this.roleManager.updateRole(role);
		Permission permission = new Permission();
		permission.setName("temp");
		permission.setDescription("Permesso temporaneo 1");
		this.roleManager.updatePermission(permission);
	}
	
	private void getNewRolesAndPermissions() throws Throwable {
		Role role = this.roleManager.getRole("temp");
		assertEquals("Ruolo temporaneo 1", role.getDescription());
		Iterator<Permission> iter = this.roleManager.getPermissions().iterator();
		boolean contains = false;
		while (iter.hasNext()) {
			Permission permission = (Permission) iter.next();
			if (permission.getDescription().equals("Permesso temporaneo 1")) {
				contains = true;
			}
		}
		assertTrue(contains);
	}
	
	private void deleteRoleAndPermission() throws Throwable {
		Role role = new Role();
		role.setName("temp");
		role.setDescription("temp description");
		this.roleManager.updateRole(role);
		this.roleManager.removeRole(role);
		this.roleManager.removePermission("temp");
	} 	
	
	private void deletedRoleAndPermission() throws Throwable {
		Role role = this.roleManager.getRole("temp");
		assertNull(role);
		Iterator<Permission> iter = this.roleManager.getPermissions().iterator();
		boolean contains = false;
		while (iter.hasNext()) {
			Permission permission = iter.next();
			if (permission.getName().equals("temp")) {
				contains = true;
			}
		}
		assertFalse(contains);
	}
	
    @Test
	public void testGetRolesWithPemission() throws Throwable {
    	String permission = Permission.SUPERVISOR;
    	List<Role> roles = this.roleManager.getRolesWithPermission(permission);
    	assertEquals(1, roles.size());
    	for (int i=0; i<roles.size(); i++) {
    		Role role = roles.get(i);
    		assertEquals("supervisor", role.getName());
    	}
    }
	
}

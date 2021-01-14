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

import com.agiletec.aps.BaseTestCaseJunit5;
import com.agiletec.aps.system.SystemConstants;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * @author M.Casari
 */
public class TestRoleManager extends BaseTestCaseJunit5 {
	
    @Test
    public void testAddUpdateDeletePermission() throws Throwable {
        IRoleManager roleManager = (IRoleManager) this.getService(SystemConstants.ROLE_MANAGER);
		Role role = new Role();
		role.setName("temp");
		try {
			roleManager.removeRole(role);
			roleManager.removePermission("temp");
		} catch (Throwable t) {
			throw t;
		}
		Permission permission = new Permission();
		permission.setName("temp");
		permission.setDescription("Permesso temporaneo");
		roleManager.addPermission(permission);		
		role = new Role();
		role.setName("temp");
		role.setDescription("Ruolo temporaneo");
		role.addPermission("temp");
		roleManager.addRole(role);
		
		this.getRolesAndPermissions();
		this.updateRoleAndPermission();
		this.getNewRolesAndPermissions();
		this.deleteRoleAndPermission();
		this.deletedRoleAndPermission();
	}
	
	private void getRolesAndPermissions() throws Throwable {
        IRoleManager roleManager = (IRoleManager) this.getService(SystemConstants.ROLE_MANAGER);
		Role role = roleManager.getRole("temp");
		assertNotNull(role);
		
		assertEquals(role.getDescription(), "Ruolo temporaneo");
		Iterator<Permission> iter = roleManager.getPermissions().iterator();
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
        IRoleManager roleManager = (IRoleManager) this.getService(SystemConstants.ROLE_MANAGER);
		Role role = new Role();
		role.setName("temp");
		role.setDescription("Ruolo temporaneo 1");
		role.addPermission("temp");
		roleManager.updateRole(role);
		Permission permission = new Permission();
		permission.setName("temp");
		permission.setDescription("Permesso temporaneo 1");
		roleManager.updatePermission(permission);
	}
	
	private void getNewRolesAndPermissions() throws Throwable {
        IRoleManager roleManager = (IRoleManager) this.getService(SystemConstants.ROLE_MANAGER);
		Role role = roleManager.getRole("temp");
		assertEquals(role.getDescription(), "Ruolo temporaneo 1");
		Iterator<Permission> iter = roleManager.getPermissions().iterator();
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
        IRoleManager roleManager = (IRoleManager) this.getService(SystemConstants.ROLE_MANAGER);
		Role role = new Role();
		role.setName("temp");
		role.setDescription("temp description");
		roleManager.updateRole(role);
		roleManager.removeRole(role);
		roleManager.removePermission("temp");
	} 	
	
	private void deletedRoleAndPermission() throws Throwable {
        IRoleManager roleManager = (IRoleManager) this.getService(SystemConstants.ROLE_MANAGER);
		Role role = roleManager.getRole("temp");
		assertNull(role);
		Iterator<Permission> iter = roleManager.getPermissions().iterator();
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
        IRoleManager roleManager = (IRoleManager) this.getService(SystemConstants.ROLE_MANAGER);
    	String permission = Permission.SUPERVISOR;
    	List<Role> roles = roleManager.getRolesWithPermission(permission);
    	assertEquals(1, roles.size());
    	for (int i=0; i<roles.size(); i++) {
    		Role role = roles.get(i);
    		assertEquals("supervisor", role.getName());
    	}
    }
	
}

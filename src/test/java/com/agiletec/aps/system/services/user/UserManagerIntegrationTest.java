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
package com.agiletec.aps.system.services.user;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;
import org.entando.entando.aps.util.crypto.CompatiblePasswordEncoder;
import org.junit.jupiter.api.Test;

class UserManagerIntegrationTest extends BaseTestCase {

    @Test
    void testGetUsers() throws Throwable {
        IUserManager userManager = (IUserManager) this.getService(SystemConstants.USER_MANAGER);
        List<UserDetails> users = userManager.getUsers();
        assertTrue(users.size() >= 8);
    }

    @Test
    void testAdminUserPasswordIsBCrypt() throws Throwable {
        IUserManager userManager = (IUserManager) this.getService(SystemConstants.USER_MANAGER);
        UserDetails admin = this.getUser("admin");
        assertNotNull(admin);
        assertTrue(CompatiblePasswordEncoder.isBCrypt(admin.getPassword()));
    }

    @Test
    void testAllUsersPasswordsIsArgon2() throws Throwable {
        IUserManager userManager = (IUserManager) this.getService(SystemConstants.USER_MANAGER);
        for (UserDetails user : userManager.getUsers()) {
            assertTrue(CompatiblePasswordEncoder.isBCrypt(user.getPassword()));
        }
    }
    
}

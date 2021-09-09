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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import java.util.HashMap;
import java.util.List;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;
import java.util.Map;
import org.entando.entando.aps.util.crypto.CompatiblePasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserManagerIntegrationTest extends BaseTestCase {

    private IUserManager userManager = null;

    @BeforeEach
    private void init() {
        this.userManager = (IUserManager) this.getApplicationContext().getBean(SystemConstants.USER_MANAGER);
    }

    @Test
    void testGetUsers() throws Throwable {
        List<UserDetails> users = this.userManager.getUsers();
        assertTrue(users.size() >= 8);
    }

    @Test
    void testAdminUserPasswordIsBCrypt() throws Throwable {
        UserDetails admin = this.getUser("admin");
        assertNotNull(admin);
        assertTrue(CompatiblePasswordEncoder.isBCrypt(admin.getPassword()));
    }

    @Test
    void testAllUsersPasswordsIsArgon2() throws Throwable {
        for (UserDetails user : this.userManager.getUsers()) {
            assertTrue(CompatiblePasswordEncoder.isBCrypt(user.getPassword()));
        }
    }
    
    @Test
    void testUpdateParams() throws Throwable {
        ConfigInterface configManager = getApplicationContext().getBean(ConfigInterface.class);
        String value = this.userManager.getConfig(IUserManager.CONFIG_PARAM_PM_MM_LAST_ACCESS);
        assertEquals("6", value);
        assertEquals(value, configManager.getParam(IUserManager.CONFIG_PARAM_PM_MM_LAST_ACCESS));

        Map<String, String> map = new HashMap<>();
        map.put(IUserManager.CONFIG_PARAM_PM_MM_LAST_ACCESS, "7");
        this.userManager.updateParams(map);
        value = this.userManager.getConfig(IUserManager.CONFIG_PARAM_PM_MM_LAST_ACCESS);
        assertEquals("7", value);
        assertEquals(value, configManager.getParam(IUserManager.CONFIG_PARAM_PM_MM_LAST_ACCESS));

        map.put(IUserManager.CONFIG_PARAM_PM_MM_LAST_ACCESS, "6");
        this.userManager.updateParams(map);
        value = this.userManager.getConfig(IUserManager.CONFIG_PARAM_PM_MM_LAST_ACCESS);
        assertEquals("6", value);
        assertEquals(value, configManager.getParam(IUserManager.CONFIG_PARAM_PM_MM_LAST_ACCESS));

        map.put("invalidKey", "value");
        this.userManager.updateParams(map);
        assertNull(this.userManager.getConfig("invalidKey"));
        assertNull(configManager.getParam("invalidKey"));
    }
    
}

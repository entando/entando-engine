/*
 * Copyright 2022-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.services.user.IAuthenticationProviderManager;
import com.agiletec.aps.system.services.user.UserDetails;
import java.util.List;
import org.entando.entando.aps.system.services.group.model.GroupDto;
import org.entando.entando.aps.system.services.user.model.UserAuthorityDto;
import org.entando.entando.web.user.model.UserAuthoritiesRequest;
import org.entando.entando.web.user.model.UserAuthority;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author paddeo
 */
class UserServiceIntegrationTest extends BaseTestCase {

    private IUserService userService;
    private IAuthenticationProviderManager authenticationProviderManager;
    
    @BeforeEach
    private void init() throws Exception {
        try {
            this.userService = (IUserService) this.getApplicationContext().getBean(IUserService.BEAN_NAME);
            this.authenticationProviderManager = this.getApplicationContext().getBean(IAuthenticationProviderManager.class);
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    void testAddAndRemoveUserAuthorities() throws Throwable {
        String username = "editorCustomers";
        List<UserAuthorityDto> master = userService.getUserAuthorities(username);
        assertEquals(1, master.size());
        try {
            UserAuthoritiesRequest request = new UserAuthoritiesRequest();
            UserAuthority auth = new UserAuthority();
            auth.setGroup("management");
            auth.setRole("pageManager");
            request.add(auth);
            List<UserAuthorityDto> resp = userService.addUserAuthorities(username, request);
            assertNotNull(resp);
            assertEquals(1, resp.size());
            assertEquals("management", resp.get(0).getGroup());
            List<UserAuthorityDto> authorities = userService.getUserAuthorities(username);
            assertEquals(2, authorities.size());
            UserDetails user = this.authenticationProviderManager.getUser(username);
            List<GroupDto> myGroups = this.userService.getMyGroups(user);
            Assertions.assertEquals(2, myGroups.size());
        } finally {
            UserAuthoritiesRequest request = new UserAuthoritiesRequest();
            UserAuthority auth = new UserAuthority();
            auth.setGroup(master.get(0).getGroup());
            auth.setRole(master.get(0).getRole());
            request.add(auth);
            List<UserAuthorityDto> resp = userService.updateUserAuthorities(username, request);
            assertNotNull(resp);
            assertEquals(1, resp.size());
            assertEquals("customers", resp.get(0).getGroup());
        }
    }
    
    @Test
    void testAddAndRemoveAuthorityWithoutRole() throws Throwable {
        String username = "pageManagerCustomers";
        List<UserAuthorityDto> master = userService.getUserAuthorities(username);
        assertEquals(1, master.size());
        try {
            UserAuthoritiesRequest request = new UserAuthoritiesRequest();
            UserAuthority auth = new UserAuthority();
            auth.setGroup("helpdesk");
            request.add(auth);
            List<UserAuthorityDto> resp = userService.addUserAuthorities(username, request);
            assertNotNull(resp);
            assertEquals(1, resp.size());
            assertEquals("helpdesk", resp.get(0).getGroup());
            Assertions.assertNull(resp.get(0).getRole());
            List<UserAuthorityDto> authorities = userService.getUserAuthorities(username);
            assertEquals(2, authorities.size());
            UserDetails user = this.authenticationProviderManager.getUser(username);
            List<GroupDto> myGroups = this.userService.getMyGroups(user);
            Assertions.assertEquals(2, myGroups.size());
        } finally {
            UserAuthoritiesRequest request = new UserAuthoritiesRequest();
            UserAuthority auth = new UserAuthority();
            auth.setGroup(master.get(0).getGroup());
            auth.setRole(master.get(0).getRole());
            request.add(auth);
            List<UserAuthorityDto> resp = userService.updateUserAuthorities(username, request);
            assertNotNull(resp);
            assertEquals(1, resp.size());
            assertEquals("customers", resp.get(0).getGroup());
            assertEquals("pageManager", resp.get(0).getRole());
        }
    }
    
    @Test
    void testAddAndRemoveAuthorityWithoutGroup() throws Throwable {
        String username = "supervisorCustomers";
        List<UserAuthorityDto> master = userService.getUserAuthorities(username);
        assertEquals(1, master.size());
        try {
            UserAuthoritiesRequest request = new UserAuthoritiesRequest();
            UserAuthority auth = new UserAuthority();
            auth.setRole("pageManager");
            request.add(auth);
            List<UserAuthorityDto> resp = userService.addUserAuthorities(username, request);
            assertNotNull(resp);
            assertEquals(1, resp.size());
            Assertions.assertEquals("pageManager", resp.get(0).getRole());
            Assertions.assertNull(resp.get(0).getGroup());
            List<UserAuthorityDto> authorities = userService.getUserAuthorities(username);
            assertEquals(2, authorities.size());
            UserDetails user = this.authenticationProviderManager.getUser(username);
            List<GroupDto> myGroups = this.userService.getMyGroups(user);
            Assertions.assertEquals(1, myGroups.size());
        } finally {
            UserAuthoritiesRequest request = new UserAuthoritiesRequest();
            UserAuthority auth = new UserAuthority();
            auth.setGroup(master.get(0).getGroup());
            auth.setRole(master.get(0).getRole());
            request.add(auth);
            List<UserAuthorityDto> resp = userService.updateUserAuthorities(username, request);
            assertNotNull(resp);
            assertEquals(1, resp.size());
            assertEquals("customers", resp.get(0).getGroup());
            assertEquals("supervisor", resp.get(0).getRole());
        }
    }
    
}

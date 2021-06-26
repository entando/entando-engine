/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
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

import com.agiletec.aps.system.SystemConstants;
import org.entando.entando.ent.exception.EntException;
import com.agiletec.aps.system.services.authorization.Authorization;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.role.Role;
import com.agiletec.aps.util.DateConverter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.entando.entando.aps.system.services.oauth2.IApiOAuth2TokenManager;
import org.entando.entando.aps.system.services.oauth2.model.OAuth2AccessTokenImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * @author E.Santoboni
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationProviderManagerTest {

    @Mock
    private IUserManager userManager;

    @Mock
    private IAuthorizationManager authorizationManager;

    @Mock
    private IApiOAuth2TokenManager tokenManager;

    @InjectMocks
    private AuthenticationProviderManager authenticationProviderManager;

    @BeforeAll
    public static void setUp() throws Exception {
        MockitoAnnotations.initMocks(AuthenticationProviderManagerTest.class);
    }

    @Test
    void getUser_1() throws Exception {
        UserDetails activeUser = this.createMockUser("admin", true, false, false);
        when(this.userManager.getUser("admin")).thenReturn(activeUser);
        when(this.authorizationManager.getUserAuthorizations("admin")).thenReturn(this.createMockAuthorization());
        when(this.tokenManager.createAccessTokenForLocalUser("admin")).thenReturn(this.createMockToken());
        UserDetails user = this.authenticationProviderManager.getUser("admin");
        Assertions.assertNotNull(user);
        Assertions.assertEquals("admin", user.getUsername());
        Assertions.assertEquals(2, user.getAuthorizations().size());
        Assertions.assertEquals("access_token_x", user.getAccessToken());
        Mockito.verify(userManager, Mockito.times(1)).updateLastAccess(Mockito.any(UserDetails.class));
        Mockito.verify(authorizationManager, Mockito.times(1)).getUserAuthorizations(Mockito.anyString());
        Mockito.verify(tokenManager, Mockito.times(1)).createAccessTokenForLocalUser(Mockito.anyString());
    }

    @Test
    void getUser_2() throws Exception {
        UserDetails disabledUser = this.createMockUser("admin", false, false, false);
        when(this.userManager.getUser("admin")).thenReturn(disabledUser);
        Mockito.lenient().when(this.authorizationManager.getUserAuthorizations("admin")).thenReturn(this.createMockAuthorization());
        Mockito.lenient().when(this.tokenManager.createAccessTokenForLocalUser("admin")).thenReturn(this.createMockToken());
        UserDetails user = this.authenticationProviderManager.getUser("admin");
        Assertions.assertNull(user);
        Mockito.verify(userManager, Mockito.times(0)).updateLastAccess(Mockito.any(UserDetails.class));
        Mockito.verify(authorizationManager, Mockito.times(0)).getUserAuthorizations(Mockito.anyString());
        Mockito.verify(tokenManager, Mockito.times(0)).createAccessTokenForLocalUser(Mockito.anyString());
    }

    @Test
    void getUser_3() throws Exception {
        UserDetails expiredUser_1 = this.createMockUser("admin", true, false, true);
        when(this.userManager.getUser("admin")).thenReturn(expiredUser_1);
        Mockito.lenient().when(this.authorizationManager.getUserAuthorizations("admin")).thenReturn(this.createMockAuthorization());
        Mockito.lenient().when(this.tokenManager.createAccessTokenForLocalUser("admin")).thenReturn(this.createMockToken());
        UserDetails user = this.authenticationProviderManager.getUser("admin");
        Assertions.assertNotNull(user);
        Assertions.assertEquals("admin", user.getUsername());
        Assertions.assertEquals(0, user.getAuthorizations().size());
        Assertions.assertNull(user.getAccessToken());
        Mockito.verify(userManager, Mockito.times(1)).updateLastAccess(Mockito.any(UserDetails.class));
        Mockito.verify(authorizationManager, Mockito.times(0)).getUserAuthorizations(Mockito.anyString());
        Mockito.verify(tokenManager, Mockito.times(0)).createAccessTokenForLocalUser(Mockito.anyString());
    }

    @Test
    void getUser_4_1() throws Exception {
        this.execute_getUser_4("admin");
    }

    @Test
    void getUser_4_2() throws Exception {
        this.execute_getUser_4("test_user");
    }

    private void execute_getUser_4(String username) throws Exception {
        UserDetails expiredUser_2 = this.createMockUser(username, true, true, false);
        when(this.userManager.getUser(username)).thenReturn(expiredUser_2);
        Mockito.lenient().when(this.authorizationManager.getUserAuthorizations(username)).thenReturn(this.createMockAuthorization());
        Mockito.lenient().when(this.tokenManager.createAccessTokenForLocalUser(username)).thenReturn(this.createMockToken());
        UserDetails user = this.authenticationProviderManager.getUser(username);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(username, user.getUsername());
        if (username.equals(SystemConstants.ADMIN_USER_NAME)) {
            Assertions.assertEquals(2, user.getAuthorizations().size());
            Assertions.assertEquals("access_token_x", user.getAccessToken());
            Mockito.verify(userManager, Mockito.times(1)).updateLastAccess(Mockito.any(UserDetails.class));
            Mockito.verify(authorizationManager, Mockito.times(1)).getUserAuthorizations(Mockito.anyString());
            Mockito.verify(tokenManager, Mockito.times(1)).createAccessTokenForLocalUser(Mockito.anyString());
        } else {
            Assertions.assertEquals(0, user.getAuthorizations().size());
            Assertions.assertNull(user.getAccessToken());
            Mockito.verify(userManager, Mockito.times(0)).updateLastAccess(Mockito.any(UserDetails.class));
            Mockito.verify(authorizationManager, Mockito.times(0)).getUserAuthorizations(Mockito.anyString());
            Mockito.verify(tokenManager, Mockito.times(0)).createAccessTokenForLocalUser(Mockito.anyString());
        }
    }

    @Test
    void getUser_5() throws Exception {
        when(this.userManager.getUser(Mockito.anyString())).thenReturn(null);
        UserDetails user = this.authenticationProviderManager.getUser("test_user");
        Assertions.assertNull(user);
        Mockito.verify(userManager, Mockito.times(0)).updateLastAccess(Mockito.any(UserDetails.class));
        Mockito.verify(authorizationManager, Mockito.times(0)).getUserAuthorizations(Mockito.anyString());
        Mockito.verify(tokenManager, Mockito.times(0)).createAccessTokenForLocalUser(Mockito.anyString());
    }

    @Test
    void getUser_6() throws Exception {
        UserDetails testUser = this.createMockUser("test_user", true, false, false);
        when(this.userManager.getUser(Mockito.anyString(), Mockito.anyString())).thenReturn(testUser);
        when(this.authorizationManager.getUserAuthorizations("test_user")).thenReturn(this.createMockAuthorization());
        when(this.tokenManager.createAccessTokenForLocalUser("test_user")).thenReturn(this.createMockToken());
        UserDetails user = this.authenticationProviderManager.getUser("test_user", "password");
        Assertions.assertNotNull(user);
        Assertions.assertEquals("test_user", user.getUsername());
        Assertions.assertEquals(2, user.getAuthorizations().size());
        Assertions.assertEquals("access_token_x", user.getAccessToken());
        Mockito.verify(userManager, Mockito.times(1)).updateLastAccess(Mockito.any(UserDetails.class));
        Mockito.verify(authorizationManager, Mockito.times(1)).getUserAuthorizations(Mockito.anyString());
        Mockito.verify(tokenManager, Mockito.times(1)).createAccessTokenForLocalUser(Mockito.anyString());
    }

    @Test
    void getUser_7() throws Exception {
        when(this.userManager.getUser(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        UserDetails user = this.authenticationProviderManager.getUser("test_user", "password");
        Assertions.assertNull(user);
        Mockito.verify(userManager, Mockito.times(0)).updateLastAccess(Mockito.any(UserDetails.class));
        Mockito.verify(authorizationManager, Mockito.times(0)).getUserAuthorizations(Mockito.anyString());
        Mockito.verify(tokenManager, Mockito.times(0)).createAccessTokenForLocalUser(Mockito.anyString());
    }

    @Test
    void getUser_8() throws Exception {
        when(this.userManager.getUser(Mockito.anyString(), Mockito.anyString())).thenThrow(new EntException("System error"));
        Assertions.assertThrows(EntException.class, () -> {
            this.authenticationProviderManager.getUser("test_user", "password");
        });
    }

    @Test
    void authenticate() throws Exception {
        UserDetails activeUser = this.createMockUser("admin", true, false, false);
        when(this.userManager.getUser(Mockito.anyString(), Mockito.anyString())).thenReturn(activeUser);
        when(this.authorizationManager.getUserAuthorizations("admin")).thenReturn(this.createMockAuthorization());
        Mockito.lenient().when(this.tokenManager.createAccessTokenForLocalUser("admin")).thenReturn(this.createMockToken());
        TestingAuthenticationToken authTest = new TestingAuthenticationToken("admin", "password");
        Authentication auth = this.authenticationProviderManager.authenticate(authTest);
        Assertions.assertNotNull(auth);
        Assertions.assertTrue(auth instanceof UsernamePasswordAuthenticationToken);
        Assertions.assertEquals("admin", auth.getPrincipal());
        Assertions.assertEquals(2, auth.getAuthorities().size());
        Mockito.verify(userManager, Mockito.times(1)).updateLastAccess(Mockito.any(UserDetails.class));
        Mockito.verify(authorizationManager, Mockito.times(1)).getUserAuthorizations(Mockito.anyString());
        Mockito.verify(tokenManager, Mockito.times(0)).createAccessTokenForLocalUser(Mockito.anyString());
    }

    @Test
    void authenticate_userNotFound_1() throws Exception {
        when(this.userManager.getUser(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        TestingAuthenticationToken authTest = new TestingAuthenticationToken("admin", "");
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            try {
                Authentication auth = this.authenticationProviderManager.authenticate(authTest);
                Assertions.fail();
            } catch (UsernameNotFoundException e) {
                Mockito.verify(userManager, Mockito.times(1)).getUser(Mockito.anyString(), Mockito.anyString());
                Mockito.verify(userManager, Mockito.times(0)).updateLastAccess(Mockito.any(UserDetails.class));
                Mockito.verify(authorizationManager, Mockito.times(0)).getUserAuthorizations(Mockito.anyString());
                Mockito.verify(tokenManager, Mockito.times(0)).createAccessTokenForLocalUser(Mockito.anyString());
                throw e;
            }
        });
    }

    @Test
    void authenticate_userNotFound_2() throws Exception {
        when(this.userManager.getUser(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        TestingAuthenticationToken authTest = new TestingAuthenticationToken("username", "password");
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            try {
                Authentication auth = this.authenticationProviderManager.authenticate(authTest);
                Assertions.fail();
            } catch (UsernameNotFoundException e) {
                Mockito.verify(userManager, Mockito.times(1)).getUser(Mockito.anyString(), Mockito.anyString());
                Mockito.verify(userManager, Mockito.times(0)).updateLastAccess(Mockito.any(UserDetails.class));
                Mockito.verify(authorizationManager, Mockito.times(0)).getUserAuthorizations(Mockito.anyString());
                Mockito.verify(tokenManager, Mockito.times(0)).createAccessTokenForLocalUser(Mockito.anyString());
                throw e;
            }
        });
    }

    @Test
    void failAuthenticate_serviceError() throws Exception {
        when(this.userManager.getUser(Mockito.anyString(), Mockito.anyString())).thenThrow(new EntException("System error"));
        TestingAuthenticationToken authTest = new TestingAuthenticationToken("username", "password");
        Assertions.assertThrows(AuthenticationServiceException.class, () -> {
            try {
                Authentication auth = this.authenticationProviderManager.authenticate(authTest);
                Assertions.fail();
            } catch (AuthenticationServiceException e) {
                Mockito.verify(userManager, Mockito.times(1)).getUser(Mockito.anyString(), Mockito.anyString());
                Mockito.verify(userManager, Mockito.times(0)).updateLastAccess(Mockito.any(UserDetails.class));
                Mockito.verify(authorizationManager, Mockito.times(0)).getUserAuthorizations(Mockito.anyString());
                Mockito.verify(tokenManager, Mockito.times(0)).createAccessTokenForLocalUser(Mockito.anyString());
                throw e;
            }
        });
    }

    @Test
    void loadUserByUsername() throws Exception {
        UserDetails activeUser = this.createMockUser("admin", true, false, false);
        when(this.userManager.getUser(Mockito.anyString())).thenReturn(activeUser);
        when(this.authorizationManager.getUserAuthorizations("admin")).thenReturn(this.createMockAuthorization());
        org.springframework.security.core.userdetails.UserDetails userDetails
                = this.authenticationProviderManager.loadUserByUsername("admin");
        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals("admin", userDetails.getUsername());
        Assertions.assertEquals(2, userDetails.getAuthorities().size());
        Mockito.verify(userManager, Mockito.times(1)).updateLastAccess(Mockito.any(UserDetails.class));
        Mockito.verify(userManager, Mockito.times(0)).getUser(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(userManager, Mockito.times(1)).getUser(Mockito.anyString());
        Mockito.verify(authorizationManager, Mockito.times(1)).getUserAuthorizations(Mockito.anyString());
        Mockito.verifyZeroInteractions(this.tokenManager);
    }

    @Test
    void failLoadUserByUsername_1() throws Exception {
        when(this.userManager.getUser(Mockito.anyString())).thenReturn(null);
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            try {
                org.springframework.security.core.userdetails.UserDetails userDetails
                        = this.authenticationProviderManager.loadUserByUsername("username");
                Assertions.fail();
            } catch (UsernameNotFoundException e) {
                Mockito.verifyZeroInteractions(this.authorizationManager);
                Mockito.verifyZeroInteractions(this.tokenManager);
                throw e;
            }
        });
    }

    @Test
    void failLoadUserByUsername_2() throws Exception {
        when(this.userManager.getUser(Mockito.anyString())).thenThrow(new EntException("System error"));
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            try {
                org.springframework.security.core.userdetails.UserDetails userDetails
                        = this.authenticationProviderManager.loadUserByUsername("username");
                Assertions.fail();
            } catch (UsernameNotFoundException e) {
                Mockito.verify(userManager, Mockito.times(0)).updateLastAccess(Mockito.any(UserDetails.class));
                Mockito.verify(userManager, Mockito.times(0)).getUser(Mockito.anyString(), Mockito.anyString());
                Mockito.verify(userManager, Mockito.times(1)).getUser(Mockito.anyString());
                Mockito.verifyZeroInteractions(this.authorizationManager);
                Mockito.verifyZeroInteractions(this.tokenManager);
                throw e;
            }
        });
    }

    private UserDetails createMockUser(String username, boolean enabled, boolean accoutExpired, boolean credentialExpired) {
        User user = new User();
        user.setUsername(username);
        user.setDisabled(!enabled);
        user.setCheckCredentials(true);
        user.setCreationDate(DateConverter.parseDate("20000101", "yyyyMMdd"));
        int maxMonthsSinceLastAccess = 6;
        user.setMaxMonthsSinceLastAccess(maxMonthsSinceLastAccess);
        Calendar lastAccess = Calendar.getInstance();
        if (accoutExpired) {
            lastAccess.add(Calendar.MONTH, -(maxMonthsSinceLastAccess + 2));
        } else {
            lastAccess.add(Calendar.MONTH, -(maxMonthsSinceLastAccess - 2));
        }
        user.setLastAccess(lastAccess.getTime());
        int maxMonthsSinceLastPasswordChange = 3;
        user.setMaxMonthsSinceLastPasswordChange(maxMonthsSinceLastPasswordChange);
        Calendar lastPasswordChange = Calendar.getInstance();
        if (credentialExpired) {
            lastPasswordChange.add(Calendar.MONTH, -(maxMonthsSinceLastPasswordChange + 2));
        } else {
            lastPasswordChange.add(Calendar.MONTH, -(maxMonthsSinceLastPasswordChange - 2));
        }
        user.setLastPasswordChange(lastPasswordChange.getTime());
        return user;
    }

    private List<Authorization> createMockAuthorization() {
        List<Authorization> auths = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Group group = new Group();
            group.setName("group_" + i);
            group.setDescription("description_" + i);
            Role role = new Role();
            role.setName("role_" + i);
            role.setDescription("description_" + i);
            auths.add(new Authorization(group, role));
        }
        return auths;
    }

    private OAuth2AccessToken createMockToken() {
        OAuth2AccessTokenImpl token = new OAuth2AccessTokenImpl("access_token_x");
        token.setRefreshToken(new DefaultOAuth2RefreshToken("refresh_token_x"));
        return token;
    }

}

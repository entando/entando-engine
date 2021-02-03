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
package org.entando.entando.aps.system.services.userprofile;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.attribute.MonoTextAttribute;
import com.agiletec.aps.system.common.entity.parse.attribute.MonoTextAttributeHandler;
import org.entando.entando.ent.exception.EntException;
import com.agiletec.aps.system.services.user.User;
import org.entando.entando.aps.system.services.userprofile.model.IUserProfile;
import org.entando.entando.aps.system.services.userprofile.model.UserProfile;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserProfileManagerAspectTest {

	@InjectMocks
	private UserProfileManagerAspect userProfileManagerAspect;
    
	@Mock
	private UserProfileManager userProfileManager;
    
	@BeforeEach
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
    
    @Test
    void testInjectProfile_1() throws EntException {
        IUserProfile returned = this.createFakeProfile("test", SystemConstants.DEFAULT_PROFILE_TYPE_CODE);
        when(userProfileManager.getProfile(Mockito.anyString())).thenReturn(returned);
        
        User user = new User();
        user.setUsername("test");
        Assertions.assertNull(user.getProfile());
        
        userProfileManagerAspect.injectProfile(user);
        Mockito.verify(userProfileManager, Mockito.times(1)).getProfile("test");
        IUserProfile profile = (IUserProfile) user.getProfile();
        Assertions.assertNotNull(profile);
        Assertions.assertEquals("test", profile.getUsername());
    }
    
    @Test
    void testInjectProfile_2() throws EntException {
        when(userProfileManager.getProfile(Mockito.anyString())).thenThrow(EntException.class);
        
        User user = new User();
        user.setUsername("test");
        Assertions.assertNull(user.getProfile());
        
        userProfileManagerAspect.injectProfile(user);
        Mockito.verify(userProfileManager, Mockito.times(1)).getProfile("test");
        Assertions.assertNull(user.getProfile());
    }
    
    @Test
    void testInjectProfile_3() throws EntException {
        Mockito.lenient().when(userProfileManager.getProfile(Mockito.anyString())).thenThrow(EntException.class);
        
        IUserProfile profile = this.createFakeProfile("test", SystemConstants.DEFAULT_PROFILE_TYPE_CODE);
        User user = new User();
        user.setUsername("test");
        user.setProfile(profile);
        
        userProfileManagerAspect.injectProfile(user);
        Mockito.verify(userProfileManager, Mockito.times(0)).getProfile("test");
        Assertions.assertNotNull(user.getProfile());
        Assertions.assertSame(profile, user.getProfile());
    }
    
    @Test
    void testAddProfile_1() throws EntException {
        IUserProfile profile = this.createFakeProfile("test", SystemConstants.DEFAULT_PROFILE_TYPE_CODE);
        User user = new User();
        user.setUsername("test");
        user.setProfile(profile);
        userProfileManagerAspect.addProfile(user);
        Mockito.verify(userProfileManager, Mockito.times(1)).addProfile("test", profile);
    }
    
    @Test
    void testAddProfile_2() throws EntException {
        User user = new User();
        user.setUsername("test");
        userProfileManagerAspect.addProfile(user);
        Mockito.verify(userProfileManager, Mockito.times(0)).addProfile(Mockito.anyString(), Mockito.any(IUserProfile.class));
    }
    
    @Test
    void testUpdateProfile_1() throws EntException {
        IUserProfile profile = this.createFakeProfile("test", SystemConstants.DEFAULT_PROFILE_TYPE_CODE);
        User user = new User();
        user.setUsername("test");
        user.setProfile(profile);
        userProfileManagerAspect.updateProfile(user);
        Mockito.verify(userProfileManager, Mockito.times(1)).updateProfile("test", profile);
    }
    
    @Test
    void testUpdateProfile_2() throws EntException {
        User user = new User();
        user.setUsername("test");
        userProfileManagerAspect.updateProfile(user);
        Mockito.verify(userProfileManager, Mockito.times(0)).updateProfile(Mockito.anyString(), Mockito.any(IUserProfile.class));
    }
    
    @Test
    void testDeleteProfile_1() throws EntException {
        User user = new User();
        user.setUsername("test");
        userProfileManagerAspect.deleteProfile(user);
        Mockito.verify(userProfileManager, Mockito.times(1)).deleteProfile("test");
    }
    
    @Test
    void testDeleteProfile_2() throws EntException {
        userProfileManagerAspect.deleteProfile("test");
        Mockito.verify(userProfileManager, Mockito.times(1)).deleteProfile("test");
    }
    
    @Test
    void testDeleteProfile_3() throws EntException {
        userProfileManagerAspect.deleteProfile(null);
        Mockito.verify(userProfileManager, Mockito.times(0)).deleteProfile("test");
    }
    
	private IUserProfile createFakeProfile(String username, String defaultProfileTypeCode) {
		UserProfile userProfile = new UserProfile();
        userProfile.setId(username);
		MonoTextAttribute monoTextAttribute = new MonoTextAttribute();
		monoTextAttribute.setName("Name");
		monoTextAttribute.setHandler(new MonoTextAttributeHandler());
		userProfile.addAttribute(monoTextAttribute);
		userProfile.setTypeCode(defaultProfileTypeCode);
		return userProfile;
	}
    
}

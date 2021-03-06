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
package org.entando.entando.aps.system.services.userprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Calendar;
import java.util.Date;

import org.entando.entando.aps.system.services.userprofile.model.IUserProfile;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.attribute.DateAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.MonoTextAttribute;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author E.Santoboni
 */
class TestUserManager extends BaseTestCase {

    @Test
    void testInitialize() {
        assertNotNull(this._userManager);
        assertNotNull(this._profileManager);
    }

    @Test
    void testAddDeleteUser() throws Throwable {
        String username = "UserForTest1";
        MockUser user = this.createUserForTest(username);
        Date birthdate = this.steBirthdate(1982, 10, 25);
        IUserProfile profile = this.createProfile("stefano", "puddu", "spuddu@agiletec.it", birthdate, "it");
        user.setProfile(profile);
        try {
            this._userManager.removeUser(user);
            UserDetails extractedUser = this._userManager.getUser(username);
            assertNull(extractedUser);
            this._userManager.addUser(user);
            extractedUser = this._userManager.getUser(username);
            assertEquals(user.getUsername(), extractedUser.getUsername());
            assertNotNull(user.getProfile());
            assertEquals("spuddu@agiletec.it", ((IUserProfile) user.getProfile()).getValue("email"));
        } catch (Throwable t) {
            throw t;
        } finally {
            this._userManager.removeUser(user);
            UserDetails extractedUser = this._userManager.getUser(username);
            assertNull(extractedUser);
        }
    }

    @Test
    void testUpdateUser() throws Throwable {
        String username = "UserForTest2";
        Date birthdate = this.steBirthdate(1982, 10, 25);
        MockUser user = this.createUserForTest(username);
        IUserProfile profile = this.createProfile("stefano", "puddu", "spuddu@agiletec.it", birthdate, "it");
        user.setProfile(profile);
        try {
            this._userManager.removeUser(user);
            UserDetails extractedUser = this._userManager.getUser(username);
            assertNull(extractedUser);
            this._userManager.addUser(user);
            extractedUser = this._userManager.getUser(username);
            assertEquals(user.getUsername(), extractedUser.getUsername());
            assertNotNull(extractedUser);
            assertEquals(0, extractedUser.getAuthorizations().size());
            user.setPassword("changedPassword");
            IUserProfile extractedProfile = (IUserProfile) extractedUser.getProfile();
            assertNotNull(extractedProfile);
            MonoTextAttribute emailAttr = (MonoTextAttribute) ((IUserProfile) extractedProfile).getAttribute("email");
            assertEquals("spuddu@agiletec.it", emailAttr.getText());
            emailAttr.setText("agiletectest@gmail.com");
            user.setProfile(extractedProfile);
            this._userManager.updateUser(user);
            this._userManager.changePassword(user.getUsername(), user.getUsername());
            extractedUser = this._userManager.getUser(username);
            assertEquals(user.getUsername(), extractedUser.getUsername());
            extractedUser = this._userManager.getUser(username);
            assertNotNull(extractedUser);
            extractedProfile = (IUserProfile) extractedUser.getProfile();
            MonoTextAttribute extractedEmailAttr = (MonoTextAttribute) ((IUserProfile) extractedProfile).getAttribute("email");
            assertEquals("agiletectest@gmail.com", extractedEmailAttr.getText());
        } catch (Throwable t) {
            throw t;
        } finally {
            this._userManager.removeUser(user);
            UserDetails extractedUser = this._userManager.getUser(username);
            assertNull(extractedUser);
        }
    }

    private IUserProfile createProfile(String name, String surname, String email, Date birthdate, String language) {
        IUserProfile profile = _profileManager.getDefaultProfileType();
        MonoTextAttribute nameAttr = (MonoTextAttribute) profile.getAttribute("fullname");
        nameAttr.setText(name + " " + surname);
        MonoTextAttribute emailAttr = (MonoTextAttribute) profile.getAttribute("email");
        DateAttribute birthdateAttr = (DateAttribute) profile.getAttribute("birthdate");
        birthdateAttr.setDate(birthdate);
        MonoTextAttribute languageAttr = (MonoTextAttribute) profile.getAttribute("language");
        languageAttr.setText(language);
        emailAttr.setText(email);
        return profile;
    }

    private Date steBirthdate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        Date birthdate = new Date(calendar.getTimeInMillis());
        return birthdate;
    }

    protected MockUser createUserForTest(String username) {
        MockUser user = new MockUser();
        user.setUsername(username);
        user.setPassword("temp");
        return user;
    }

    @BeforeEach
    private void init() throws Exception {
        try {
            this._userManager = (IUserManager) this.getService(SystemConstants.USER_MANAGER);
            this._profileManager = (IUserProfileManager) this.getService(SystemConstants.USER_PROFILE_MANAGER);
        } catch (Exception e) {
            throw e;
        }
    }

    private IUserProfileManager _profileManager;
    private IUserManager _userManager;

}

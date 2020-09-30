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

import java.util.List;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.common.entity.model.FieldError;
import com.agiletec.aps.system.common.entity.model.attribute.ITextAttribute;
import com.agiletec.aps.system.services.group.IGroupManager;
import java.util.Arrays;
import org.entando.entando.aps.system.services.userprofile.model.IUserProfile;

/**
 * @author E.Santoboni
 */
public class ValidateUserProfileIntegrationTest extends BaseTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.init();
	}

	public void testValidate_1() throws Throwable {
		try {
			IUserProfile profile = this.userProfileManager.getDefaultProfileType();
			List<FieldError> errors = profile.validate(this._groupManager);
			assertNotNull(errors);
            String[] requiredFields = {"fullname", "email", "birthdate", "language"};
			assertEquals(requiredFields.length, errors.size());
            for (int i = 0; i < requiredFields.length; i++) {
                String requiredField = requiredFields[i];
                FieldError error = errors.get(i);
                if (error.getFieldCode().startsWith("Date")) {
                    assertEquals("Date:" + requiredField, error.getFieldCode());
                } else {
                    assertEquals("Monotext:" + requiredField, error.getFieldCode());
                }
                assertEquals(FieldError.MANDATORY, error.getErrorCode());
            }
            ITextAttribute textAttribute = (ITextAttribute) profile.getAttribute("email");
			textAttribute.setText("invalid address", "it");
            errors = profile.validate(this._groupManager);
            assertNotNull(errors);
            assertEquals(requiredFields.length, errors.size());
            errors.stream().forEach(error -> {
                if (error.getFieldCode().equals("Monotext:email")) {
                    assertEquals(FieldError.INVALID_FORMAT, error.getErrorCode());
                }
            });
		} catch (Throwable t) {
			throw t;
		}
	}

	public void testValidate_2() throws Throwable {
		try {
			IUserProfile profile = this.userProfileManager.getProfileType("OTH");
			List<FieldError> errors = profile.validate(this._groupManager);
			assertNotNull(errors);
            String[] requiredFields = {"firstname", "surname", "email"};
			assertEquals(requiredFields.length, errors.size());
            for (int i = 0; i < requiredFields.length; i++) {
                String requiredField = requiredFields[i];
                FieldError error = errors.get(i);
                if (error.getFieldCode().startsWith("Email")) {
                    assertEquals("Email:" + requiredField, error.getFieldCode());
                } else {
                    assertEquals("Monotext:" + requiredField, error.getFieldCode());
                }
                assertEquals(FieldError.MANDATORY, error.getErrorCode());
            }
            ITextAttribute textAttribute = (ITextAttribute) profile.getAttribute("email");
            String[] invalidEmails = {"j.brown@entando", "j.brown@entando com", "j.brown@ent@ando.com", 
                "@j.brownentando.com", "j.brown@ent ando.com", "j.brown@ent$ando.com", "j.brown@@entando.com", "j.brown.entando.com"};
            Arrays.stream(invalidEmails).forEach(email -> {
                textAttribute.setText(email, "it");
                List<FieldError> errors_adv = profile.validate(this._groupManager);
                assertEquals(requiredFields.length, errors_adv.size());
                for (int i = 0; i < errors_adv.size(); i++) {
                    FieldError error_adv = errors_adv.get(i);
                    if (error_adv.getFieldCode().startsWith("Email")) {
                        assertEquals(FieldError.INVALID_FORMAT, error_adv.getErrorCode());
                    } else {
                        assertEquals(FieldError.MANDATORY, error_adv.getErrorCode());
                    }
                }
            });
		} catch (Throwable t) {
			throw t;
		}
	}

	private void init() throws Exception {
		try {
			this.userProfileManager = this.getApplicationContext().getBean(IUserProfileManager.class);
		} catch (Throwable t) {
			throw new Exception(t);
		}
	}

	private IUserProfileManager userProfileManager = null;
	private IGroupManager _groupManager = null;

}

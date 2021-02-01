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

import java.util.List;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.FieldError;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.ITextAttribute;
import com.agiletec.aps.system.services.group.IGroupManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import java.util.Arrays;
import org.entando.entando.aps.system.services.userprofile.model.IUserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author E.Santoboni
 */
class ValidateUserProfileIntegrationTest extends BaseTestCase {

	@Test
    void testValidate_1() throws Throwable {
		try {
			IUserProfile profile = this.userProfileManager.getDefaultProfileType();
			List<FieldError> errors = profile.validate(this._groupManager, this._langManager);
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
            errors = profile.validate(this._groupManager, this._langManager);
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

	@Test
    void testValidate_2() throws Throwable {
		try {
			IUserProfile profile = this.userProfileManager.getProfileType("OTH");
			List<FieldError> errors = profile.validate(this._groupManager, this._langManager);
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
                List<FieldError> errors_adv = profile.validate(this._groupManager, this._langManager);
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
    
	@Test
    void testValidate_3() throws Throwable {
		try {
			IUserProfile profile = this.userProfileManager.getProfileType("ALL");
			List<FieldError> errors = profile.validate(this._groupManager, this._langManager);
			assertNotNull(errors);
            for (int i = 0; i < errors.size(); i++) {
                FieldError fieldError = errors.get(i);
                String fieldCode = fieldError.getFieldCode();
                String[] fieldNameSections = fieldCode.split(":");
                String[] parts = fieldNameSections[1].split("_");
                String attributeName = (parts.length == 1) ?  parts[0] : parts[1];
                AttributeInterface attribute = profile.getAttribute(attributeName);
                assertEquals(attribute.isMultilingual(), parts.length == 2);
                assertEquals(attribute.getType() + ":" + (attribute.isMultilingual() ? "it_":"") + attributeName, fieldError.getFieldCode());
                assertEquals(FieldError.MANDATORY, fieldError.getErrorCode());
            }
		} catch (Throwable t) {
			throw t;
		}
	}

    @BeforeEach
	private void init() throws Exception {
		try {
			this.userProfileManager = this.getApplicationContext().getBean(IUserProfileManager.class);
			this._groupManager = (IGroupManager) this.getService(SystemConstants.GROUP_MANAGER);
			this._langManager = (ILangManager) this.getService(SystemConstants.LANGUAGE_MANAGER);
		} catch (Throwable t) {
			throw new Exception(t);
		}
	}

	private IUserProfileManager userProfileManager = null;
	private IGroupManager _groupManager = null;
	private ILangManager _langManager = null;

}

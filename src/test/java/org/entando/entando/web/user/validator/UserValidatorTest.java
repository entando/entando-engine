package org.entando.entando.web.user.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class UserValidatorTest {

    @Test
    public void deleteAdminIsNotValid() {
        assertFalse(UserValidator.isValidDeleteUser("admin"));
    }

    @Test
    public void deleteNotAdminIsValid() {
        assertTrue(UserValidator.isValidDeleteUser("notAdmin"));
    }

    @Test
    public void createErrorDeleteAdminContainsErrorMessage() {
        BindingResult bindingAdminDeleteError = UserValidator.createDeleteAdminError();

        assertEquals(1, bindingAdminDeleteError.getErrorCount());

        List<ObjectError> errors = bindingAdminDeleteError.getAllErrors();
        ObjectError adminError = errors.get(0);

        assertEquals(UserValidator.ERRCODE_DELETE_ADMIN, adminError.getCode());
        assertEquals("user.admin.cant.delete", adminError.getDefaultMessage());
    }
}
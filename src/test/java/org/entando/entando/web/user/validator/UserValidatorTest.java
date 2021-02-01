package org.entando.entando.web.user.validator;

import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;

class UserValidatorTest {

    @Test
    void deleteAdminIsNotValid() {
        Assertions.assertTrue(UserValidator.isAdminUser("admin"));
    }

    @Test
    void deleteNotAdminIsValid() {
        Assertions.assertFalse(UserValidator.isAdminUser("notAdmin"));
    }

    @Test
    void createErrorDeleteAdminContainsErrorMessage() {
        BindingResult bindingAdminDeleteError = UserValidator.createDeleteAdminError();

        Assertions.assertEquals(1, bindingAdminDeleteError.getErrorCount());

        List<ObjectError> errors = bindingAdminDeleteError.getAllErrors();
        ObjectError adminError = errors.get(0);

        Assertions.assertEquals(UserValidator.ERRCODE_DELETE_ADMIN, adminError.getCode());
        Assertions.assertEquals("user.admin.cant.delete", adminError.getDefaultMessage());
    }

    @Test
    void createErrorSelfDeleteContainsErrorMessage() {
        MapBindingResult bindingResult = new MapBindingResult(new HashMap<Object, Object>(), "username");

        BindingResult bindingSelfDeleteError = UserValidator.createSelfDeleteUserError(bindingResult);

        Assertions.assertEquals(1, bindingSelfDeleteError.getErrorCount());

        List<ObjectError> errors = bindingSelfDeleteError.getAllErrors();
        ObjectError error = errors.get(0);

        Assertions.assertEquals(UserValidator.ERRCODE_SELF_DELETE, error.getCode());
        Assertions.assertEquals("user.self.delete.error", error.getDefaultMessage());
    }
}
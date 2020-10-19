package org.entando.entando.web.userpreferences.validator;

import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
import org.entando.entando.web.entity.validator.EntityValidator;
import org.entando.entando.web.user.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class UserPreferencesValidator {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(this.getClass());

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private IUserManager userManager;

    public void validate(UserDetails user, String username, BindingResult bindingResult) {
        validateSameUser(user, username, bindingResult);
        if (!userExists(username)) {
            throw new ResourceNotFoundException(EntityValidator.ERRCODE_ENTITY_DOES_NOT_EXIST, "User", username);
        }
    }

    private void validateSameUser(UserDetails user, String username, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        userValidator.validateSameUser(username, user.getUsername(), bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
    }

    private boolean userExists(String username) {
        try {
            return userManager.getUser(username) != null;
        } catch (EntException e) {
            logger.error("Error in checking user existence {}", username, e);
            throw new RestServerError("Error in loading user", e);
        }
    }

}

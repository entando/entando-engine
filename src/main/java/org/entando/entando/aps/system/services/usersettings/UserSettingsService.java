package org.entando.entando.aps.system.services.usersettings;

import com.agiletec.aps.system.services.user.IUserManager;
import java.util.Map;

import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.aps.system.services.IDtoBuilder;
import org.entando.entando.aps.system.services.usersettings.model.UserSettingsDto;
import org.entando.entando.aps.system.services.usersettings.model.UserSettingsDtoBuilder;
import org.entando.entando.web.usersettings.model.UserSettingsRequest;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class UserSettingsService implements IUserSettingsService {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(getClass());

    @Autowired
    private IUserManager userManager;

    private IDtoBuilder<Map<String, String>, UserSettingsDto> dtoBuilder = new UserSettingsDtoBuilder();

    public IUserManager getUserManager() {
        return userManager;
    }
    public void setUserManager(IUserManager userManager) {
        this.userManager = userManager;
    }

    public IDtoBuilder<Map<String, String>, UserSettingsDto> getDtoBuilder() {
        return dtoBuilder;
    }

    public void setDtoBuilder(IDtoBuilder<Map<String, String>, UserSettingsDto> dtoBuilder) {
        this.dtoBuilder = dtoBuilder;
    }

    @Override
    public UserSettingsDto getUserSettings() {
        try {
            Map<String, String> systemParams = this.getUserManager().getParams();
            return this.getDtoBuilder().convert(systemParams);
        } catch (Throwable e) {
            logger.error("Error getting user settings", e);
            throw new RestServerError("Error getting user settings", e);
        }
    }

    @Override
    public UserSettingsDto updateUserSettings(UserSettingsRequest request) {
        try {
            Map<String, String> params = request.toMap();
            this.getUserManager().updateParams(params);
            return this.getUserSettings();
        } catch (Throwable e) {
            logger.error("Error updating user settings", e);
            throw new RestServerError("Error updating user settings", e);
        }
    }

}

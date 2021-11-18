package org.entando.entando.aps.system.services.usersettings.model;

import com.agiletec.aps.system.services.user.IUserManager;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserSettingsDto {

    public static final String EXTENDED_PRIVACY_MODULE_ENABLED = IUserManager.CONFIG_PARAM_PM_ENABLED;
    public static final String GRAVATAR_INTEGRATION_ENABLED = IUserManager.CONFIG_PARAM_GRAVATAR_INTEGRATION_ENABLED;
    public static final String MAX_MONTHS_SINCE_LASTACCESS = IUserManager.CONFIG_PARAM_PM_MM_LAST_ACCESS;
    public static final String MAX_MONTHS_SINCE_LASTPASSWORDCHANGE = IUserManager.CONFIG_PARAM_PM_MM_LAST_PASSWORD_CHANGE;

    @JsonProperty("restrictionsActive")
    private boolean extendedPrivacyModuleEnabled;

    @JsonProperty("enableGravatarIntegration")
    private boolean gravatarIntegrationEnabled;

    @JsonProperty("lastAccessPasswordExpirationMonths")
    private int maxMonthsSinceLastAccess;

    @JsonProperty("maxMonthsPasswordValid")
    private int maxMonthsSinceLastPasswordChange;

    private boolean passwordAlwaysActive;

    public boolean isExtendedPrivacyModuleEnabled() {
        return extendedPrivacyModuleEnabled;
    }

    public void setExtendedPrivacyModuleEnabled(boolean extendedPrivacyModuleEnabled) {
        this.extendedPrivacyModuleEnabled = extendedPrivacyModuleEnabled;
    }

    public boolean isGravatarIntegrationEnabled() {
        return gravatarIntegrationEnabled;
    }

    public void setGravatarIntegrationEnabled(boolean gravatarIntegrationEnabled) {
        this.gravatarIntegrationEnabled = gravatarIntegrationEnabled;
    }

    public int getMaxMonthsSinceLastAccess() {
        return maxMonthsSinceLastAccess;
    }

    public void setMaxMonthsSinceLastAccess(int maxMonthsSinceLastAccess) {
        this.maxMonthsSinceLastAccess = maxMonthsSinceLastAccess;
    }

    public int getMaxMonthsSinceLastPasswordChange() {
        return maxMonthsSinceLastPasswordChange;
    }

    public void setMaxMonthsSinceLastPasswordChange(int maxMonthsSinceLastPasswordChange) {
        this.maxMonthsSinceLastPasswordChange = maxMonthsSinceLastPasswordChange;
    }

    public boolean isPasswordAlwaysActive() {
        return passwordAlwaysActive;
    }

    public void setPasswordAlwaysActive(boolean passwordAlwaysActive) {
        this.passwordAlwaysActive = passwordAlwaysActive;
    }

}

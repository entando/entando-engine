package org.entando.entando.web.user.model;

import javax.validation.constraints.NotBlank;

public class UpdatePasswordRequest {

    @NotBlank(message = "user.old.password.NotBlank")
    private String oldPassword;
    @NotBlank(message = "user.new.password.NotBlank")
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

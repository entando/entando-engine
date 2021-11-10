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
package org.entando.entando.web.user.model;

import org.entando.entando.aps.system.services.user.IUserService;
import org.entando.entando.web.common.annotation.ValidateString;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author paddeo
 */
public class UserRequest {

    @NotBlank(message = "user.username.NotBlank")
    private String username;
    @ValidateString(acceptedValues = {IUserService.STATUS_ACTIVE, IUserService.STATUS_DISABLED}, message = "user.status.invalid")
    private String status = IUserService.STATUS_DISABLED;

    //@NotBlank(message = "user.password.NotBlank")
    //@Size(min = 6, message = "user.password.size")
    private String password;

    private String passwordConfirm;

    private boolean reset;

    private String profileType;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public boolean isReset() {
        return reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    @Override
    public String toString() {
        return "UserRequest{" +
                "username='" + username + '\'' +
                ", status='" + status + '\'' +
                ", reset=" + reset +
                ", profileType='" + profileType + '\'' +
                '}';
    }
}

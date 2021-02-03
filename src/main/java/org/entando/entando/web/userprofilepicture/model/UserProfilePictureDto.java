package org.entando.entando.web.userprofilepicture.model;

import java.util.ArrayList;
import java.util.List;

public class UserProfilePictureDto {

    private String username;
    private List<UserProfilePictureVersionDto> versions = new ArrayList<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<UserProfilePictureVersionDto> getVersions() {
        return versions;
    }

    public void setVersions(List<UserProfilePictureVersionDto> versions) {
        this.versions = versions;
    }
}

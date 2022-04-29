package com.agiletec.aps.system.services.user;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.role.Role;
import java.util.Set;

public class UserGroupPermissions {

    private String group;
    private Set<String> permissions;

    public UserGroupPermissions(Group groupObject, Role role) {
        this.group = (null != groupObject) ? groupObject.getName() : null;
        this.permissions = (null != role) ? role.getPermissions() : null;
    }

    public UserGroupPermissions(String group, Set<String> permissions) {
        this.group = group;
        this.permissions = permissions;
    }

    public String getGroup() {
        return group;
    }

    public UserGroupPermissions setGroup(String group) {
        this.group = group;
        return this;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public UserGroupPermissions setPermissions(Set<String> permissions) {
        this.permissions = permissions;
        return this;
    }
}

/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.user;

import java.util.List;

import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.system.services.user.UserGroupPermissions;
import org.entando.entando.aps.system.services.group.model.GroupDto;
import org.entando.entando.aps.system.services.user.model.UserAuthorityDto;
import org.entando.entando.aps.system.services.user.model.UserDto;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.user.model.UserAuthoritiesRequest;
import org.entando.entando.web.user.model.UserUpdatePasswordRequest;
import org.entando.entando.web.user.model.UserRequest;

/**
 * @author paddeo
 */
public interface IUserService {

    String BEAN_NAME = "UserService";
    String STATUS_ACTIVE = "active";
    String STATUS_DISABLED = "inactive";

    List<UserAuthorityDto> getUserAuthorities(String username);
    List<UserAuthorityDto> addUserAuthorities(String username, UserAuthoritiesRequest request);
    List<UserAuthorityDto> updateUserAuthorities(String username, UserAuthoritiesRequest request);
    void deleteUserAuthorities(String username);
    PagedMetadata<UserDto> getUsers(RestListRequest requestList, String withProfile);
    UserDto getUser(String username);
    UserDto updateUser(UserRequest userRequest);
    UserDto addUser(UserRequest userRequest);
    void removeUser(String username);
    UserDto updateUserPassword(UserUpdatePasswordRequest userUpdatePasswordRequest);

    /**
     * gets and returns informations about received user group and received user permissions
     *
     * @param user the user of which return permission infos
     * @return informations about received user group and received user permissions
     */
    List<UserGroupPermissions> getMyGroupPermissions(UserDetails user);

    /**
     * Method to get all the groups that the user has at least a permission on it, plus the free group.
     *
     * @param user the user that will be used to retrieve the groups
     *
     * @return all the groups that the user has at least a permission on it, plus the free group
     */
    List<GroupDto> getMyGroups(UserDetails user);
}

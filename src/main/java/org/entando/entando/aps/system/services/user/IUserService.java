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
import org.entando.entando.aps.system.services.user.model.UserAuthorityDto;
import org.entando.entando.aps.system.services.user.model.UserDto;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.user.model.UserAuthoritiesRequest;
import org.entando.entando.web.user.model.UserPasswordRequest;
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

    UserDto updateUserPassword(UserPasswordRequest passwordRequest);
}

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

package com.agiletec.aps.system.services.group.cache;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupDAO;
import java.util.Map;

/**
 * @author E.Santoboni
 */
public interface IGroupManagerCacheWrapper {

    String GROUP_MANAGER_CACHE_NAME = "Entando_GroupManager";
    String GROUP_CACHE_NAME_PREFIX = "GroupManager_group_";
    String GROUP_CODES_CACHE_NAME = "GroupManager_groups";

    void initCache(IGroupDAO groupDAO) throws ApsSystemException;

    Map<String, Group> getGroups();

    Group getGroup(String code);

    void addGroup(Group group);

    void updateGroup(Group group);

    void removeGroup(Group group);

}

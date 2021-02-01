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
package org.entando.entando.aps.system.services.userprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.entando.entando.aps.system.services.entity.model.EntityTypeShortDto;
import org.entando.entando.aps.system.services.userprofile.model.UserProfile;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserProfileTypeServiceTest {

    @Mock
    private IUserProfileManager userProfileManager;

    @InjectMocks
    private UserProfileTypeService userProfileTypeService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userProfileTypeService.setEntityManagers(Collections.singletonList(userProfileManager));
        when(userProfileManager.getName()).thenReturn(SystemConstants.USER_PROFILE_MANAGER);
        when(userProfileManager.getEntityPrototypes()).thenReturn(createUserProfilesMap());
    }

    private Map<String, IApsEntity> createUserProfilesMap() {
        Map<String, IApsEntity> map = new HashMap<>();
        addProfileToMap(map, "Bbb");
        addProfileToMap(map, "aaa");
        addProfileToMap(map, "ccc");
        return map;
    }

    private void addProfileToMap(Map<String, IApsEntity> map, String profileName) {
        UserProfile userProfile = new UserProfile();
        assertTrue(profileName.length() >= 3);
        userProfile.setTypeCode(profileName.toUpperCase().substring(0, 3));
        userProfile.setTypeDescription(profileName);
        map.put(userProfile.getTypeCode(), userProfile);
    }

    @Test
    void testGetShortUserProfileTypesDefaultSort() {
        RestListRequest requestList = new RestListRequest();
        PagedMetadata<EntityTypeShortDto> pagedMetadata = userProfileTypeService.getShortUserProfileTypes(requestList);
        List<EntityTypeShortDto> list = pagedMetadata.getBody();
        assertEquals(3, list.size());
        assertEquals("AAA", list.get(0).getCode());
        assertEquals("BBB", list.get(1).getCode());
        assertEquals("CCC", list.get(2).getCode());
    }

    /**
     * Sorting must be case insensitive.
     */
    @Test
    void testGetShortUserProfileTypesSortByName() {
        RestListRequest requestList = new RestListRequest();
        requestList.setSort("name");

        PagedMetadata<EntityTypeShortDto> pagedMetadata = userProfileTypeService.getShortUserProfileTypes(requestList);
        List<EntityTypeShortDto> list = pagedMetadata.getBody();
        assertEquals(3, list.size());
        assertEquals("aaa", list.get(0).getName());
        assertEquals("Bbb", list.get(1).getName());
        assertEquals("ccc", list.get(2).getName());
    }
}

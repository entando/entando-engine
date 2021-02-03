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
package com.agiletec.aps.system.services.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.FieldSearchFilter;
import com.agiletec.aps.system.common.FieldSearchFilter.LikeOptionType;
import com.agiletec.aps.system.common.model.dao.SearcherDaoPaginatedResult;
import org.entando.entando.ent.exception.EntException;
import org.apache.commons.lang3.ArrayUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.agiletec.aps.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author E.Santoboni
 */
class TestGroupManager extends BaseTestCase {

    @Test
    void testGetGroups() {
        List<Group> groups = groupManager.getGroups();
        assertTrue(groups.size() >= 6);
    }

    @Test
    void testAddDeleteGroup() throws Throwable {
        int initSize = groupManager.getGroups().size();
        String groupCode = "Gruppo_Prova";
        Group group = new Group();
        group.setName(groupCode);
        group.setDescription("descr_gruppo_prova");
        try {
            assertNull(groupManager.getGroup(groupCode));
            groupManager.addGroup(group);
            List<Group> groups = groupManager.getGroups();
            assertEquals(initSize + 1, groups.size());
            assertNotNull(groupManager.getGroup(groupCode));
            groupManager.removeGroup(group);
            groups = groupManager.getGroups();
            assertEquals(initSize, groups.size());
            assertNull(groupManager.getGroup(groupCode));
        } catch (Throwable t) {
            throw t;
        } finally {
            groupManager.removeGroup(group);
        }
    }

    @Test
    void testUpdateGroup() throws Throwable {
        int initSize = groupManager.getGroups().size();
        Group group = new Group();
        String groupCode = "Gruppo_Prova";
        group.setName(groupCode);
        group.setDescription("descr_gruppo_prova");
        try {
            assertNull(groupManager.getGroup(groupCode));
            groupManager.addGroup(group);
            List<Group> groups = groupManager.getGroups();
            assertEquals(initSize + 1, groups.size());

            Group groupNew = new Group();
            groupNew.setName(groupCode);
            groupNew.setDescription("Nuova_descr");
            groupManager.updateGroup(groupNew);
            Group extracted = groupManager.getGroup(groupCode);
            assertEquals(groupNew.getDescription(), extracted.getDescription());

            groupManager.removeGroup(group);
            groups = groupManager.getGroups();
            assertEquals(initSize, groups.size());
            assertNull(groupManager.getGroup(groupCode));
        } catch (Throwable t) {
            throw t;
        } finally {
            groupManager.removeGroup(group);
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    void test_search_should_return_all_results() throws EntException {
        FieldSearchFilter[] fieldSearchFilters = null;
        SearcherDaoPaginatedResult<Group> result = groupManager.getGroups(fieldSearchFilters);
        assertThat(result.getCount(), is(6));
        assertThat(result.getList().size(), is(6));

        fieldSearchFilters = new FieldSearchFilter[0];
        result = groupManager.getGroups(fieldSearchFilters);
        assertThat(result.getCount(), is(6));
        assertThat(result.getList().size(), is(6));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void test_search_by_filter() throws EntException {
        FieldSearchFilter[] fieldSearchFilters = new FieldSearchFilter[0];

        FieldSearchFilter groupNameFilter = new FieldSearchFilter<>("groupname", "s", true, LikeOptionType.COMPLETE);
        fieldSearchFilters = ArrayUtils.add(fieldSearchFilters, groupNameFilter);

        SearcherDaoPaginatedResult<Group> result = groupManager.getGroups(fieldSearchFilters);
        assertThat(result.getCount(), is(3));
        assertThat(result.getList().size(), is(3));

        fieldSearchFilters = new FieldSearchFilter[0];
        FieldSearchFilter limitFilter = new FieldSearchFilter<>(2, 0);
        fieldSearchFilters = ArrayUtils.add(fieldSearchFilters, groupNameFilter);
        fieldSearchFilters = ArrayUtils.add(fieldSearchFilters, limitFilter);
        result = groupManager.getGroups(fieldSearchFilters);
        assertThat(result.getCount(), is(3));
        assertThat(result.getList().size(), is(2));

        fieldSearchFilters = new FieldSearchFilter[0];
        limitFilter = new FieldSearchFilter<>(2, 2);
        fieldSearchFilters = ArrayUtils.add(fieldSearchFilters, limitFilter);
        fieldSearchFilters = ArrayUtils.add(fieldSearchFilters, groupNameFilter);
        result = groupManager.getGroups(fieldSearchFilters);
        assertThat(result.getCount(), is(3));
        assertThat(result.getList().size(), is(1));
    }

    @BeforeEach
    private void init() {
        this.groupManager = (IGroupManager) this.getService(SystemConstants.GROUP_MANAGER);
    }

    private IGroupManager groupManager = null;

}

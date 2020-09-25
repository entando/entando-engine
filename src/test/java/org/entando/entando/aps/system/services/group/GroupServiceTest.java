package org.entando.entando.aps.system.services.group;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.entando.entando.aps.system.services.DtoBuilder;
import org.entando.entando.aps.system.services.IDtoBuilder;
import org.entando.entando.aps.system.services.group.model.GroupDto;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.web.common.exceptions.ValidationConflictException;
import org.entando.entando.web.group.model.GroupRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GroupServiceTest {

    @InjectMocks
    private GroupService groupService;

    @Mock
    private IGroupManager groupManager;
    @Mock
    private IDtoBuilder<Group, GroupDto> dtoBuilder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = ValidationConflictException.class)
    public void should_raise_exception_on_delete_reserved_group() throws JsonProcessingException {
        Group group = new Group();
        group.setName(Group.ADMINS_GROUP_NAME);
        when(groupManager.getGroup(group.getName())).thenReturn(group);
        this.groupService.removeGroup(group.getName());
    }

    @Test
    public void addExistingGroupShouldReturnTheReceivedGroup() throws EntException {

        Group existingGroup = GroupTestHelper.stubTestGroup();
        GroupDto expectedDto = GroupTestHelper.stubGroupDto();
        GroupRequest groupReq = GroupTestHelper.stubTestGroupRequest();

        when(groupManager.getGroup(anyString())).thenReturn(existingGroup);
        when(dtoBuilder.convert(existingGroup)).thenReturn(expectedDto);

        GroupDto actualGroupDto = this.groupService.addGroup(groupReq);

        verify(groupManager, times(0)).addGroup(any());
        GroupTestHelper.assertGroupDtoEquals(expectedDto, actualGroupDto);
    }

    @Test(expected = ValidationConflictException.class)
    public void addExistingGroupWithDifferentDescriptionsShouldThrowValidationConflictException() {

        Group existingGroup = GroupTestHelper.stubTestGroup();
        existingGroup.setDescription("Description old");
        GroupRequest groupReq = GroupTestHelper.stubTestGroupRequest();

        when(groupManager.getGroup(anyString())).thenReturn(existingGroup);

        this.groupService.addGroup(groupReq);
    }

}

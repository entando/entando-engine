package org.entando.entando.aps.system.services.group;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupManager;
import org.entando.entando.aps.system.services.IDtoBuilder;
import org.entando.entando.aps.system.services.group.model.GroupDto;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.web.common.exceptions.ValidationConflictException;
import org.entando.entando.web.group.model.GroupRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @InjectMocks
    private GroupService groupService;

    @Mock
    private IGroupManager groupManager;
    @Mock
    private IDtoBuilder<Group, GroupDto> dtoBuilder;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void should_raise_exception_on_delete_reserved_group() {
        Group group = new Group();
        group.setName(Group.ADMINS_GROUP_NAME);
        when(groupManager.getGroup(group.getName())).thenReturn(group);
        Assertions.assertThrows(ValidationConflictException.class, () -> {
            this.groupService.removeGroup(group.getName());
        });
    }

    @Test
    void addExistingGroupShouldThrowValidationConflictException() {

        Group existingGroup = GroupTestHelper.stubTestGroup();
        GroupRequest groupReq = GroupTestHelper.stubTestGroupRequest();

        when(groupManager.getGroup(anyString())).thenReturn(existingGroup);
        Assertions.assertThrows(ValidationConflictException.class, () -> {
            this.groupService.addGroup(groupReq);
        });
    }

    @Test
    void addExistingGroupWithDifferentDescriptionsShouldThrowValidationConflictException() {

        Group existingGroup = GroupTestHelper.stubTestGroup();
        existingGroup.setDescription("Description old");
        GroupRequest groupReq = GroupTestHelper.stubTestGroupRequest();

        when(groupManager.getGroup(anyString())).thenReturn(existingGroup);
        Assertions.assertThrows(ValidationConflictException.class, () -> {
            this.groupService.addGroup(groupReq);
        });
    }

}

package org.entando.entando.aps.system.services.group;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.agiletec.aps.system.services.group.Group;
import org.entando.entando.aps.system.services.group.model.GroupDto;
import org.entando.entando.web.group.model.GroupRequest;
import org.springframework.test.web.servlet.ResultActions;

public class GroupTestHelper {

    public static final String GROUP_NAME = "group1";
    public static final String GROUP_DESCRIPTION = "Funny group";

    public static GroupDto stubGroupDto() {
        GroupDto groupDto = new GroupDto();
        groupDto.setCode(GROUP_NAME);
        groupDto.setName(GROUP_DESCRIPTION);
        return groupDto;
    }

    public static Group stubTestGroup() {
        Group group = new Group();
        group.setName(GROUP_NAME);
        group.setDescription(GROUP_DESCRIPTION);
        return group;
    }


    public static GroupRequest stubTestGroupRequest() {
        GroupRequest groupReq = new GroupRequest();
        groupReq.setCode(GROUP_NAME);
        groupReq.setName(GROUP_DESCRIPTION);
        return groupReq;
    }


    public static void assertGroupDtoEquals(GroupDto expected, GroupDto actual) {
        assertEquals(expected.getCode(), actual.getCode());
        assertEquals(expected.getName(), actual.getName());
    }


    public static void assertGroups(GroupDto expected, ResultActions actual) throws Exception {

        actual.andExpect(jsonPath("$.payload.code", is(expected.getCode())))
                .andExpect(jsonPath("$.payload.name", is(expected.getName())));
    }

}

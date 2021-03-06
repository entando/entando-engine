package org.entando.entando.aps.system.services.group;

import com.agiletec.aps.BaseTestCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.entando.entando.aps.system.services.group.model.GroupDto;
import org.entando.entando.web.common.model.Filter;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GroupServiceIntegrationTest extends BaseTestCase {

    private IGroupService groupService;

    @BeforeEach
    private void init() throws Exception {
        try {
            groupService = (IGroupService) this.getApplicationContext().getBean(IGroupService.BEAN_NAME);
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    void testGetGroups() throws JsonProcessingException {
        RestListRequest restListRequest = new RestListRequest();
        restListRequest.setPageSize(5);
        PagedMetadata<GroupDto> res = this.groupService.getGroups(restListRequest);
        assertThat(res.getPage(), is(1));
        assertThat(res.getPageSize(), is(5));
        assertThat(res.getLastPage(), is(2));
        assertThat(res.getTotalItems(), is(6));

        //
        restListRequest.setPageSize(2);
        res = this.groupService.getGroups(restListRequest);
        assertThat(res.getPage(), is(1));
        assertThat(res.getPageSize(), is(2));
        assertThat(res.getLastPage(), is(3));
        assertThat(res.getTotalItems(), is(6));

        //
        restListRequest.setPageSize(4);
        res = this.groupService.getGroups(restListRequest);
        assertThat(res.getPage(), is(1));
        assertThat(res.getPageSize(), is(4));
        assertThat(res.getLastPage(), is(2));
        assertThat(res.getTotalItems(), is(6));

        //
        restListRequest.setPageSize(4);
        restListRequest.setPage(1);
        res = this.groupService.getGroups(restListRequest);
        assertThat(res.getPage(), is(1));
        assertThat(res.getPageSize(), is(4));
        assertThat(res.getLastPage(), is(2));
        assertThat(res.getTotalItems(), is(6));
        //
        restListRequest.setPageSize(4);
        restListRequest.setPage(1000);
        res = this.groupService.getGroups(restListRequest);
        assertThat(res.getPage(), is(1000));
        assertThat(res.getPageSize(), is(4));
        assertThat(res.getLastPage(), is(2));
        assertThat(res.getTotalItems(), is(6));
    }

    @Test
    void testGetGroups_filter() throws JsonProcessingException {
        RestListRequest restListRequest = new RestListRequest();
        restListRequest.addFilter(new Filter("groupname", "fr"));

        PagedMetadata<GroupDto> res = this.groupService.getGroups(restListRequest);
        assertThat(res.getPage(), is(1));
        assertThat(res.getPageSize(), is(100));
        assertThat(res.getLastPage(), is(1));
        assertThat(res.getTotalItems(), is(1));
    }

    @Test
    void testGetGroups_filter_and_pagination() throws JsonProcessingException {
        RestListRequest restListRequest = new RestListRequest();
        restListRequest.setPageSize(2);
        restListRequest.addFilter(new Filter("groupname", "s"));

        PagedMetadata<GroupDto> res = this.groupService.getGroups(restListRequest);
        assertThat(res.getPage(), is(1));
        assertThat(res.getPageSize(), is(2));
        assertThat(res.getLastPage(), is(2));
        assertThat(res.getTotalItems(), is(3));

        restListRequest.setPage(1);
        res = this.groupService.getGroups(restListRequest);
        assertThat(res.getPage(), is(1));
        assertThat(res.getPageSize(), is(2));
        assertThat(res.getLastPage(), is(2));
        assertThat(res.getTotalItems(), is(3));

        restListRequest.setPage(2);
        res = this.groupService.getGroups(restListRequest);
        assertThat(res.getPage(), is(2));
        assertThat(res.getPageSize(), is(2));
        assertThat(res.getLastPage(), is(2));
        assertThat(res.getTotalItems(), is(3));
    }

}

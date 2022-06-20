package org.entando.entando.aps.system.services.entity;

import com.agiletec.aps.system.common.FieldSearchFilter;
import com.agiletec.aps.system.common.entity.IEntityManager;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.parse.IApsEntityDOM;
import com.google.common.collect.*;
import org.entando.entando.aps.system.services.entity.model.EntityTypeShortDto;
import org.entando.entando.aps.system.services.userprofile.model.UserProfile;
import org.entando.entando.web.common.model.*;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AbstractEntityTypeServiceTest {

    private static final String ENTITY_MANAGER_CODE = "TEST_MANAGER";

    @Spy
    AbstractEntityTypeService service;

    @Mock
    IEntityManager entityManager;

    @Mock
    IApsEntityDOM entityDom;

    @BeforeEach
    public void setUp() {
        service.setEntityManagers(ImmutableList.of(entityManager));
    }

    @Test
    void getShortEntityTypesFilterWorks() {
        UserProfile user2 = createUserProfile("user2");
        UserProfile user1 = createUserProfile("USER1");
        mockUserEntities(user1, user2);

        RestListRequest requestList = new RestListRequest();
        Filter filter = new Filter();
        filter.setAttribute("id");
        filter.setValue("user");
        requestList.addFilter(filter);

        checkResult(requestList, new EntityTypeShortDto(user1), new EntityTypeShortDto(user2));
    }

    @Test
    void getShortEntityTypesFilterWorksReversed() {
        UserProfile user2 = createUserProfile("user2");
        UserProfile user1 = createUserProfile("USER1");
        mockUserEntities(user1, user2);

        RestListRequest requestList = new RestListRequest();
        Filter filter = new Filter();
        filter.setAttribute("id");
        filter.setValue("user");
        requestList.addFilter(filter);

        requestList.setDirection(FieldSearchFilter.Order.DESC.toString());

        checkResult(requestList, new EntityTypeShortDto(user2), new EntityTypeShortDto(user1));
    }

    @Test
    void getShortEntityTypesFilterWorksWithEqualOperator() {
        UserProfile user2 = createUserProfile("user2");
        UserProfile user1 = createUserProfile("USER1");
        mockUserEntities(user1, user2);

        RestListRequest requestList = new RestListRequest();
        Filter filter = new Filter();
        filter.setAttribute("id");
        filter.setValue("user2");
        filter.setOperator(FilterOperator.EQUAL.getValue());
        requestList.addFilter(filter);

        checkResult(requestList, new EntityTypeShortDto(user2));
    }

    @Test
    void getShortEntityTypesFilterWorksWithOrCondition() {
        UserProfile user2 = createUserProfile("user2");
        UserProfile user1 = createUserProfile("USER1");
        mockUserEntities(user1, user2);

        RestListRequest requestList = new RestListRequest();
        Filter filter = new Filter();
        filter.setAttribute("id");
        filter.setAllowedValues(new String[]{"user1", "user2"});
        requestList.addFilter(filter);

        checkResult(requestList, new EntityTypeShortDto(user1), new EntityTypeShortDto(user2));
    }

    private UserProfile createUserProfile(String userId) {
        when(entityDom.clone()).thenReturn(entityDom);

        UserProfile userProfile = new UserProfile();
        userProfile.setId(userId);
        userProfile.setEntityDOM(entityDom);
        userProfile.setTypeCode(userId);
        return userProfile;
    }

    private void mockUserEntities(UserProfile user1, UserProfile user2) {

        Map<String, IApsEntity> mapOfEntities = ImmutableMap.of(
                "B", user2,
                "A", user1,
                "C", createUserProfile("xyz")
        );

        when(entityManager.getName()).thenReturn(ENTITY_MANAGER_CODE);
        when(entityManager.getEntityPrototypes()).thenReturn(mapOfEntities);
    }

    private void checkResult(RestListRequest requestList, EntityTypeShortDto... items) {
        PagedMetadata entities = service.getShortEntityTypes(ENTITY_MANAGER_CODE, requestList);

        assertThat(entities).isNotNull();
        assertThat(entities.getTotalItems()).isEqualTo(items.length);
        //noinspection unchecked
        assertThat(entities.getBody()).containsExactly(items);
    }
}

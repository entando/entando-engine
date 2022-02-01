package org.entando.entando.aps.system.services.label;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.entando.entando.ent.exception.EntException;
import com.agiletec.aps.system.services.i18n.I18nManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.util.ApsProperties;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.aps.system.services.label.model.LabelDto;
import org.entando.entando.web.common.exceptions.ValidationConflictException;
import org.entando.entando.web.common.model.Filter;
import org.entando.entando.web.common.model.FilterOperator;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LabelServiceTest {

    @Mock private I18nManager i18nManager;
    @Mock private ILangManager langManager;

    private LabelService labelService;
    private Lang lang;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        labelService = new LabelService();
        labelService.setI18nManager(i18nManager);
        labelService.setLangManager(langManager);
        lang = new Lang();
        lang.setCode("EN");
    }

    @Test
    void testGetLabelGroupsFilteringEqual() {
        RestListRequest request = new RestListRequest();
        Filter filter = new Filter("value", "some_value", FilterOperator.EQUAL.getValue());

        when(i18nManager.getLabelGroups()).thenReturn(singletonMap("EN", create(singletonMap("EN", "some_value"))));

        request.setFilters(new Filter [] { filter });
        PagedMetadata<LabelDto> labelGroups = labelService.getLabelGroups(request);

        assertThat(labelGroups.getBody()).hasSize(1);
        assertThat(labelGroups.getBody().get(0).getKey()).isEqualTo("EN");
        assertThat(labelGroups.getBody().get(0).getTitles().get("EN")).isEqualTo("some_value");

        verify(i18nManager, times(1)).getLabelGroups();

        reset(i18nManager);
        when(i18nManager.getLabelGroups()).thenReturn(singletonMap("EN", create(singletonMap("EN", "some_value"))));

        filter = new Filter("value", "some_", FilterOperator.EQUAL.getValue());
        request.setFilters(new Filter [] { filter });

        labelGroups = labelService.getLabelGroups(request);

        assertThat(labelGroups.getBody()).hasSize(0);
        assertThat(labelGroups.getTotalItems()).isZero();
    }

    @Test
    void testGetLabelGroupsFilteringLike() {
        RestListRequest request = new RestListRequest();
        Filter filter = new Filter("value", "some_value", FilterOperator.LIKE.getValue());

        when(i18nManager.getLabelGroups()).thenReturn(singletonMap("EN", create(singletonMap("EN", "some_value"))));

        request.setFilters(new Filter [] { filter });
        PagedMetadata<LabelDto> labelGroups = labelService.getLabelGroups(request);

        assertThat(labelGroups.getBody()).hasSize(1);
        assertThat(labelGroups.getBody().get(0).getKey()).isEqualTo("EN");
        assertThat(labelGroups.getBody().get(0).getTitles()).containsOnly(entry("EN", "some_value"));

        verify(i18nManager, times(1)).getLabelGroups();

        reset(i18nManager);
        when(i18nManager.getLabelGroups()).thenReturn(singletonMap("EN", create(singletonMap("EN", "some_value"))));

        filter = new Filter("value", "some_", FilterOperator.LIKE.getValue());
        request.setFilters(new Filter [] { filter });

        labelGroups = labelService.getLabelGroups(request);

        assertThat(labelGroups.getBody()).hasSize(1);
        assertThat(labelGroups.getBody().get(0).getKey()).isEqualTo("EN");
        assertThat(labelGroups.getBody().get(0).getTitles()).containsOnly(entry("EN", "some_value"));
        assertThat(labelGroups.getTotalItems()).isEqualTo(1);
    }

    @Test
    void testGetLabelGroupNotFound() throws EntException {
        when(i18nManager.getLabelGroup(eq("not_found"))).thenReturn(null);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            labelService.getLabelGroup("not_found");
            verify(i18nManager, times(1)).getLabelGroup(eq("not_found"));
        });
    }

    @Test
    void testGetLabelGroup() throws EntException {
        when(i18nManager.getLabelGroup(eq("lab"))).thenReturn(create(singletonMap("EN", "some_value")));
        final LabelDto label = labelService.getLabelGroup("lab");
        assertThat(label.getKey()).isEqualTo("lab");
        assertThat(label.getTitles()).hasSize(1);
        assertThat(label.getTitles()).containsOnly(entry("EN", "some_value"));
        verify(i18nManager, times(1)).getLabelGroup(eq("lab"));
    }

    @Test
    void testAddLabelGroupError() throws EntException {
        when(langManager.getDefaultLang()).thenReturn(lang);
        when(langManager.getAssignableLangs()).thenReturn(singletonList(lang));
        when(langManager.getLangs()).thenReturn(singletonList(lang));

        doThrow(EntException.class).when(i18nManager).addLabelGroup(anyString(), any(ApsProperties.class));
        Assertions.assertThrows(RestServerError.class, () -> {
            final LabelDto label = new LabelDto("lab", singletonMap("EN", "some_value"));
            labelService.addLabelGroup(label);
        });
    }

    @Test
    void testAddLabelGroupNotAssignableLang() throws EntException {
        when(langManager.getDefaultLang()).thenReturn(lang);
        when(langManager.getAssignableLangs()).thenReturn(Collections.emptyList());
        when(langManager.getLangs()).thenReturn(singletonList(lang));
        Assertions.assertThrows(ValidationConflictException.class, () -> {
            final LabelDto label = new LabelDto("lab", singletonMap("EN", "some_value"));
            labelService.addLabelGroup(label);
        });
    }

    @Test
    void testAddLabelGroup() throws EntException {
        when(langManager.getDefaultLang()).thenReturn(lang);
        when(langManager.getAssignableLangs()).thenReturn(singletonList(lang));
        when(langManager.getLangs()).thenReturn(singletonList(lang));

        final LabelDto label = new LabelDto("lab", singletonMap("EN", "some_value"));
        final LabelDto labelResult = labelService.addLabelGroup(label);
        ArgumentCaptor<ApsProperties> captor = ArgumentCaptor.forClass(ApsProperties.class);
        verify(i18nManager, times(1)).addLabelGroup(eq("lab"), captor.capture());

        final ApsProperties value = captor.getValue();
        assertThat(value.getProperty("EN")).isEqualTo("some_value");

        assertThat(labelResult.getKey()).isEqualTo("lab");
        assertThat(labelResult.getTitles()).hasSize(1);
        assertThat(labelResult.getTitles()).containsOnly(entry("EN", "some_value"));
    }


    @Test
    void addExistingLabelShouldReturnTheReceivedLabel() throws EntException {

        String value = "some_value";
        String key = "lab";

        when(langManager.getDefaultLang()).thenReturn(lang);
        when(langManager.getAssignableLangs()).thenReturn(singletonList(lang));
        when(langManager.getLangs()).thenReturn(singletonList(lang));

        ApsProperties existinglabels = new ApsProperties();
        existinglabels.put(key, singletonMap("EN", value));
        when(i18nManager.getLabelGroup(key)).thenReturn(existinglabels);

        final LabelDto label = new LabelDto(key, singletonMap("EN", value));

        LabelDto actualLabelDto = labelService.addLabelGroup(label);

        verify(i18nManager, times(0)).addLabelGroup(anyString(), any());
        LabelTestHelper.assertLabelsDtoEquals(label, actualLabelDto);
    }


    @Test
    void addExistingLabelGroupWithDifferentValuesShouldThrowValidationConflictException() throws EntException {

        when(langManager.getDefaultLang()).thenReturn(lang);
        Mockito.lenient().when(langManager.getAssignableLangs()).thenReturn(singletonList(lang));
        Mockito.lenient().when(langManager.getLangs()).thenReturn(singletonList(lang));

        ApsProperties existinglabels = LabelTestHelper.stubTestApsProperties();
        existinglabels.put(LabelTestHelper.LABEL_KEY, singletonMap(LabelTestHelper.KEY, "some_old_value"));
        Mockito.lenient().when(i18nManager.getLabelGroup(LabelTestHelper.LABEL_KEY)).thenReturn(existinglabels);

        Assertions.assertThrows(ValidationConflictException.class, () -> {
            final LabelDto label = LabelTestHelper.stubTestLabelDto();
            labelService.addLabelGroup(label);
        });
    }

    @Test
    void addExistingLabelGroupWithMoreValuesShouldThrowValidationConflictException() throws EntException {

        when(langManager.getDefaultLang()).thenReturn(lang);
        Mockito.lenient().when(langManager.getAssignableLangs()).thenReturn(singletonList(lang));
        Mockito.lenient().when(langManager.getLangs()).thenReturn(singletonList(lang));

        ApsProperties existinglabels = LabelTestHelper.stubTestApsProperties();
        existinglabels.put(LabelTestHelper.LABEL_KEY, singletonMap(LabelTestHelper.KEY, "some_old_value"));
        Mockito.lenient().when(i18nManager.getLabelGroup(LabelTestHelper.LABEL_KEY)).thenReturn(existinglabels);

        Assertions.assertThrows(ValidationConflictException.class, () -> {
            final LabelDto label = LabelTestHelper.stubTestLabelDto();
            label.getTitles().put("en", "test");

            labelService.addLabelGroup(label);
        });
    }

    @Test
    void testGetLabelsPagination() {
        RestListRequest request = new RestListRequest();
        Map<String, String> labelsMap = new HashMap<>();

        int pageSize = 5;
        int totalItems = 14;
        HashMap<String,ApsProperties> apsPropertiesMap = new HashMap();
        for(int i=0; i<totalItems; i++) {
            labelsMap.put("it" , "value");
            labelsMap.put("en" , "value");
            apsPropertiesMap.put(String.valueOf(i),create(labelsMap));
        }

        when(i18nManager.getLabelGroups()).thenReturn(apsPropertiesMap);

        int page=1;
        request.setPage(page);
        request.setPageSize(pageSize);
        PagedMetadata<LabelDto> labelGroups = labelService.getLabelGroups(request);
        assertThat(labelGroups.getBody()).hasSize(pageSize);
        assertThat(labelGroups.getTotalItems()).isEqualTo(totalItems);
        assertThat(labelGroups.getPage()).isEqualTo(page);

        page=2;
        request.setPage(page);
        request.setPageSize(pageSize);
        labelGroups = labelService.getLabelGroups(request);
        assertThat(labelGroups.getBody()).hasSize(pageSize);
        assertThat(labelGroups.getTotalItems()).isEqualTo(totalItems);
        assertThat(labelGroups.getPage()).isEqualTo(page);

        page=3;
        request.setPage(page);
        request.setPageSize(pageSize);
        labelGroups = labelService.getLabelGroups(request);
        assertThat(labelGroups.getBody()).hasSize(totalItems - (pageSize * (page-1)));
        assertThat(labelGroups.getTotalItems()).isEqualTo(totalItems);
        assertThat(labelGroups.getPage()).isEqualTo(page);
    }

    @Test
    void testUpdateLabelGroup() throws EntException {
        when(langManager.getDefaultLang()).thenReturn(lang);
        when(langManager.getAssignableLangs()).thenReturn(singletonList(lang));
        when(langManager.getLangs()).thenReturn(singletonList(lang));

        when(i18nManager.getLabelGroup(eq("lab"))).thenReturn(create(singletonMap("EN", "some_value")));

        final LabelDto label = new LabelDto("lab", singletonMap("EN", "another_value"));
        final LabelDto labelResult = labelService.updateLabelGroup(label);
        ArgumentCaptor<ApsProperties> captor = ArgumentCaptor.forClass(ApsProperties.class);

        verify(i18nManager, times(1)).getLabelGroup(eq("lab"));
        verify(i18nManager, times(1)).updateLabelGroup(eq("lab"), captor.capture());

        final ApsProperties value = captor.getValue();
        assertThat(value.getProperty("EN")).isEqualTo("another_value");

        assertThat(labelResult.getKey()).isEqualTo("lab");
        assertThat(labelResult.getTitles()).hasSize(1);
        assertThat(labelResult.getTitles()).containsOnly(entry("EN", "another_value"));
    }

    private ApsProperties create(final Map<String, String> value) {
        final ApsProperties properties = new ApsProperties();
        value.keySet().forEach(item -> properties.setProperty(item, value.get(item)));
        return properties;
    }

}
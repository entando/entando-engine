package org.entando.entando.aps.system.services.pagemodel;

import com.agiletec.aps.system.common.model.dao.SearcherDaoPaginatedResult;
import com.agiletec.aps.system.services.pagemodel.Frame;
import com.agiletec.aps.system.services.pagemodel.IPageModelManager;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import org.entando.entando.aps.system.services.assertionhelper.PageModelAssertionHelper;
import org.entando.entando.aps.system.services.mockhelper.PageMockHelper;
import org.entando.entando.aps.system.services.page.IPageService;
import org.entando.entando.aps.system.services.page.model.PageDto;
import org.entando.entando.aps.system.services.pagemodel.model.PageModelDto;
import org.entando.entando.aps.system.services.pagemodel.model.PageModelDtoBuilder;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.web.common.assembler.PagedMetadataMapper;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.component.ComponentUsageEntity;
import org.entando.entando.web.page.model.PageSearchRequest;
import org.entando.entando.web.pagemodel.model.PageModelConfigurationRequest;
import org.entando.entando.web.pagemodel.model.PageModelFrameReq;
import org.entando.entando.web.pagemodel.model.PageModelRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.entando.entando.aps.system.services.pagemodel.PageModelTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PageModelServiceTest {

    private static final int DEFAULT_MAIN_FRAME = -1;
    private static final String PAGE_MODEL_CODE = "TEST_PM_CODE";

    private static final RestListRequest EMPTY_REQUEST = new RestListRequest();

    @Mock
    private IPageModelManager pageModelManager;

    @Mock
    private IWidgetTypeManager widgetTypeManager;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private PageModelServiceUtilizer pageModelServiceUtilizer;
    
    @Mock
    private PagedMetadataMapper pagedMetadataMapper;

    private PageModelDtoBuilder dtoBuilder;

    private PageModelService pageModelService;

    @BeforeEach
    public void setUp() throws Exception {
        dtoBuilder = new PageModelDtoBuilder();
        pageModelService = new PageModelService(pageModelManager, widgetTypeManager, dtoBuilder);
        pageModelService.setApplicationContext(applicationContext);
        Field pagedMetadataMapper = ReflectionUtils.findField(pageModelService.getClass(), "pagedMetadataMapper");
        pagedMetadataMapper.setAccessible(true);
        pagedMetadataMapper.set(pageModelService, this.pagedMetadataMapper);
    }

    @Test 
    void addPageModelCallsPageModelManager() throws Exception {
        WidgetType mockType = Mockito.mock(WidgetType.class);
        when(mockType.hasParameter(Mockito.anyString())).thenReturn(true);
        when(widgetTypeManager.getWidgetType(Mockito.anyString())).thenReturn(mockType);
        PageModelRequest pageModelRequest = validPageModelRequest();
        PageModelDto result = pageModelService.addPageModel(pageModelRequest);
        verify(pageModelManager, Mockito.times(1)).addPageModel(Mockito.any(PageModel.class));
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo(pageModelRequest.getCode());
        assertThat(result.getDescr()).isEqualTo(pageModelRequest.getDescr());
        assertThat(result.getPluginCode()).isEqualTo(pageModelRequest.getPluginCode());
        assertThat(result.getMainFrame()).isEqualTo(DEFAULT_MAIN_FRAME);
    }

    @Test 
    void get_page_models_returns_page_models() throws EntException {
        when(pageModelManager.searchPageModels(any())).thenReturn(pageModels());
        PagedMetadata<PageModelDto> result = pageModelService.getPageModels(EMPTY_REQUEST, null);
        PagedMetadata<PageModelDto> expected = resultPagedMetadata();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getPageModelUsageForNonExistingCodeShouldReturnZero() {

        int componentUsage = pageModelService.getComponentUsage("non_existing");
        assertEquals(0, componentUsage);
    }

    @Test
    void getPageModelUsageTest() {
        String managerName = "PageManager";
        PageModel pageModel = PageMockHelper.mockServicePageModel();
        PageDto pageDto = PageMockHelper.mockPageDto();
        Map<String, Object> pageModelServiceUtilizerMap = new HashMap<>();
        pageModelServiceUtilizerMap.put(managerName, pageModelServiceUtilizer);
        when(pageModelManager.getPageModel(Mockito.anyString())).thenReturn(pageModel);
        when(applicationContext.getBeansOfType(any())).thenReturn(pageModelServiceUtilizerMap);
        when(pageModelServiceUtilizer.getManagerName()).thenReturn(managerName);
        when(pageModelServiceUtilizer.getPageModelUtilizer(Mockito.anyString())).thenReturn(Collections.singletonList(pageDto));
        RestListRequest restListRequest = new RestListRequest();
        restListRequest.setPageSize(1);
        List<ComponentUsageEntity> componentUsageEntityList = Arrays.asList(new ComponentUsageEntity(ComponentUsageEntity.TYPE_PAGE, PageMockHelper.PAGE_CODE, IPageService.STATUS_ONLINE));
        PagedMetadata pagedMetadata = new PagedMetadata(restListRequest, componentUsageEntityList, 1);
        pagedMetadata.setPageSize(1);
        pagedMetadata.setPage(1);
        pagedMetadata.imposeLimits();
        when(pagedMetadataMapper.getPagedResult(any(), any())).thenReturn(pagedMetadata);
        PagedMetadata<ComponentUsageEntity> usageDetails = pageModelService.getComponentUsageDetails(pageModel.getCode(), new PageSearchRequest(pageModel.getCode()));
        PageModelAssertionHelper.assertUsageDetails(usageDetails);
    }


    @Test
    void shouldCreateTheRightPageModel() {
        String expectedTemplate = "<#assign wp=JspTaglibs[ \"/aps-core\"]>\n"
                + "<script nonce=\"<@wp.cspNonce />\">my_js_script</script>";

        PageModelRequest pageModelRequest = validPageModelRequest();
        pageModelRequest.getConfiguration().getFrames().get(0).setMainFrame(true);
        PageModel pageModel = pageModelService.createPageModel(pageModelRequest);

        assertThat(pageModel).isNotNull();
        assertThat(pageModel.getCode()).isEqualTo(pageModelRequest.getCode());
        assertThat(pageModel.getDescription()).isEqualTo(pageModelRequest.getDescr());
        assertThat(pageModel.getPluginCode()).isEqualTo(pageModelRequest.getPluginCode());
        assertThat(pageModel.getMainFrame()).isZero();
        assertThat(pageModel.getTemplate()).isEqualTo(expectedTemplate);
        assertThat(pageModel.getConfiguration()).hasSize(pageModelRequest.getConfiguration().getFrames().size());
    }

    @Test
    void shouldUpdatePageModel() throws Exception {
        String expectedTemplate = "<#assign wp=JspTaglibs[ \"/aps-core\"]>\n"
                + "<script nonce=\"<@wp.cspNonce />\">my_js_script</script>";

        when(widgetTypeManager.getWidgetType(any())).thenReturn(createDefaultWidgetType());
        when(pageModelManager.getPageModel(any())).thenReturn(validPageModel());

        PageModelRequest pageModelRequest = validPageModelRequest();
        pageModelRequest.getConfiguration().getFrames().get(0).setMainFrame(true);
        PageModelDto pageModel = pageModelService.updatePageModel(pageModelRequest);

        assertThat(pageModel).isNotNull();
        assertThat(pageModel.getCode()).isEqualTo(pageModelRequest.getCode());
        assertThat(pageModel.getDescr()).isEqualTo(pageModelRequest.getDescr());
        assertThat(pageModel.getPluginCode()).isEqualTo(pageModelRequest.getPluginCode());
        assertThat(pageModel.getMainFrame()).isZero();
        assertThat(pageModel.getTemplate()).isEqualTo(expectedTemplate);
        assertThat(pageModel.getConfiguration().getFrames()).hasSize(pageModelRequest.getConfiguration().getFrames().size());
    }

    @Test
    void shouldUpdatePageModelWithNullDefaultWidgetConfig() throws Exception {
        String expectedTemplate = "<#assign wp=JspTaglibs[ \"/aps-core\"]>\n"
                + "<script nonce=\"<@wp.cspNonce />\">my_js_script</script>";

        when(widgetTypeManager.getWidgetType(any())).thenReturn(createDefaultWidgetType());
        when(pageModelManager.getPageModel(any())).thenReturn(validPageModelDefaultWidgetConfigNull());

        PageModelRequest pageModelRequest = validPageModelRequestDefaultWidgetConfigNull();
        pageModelRequest.getConfiguration().getFrames().get(0).setMainFrame(true);
        PageModelDto pageModel = pageModelService.updatePageModel(pageModelRequest);
        assertNull(pageModel.getConfiguration().getFrames().get(0).getDefaultWidget().getProperties());
        assertThat(pageModel).isNotNull();
        assertThat(pageModel.getCode()).isEqualTo(pageModelRequest.getCode());
        assertThat(pageModel.getDescr()).isEqualTo(pageModelRequest.getDescr());
        assertThat(pageModel.getPluginCode()).isEqualTo(pageModelRequest.getPluginCode());
        assertThat(pageModel.getMainFrame()).isZero();
        assertThat(pageModel.getTemplate()).isEqualTo(expectedTemplate);
        assertThat(pageModel.getConfiguration().getFrames()).hasSize(pageModelRequest.getConfiguration().getFrames().size());
        assertThat(pageModel.getConfiguration().getFrames()).hasSize(pageModelRequest.getConfiguration().getFrames().size());
    }

    @Test
    void shouldNotChangeTemplateWithNonce() throws Exception {
        String expectedTemplate = "<#assign wp=JspTaglibs[ \"/aps-core\"]>\n"
                + "<script nonce=\"<@wp.cspNonce />\">my_js_script</script>";

        when(widgetTypeManager.getWidgetType(any())).thenReturn(createDefaultWidgetType());
        when(pageModelManager.getPageModel(any())).thenReturn(validPageModel());

        PageModelRequest pageModelRequest = validPageModelRequest();
        pageModelRequest.setTemplate(expectedTemplate);
        pageModelRequest.getConfiguration().getFrames().get(0).setMainFrame(true);
        PageModelDto pageModel = pageModelService.updatePageModel(pageModelRequest);

        assertThat(pageModel).isNotNull();
        assertThat(pageModel.getCode()).isEqualTo(pageModelRequest.getCode());
        assertThat(pageModel.getDescr()).isEqualTo(pageModelRequest.getDescr());
        assertThat(pageModel.getPluginCode()).isEqualTo(pageModelRequest.getPluginCode());
        assertThat(pageModel.getMainFrame()).isZero();
        assertThat(pageModel.getTemplate()).isEqualTo(expectedTemplate);
        assertThat(pageModel.getConfiguration().getFrames()).hasSize(pageModelRequest.getConfiguration().getFrames().size());

        verify(pageModelManager, times(1)).updatePageModel(any());
    }


    private PagedMetadata<PageModelDto> resultPagedMetadata() {
        RestListRequest request = new RestListRequest();
        return new PagedMetadata<>(request, asList(dtoBuilder.convert(pageModel())), 1);
    }

    private static SearcherDaoPaginatedResult<PageModel> pageModels() {
        return new SearcherDaoPaginatedResult<>(asList(pageModel()));
    }

    private static PageModel pageModel() {
        PageModel localPageModel = new PageModel();
        localPageModel.setCode(PAGE_MODEL_CODE);
        return localPageModel;
    }

    private static PageModel pageModelFrom(PageModelRequest pageModelRequest) {
        Frame[] frames = framesFrom(pageModelRequest.getConfiguration());
        PageModel pageModel = new PageModel();
        pageModel.setCode(pageModelRequest.getCode());
        pageModel.setDescription(pageModelRequest.getDescr());
        pageModel.setConfiguration(frames);
        return pageModel;
    }

    private static Frame[] framesFrom(PageModelConfigurationRequest configuration) {
        List<PageModelFrameReq> requestFrames = configuration.getFrames();
        if (requestFrames == null) {
            return new Frame[]{};
        }
        Frame[] frames = new Frame[requestFrames.size()];
        for (int i = 0; i < requestFrames.size(); i++) {
            frames[i] = frameFrom(requestFrames.get(i));
        }
        return frames;
    }

    private static Frame frameFrom(PageModelFrameReq request) {
        Frame frame = new Frame();
        frame.setDescription(request.getDescr());
        return frame;
    }
    
}

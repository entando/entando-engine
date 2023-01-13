package org.entando.entando.aps.util;

import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import java.util.List;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.aps.system.services.widgettype.WidgetTypeParameter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PageUtilsTest {

    private static final String WIDGET_CODE = "content_viewer";

    @Mock
    private IWidgetTypeManager widgetTypeManager;

    @Test
    void testIsDraftFreeViewerPageNullPageModel() {
        Assertions.assertFalse(PageUtils.isDraftFreeViewerPage(getPage(), null, null, widgetTypeManager));
    }

    @Test
    void testIsDraftFreeViewerPageWidgetType() {
        Assertions.assertFalse(PageUtils.isDraftFreeViewerPage(getPage(), getPageModel(), null, widgetTypeManager));
    }

    @Test
    void testIsDraftFreeViewerPage() {
        Mockito.when(widgetTypeManager.getWidgetType(WIDGET_CODE)).thenReturn(getContentViewerWidgetType());
        Assertions.assertTrue(
                PageUtils.isDraftFreeViewerPage(getPage(), getPageModel(), WIDGET_CODE, widgetTypeManager));
    }

    private PageModel getPageModel() {
        PageModel pageModel = new PageModel();
        pageModel.setMainFrame(0);
        return pageModel;
    }

    private IPage getPage() {
        IPage page = Mockito.mock(IPage.class);
        Widget widget = new Widget();
        widget.setTypeCode(WIDGET_CODE);
        Mockito.when(page.getWidgets()).thenReturn(new Widget[]{widget});
        return page;
    }

    private WidgetType getContentViewerWidgetType() {
        WidgetType widgetType = new WidgetType();
        widgetType.setCode(WIDGET_CODE);
        widgetType.setAction("viewerConfig");
        WidgetTypeParameter param1 = new WidgetTypeParameter();
        widgetType.setTypeParameters(List.of(param1));
        return widgetType;
    }
}

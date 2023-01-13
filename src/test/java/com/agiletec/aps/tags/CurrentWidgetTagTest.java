package com.agiletec.aps.tags;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.util.ApsProperties;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.aps.system.services.widgettype.WidgetTypeManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(MockitoExtension.class)
class CurrentWidgetTagTest {

    private static final String WIDGET_CODE = "my-widget";

    private MockServletContext mockServletContext;
    private MockPageContext mockPageContext;

    @Mock
    private RequestContext requestContext;

    @BeforeEach
    void setUp() {
        mockServletContext = new MockServletContext();
        mockPageContext = new MockPageContext(mockServletContext);
        mockPageContext.getRequest().setAttribute(RequestContext.REQCTX, requestContext);

        Widget widget = new Widget();
        widget.setTypeCode(WIDGET_CODE);
        Mockito.when(requestContext.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_WIDGET)).thenReturn(widget);
    }

    @Test
    void testCurrentWidgetCode() throws Exception {
        CurrentWidgetTag currentWidgetTag = new CurrentWidgetTag();
        currentWidgetTag.setPageContext(mockPageContext);
        currentWidgetTag.setParam("code");
        currentWidgetTag.doStartTag();

        String output = ((MockHttpServletResponse) mockPageContext.getResponse()).getContentAsString();
        Assertions.assertEquals(WIDGET_CODE, output);
    }

    @Test
    void testCurrentWidgetTitle() throws Exception {
        Lang currentLang = new Lang();
        currentLang.setCode("en");
        Mockito.when(requestContext.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG)).thenReturn(currentLang);

        WebApplicationContext webApplicationContext = Mockito.mock(WebApplicationContext.class);
        mockServletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                webApplicationContext);
        WidgetTypeManager widgetTypeManager = Mockito.mock(WidgetTypeManager.class);
        Mockito.when(webApplicationContext.getBean(SystemConstants.WIDGET_TYPE_MANAGER)).thenReturn(widgetTypeManager);
        WidgetType widgetType = new WidgetType();
        widgetType.setCode(WIDGET_CODE);
        ApsProperties titles = new ApsProperties();
        titles.setProperty("en", "Widget Title");
        widgetType.setTitles(titles);
        Mockito.when(widgetTypeManager.getWidgetType(WIDGET_CODE)).thenReturn(widgetType);

        CurrentWidgetTag currentWidgetTag = new CurrentWidgetTag();
        currentWidgetTag.setPageContext(mockPageContext);
        currentWidgetTag.setParam("title");
        currentWidgetTag.doStartTag();

        String output = ((MockHttpServletResponse) mockPageContext.getResponse()).getContentAsString();
        Assertions.assertEquals("Widget Title", output);
    }
}

package com.agiletec.aps.tags;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.util.ApsProperties;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.aps.system.services.widgettype.WidgetTypeManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(MockitoExtension.class)
class InternalServletTagTest {

    private static final String WIDGET_CODE = "my-widget";

    private MockServletContext mockServletContext;
    private MockPageContext mockPageContext;
    private MockHttpServletRequest mockHttpServletRequest;

    @Mock
    private RequestContext requestContext;

    @Mock
    private WidgetTypeManager widgetTypeManager;

    @BeforeEach
    void setUp() {
        mockServletContext = new MockServletContext();
        mockPageContext = new MockPageContext(mockServletContext);
        mockHttpServletRequest = (MockHttpServletRequest) mockPageContext.getRequest();
        mockHttpServletRequest.setAttribute(RequestContext.REQCTX, requestContext);

        WebApplicationContext webApplicationContext = Mockito.mock(WebApplicationContext.class);
        mockServletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                webApplicationContext);
        Mockito.when(webApplicationContext.getBean(SystemConstants.WIDGET_TYPE_MANAGER)).thenReturn(widgetTypeManager);

        Mockito.when(requestContext.getRequest()).thenReturn(mockHttpServletRequest);
    }

    @Test
    void testActionPathFromConfig() throws Exception {
        WidgetType parentWidgetType = new WidgetType();
        parentWidgetType.setCode("parent-type");
        WidgetType widgetType = new WidgetType();
        widgetType.setCode(WIDGET_CODE);
        widgetType.setParentType(parentWidgetType);
        ApsProperties config = new ApsProperties();
        config.setProperty(InternalServletTag.CONFIG_PARAM_ACTIONPATH, "/config-path");
        widgetType.setConfig(config);
        Mockito.when(widgetTypeManager.getWidgetType(WIDGET_CODE)).thenReturn(widgetType);

        Widget widget = new Widget();
        widget.setTypeCode(WIDGET_CODE);
        Mockito.when(requestContext.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_WIDGET)).thenReturn(widget);

        mockHttpServletRequest.setParameter(InternalServletTag.REQUEST_PARAM_ACTIONPATH, "/req-path");
        mockHttpServletRequest.setParameter(InternalServletTag.REQUEST_PARAM_FRAMEDEST, "1");
        Mockito.when(requestContext.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME)).thenReturn(1);

        InternalServletTag internalServletTag = new InternalServletTag();
        internalServletTag.setPageContext(mockPageContext);
        internalServletTag.doStartTag();
        internalServletTag.doEndTag();
    }
}

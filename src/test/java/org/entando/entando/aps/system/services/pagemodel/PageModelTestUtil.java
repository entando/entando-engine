/*
 * Copyright 2020-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.entando.aps.system.services.pagemodel;

import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.system.services.pagemodel.Frame;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.entando.entando.aps.system.services.pagemodel.model.FrameDto;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.aps.system.services.widgettype.WidgetTypeParameter;
import org.entando.entando.web.pagemodel.model.PageModelConfigurationRequest;
import org.entando.entando.web.pagemodel.model.PageModelFrameReq;
import org.entando.entando.web.pagemodel.model.PageModelRequest;

public final class PageModelTestUtil {

    private static final String PAGE_MODEL_CODE = "pageModelCode";
    private static final String DESCRIPTION = "description";
    private static final String FRAME_DESCRIPTION = "frame description";
    private static final String PAGE_MODEL_TEMPLATE = "<script>my_js_script</script>";

    private PageModelTestUtil() {
        // No instance - utility class
    }

    public static PageModelRequest validPageModelRequest() {
        PageModelRequest request = new PageModelRequest();
        request.setCode(PAGE_MODEL_CODE);
        request.setDescr(DESCRIPTION);
        request.setTemplate(PAGE_MODEL_TEMPLATE);
        request.setConfiguration(createValidPageModelConfigurationRequest());
        return request;
    }

    public static PageModel validPageModel() throws JsonProcessingException {
        PageModel pageModel = new PageModel();
        pageModel.setCode(PAGE_MODEL_CODE);
        pageModel.setDescription(DESCRIPTION);
        pageModel.setTemplate(PAGE_MODEL_TEMPLATE);
        pageModel.setConfiguration(createValidPageModelConfiguration());
        return pageModel;
    }

    private static PageModelConfigurationRequest createValidPageModelConfigurationRequest() {
        PageModelConfigurationRequest configuration = new PageModelConfigurationRequest();
        List<PageModelFrameReq> frames = new ArrayList<>();
        frames.add(createValidFrameRequest());
        frames.add(new PageModelFrameReq(1, "Position 1"));
        configuration.setFrames(frames);
        return configuration;
    }

    private static Frame[] createValidPageModelConfiguration() throws JsonProcessingException {
        return new Frame[] {
            createValidFrame(0, FRAME_DESCRIPTION),
            createValidFrame(1, "Position 1")
        };
    }

    private static PageModelFrameReq createValidFrameRequest() {
        PageModelFrameReq pageReq = new PageModelFrameReq(0, FRAME_DESCRIPTION);
        pageReq.getDefaultWidget().setCode("leftmenu");
        pageReq.getDefaultWidget().getProperties().put("navSpec", "code(homepage).subtree(5)");
        return pageReq;
    }

    private static Frame createValidFrame(int pos, String description) throws JsonProcessingException{
        Frame frame = new Frame();
        frame.setPos(pos);
        frame.setDescription(description);
        frame.setDefaultWidget(createDefaultWidget());
        return frame;
    }

    private static Widget createDefaultWidget() throws JsonProcessingException {
        Widget defaultWidget = new Widget();
        defaultWidget.setType(createDefaultWidgetType());

        return defaultWidget;
    }

    public static WidgetType createDefaultWidgetType() throws JsonProcessingException {
        WidgetType widgetType = new WidgetType();
        widgetType.setCode("leftmenu");
        widgetType.setMainGroup("group1");
        widgetType.setLocked(true);
        widgetType.setReadonlyPageWidgetConfig(true);
        widgetType.setTypeParameters(Collections.singletonList(
                new WidgetTypeParameter("navSpec", "code(homepage).subtree(5)")));

        return widgetType;
    }
    
}

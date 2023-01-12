/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.util;

import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.PageMetadata;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import java.util.List;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.aps.system.services.widgettype.WidgetTypeParameter;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

/**
 * @author E.Santoboni
 */
public class PageUtils {

    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(PageUtils.class);

    /**
     * Check whether the page can publish free content, related to the draft
     * configuration of the page.
     *
     * @param page The page to check.
     * @param model
     * @param viewerWidgetCode The code of the viewer widget (optional)
     * @param widgetTypeManager
     * @return True if the page can publish free content, false else.
     */
    public static boolean isDraftFreeViewerPage(IPage page, PageModel model, String viewerWidgetCode, IWidgetTypeManager widgetTypeManager) {
        if (page.isOnlineInstance()) {
            logger.warn("this check expects a draft instance of the page");
            return false;
        }
        boolean found = false;
        Widget[] widgets = page.getWidgets();
        if (model != null) {
            found = isFreeViewerPage(model, widgets, viewerWidgetCode, widgetTypeManager);
        }
        return found;
    }

    /**
     * Check whether the page can publish free content, related to the online
     * configuration of the page.
     *
     * @param page The page to check.
     * @param model 
     * @param viewerWidgetCode The code of the viewer widget (optional)
     * @param widgetTypeManager
     * @return True if the page can publish free content, false else.
     */
    public static boolean isOnlineFreeViewerPage(IPage page, PageModel model, String viewerWidgetCode, IWidgetTypeManager widgetTypeManager) {
        if (!page.isOnlineInstance()) {
            logger.warn("this check expects an online instance of the page");
            return false;
        }
        boolean found = false;
        Widget[] widgets = page.getWidgets();
        if (model != null) {
            found = isFreeViewerPage(model, widgets, viewerWidgetCode, widgetTypeManager);
        }
        return found;
    }

    /**
     * Check whether the page can publish free content, related to the model and
     * the widgets of the page.
     *
     * @param model The model of the page to check.
     * @param widgets The widgets of the page to check.
     * @param viewerWidgetCode The code of the viewer widget (optional)
     * @return True if the page can publish free content, false else.
     */
    public static boolean isFreeViewerPage(PageModel model, Widget[] widgets, String viewerWidgetCode, IWidgetTypeManager widgetTypeManager) {
        try {
            if (model != null && widgets != null) {
                int mainFrame = model.getMainFrame();
                if (mainFrame < 0) {
                    return false;
                }
                Widget viewer = widgets[mainFrame];
                if (null == viewer) {
                    return false;
                }
                boolean isRightCode = null == viewerWidgetCode || viewer.getTypeCode().equals(viewerWidgetCode);
                WidgetType type = widgetTypeManager.getWidgetType(viewer.getTypeCode());
                if (null == type) {
                    return false;
                }
                String actionName = type.getAction();
                boolean isRightAction = (null != actionName && actionName.toLowerCase().indexOf("viewer") >= 0);
                List<WidgetTypeParameter> typeParameters = type.getTypeParameters();
                if ((isRightCode || isRightAction) && (null != typeParameters && !typeParameters.isEmpty()) && (null == viewer.getConfig()
                        || viewer.getConfig().isEmpty())) {
                    return true;
                }
            }
        } catch (Throwable t) {
            logger.error("Error while checking page for widget '{}'", viewerWidgetCode, t);
        }
        return false;
    }
}

/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
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

package org.entando.entando.aps.system.services.widgettype.cache;

import com.agiletec.aps.system.exception.ApsSystemException;
import java.util.Collection;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeDAO;
import org.entando.entando.aps.system.services.widgettype.WidgetType;

/**
 * @author E.Santoboni
 */
public interface IWidgetTypeManagerCacheWrapper {

    String WIDGET_TYPE_MANAGER_CACHE_NAME = "Entando_WidgetTypeManager";
    String WIDGET_TYPE_CACHE_NAME_PREFIX = "WidgetTypeManager_type_";
    String WIDGET_TYPE_CODES_CACHE_NAME = "WidgetTypeManager_codes";

    void initCache(IWidgetTypeDAO widgetTypeDAO) throws ApsSystemException;

    WidgetType getWidgetType(String code);

    Collection<WidgetType> getWidgetTypes();

    void addWidgetType(WidgetType type);

    void updateWidgetType(WidgetType type);

    void deleteWidgetType(String code);

}

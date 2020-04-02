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

package org.entando.entando.aps.system.services.dataobject.helper;

import com.agiletec.aps.system.common.entity.helper.IEntityFilterBean;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.services.user.UserDetails;
import java.util.List;

/**
 * @author E.Santoboni
 */
public interface IDataTypeListHelper {

    String CATEGORIES_SEPARATOR = ",";

    /**
     * Restituisce la lista di identificativi di DataType in base ai parametri di ricerca.
     *
     * @param bean Il contenitore delle informazioni base sulla interrogazione da eseguire.
     * @return La lista di identificativi di DataType in base ai parametri di ricerca.
     * @throws Throwable In caso di errore.
     */
    List<String> getDataTypesId(IDataTypeListBean bean, UserDetails user) throws Throwable;

    EntitySearchFilter[] getFilters(String dataType, String filtersShowletParam, String langCode);

    /**
     * @deprecated From Entando 2.0 version 2.4.1. Use getFilter(String dataType, IEntityFilterBean, String) method
     */
    EntitySearchFilter getFilter(String dataType, IDataTypeListFilterBean bean, String langCode);

    EntitySearchFilter getFilter(String dataType, IEntityFilterBean bean, String langCode);

    String getFilterParam(EntitySearchFilter[] filters);

}

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

package org.entando.entando.aps.system.services.dataobjectmodel;

import com.agiletec.aps.system.common.FieldSearchFilter;
import com.agiletec.aps.system.common.model.dao.SearcherDaoPaginatedResult;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.page.IPage;
import java.util.List;
import java.util.Map;
import org.entando.entando.aps.system.services.dataobject.model.SmallDataType;

/**
 * Interfaccia base per i Manager dei modelli di DataObject.
 *
 * @author E.Santoboni
 */
public interface IDataObjectModelManager {

    void addDataObjectModel(DataObjectModel model) throws ApsSystemException;

    void removeDataObjectModel(DataObjectModel model) throws ApsSystemException;

    void updateDataObjectModel(DataObjectModel model) throws ApsSystemException;

    DataObjectModel getDataObjectModel(long dataObjectModelId);

    List<DataObjectModel> getDataObjectModels();

    List<DataObjectModel> getModelsForDataObjectType(String contentType);

    Map<String, List<IPage>> getReferencingPages(long modelId);

    SmallDataType getDefaultUtilizer(long modelId);

    SearcherDaoPaginatedResult<DataObjectModel> getDataObjectModels(List<FieldSearchFilter> fieldSearchFilters)
            throws ApsSystemException;

    List<Long> searchDataObjectModels(FieldSearchFilter[] filters) throws ApsSystemException;

}

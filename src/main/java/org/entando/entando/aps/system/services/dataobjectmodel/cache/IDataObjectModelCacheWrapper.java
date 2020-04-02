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

package org.entando.entando.aps.system.services.dataobjectmodel.cache;

import com.agiletec.aps.system.exception.ApsSystemException;
import java.util.List;
import org.entando.entando.aps.system.services.dataobjectmodel.DataObjectModel;
import org.entando.entando.aps.system.services.dataobjectmodel.IDataObjectModelDAO;

public interface IDataObjectModelCacheWrapper {

    String CACHE_NAME = "Entando_DataObjectModelManager";

    String CACHE_NAME_PREFIX = "DataObjectModelManager_model_";

    String CODES_CACHE_NAME = "DataObjectModelManager_codes";

    void initCache(IDataObjectModelDAO dataObjectModelDAO) throws ApsSystemException;

    DataObjectModel getModel(String code);

    void addModel(DataObjectModel model);

    void updateModel(DataObjectModel model);

    void removeModel(DataObjectModel model);

    List<DataObjectModel> getModels();

}

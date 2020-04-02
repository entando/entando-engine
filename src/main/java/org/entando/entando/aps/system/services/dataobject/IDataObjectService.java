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

package org.entando.entando.aps.system.services.dataobject;

import java.util.List;
import java.util.Map;
import org.entando.entando.aps.system.services.dataobject.model.DataTypeDto;
import org.entando.entando.aps.system.services.entity.model.AttributeTypeDto;
import org.entando.entando.aps.system.services.entity.model.EntityTypeAttributeFullDto;
import org.entando.entando.aps.system.services.entity.model.EntityTypeShortDto;
import org.entando.entando.aps.system.services.entity.model.EntityTypesStatusDto;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.dataobject.model.DataTypeDtoRequest;
import org.springframework.validation.BindingResult;

/**
 * @author E.Santoboni
 */
public interface IDataObjectService {

    PagedMetadata<EntityTypeShortDto> getShortDataTypes(RestListRequest requestList);

    DataTypeDto getDataType(String dataTypeCode);

    DataTypeDto addDataType(DataTypeDtoRequest bodyRequest, BindingResult bindingResult);

    DataTypeDto updateDataType(DataTypeDtoRequest request, BindingResult bindingResult);

    void deleteDataType(String dataTypeCode);

    // ----------------------------------
    PagedMetadata<String> getAttributeTypes(RestListRequest requestList);

    AttributeTypeDto getAttributeType(String attributeCode);

    // ----------------------------------
    EntityTypeAttributeFullDto getDataTypeAttribute(String dataTypeCode, String attributeCode);

    EntityTypeAttributeFullDto addDataTypeAttribute(String dataTypeCode, EntityTypeAttributeFullDto bodyRequest,
            BindingResult bindingResult);

    EntityTypeAttributeFullDto updateDataTypeAttribute(String dataTypeCode, EntityTypeAttributeFullDto bodyRequest,
            BindingResult bindingResult);

    void deleteDataTypeAttribute(String dataTypeCode, String attributeCode);

    void moveDataTypeAttribute(String dataTypeCode, String attributeCode, boolean moveUp);

    void reloadDataTypeReferences(String dataTypeCode);

    Map<String, Integer> reloadDataTypesReferences(List<String> dataTypeCodes);

    EntityTypesStatusDto getDataTypesRefreshStatus();

}

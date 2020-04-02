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

package org.entando.entando.aps.system.services.entity;

import org.entando.entando.aps.system.services.entity.model.EntityManagerDto;
import org.entando.entando.aps.system.services.entity.model.EntityTypeFullDto;
import org.entando.entando.aps.system.services.entity.model.EntityTypeShortDto;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.entity.model.EntityTypeDtoRequest;
import org.springframework.validation.BindingResult;

/**
 * @author E.Santoboni
 */
public interface IEntityManagerService {

    PagedMetadata<String> getEntityManagers(RestListRequest requestList);

    EntityManagerDto getEntityManager(String entityManagerCode);

    PagedMetadata<EntityTypeShortDto> getShortEntityTypes(String entityManagerCode, RestListRequest requestList);

    EntityTypeFullDto getFullEntityType(String entityManagerCode, String entityTypeCode);

    EntityTypeFullDto addEntityType(String entityManagerCode, EntityTypeDtoRequest bodyRequest, BindingResult bindingResult);

    EntityTypeFullDto updateEntityType(String entityManagerCode, EntityTypeDtoRequest request, BindingResult bindingResult);

    void deleteEntityType(String entityManagerCode, String entityTypeCode);

    PagedMetadata<String> getAttributeTypes(String entityManagerCode, RestListRequest requestList);

}

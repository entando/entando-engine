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
package com.agiletec.aps.system.common.entity;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import org.entando.entando.ent.exception.EntException;

/**
 * @author E.Santoboni
 */
public interface IEntityTypesConfigurer {
	
	/**
	 * Add a new entity prototype on the catalog.
	 * @param entityType The entity type to add.
	 * @throws EntException In case of error.
	 */
	public void addEntityPrototype(IApsEntity entityType) throws EntException;
	
	/**
	 * Update an entity prototype on the catalog.
	 * @param entityType The entity type to update
	 * @throws EntException In case of error.
	 */
	public void updateEntityPrototype(IApsEntity entityType) throws EntException;
	
	/**
	 * Remove an entity type from the catalog.
	 * @param entityTypeCode The code of the entity type to remove.
	 * @throws EntException In case of error.
	 */
	public void removeEntityPrototype(String entityTypeCode) throws EntException;
	
}

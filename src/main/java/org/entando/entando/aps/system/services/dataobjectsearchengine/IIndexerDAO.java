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

package org.entando.entando.aps.system.services.dataobjectsearchengine;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.category.ICategoryManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import java.io.File;

/**
 * Data Access Object dedita alla indicizzazione di documenti.
 *
 * @author W.Ambu
 */
public interface IIndexerDAO {

    String FIELD_PREFIX = "entity:";
    String DATAOBJECT_ID_FIELD_NAME = FIELD_PREFIX + "id";
    String DATAOBJECT_TYPE_FIELD_NAME = FIELD_PREFIX + "type";
    String DATAOBJECT_GROUP_FIELD_NAME = FIELD_PREFIX + "group";
    String DATAOBJECT_CATEGORY_FIELD_NAME = FIELD_PREFIX + "category";
    String DATAOBJECT_CATEGORY_SEPARATOR = "/";

    /**
     * Inizializzazione dell'indicizzatore.
     *
     * @param dir La cartella locale contenitore dei dati persistenti.
     * @throws ApsSystemException In caso di errori.
     */
    void init(File dir) throws ApsSystemException;

    /**
     * Aggiunge un dataobject nel db del motore di ricerca.
     *
     * @param entity Il dataobject da aggiungere.
     * @throws ApsSystemException In caso di errori.
     */
    void add(IApsEntity entity) throws ApsSystemException;

    /**
     * Cancella un documento indicizzato.
     *
     * @param name Il nome del campo Field da utilizzare per recupero del documento.
     * @param value La chiave mediante il quale è stato indicizzato il documento.
     * @throws ApsSystemException In caso di errori.
     */
    void delete(String name, String value) throws ApsSystemException;

    void close();

    void setLangManager(ILangManager langManager);

    void setCategoryManager(ICategoryManager categoryManager);

}

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

package com.agiletec.aps.system.services.group;

import com.agiletec.aps.system.common.FieldSearchFilter;
import com.agiletec.aps.system.common.model.dao.SearcherDaoPaginatedResult;
import com.agiletec.aps.system.exception.ApsSystemException;
import java.util.List;
import java.util.Map;

/**
 * Interfaccia base per i servizi gestori dei gruppi.
 *
 * @author E.Santoboni
 */
public interface IGroupManager {

    /**
     * Aggiunge un gruppo nel sistema.
     *
     * @param group Il gruppo da aggiungere.
     * @throws ApsSystemException In caso di errori in accesso al db.
     */
    void addGroup(Group group) throws ApsSystemException;

    /**
     * Rimuove un gruppo dal sistema.
     *
     * @param group Il gruppo da rimuovere.
     * @throws ApsSystemException In caso di errori in accesso al db.
     */
    void removeGroup(Group group) throws ApsSystemException;

    /**
     * Aggiorna un gruppo di sistema.
     *
     * @param group Il gruppo da aggiornare.
     * @throws ApsSystemException In caso di errori in accesso al db.
     */
    void updateGroup(Group group) throws ApsSystemException;

    /**
     * Restituisce la lista ordinata dei gruppi presenti nel sistema.
     *
     * @return La lista dei gruppi presenti nel sistema.
     */
    List<Group> getGroups();

    /**
     * Restituisce la mappa dei gruppi presenti nel sistema. La mappa è indicizzata in base al nome del gruppo.
     *
     * @return La mappa dei gruppi presenti nel sistema.
     */
    Map<String, Group> getGroupsMap();

    /**
     * Restituisce un gruppo in base al nome.
     *
     * @param groupName Il nome del gruppo.
     * @return Il gruppo cercato.
     */
    Group getGroup(String groupName);

    SearcherDaoPaginatedResult<Group> getGroups(FieldSearchFilter[] fieldSearchFilters) throws ApsSystemException;

    SearcherDaoPaginatedResult<Group> getGroups(List<FieldSearchFilter> fieldSearchFilters) throws ApsSystemException;

}

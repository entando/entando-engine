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
package com.agiletec.aps.system.services.role;

import java.util.List;

import org.entando.entando.ent.exception.EntException;

/**
 * Interfaccia base per il servizio di gestione dei ruoli.
 *
 * @author E.Santoboni
 */
public interface IRoleManager {

    public static final String ROLE_MANAGER_CACHE_NAME = "Entando_RoleManager";

    /**
     * Restituisce la lista ordinata dei ruoli esistenti.
     *
     * @return La lista ordinata dei ruoli esistenti.
     */
    public List<Role> getRoles();

    /**
     * Restituisce un ruolo in base al nome.
     *
     * @param roleName Il nome del ruolo richesto.
     * @return Il ruolo cercato.
     */
    public Role getRole(String roleName);

    /**
     * Rimuove un ruolo.
     *
     * @param role Oggetto di tipo Role relativo al ruolo da rimuovere.
     * @throws EntException in caso di errore.
     */
    public void removeRole(Role role) throws EntException;

    /**
     * Aggiorna un ruolo.
     *
     * @param role Il ruolo da aggiornare.
     * @throws EntException in caso di errore.
     */
    public void updateRole(Role role) throws EntException;

    /**
     * Aggiunge un ruolo.
     *
     * @param role Oggetto di tipo Role relativo al ruolo da aggiungere.
     * @throws EntException in caso di errore.
     */
    public void addRole(Role role) throws EntException;

    /**
     * Restituisce la lista ordinata dei permessi di autorizzazione.
     *
     * @return La lista ordinata dei permessi
     */
    public List<Permission> getPermissions();

    /**
     * Restituisce un permesso in base al nome.
     *
     * @param permissionName Il nome del permesso richiesto.
     * @return Il permesso richiesto.
     */
    public Permission getPermission(String permissionName);

    /**
     * Rimuove il permesso specificato e dai ruoli.
     *
     * @param permissionName Il permesso da rimuovere dal ruolo.
     * @throws EntException in caso di errore.
     */
    public void removePermission(String permissionName) throws EntException;

    /**
     * Aggiorna un permesso di autorizzazione.
     *
     * @param permission Il permesso da aggiornare.
     * @throws EntException in caso di errore.
     */
    public void updatePermission(Permission permission) throws EntException;

    /**
     * Aggiunge un permesso di autorizzazione.
     *
     * @param permission Il permesso da aggiungere.
     * @throws EntException in caso di errore.
     */
    public void addPermission(Permission permission) throws EntException;

    /**
     * Restituisce la lista di ruoli comprendente il permesso specificato.
     *
     * @param permissionName Il nome del permesso.
     * @return La lista di ruoli.
     */
    public List<Role> getRolesWithPermission(String permissionName);

    /**
     * Return the permission codes list
     * 
     * @return the permission codes list
     */
    public List<String> getPermissionsCodes();

}

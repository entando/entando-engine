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

package com.agiletec.aps.system.services.authorization;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.role.Role;
import com.agiletec.aps.system.services.user.UserDetails;
import java.util.List;
import java.util.Set;

/**
 * Interfaccia base per il servizio di autorizzazione. Il servizio verifica le autorizzazioni di utenti ad azioni (attraverso il permesso
 * associato) o ad oggetti del sistema (attraverso il gruppo associato).
 *
 * @author E.Santoboni
 */
public interface IAuthorizationManager {

    /**
     * Verifica se l'utente specificato possiede l'autorizzazione richiesta.
     *
     * @param user L'utente di cui verificare l'autorizzazione.
     * @param auth L'autorizzazione da verificare.
     * @return True se l'utente possiede l'autorizzazione, false in caso contrario.
     * @deprecated Since Entando 4.1.1, use getRelatedAuthorities(UserDetails, IApsAuthority)
     */
    boolean isAuth(UserDetails user, IApsAuthority auth);

    boolean isAuthOnGroupAndPermission(UserDetails user, String groupName, String permissionName, boolean chechAdmin);

    boolean isAuthOnGroupAndRole(UserDetails user, String groupName, String roleName, boolean chechAdmin);

    List<IApsAuthority> getRelatedAuthorities(UserDetails user, IApsAuthority auth);

    /**
     * Verifica se l'utente specificato possiede il permesso richiesto.
     *
     * @param user L'utente di cui verificare l'autorizzazione.
     * @param permission Il permesso da verificare.
     * @return True se l'utente possiede il permesso, false in caso contrario.
     */
    boolean isAuth(UserDetails user, Permission permission);

    List<IApsAuthority> getAuthoritiesByPermission(UserDetails user, Permission permission);

    List<Group> getGroupsByPermission(UserDetails user, Permission permission);

    /**
     * Verifica se l'utente specificato appartiene al gruppo specificato.
     *
     * @param user L'utente di cui verificare l'autorizzazione.
     * @param group Il gruppo da verificare.
     * @return True se l'utente fa parte del gruppo, false in caso contrario.
     */
    boolean isAuth(UserDetails user, Group group);

    List<IApsAuthority> getAuthoritiesByGroup(UserDetails user, Group group);

    List<Role> getRolesByGroup(UserDetails user, Group group);

    /**
     * Verifica se l'utente specificato è abilitato all'accesso alla pagina specificata.
     *
     * @param user L'utente di cui verificare l'autorizzazione.
     * @param page La pagina da analizzare.
     * @return True se l'utente è abilitato all'accesso alla pagina specificata, false in caso contrario.
     */
    boolean isAuth(UserDetails user, IPage page);

    /**
     * Verifica se l'utente specificato appartiene al gruppo specificato.
     *
     * @param user L'utente di cui verificare l'autorizzazione.
     * @param groupName Il nome del gruppo da verificare.
     * @return True se l'utente fa parte del gruppo, false in caso contrario.
     */
    boolean isAuthOnGroup(UserDetails user, String groupName);

    List<IApsAuthority> getAuthoritiesByGroup(UserDetails user, String groupName);

    List<Role> getRolesByGroup(UserDetails user, String groupName);

    /**
     * Verifica se l'utente specificato appartiene al ruolo specificato.
     *
     * @param user L'utente di cui verificare l'autorizzazione.
     * @param roleName Il nome del ruolo da verificare.
     * @return True se l'utente possiede il ruolo specificato, false in caso contrario.
     */
    boolean isAuthOnRole(UserDetails user, String roleName);

    List<IApsAuthority> getAuthoritiesByRole(UserDetails user, String roleName);

    List<Group> getGroupsByRole(UserDetails user, String roleName);

    /**
     * Verifica se l'utente specificato possiede il permesso richiesto.
     *
     * @param user L'utente di cui verificare l'autorizzazione.
     * @param permissionName Il nome del permesso da verificare.
     * @return True se l'utente possiede il permesso, false in caso contrario.
     */
    boolean isAuthOnPermission(UserDetails user, String permissionName);

    List<Group> getGroupsByPermission(UserDetails user, String permissionName);

    /**
     * Verifica se l'utente specificato possiede l'autorizzazione all'accesso all'entità specificata.
     *
     * @param user L'utente di cui verificare l'autorizzazione.
     * @param entity L'entità di cui verificare l'accesso.
     * @return True se l'utente possiede il permesso di accesso all'entità, false in caso contrario.
     */
    boolean isAuth(UserDetails user, IApsEntity entity);

    /**
     * Check if the user has at least one of the specified groups.
     *
     * @param user the user to check. Must be not null
     * @param groups A set of group codes to check against user's groups
     * @return True if the user has at least one group specified in groups, or if groups contains "free" or if the user belongs to the
     * "administrators" group
     */
    boolean isAuth(UserDetails user, Set<String> groups);

    /**
     * Returns the groups of the given user
     *
     * @param user The user
     * @return The list of groups the given user
     * @deprecated from Entando 2.4.0, Use getUserGroups(UserDetails)
     */
    List<Group> getGroupsOfUser(UserDetails user);

    /**
     * Returns the groups of the given user
     *
     * @param user The user
     * @return The list of groups the given user
     */
    List<Group> getUserGroups(UserDetails user);

    /**
     * Returns the roles of the given user
     *
     * @param user The user.
     * @return The list of roles of the given user.
     */
    List<Role> getUserRoles(UserDetails user);

    void addUserAuthorization(String username, String groupName, String roleName) throws ApsSystemException;

    void addUserAuthorization(String username, Authorization authorization) throws ApsSystemException;

    void addUserAuthorizations(String username, List<Authorization> authorizations) throws ApsSystemException;

    void updateUserAuthorizations(String username, List<Authorization> authorizations) throws ApsSystemException;

    void deleteUserAuthorization(String username, String groupname, String rolename) throws ApsSystemException;

    List<Authorization> getUserAuthorizations(String username) throws ApsSystemException;

    void deleteUserAuthorizations(String username) throws ApsSystemException;

    List<String> getUsersByAuthority(IApsAuthority authority, boolean includeAdmin) throws ApsSystemException;

    List<String> getUsersByAuthorities(String groupName, String roleName, boolean includeAdmin) throws ApsSystemException;

    List<String> getUsersByRole(IApsAuthority authority, boolean includeAdmin) throws ApsSystemException;

    List<String> getUsersByRole(String roleName, boolean includeAdmin) throws ApsSystemException;

    List<String> getUsersByGroup(IApsAuthority authority, boolean includeAdmin) throws ApsSystemException;

    List<String> getUsersByGroup(String groupName, boolean includeAdmin) throws ApsSystemException;

}
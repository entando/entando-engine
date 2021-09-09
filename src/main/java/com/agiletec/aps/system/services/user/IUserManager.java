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
package com.agiletec.aps.system.services.user;

import com.agiletec.aps.system.common.IParameterizableManager;
import java.util.List;

import org.entando.entando.ent.exception.EntException;

/**
 * Interfaccia base per i servizi di gestione utenti.
 *
 * @author M.Diana - E.Santoboni
 */
public interface IUserManager extends IParameterizableManager {

    /**
     * Parametro di sistema: abilitazione del modulo Privacy. Possibili
     * immissioni "true" o "false" (default).
     */
    public static final String CONFIG_PARAM_PM_ENABLED = "extendedPrivacyModuleEnabled";

    /**
     * Parametro di sistema a uso del modulo Privacy. Numero massimo di mesi
     * consentiti dal ultimo accesso. Nel caso che il modulo privacy sia attivo
     * e che una utenza abbia oltrepassato la soglia massima di inattività
     * dell'utenza definita da questo parametro, l'utenza sarà dichiarata
     * scaduta e in occasione del login tutte le autorizzazioni verranno
     * disabilitate.
     */
    public static final String CONFIG_PARAM_PM_MM_LAST_ACCESS = "maxMonthsSinceLastAccess";

    /**
     * Parametro di sistema a uso del modulo Privacy. Numero massimo di mesi
     * consentiti dal ultimo cambio password. Nel caso che il modulo privacy sia
     * attivo e che una utenza presenti la password invariata per un tempo oltre
     * la soglia massima definita da questo parametro, in occasione del login
     * tutte le autorizzazioni verranno disabilitate.
     */
    public static final String CONFIG_PARAM_PM_MM_LAST_PASSWORD_CHANGE = "maxMonthsSinceLastPasswordChange";

    public static final String CONFIG_PARAM_GRAVATAR_INTEGRATION_ENABLED = "gravatarIntegrationEnabled";

    public List<String> getUsernames() throws EntException;

    public List<String> searchUsernames(String text) throws EntException;

    /**
     * Restituisce la lista completa degli utenti (in oggetti User).
     *
     * @return La lista completa degli utenti (in oggetti User).
     * @throws EntException In caso di errore.
     */
    public List<UserDetails> getUsers() throws EntException;

    /**
     * Restituisce la lista di utenti ricavata dalla ricerca sulla username (o
     * porzione di essa).
     *
     * @param text Il testo tramite il quale effettuare la ricerca sulla
     * username.
     * @return La lista di utenti ricavati.
     * @throws EntException In caso di errore.
     */
    public List<UserDetails> searchUsers(String text) throws EntException;

    /**
     * Elimina un utente.
     *
     * @param user L'utente da eliminare dal db.
     * @throws EntException in caso di errore.
     */
    public void removeUser(UserDetails user) throws EntException;

    /**
     * Elimina un utente.
     *
     * @param username La username dell'utente da eliminare.
     * @throws EntException in caso di errore.
     */
    public void removeUser(String username) throws EntException;

    /**
     * Aggiorna un utente.
     *
     * @param user L'utente da aggiornare.
     * @throws EntException in caso di errore.
     */
    public void updateUser(UserDetails user) throws EntException;

    /**
     * Aggiorna la data (a quella odierna) di ultimo accesso dell'utente
     * specificato.
     *
     * @param user L'utente a cui aggiornare la data di ultimo accesso.
     * @throws EntException In caso di errore.
     */
    public void updateLastAccess(UserDetails user) throws EntException;

    /**
     * Effettua l'operazione di cambio password.
     *
     * @param username Lo username al quale cambiare la password.
     * @param password La nuova password.
     * @throws EntException In caso di errore.
     */
    public void changePassword(String username, String password) throws EntException;

    /**
     * Aggiunge un utente.
     *
     * @param user L'utente da aggiungere.
     * @throws EntException in caso di errore.
     */
    public void addUser(UserDetails user) throws EntException;

    /**
     * Restituisce un utente. Se la userName non corrisponde ad un utente
     * restituisce null.
     *
     * @param username Lo username dell'utente da restituire.
     * @return L'utente cercato, null se non vi è nessun utente corrispondente
     * alla username immessa.
     * @throws EntException in caso di errore.
     */
    public UserDetails getUser(String username) throws EntException;

    /**
     * Restituisce un utente. Se userName e password non corrispondono ad un
     * utente, restituisce null.
     *
     * @param username Lo username dell'utente da restituire.
     * @param password La password dell'utente da restituire.
     * @return L'utente cercato, null se non vi è nessun utente corrispondente
     * alla username e password immessa.
     * @throws EntException in caso di errore.
     */
    public UserDetails getUser(String username, String password) throws EntException;

    /**
     * Restituisce l'utente di default di sistema. L'utente di default
     * rappresenta un utente "ospite" senza nessuna autorizzazione di accesso ad
     * elementi non "liberi" e senza nessuna autorizzazione ad eseguire
     * qualunque azione sugli elementi del sistema.
     *
     * @return L'utente di default di sistema.
     */
    public UserDetails getGuestUser();

}

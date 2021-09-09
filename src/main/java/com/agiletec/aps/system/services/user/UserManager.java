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

import java.util.List;

import com.agiletec.aps.system.common.AbstractParameterizableService;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

import com.agiletec.aps.system.SystemConstants;
import org.entando.entando.ent.exception.EntException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Servizio di gestione degli utenti.
 *
 * @author M.Diana - E.Santoboni
 */
public class UserManager extends AbstractParameterizableService implements IUserManager {

    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(UserManager.class);

    @Autowired
    @Qualifier(value = "UserManagerParameterNames")
    public transient List<String> parameterNames;

    private IUserDAO userDao;
    
    @Override
    public void init() throws Exception {
        logger.debug("{} ready", this.getClass().getName());
    }

    @Override
    public List<String> getUsernames() throws EntException {
        return this.searchUsernames(null);
    }

    @Override
    public List<String> searchUsernames(String text) throws EntException {
        List<String> usernames = null;
        try {
            usernames = this.getUserDAO().searchUsernames(text);
        } catch (Throwable t) {
            logger.error("Error searching usernames by text '{}'", text, t);
            throw new EntException("Error loading the username list", t);
        }
        return usernames;
    }

    /**
     * Restituisce la lista completa degli utenti (in oggetti User).
     *
     * @return La lista completa degli utenti (in oggetti User).
     * @throws EntException In caso di errore in accesso al db.
     */
    @Override
    public List<UserDetails> getUsers() throws EntException {
        return this.searchUsers(null);
    }

    @Override
    public List<UserDetails> searchUsers(String text) throws EntException {
        List<UserDetails> users = null;
        try {
            users = this.getUserDAO().searchUsers(text);
            for (int i = 0; i < users.size(); i++) {
                this.setUserCredentialCheckParams(users.get(i));
            }
        } catch (Throwable t) {
            logger.error("Error searching users by text '{}'", text, t);
            throw new EntException("Error loading the user list", t);
        }
        return users;
    }

    /**
     * Elimina un'utente dal db.
     *
     * @param user L'utente da eliminare dal db.
     * @throws EntException in caso di errore nell'accesso al db.
     */
    @Override
    public void removeUser(UserDetails user) throws EntException {
        try {
            this.getUserDAO().deleteUser(user);
        } catch (Throwable t) {
            logger.error("Error deleting user '{}'", user, t);
            throw new EntException("Error deleting a user", t);
        }
    }

    @Override
    public void removeUser(String username) throws EntException {
        try {
            this.getUserDAO().deleteUser(username);
        } catch (Throwable t) {
            logger.error("Error deleting user '{}'", username, t);
            throw new EntException("Error deleting a user", t);
        }
    }

    /**
     * Aggiorna un utente nel db.
     *
     * @param user L'utente da aggiornare nel db.
     * @throws EntException in caso di errore nell'accesso al db.
     */
    @Override
    public void updateUser(UserDetails user) throws EntException {
        try {
            this.getUserDAO().updateUser(user);
        } catch (Throwable t) {
            logger.error("Error updating user '{}'", user, t);
            throw new EntException("Error updating the User", t);
        }
    }

    @Override
    public void changePassword(String username, String password) throws EntException {
        try {
            this.getUserDAO().changePassword(username, password);
        } catch (Throwable t) {
            logger.error("Error on change password for user '{}'", username, t);
            throw new EntException("Error updating the password of the User" + username, t);
        }
    }

    @Override
    public void updateLastAccess(UserDetails user) throws EntException {
        if (!user.isEntandoUser()) {
            return;
        }
        try {
            this.getUserDAO().updateLastAccess(user.getUsername());
        } catch (Throwable t) {
            logger.error("Error on update last access for user '{}'", user, t);
            throw new EntException("Error while refreshing the last access date of the User " + user.getUsername(), t);
        }
    }

    /**
     * Aggiunge un utente nel db.
     *
     * @param user L'utente da aggiungere nel db.
     * @throws EntException in caso di errore nell'accesso al db.
     */
    @Override
    public void addUser(UserDetails user) throws EntException {
        try {
            this.getUserDAO().addUser(user);
        } catch (Throwable t) {
            logger.error("Error on add user '{}'", user, t);
            throw new EntException("Error adding a new user ", t);
        }
    }

    /**
     * Recupera un'user caricandolo da db. Se la userName non corrisponde ad un
     * utente restituisce null.
     *
     * @param username Lo username dell'utente da restituire.
     * @return L'utente cercato, null se non vi è nessun utente corrispondente
     * alla username immessa.
     * @throws EntException in caso di errore nell'accesso al db.
     */
    @Override
    public UserDetails getUser(String username) throws EntException {
        UserDetails user = null;
        try {
            user = this.getUserDAO().loadUser(username);
        } catch (Throwable t) {
            logger.error("Error loading user by username '{}'", username, t);
            throw new EntException("Error loading user", t);
        }
        this.setUserCredentialCheckParams(user);
        return user;
    }

    /**
     * Recupera un'user caricandolo da db. Se userName e password non
     * corrispondono ad un utente, restituisce null.
     *
     * @param username Lo username dell'utente da restituire.
     * @param password La password dell'utente da restituire.
     * @return L'utente cercato, null se non vi è nessun utente corrispondente
     * alla username e password immessa.
     * @throws EntException in caso di errore nell'accesso al db.
     */
    @Override
    public UserDetails getUser(String username, String password) throws EntException {
        UserDetails user = null;
        try {
            user = this.getUserDAO().loadUser(username, password);
        } catch (Throwable t) {
            logger.error("Error loading user by username and password. username: '{}'", username, t);
            throw new EntException("Error loading user", t);
        }
        this.setUserCredentialCheckParams(user);
        return user;
    }

    /**
     * Inserisce nell'utenza le informazioni necessarie per la verifica della
     * validità delle credenziali. In particolare, in base allo stato del Modulo
     * Privacy (attivo oppure no), inserisce le informazioni riguardo il numero
     * massimo di mesi consentiti dal ultimo accesso e il numero massimo di mesi
     * consentiti dal ultimo cambio password (parametri estratti dalla
     * configurazioni di sistema).
     *
     * @param user L'utenza sulla quale inserire le informazioni necessarie per
     * la verifica della validità delle credenziali.
     */
    protected void setUserCredentialCheckParams(UserDetails user) {
        if (null != user && user.isEntandoUser()) {
            User japsUser = (User) user;
            String enabledPrivacyModuleParValue = this.getConfig(IUserManager.CONFIG_PARAM_PM_ENABLED);
            boolean enabledPrivacyModule = Boolean.parseBoolean(enabledPrivacyModuleParValue);
            japsUser.setCheckCredentials(enabledPrivacyModule);
            if (enabledPrivacyModule) {
                int maxMonthsSinceLastAccess = this.extractNumberParamValue(IUserManager.CONFIG_PARAM_PM_MM_LAST_ACCESS, 6);
                japsUser.setMaxMonthsSinceLastAccess(maxMonthsSinceLastAccess);
                int maxMonthsSinceLastPasswordChange = this.extractNumberParamValue(IUserManager.CONFIG_PARAM_PM_MM_LAST_PASSWORD_CHANGE, 3);
                japsUser.setMaxMonthsSinceLastPasswordChange(maxMonthsSinceLastPasswordChange);
            }
        }
    }

    private int extractNumberParamValue(String paramName, int defaultValue) {
        String parValue = this.getConfig(paramName);
        int value = 0;
        try {
            value = Integer.parseInt(parValue);
        } catch (NumberFormatException e) {
            value = defaultValue;
        }
        return value;
    }

    private boolean isArgon2Encoded(String encoded) {
        if (StringUtils.isBlank(encoded)) {
            return false;
        }
        return encoded.startsWith("$argon2");
    }

    private boolean isBCryptEncoded(String encoded) {
        if (StringUtils.isBlank(encoded)) {
            return false;
        }
        return encoded.startsWith("{bcrypt}");
    }

    /**
     * Restituisce l'utente di default di sistema. L'utente di default
     * rappresenta un utente "ospite" senza nessuna autorizzazione di accesso ad
     * elementi non "liberi" e senza nessuna autorizzazione ad eseguire
     * qualunque azione sugli elementi del sistema.
     *
     * @return L'utente di default di sistema.
     */
    @Override
    public UserDetails getGuestUser() {
        User user = new User();
        user.setUsername(SystemConstants.GUEST_USER_NAME);
        return user;
    }

    @Override
    protected List<String> getParameterNames() {
        return parameterNames;
    }

    protected IUserDAO getUserDAO() {
        return userDao;
    }

    public void setUserDAO(IUserDAO userDao) {
        this.userDao = userDao;
    }
}

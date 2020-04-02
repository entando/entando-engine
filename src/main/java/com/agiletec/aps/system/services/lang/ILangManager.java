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

package com.agiletec.aps.system.services.lang;

import com.agiletec.aps.system.exception.ApsSystemException;
import java.util.List;

/**
 * Interfaccia base per i servizi di gestione delle lingue.
 *
 * @author E.Santoboni
 */
public interface ILangManager {

    /**
     * Return the list of assignable langs to system ordered by lang's description.
     *
     * @return The List of assignable langs.
     */
    List<Lang> getAssignableLangs() throws ApsSystemException;

    /**
     * Add a lang on system.
     *
     * @param code The code of the lang to add.
     * @throws ApsSystemException In case of error on update config.
     */
    void addLang(String code) throws ApsSystemException;

    /**
     * Update the description of a system langs.
     *
     * @param code The code of the lang to update.
     * @param descr The new description.
     * @throws ApsSystemException In case of error on update config.
     */
    void updateLang(String code, String descr) throws ApsSystemException;

    /**
     * Remove a lang from the system.
     *
     * @param code The code of the lang to remove.
     * @throws ApsSystemException In case of error on update config.
     */
    void removeLang(String code) throws ApsSystemException;

    /**
     * Restituisce un oggetto lingua in base al codice
     *
     * @param code Il codice della lingua
     * @return La lingua richiesta
     */
    Lang getLang(String code);

    /**
     * Return the default lang.
     *
     * @return The default lang.
     */
    Lang getDefaultLang();

    /**
     * Restituisce la lista (ordinata) delle lingue. La lingua di default è in prima posizione.
     *
     * @return La lista delle lingue
     */
    List<Lang> getLangs();

}

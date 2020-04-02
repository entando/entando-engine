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

package com.agiletec.aps.system.services.category;

import com.agiletec.aps.system.services.lang.ILangManager;
import java.util.List;

/**
 * Interfaccia di base per le classi DAO di gestione Categorie.
 *
 * @author E.Santoboni
 */
public interface ICategoryDAO {

    /**
     * Carica la lista delle categorie inserite nel sistema.
     *
     * @param langManager Il manager delle lingue.
     * @return La lista delle categorie inserite nel sistema.
     */
    List<Category> loadCategories(ILangManager langManager);

    /**
     * Cancella la categoria corrispondente al codice immesso.
     *
     * @param code Il codice relativo alla categoria da cancellare.
     */
    void deleteCategory(String code);

    /**
     * Inserisce una nuova Categoria.
     *
     * @param category La nuova Categoria da inserire.
     */
    void addCategory(Category category);

    /**
     * Aggiorna una categoria sul db.
     *
     * @param category La categoria da aggiornare.
     */
    void updateCategory(Category category);

    /**
     * Move a category under a a new parent node
     *
     * @param currentCategory category to move
     * @param newParent the new parent
     */
    void moveCategory(Category currentCategory, Category newParent);

}

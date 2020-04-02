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

package com.agiletec.aps.system.services.category.cache;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.category.Category;
import com.agiletec.aps.system.services.category.ICategoryDAO;
import com.agiletec.aps.system.services.lang.ILangManager;
import java.util.Map;

/**
 * @author E.Santoboni
 */
public interface ICategoryManagerCacheWrapper {

    String CATEGORY_MANAGER_CACHE_NAME = "Entando_CategoryManager";
    String CATEGORY_CACHE_NAME_PREFIX = "CategoryManager_node_";
    String CATEGORY_ROOT_CACHE_NAME = "CategoryManager_root";
    String CATEGORY_CODES_CACHE_NAME = "CategoryManager_codes";
    String CATEGORY_STATUS_CACHE_NAME = "CategoryManager_status";

    void initCache(ICategoryDAO categoryDAO, ILangManager langManager) throws ApsSystemException;

    Category getRoot();

    Category getCategory(String code);

    void addCategory(Category category);

    void updateCategory(Category category);

    void deleteCategory(String code);

    Map<String, Integer> getMoveNodeStatus();

    void updateMoveNodeStatus(String beanName, Integer status);

    void moveCategory(String categoryCode, String newParentCode);

}

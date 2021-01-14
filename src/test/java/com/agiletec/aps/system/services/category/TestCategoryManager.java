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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.agiletec.aps.BaseTestCaseJunit5;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.IManager;
import com.agiletec.aps.util.ApsProperties;
import java.util.Arrays;

/**
 * Tests for Category Manager
 *
 * @author E.Santoboni
 */
public class TestCategoryManager extends BaseTestCaseJunit5 {

    public void testGetCategory() {
        ICategoryManager categoryManager = (ICategoryManager) getService(SystemConstants.CATEGORY_MANAGER);
        Category category = categoryManager.getCategory("cat1");
        assertNotNull(category);
        assertEquals(category.getTitle(), "Animali");
    }

    public void testAddCategory() throws Throwable {
        ICategoryManager categoryManager = (ICategoryManager) getService(SystemConstants.CATEGORY_MANAGER);
        Category cat = this.createCategory();
        try {
            assertNull(categoryManager.getCategory(cat.getCode()));
            categoryManager.addCategory(cat);
            Category extractedCat = categoryManager.getCategory(cat.getCode());
            assertNotNull(extractedCat);
            assertEquals(cat.getTitle(), extractedCat.getTitle());
            assertEquals(cat.getDefaultFullTitle(categoryManager), extractedCat.getDefaultFullTitle(categoryManager));
            assertEquals(cat.getParentCode(), cat.getParentCode());

            Category extractedParent = categoryManager.getCategory(cat.getParentCode());
            assertEquals(4, extractedParent.getChildrenCodes().length);
            assertTrue(Arrays.asList(extractedParent.getChildrenCodes()).containsAll(Arrays.asList("general_cat1", cat.getCode(), "general_cat2", "general_cat3")));
        } catch (Throwable t) {
            throw t;
        } finally {
            categoryManager.deleteCategory(cat.getCode());
            assertNull(categoryManager.getCategory(cat.getCode()));
            Category extractedParent = categoryManager.getCategory(cat.getParentCode());
            assertEquals(3, extractedParent.getChildrenCodes().length);
            assertTrue(Arrays.asList(extractedParent.getChildrenCodes()).containsAll(Arrays.asList("general_cat1", "general_cat2", "general_cat3")));
        }
        ((IManager) categoryManager).refresh();
        Category extractedParent = categoryManager.getCategory(cat.getParentCode());
        assertEquals(3, extractedParent.getChildrenCodes().length);
        assertTrue(Arrays.asList(extractedParent.getChildrenCodes()).containsAll(Arrays.asList("general_cat1", "general_cat2", "general_cat3")));
    }

    public void testUpdateRemoveCategory() throws Throwable {
        ICategoryManager categoryManager = (ICategoryManager) getService(SystemConstants.CATEGORY_MANAGER);
        Category cat = this.createCategory();
        try {
            assertNull(categoryManager.getCategory(cat.getCode()));
            categoryManager.addCategory(cat);
            Category extractedCat = categoryManager.getCategory(cat.getCode());
            assertNotNull(extractedCat);

            String newTitle = "Nuovo titolo dell categoria temporanea";
            extractedCat.getTitles().put("it", newTitle);
            categoryManager.updateCategory(extractedCat);
            extractedCat = categoryManager.getCategory(cat.getCode());
            assertEquals(extractedCat.getTitle(), newTitle);
            assertEquals(extractedCat.getParentCode(), cat.getParentCode());
        } catch (Throwable t) {
            throw t;
        } finally {
            categoryManager.deleteCategory(cat.getCode());
            assertNull(categoryManager.getCategory(cat.getCode()));
        }
    }

    public void testGetCategories() {
        ICategoryManager categoryManager = (ICategoryManager) getService(SystemConstants.CATEGORY_MANAGER);
        List<Category> categories = categoryManager.getCategoriesList();
        assertNotNull(categories);
        assertTrue(categories.size() > 0);
    }
    
    public void testMove() throws Throwable {
        ICategoryManager categoryManager = (ICategoryManager) getService(SystemConstants.CATEGORY_MANAGER);
        Category category1 = this.createCategory("st_move_1", "cat1", "AAAA Title start");
        Category category2 = this.createCategory("st_move_2", "cat1", "BBBB Title start");
        Category category3 = this.createCategory("st_move_3", "cat1", "CCCC Title start");
        Category category4 = this.createCategory("st_move_4", "cat1", "DDDD Title start");
        
        Category dt_category1 = this.createCategory("dt_move_1", "evento", "AAAA Title destination");
        Category dt_category2 = this.createCategory("dt_move_2", "evento", "BBBB Title destination");
        Category dt_category3 = this.createCategory("dt_move_3", "evento", "CCCC Title destination");
        Category dt_category4 = this.createCategory("dt_move_4", "evento", "DDDD Title destination");
        try {
            categoryManager.addCategory(category1);
            categoryManager.addCategory(category2);
            categoryManager.addCategory(category3);
            categoryManager.addCategory(category4);
            categoryManager.addCategory(dt_category1);
            categoryManager.addCategory(dt_category2);
            categoryManager.addCategory(dt_category3);
            categoryManager.addCategory(dt_category4);
            
            categoryManager.moveCategory("st_move_2", "evento");
            this.checkParent("cat1", Arrays.asList("st_move_1", "st_move_3", "st_move_4"));
            this.checkParent("evento", Arrays.asList("dt_move_1", "dt_move_2", "dt_move_3", "dt_move_4", "st_move_2"));
            ((IManager) categoryManager).refresh();
            this.checkParent("cat1", Arrays.asList("st_move_1", "st_move_3", "st_move_4"));
            this.checkParent("evento", Arrays.asList("dt_move_1", "dt_move_2", "dt_move_3", "dt_move_4", "st_move_2"));
            
            categoryManager.moveCategory("st_move_1", "evento");
            this.checkParent("cat1", Arrays.asList("st_move_3", "st_move_4"));
            this.checkParent("evento", Arrays.asList("dt_move_1", "dt_move_2", "dt_move_3", "dt_move_4", "st_move_2", "st_move_1"));
            ((IManager) categoryManager).refresh();
            this.checkParent("cat1", Arrays.asList("st_move_3", "st_move_4"));
            this.checkParent("evento", Arrays.asList("dt_move_1", "dt_move_2", "dt_move_3", "dt_move_4", "st_move_2", "st_move_1"));
            
            categoryManager.moveCategory("dt_move_3", "cat1");
            this.checkParent("cat1", Arrays.asList("st_move_3", "st_move_4", "dt_move_3"));
            this.checkParent("evento", Arrays.asList("dt_move_1", "dt_move_2", "dt_move_4", "st_move_2", "st_move_1"));
            ((IManager) categoryManager).refresh();
            this.checkParent("cat1", Arrays.asList("st_move_3", "st_move_4", "dt_move_3"));
            this.checkParent("evento", Arrays.asList("dt_move_1", "dt_move_2", "dt_move_4", "st_move_2", "st_move_1"));
            
        } catch (Exception e) {
            throw e;
        } finally {
            for (int i = 0; i < 4; i++) {
                categoryManager.deleteCategory("st_move_" + (i+1));
                categoryManager.deleteCategory("dt_move_" + (i+1));
            }
            for (int i = 0; i < 4; i++) {
                assertNull(categoryManager.getCategory("st_move_" + (i+1)));
                assertNull(categoryManager.getCategory("dt_move_" + (i+1)));
            }
            Category extractedParent = categoryManager.getCategory("cat1");
            assertEquals(0, extractedParent.getChildrenCodes().length);
        }
        ((IManager) categoryManager).refresh();
        Category extractedParent = categoryManager.getCategory("cat1");
        assertEquals(0, extractedParent.getChildrenCodes().length);
    }
    
    private void checkParent(String code, List<String> childrenCodes) throws Throwable {
        ICategoryManager categoryManager = (ICategoryManager) getService(SystemConstants.CATEGORY_MANAGER);
        Category extractedParent = categoryManager.getCategory(code);
        assertEquals(childrenCodes.size(), extractedParent.getChildrenCodes().length);
        assertTrue(childrenCodes.containsAll(Arrays.asList(extractedParent.getChildrenCodes())));
    }
    
    private Category createCategory() {
        return this.createCategory("tempCode", "general", "Titolo");
    }
    
    private Category createCategory(String code, String parentCode, String titlePrefix) {
        Category cat = new Category();
        cat.setDefaultLang("it");
        cat.setCode(code);
        cat.setParentCode(parentCode);
        ApsProperties titles = new ApsProperties();
        titles.put("it", titlePrefix + " in Italiano");
        titles.put("en", titlePrefix + " in Inglese");
        cat.setTitles(titles);
        return cat;
    }

}

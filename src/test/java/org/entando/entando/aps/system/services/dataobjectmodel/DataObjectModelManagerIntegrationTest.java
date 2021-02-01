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
package org.entando.entando.aps.system.services.dataobjectmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Map;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.services.page.IPage;
import org.entando.entando.aps.system.services.dataobject.model.SmallDataType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataObjectModelManagerIntegrationTest extends BaseTestCase {

    private IDataObjectModelManager dataModelManager;

    @Test
    void testGetContentModel() {
        DataObjectModel model = this.dataModelManager.getDataObjectModel(1);
        assertNotNull(model);
    }

    @Test
    void testGetContentModels() {
        List<DataObjectModel> models = this.dataModelManager.getDataObjectModels();
        assertNotNull(models);
        assertEquals(4, models.size());
    }

    @Test
    void testGetModelsForContentType() {
        List<DataObjectModel> models = this.dataModelManager.getModelsForDataObjectType("ART");
        assertNotNull(models);
        assertEquals(4, models.size());
    }

    @Test
    void testAddDeleteContentModel() throws Throwable {
        List<DataObjectModel> contentModels = this.dataModelManager.getDataObjectModels();
        int size = contentModels.size();
        DataObjectModel dataModel = new DataObjectModel();
        dataModel.setId(99);
        dataModel.setDataType("ART");
        dataModel.setDescription("Descr_Prova");
        dataModel.setShape("<h2></h2>");
        try {
            assertNull(this.dataModelManager.getDataObjectModel(99));
            this.dataModelManager.addDataObjectModel(dataModel);
            contentModels = this.dataModelManager.getDataObjectModels();
            assertEquals((size + 1), contentModels.size());
            assertNotNull(this.dataModelManager.getDataObjectModel(3));
            this.dataModelManager.removeDataObjectModel(dataModel);
            contentModels = this.dataModelManager.getDataObjectModels();
            assertEquals(size, contentModels.size());
            assertNull(this.dataModelManager.getDataObjectModel(99));
        } catch (Throwable t) {
            throw t;
        } finally {
            this.dataModelManager.removeDataObjectModel(dataModel);
        }
    }

    @Test
    void testUpdateContentModel() throws Throwable {
        List<DataObjectModel> contentModels = dataModelManager.getDataObjectModels();
        int size = contentModels.size();
        DataObjectModel dataModel = new DataObjectModel();
        dataModel.setId(99);
        dataModel.setDataType("ART");
        dataModel.setDescription("Descr_Prova");
        dataModel.setShape("<h2></h2>");
        try {
            assertNull(this.dataModelManager.getDataObjectModel(99));
            this.dataModelManager.addDataObjectModel(dataModel);
            contentModels = this.dataModelManager.getDataObjectModels();
            assertEquals((size + 1), contentModels.size());

            DataObjectModel contentModelNew = new DataObjectModel();
            contentModelNew.setId(dataModel.getId());
            contentModelNew.setDataType("RAH");
            contentModelNew.setDescription("Descr_Prova");
            contentModelNew.setShape("<h1></h1>");
            this.dataModelManager.updateDataObjectModel(contentModelNew);
            DataObjectModel extracted = this.dataModelManager.getDataObjectModel(99);
            assertEquals(dataModel.getDescription(), extracted.getDescription());

            this.dataModelManager.removeDataObjectModel(dataModel);
            contentModels = this.dataModelManager.getDataObjectModels();
            assertEquals(size, contentModels.size());
            assertNull(this.dataModelManager.getDataObjectModel(99));
        } catch (Throwable t) {
            throw t;
        } finally {
            this.dataModelManager.removeDataObjectModel(dataModel);
        }
    }

    @Test
    void testGetReferencingPages() {
        Map<String, List<IPage>> utilizers = this.dataModelManager.getReferencingPages(2);
        assertNotNull(utilizers);
        assertEquals(0, utilizers.size());
    }

    @Test
    void testGetTypeUtilizer() throws Throwable {
        SmallDataType utilizer = this.dataModelManager.getDefaultUtilizer(1);
        assertNotNull(utilizer);
        assertEquals("ART", utilizer.getCode());

        utilizer = this.dataModelManager.getDefaultUtilizer(11);
        assertNotNull(utilizer);
        assertEquals("ART", utilizer.getCode());

        utilizer = this.dataModelManager.getDefaultUtilizer(126);
        assertNotNull(utilizer);
        assertEquals("RAH", utilizer.getCode());
    }

    @BeforeEach
    private void init() {
        this.dataModelManager = (IDataObjectModelManager) this.getService("DataObjectModelManager");
    }

}

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
package com.agiletec.aps.system.services.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.HashMap;
import java.util.Map;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.util.ApsProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @version 1.0
 * @author W.Ambu
 */
class I18nManagerIntegrationTest extends BaseTestCase {
    
    private II18nManager i18nManager;
    
    @BeforeEach
    private void init() {
        this.i18nManager = (II18nManager) this.getService(SystemConstants.I18N_MANAGER);
    }
    
    @Test
    void testRenderLabel() throws Throwable {
        assertEquals("titolo pagina", this.i18nManager.renderLabel("PAGE_TITLE", "it", false));
        assertEquals("page title", this.i18nManager.renderLabel("PAGE_TITLE", "en", false));

        assertEquals("fullname", this.i18nManager.renderLabel("userprofile_PFL_fullname", "en", false));
        assertNull(this.i18nManager.renderLabel("not-exists", "en", false));
        assertEquals("not-exists", this.i18nManager.renderLabel("not-exists", "en", true));
        assertEquals("Welcome ${surname} ${name} (${username} - ${name}.${surname})", this.i18nManager.renderLabel("LABEL_WITH_PARAMS", "en", true));
    }

    @Test
    void testRenderLabelWithParams() throws Throwable {
        assertEquals("titolo pagina", this.i18nManager.renderLabel("PAGE_TITLE", "it", false, null));

        Map<String, String> params = new HashMap<String, String>();
        params.put("name", "Name");
        params.put("surname", "Surname");
        params.put("username", "admin");
        assertEquals("Welcome Surname Name (admin - Name.Surname)", this.i18nManager.renderLabel("LABEL_WITH_PARAMS", "en", true, params));
        assertEquals("Benvenuto Name Surname (admin - Name.Surname)", this.i18nManager.renderLabel("LABEL_WITH_PARAMS", "it", false, params));
        assertEquals("not-exists", this.i18nManager.renderLabel("not-exists", "en", true, null));
        assertEquals("not-exists", this.i18nManager.renderLabel("not-exists", "en", true, params));
    }

    @Test
    void testGetLabelGroup() throws Throwable {
        ApsProperties labelsProp = this.i18nManager.getLabelGroup("PAGE_TITLE");
        assertNotNull(labelsProp);
        assertEquals(2, labelsProp.size());
        assertTrue(labelsProp.containsKey("it"));
        assertTrue(labelsProp.containsKey("en"));

        labelsProp = this.i18nManager.getLabelGroup("userprofile_PFL_fullname");
        assertNotNull(labelsProp);
        assertEquals(1, labelsProp.size());
        assertTrue(labelsProp.containsKey("it"));
        assertFalse(labelsProp.containsKey("en"));

        labelsProp = this.i18nManager.getLabelGroup("not-exists");
        assertNull(labelsProp);
    }

    @Test
    void testGetLabels() throws Throwable {
        String label = this.i18nManager.getLabel("PAGE_TITLE", "it");
        assertNotNull(label);
        assertEquals("titolo pagina", label);
        label = this.i18nManager.getLabel("not-exists", "it");
        assertNull(label);
    }

    @Test
    void testAddDeleteLabels() throws Throwable {
        String key = "TEST_KEY";
        ApsProperties labels = new ApsProperties();
        labels.put("it", "Testo Italiano");
        labels.put("en", "English Text");
        try {
            assertNull(this.i18nManager.getLabelGroups().get(key));
            this.i18nManager.addLabelGroup(key, labels);
            ApsProperties extracted = (ApsProperties) this.i18nManager.getLabelGroups().get(key);
            assertNotNull(extracted);
            assertEquals("Testo Italiano", extracted.getProperty("it"));
            assertEquals("English Text", extracted.getProperty("en"));
        } catch (Throwable t) {
            throw t;
        } finally {
            this.i18nManager.deleteLabelGroup(key);
            assertNull(this.i18nManager.getLabelGroups().get(key));
        }
    }

    @Test
    void testUpdateLabels() throws Throwable {
        String key = "TEST_KEY";
        ApsProperties labels = new ApsProperties();
        labels.put("it", "Testo Italiano");
        labels.put("en", "English Text");
        try {
            assertNull(this.i18nManager.getLabelGroups().get(key));
            this.i18nManager.addLabelGroup(key, labels);
            ApsProperties toUpdate = (ApsProperties) this.i18nManager.getLabelGroups().get(key);
            assertNotNull(toUpdate);
            toUpdate.put("it", "Testo Italiano Modificato");
            toUpdate.put("en", "Modified English Text");
            this.i18nManager.updateLabelGroup(key, toUpdate);
            ApsProperties extracted = (ApsProperties) this.i18nManager.getLabelGroups().get(key);
            assertNotNull(extracted);
            assertEquals("Testo Italiano Modificato", extracted.getProperty("it"));
            assertEquals("Modified English Text", extracted.getProperty("en"));
        } catch (Throwable t) {
            throw t;
        } finally {
            this.i18nManager.deleteLabelGroup(key);
            assertNull(this.i18nManager.getLabelGroups().get(key));
        }
    }

    @Test
    void testGetLabelsKey() throws Throwable {
        assertEquals(10, this.i18nManager.getLabelGroups().size());
        assertEquals(0, this.i18nManager.searchLabelsKey("*", false, false, null).size());
        assertEquals(10, this.i18nManager.searchLabelsKey("", false, false, null).size());
        assertEquals(3, this.i18nManager.searchLabelsKey("pag", false, false, null).size());
        assertEquals(4, this.i18nManager.searchLabelsKey("age", true, false, null).size());
        assertEquals(3, this.i18nManager.searchLabelsKey("pag", false, true, "it").size());
    }
    
}

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
package com.agiletec.aps.system.services.baseconfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.agiletec.aps.BaseTestCaseJunit5;
import com.agiletec.aps.system.SystemConstants;
import org.entando.entando.ent.exception.EntException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * @author M.Casari
 */
public class TestBaseConfigService extends BaseTestCaseJunit5 {

    @Test
    public void testGetParam() throws EntException {
        ConfigInterface configInterface = (ConfigInterface) this.getService(SystemConstants.BASE_CONFIG_MANAGER);
        String param = configInterface.getParam(SystemConstants.CONFIG_PARAM_NOT_FOUND_PAGE_CODE);
        assertEquals(param, "notfound");
        param = configInterface.getParam(SystemConstants.PAR_CSP_ENABLED);
        assertEquals(param, "true");
    }

    @Test
    public void testUpdateParam_1() throws EntException {
        ConfigInterface configInterface = (ConfigInterface) this.getService(SystemConstants.BASE_CONFIG_MANAGER);
        String value = configInterface.getParam(SystemConstants.CONFIG_PARAM_NOT_FOUND_PAGE_CODE);
        assertEquals(value, "notfound");
        configInterface.updateParam(SystemConstants.CONFIG_PARAM_NOT_FOUND_PAGE_CODE, "newValue");
        value = configInterface.getParam(SystemConstants.CONFIG_PARAM_NOT_FOUND_PAGE_CODE);
        assertEquals(value, "newValue");
        configInterface.updateParam(SystemConstants.CONFIG_PARAM_NOT_FOUND_PAGE_CODE, "notfound");
        value = configInterface.getParam(SystemConstants.CONFIG_PARAM_NOT_FOUND_PAGE_CODE);
        assertEquals(value, "notfound");
    }

    @Test
    public void testUpdateParam_2() throws EntException {
        ConfigInterface configInterface = (ConfigInterface) this.getService(SystemConstants.BASE_CONFIG_MANAGER);
        String paramName = "wrongParamName";
        String value = configInterface.getParam(paramName);
        assertNull(value);
        configInterface.updateParam(paramName, "newValue");
        value = configInterface.getParam(paramName);
        assertNull(value);
    }

    @Test
    public void testUpdateParams() throws EntException {
        ConfigInterface configInterface = (ConfigInterface) this.getService(SystemConstants.BASE_CONFIG_MANAGER);
        String paramName1 = SystemConstants.CONFIG_PARAM_NOT_FOUND_PAGE_CODE;
        String paramName2 = "wrongParamName";
        try {
            String value1 = configInterface.getParam(paramName1);
            String value2 = configInterface.getParam(paramName2);
            assertEquals(value1, "notfound");
            assertNull(value2);
            Map<String, String> paramsToUpdate = new HashMap<>();
            paramsToUpdate.put(paramName1, "newValue1");
            paramsToUpdate.put(paramName2, "newValue2");
            configInterface.updateParams(paramsToUpdate);
            value1 = configInterface.getParam(paramName1);
            value2 = configInterface.getParam(paramName2);
            assertEquals(value1, "newValue1");
            assertNull(value2);
        } catch (Exception e) {
            throw e;
        } finally {
            configInterface.updateParam(paramName1, "notfound");
            String value = configInterface.getParam(paramName1);
            assertEquals(value, "notfound");
        }
    }

}

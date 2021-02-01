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
package com.agiletec.aps.system.services.keygenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.sql.DataSource;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.services.mock.MockUniqueKeysDAO;
import org.junit.jupiter.api.Test;

class TestKeyGeneratorDAO extends BaseTestCase {
	
    @Test
    void testGetUniqueKey() throws Throwable {
    	DataSource dataSource = (DataSource) getApplicationContext().getBean("portDataSource");
		KeyGeneratorDAO keyGeneratorDao = new KeyGeneratorDAO();
		keyGeneratorDao.setDataSource(dataSource);
		MockUniqueKeysDAO mockUniqueKeysDao = new MockUniqueKeysDAO();
		mockUniqueKeysDao.setDataSource(dataSource);
		int key = -1;
		int current = -1;
        try {
            current = mockUniqueKeysDao.getCurrentKey(1);
    		key = keyGeneratorDao.getUniqueKey();
        } catch (Throwable t) {
        	throw t;
        }
		assertEquals(key, current);
	}
    
}

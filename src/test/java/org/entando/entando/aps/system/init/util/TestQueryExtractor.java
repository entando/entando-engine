/*
 * Copyright 2017-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.init.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.agiletec.aps.util.FileTextReader;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

/**
 * @author E.Santoboni
 */
class TestQueryExtractor {

    @Test
	public void testReadQueries() {
		try {
			InputStream is = this.getClass().getResourceAsStream("guifragment.sql");
			String script = FileTextReader.getText(is);
			assertNotNull(script);
			String[] queries = QueryExtractor.extractInsertQueries(script);
			assertNotNull(queries);
			assertEquals(56, queries.length);
			for (int i = 0; i < queries.length; i++) {
				String query = queries[i];
				assertTrue(query.startsWith("INSERT INTO guifragment (code, "));
				assertTrue(query.endsWith("', 1)") || query.endsWith("', 0)"));
			}
		} catch (Throwable e) {
			fail();
		}
	}

}

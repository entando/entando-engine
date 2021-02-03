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
package org.entando.entando.aps.system.services.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;


import org.entando.entando.aps.system.common.command.context.BaseBulkCommandContext;
import org.entando.entando.aps.system.common.command.tracer.DefaultBulkCommandTracer;
import org.entando.entando.aps.system.services.command.util.BulkCommandContainer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class TestBulkCommandManagerCache {

    @Disabled("temporary disabling")
	@Test
    void testClean() {
        BulkCommandManager bulkCommandManager = new BulkCommandManager();
		for (int i = 0; i < 10; i++) {
			String name = "test" + i;
			FakeBulkCommand command = new FakeBulkCommand(new BaseBulkCommandContext<String>(new ArrayList<String>(), new DefaultBulkCommandTracer<String>()));
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -1*((i+1)*10));
			command.setFakeEndingTime(cal.getTime());
			bulkCommandManager.addCommand(name, command);
		}
		
		Map<String, Map<String, BulkCommandContainer>> map = bulkCommandManager.getCommands();
		assertEquals(10, map.size());
        
		bulkCommandManager.cleanCache();
		map = bulkCommandManager.getCommands();
		
		assertEquals(5, map.size());
	}

}

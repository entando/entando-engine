/*
 * Copyright 2021-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.init.exception;

import java.util.List;
import java.util.Map;
import liquibase.changelog.ChangeSetStatus;

/**
 * @author E.Santoboni
 */
public class DatabaseMigrationException extends Exception {

	public DatabaseMigrationException(String message) {
		super(message);
	}

	public DatabaseMigrationException(Map<String, List<ChangeSetStatus>> pendingChangeSetMap) {
		super("Error on database migration - Components " + pendingChangeSetMap.keySet().toString()
				+ " need to be upgraded manually ");
	}

}

/*
 * Copyright 2015-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.init.model.portdb;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.entando.entando.aps.system.init.model.servdb.UserProfile;

@DatabaseTable(tableName = UserPreferences.TABLE_NAME)
public class UserPreferences {

    public static final String TABLE_NAME = "userpreferences";

    public UserPreferences() {
    }

    @DatabaseField(columnName = "username",
            dataType = DataType.STRING,
            width = 80,
            canBeNull = false, id = true)
    private String _username;

    @DatabaseField(columnName = "wizard",
            dataType = DataType.SHORT,
            canBeNull = false)
    private short _wizard;

    @DatabaseField(columnName = "loadonpageselect",
            dataType = DataType.SHORT,
            canBeNull = false)
    private short _loadonpageselect;

    @DatabaseField(columnName = "translationwarning",
            dataType = DataType.SHORT,
            canBeNull = false)
    private short _translationwarning;

}
/*
CREATE TABLE userpreferences
(
  username character varying(80) NOT NULL,
  wizard smallint NOT NULL,
  loadonpageselect smallint NOT NULL,
  translationwarning smallint NOT NULL,
  CONSTRAINT userpreferences_pkey PRIMARY KEY (username)
);
 */

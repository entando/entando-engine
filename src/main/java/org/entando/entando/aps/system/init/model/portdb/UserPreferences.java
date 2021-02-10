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

@DatabaseTable(tableName = UserPreferences.TABLE_NAME)
public class UserPreferences {

    public static final String TABLE_NAME = "userpreferences";

    @DatabaseField(columnName = "username",
            dataType = DataType.STRING,
            width = 80,
            canBeNull = false, id = true)
    private String username;

    @DatabaseField(columnName = "wizard",
            dataType = DataType.SHORT,
            canBeNull = false)
    private short wizard;

    @DatabaseField(columnName = "loadonpageselect",
            dataType = DataType.SHORT,
            canBeNull = false)
    private short loadonpageselect;

    @DatabaseField(columnName = "translationwarning",
            dataType = DataType.SHORT,
            canBeNull = false)
    private short translationwarning;

    @DatabaseField(columnName = "defaultPageOwnerGroup",
            dataType = DataType.STRING,
            width = 64)
    private String defaultPageOwnerGroup;

    @DatabaseField(columnName = "defaultPageJoinGroups",
            dataType = DataType.STRING,
            width = 256)
    private String defaultPageJoinGroups;

    @DatabaseField(columnName = "defaultContentOwnerGroup",
            dataType = DataType.STRING,
            width = 64)
    private String defaultContentOwnerGroup;

    @DatabaseField(columnName = "defaultContentJoinGroups",
            dataType = DataType.STRING,
            width = 256)
    private String defaultContentJoinGroups;

}
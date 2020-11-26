/*
 * Copyright 2020-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
 *
 */

package org.entando.entando.aps.system.services.storage;

import junit.framework.TestCase;

import java.io.IOException;

public class LocalStorageManagerTest extends TestCase {

    public void testIsSubPathOf() throws IOException {
        assertTrue(
                LocalStorageManager.isSubPathOf("/etc", "/etc/x")
        );
        assertTrue(
                LocalStorageManager.isSubPathOf("/etc/", "/etc/x")
        );
        assertTrue(
                LocalStorageManager.isSubPathOf("/etc", "/etc//x")
        );
        assertFalse(
                LocalStorageManager.isSubPathOf("/etc", "/etcx/x")
        );
        assertFalse(
                LocalStorageManager.isSubPathOf("/etc", "/etc /x")
        );
        assertTrue(
                LocalStorageManager.isSubPathOf("/etc", "/etc/./x")
        );
        assertTrue(
                LocalStorageManager.isSubPathOf("/etc", "/etc//x")
        );
        assertTrue(
                LocalStorageManager.isSubPathOf("/etc", "/etc/zz/../x")
        );
        assertTrue(
                LocalStorageManager.isSubPathOf("/etc", "/etc/zz/./../x")
        );
        assertTrue(
                LocalStorageManager.isSubPathOf("/etc", "/etc/../etc/x")
        );
        assertFalse(
                LocalStorageManager.isSubPathOf("/etc", "/etc/../etc /x")
        );
        assertTrue(
                LocalStorageManager.isSubPathOf("/etc", "/etc/../etc/ x")
        );
        assertTrue(
                LocalStorageManager.isSubPathOf("/etc", "/etc/...")   // "..." is a proper fil/dir name
        );
        assertTrue(
                LocalStorageManager.isSubPathOf("/etc", "/etc/.../etc/x")   // "..." is a proper fil/dir name
        );
        assertFalse(
                LocalStorageManager.isSubPathOf("/etc", "/etc/../../etc/x")
        );
        assertFalse(
                LocalStorageManager.isSubPathOf("/etc", "/etc/../../z/etc/x")
        );
        assertFalse(
                LocalStorageManager.isSubPathOf("/etc", "/etc/../..//etc/x")
        );
        assertFalse(
                LocalStorageManager.isSubPathOf("/etc", "/etx/x")
        );
        assertFalse(
                LocalStorageManager.isSubPathOf("/etc", "/etx/../x")
        );
    }
}
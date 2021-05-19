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
package org.entando.entando.aps.system.services.storage;

import org.entando.entando.ent.exception.EntRuntimeException;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StorageManagerUtilTest {

    @Test
    void isValidFilename_1() {
        Assertions.assertFalse(StorageManagerUtil.isValidFilename(null));
        Assertions.assertFalse(StorageManagerUtil.isValidFilename(""));
        Assertions.assertFalse(StorageManagerUtil.isValidFilename("   "));
        Assertions.assertFalse(StorageManagerUtil.isValidFilename(".txt"));
        Assertions.assertFalse(StorageManagerUtil.isValidFilename("filename."));
        Assertions.assertTrue(StorageManagerUtil.isValidFilename("filename.txt"));
        Assertions.assertTrue(StorageManagerUtil.isValidFilename("file name.png"));
        Assertions.assertTrue(StorageManagerUtil.isValidFilename("file_name.png"));
        Assertions.assertTrue(StorageManagerUtil.isValidFilename("file_name (1).jpeg"));
        Assertions.assertFalse(StorageManagerUtil.isValidFilename("file_?name (1).jpeg"));
        Assertions.assertFalse(StorageManagerUtil.isValidFilename("file_../name"));
        Assertions.assertFalse(StorageManagerUtil.isValidFilename("file_../name.jpg"));
    }

    @Test
    void isValidFilename_2() {
        Assertions.assertFalse(StorageManagerUtil.isValidFilename(null, null));
        Assertions.assertFalse(StorageManagerUtil.isValidFilename("", null));
        Assertions.assertFalse(StorageManagerUtil.isValidFilename("   ", null));
        Assertions.assertFalse(StorageManagerUtil.isValidFilename(null, " "));
        Assertions.assertFalse(StorageManagerUtil.isValidFilename("   ", ""));
        Assertions.assertFalse(StorageManagerUtil.isValidFilename(null, ".txt"));
        Assertions.assertTrue(StorageManagerUtil.isValidFilename("filename", ""));
        Assertions.assertTrue(StorageManagerUtil.isValidFilename("filename", "txt"));
        Assertions.assertTrue(StorageManagerUtil.isValidFilename("filename.txt", "txt"));
        Assertions.assertTrue(StorageManagerUtil.isValidFilename("file name", "png"));
        Assertions.assertTrue(StorageManagerUtil.isValidFilename("file_name", "png"));
        Assertions.assertTrue(StorageManagerUtil.isValidFilename("file_name (1)", "jpeg"));
        Assertions.assertTrue(StorageManagerUtil.isValidFilename("file_name (1).jpg", "jpeg"));
        Assertions.assertFalse(StorageManagerUtil.isValidFilename("file_?name (1)", "jpeg"));
        Assertions.assertFalse(StorageManagerUtil.isValidFilename("file_../name", ""));
        Assertions.assertFalse(StorageManagerUtil.isValidFilename("file_../name", "jpg"));
    }

    @Test
    void isValidFilenameNoExtension() {
        Assertions.assertFalse(StorageManagerUtil.isValidFilenameNoExtension(null));
        Assertions.assertFalse(StorageManagerUtil.isValidFilenameNoExtension(""));
        Assertions.assertFalse(StorageManagerUtil.isValidFilenameNoExtension("   "));
        Assertions.assertTrue(StorageManagerUtil.isValidFilenameNoExtension("filename"));
        Assertions.assertTrue(StorageManagerUtil.isValidFilenameNoExtension("filename.txt"));
        Assertions.assertTrue(StorageManagerUtil.isValidFilenameNoExtension("file name.png"));
        Assertions.assertTrue(StorageManagerUtil.isValidFilenameNoExtension("file_name.png"));
        Assertions.assertTrue(StorageManagerUtil.isValidFilenameNoExtension("file_name (1).jpeg"));
        Assertions.assertFalse(StorageManagerUtil.isValidFilenameNoExtension("file_?name (1).jpeg"));
        Assertions.assertFalse(StorageManagerUtil.isValidFilenameNoExtension("file_../name"));
        Assertions.assertFalse(StorageManagerUtil.isValidFilenameNoExtension("file_../name.jpg"));
        Assertions.assertFalse(StorageManagerUtil.isValidFilenameNoExtension("file_nam%2e%2e/e.jpg"));
        Assertions.assertFalse(StorageManagerUtil.isValidFilenameNoExtension("fi..%2fle_name.jpg"));
        Assertions.assertFalse(StorageManagerUtil.isValidFilenameNoExtension("file_na%2e%2e%2fme.jpg"));
        Assertions.assertFalse(StorageManagerUtil.isValidFilenameNoExtension("file_name/.."));
    }

    @Test
    void isValidDirName() {
        Assertions.assertTrue(StorageManagerUtil.isValidDirName(null));         // TODO: THIS IS QUESTIONABLE
        Assertions.assertTrue(StorageManagerUtil.isValidDirName(""));           // TODO: THIS IS QUESTIONABLE
        Assertions.assertTrue(StorageManagerUtil.isValidDirName("dirname"));
        Assertions.assertTrue(StorageManagerUtil.isValidDirName("dirname.txt"));
        Assertions.assertTrue(StorageManagerUtil.isValidDirName("dirn ame.xht"));
        Assertions.assertTrue(StorageManagerUtil.isValidDirName("dir_name.ney"));
        Assertions.assertTrue(StorageManagerUtil.isValidDirName("dir name"));
        Assertions.assertTrue(StorageManagerUtil.isValidDirName("dir_name (1)"));
        Assertions.assertFalse(StorageManagerUtil.isValidDirName("dir_?name"));
        Assertions.assertFalse(StorageManagerUtil.isValidDirName("dir_../name"));
        Assertions.assertFalse(StorageManagerUtil.isValidDirName("dir../name.subname"));
        Assertions.assertFalse(StorageManagerUtil.isValidDirName("dir_nam%2e%2e/e"));
        Assertions.assertFalse(StorageManagerUtil.isValidDirName("dir..%2f_name"));
        Assertions.assertFalse(StorageManagerUtil.isValidDirName("dir_na%2e%2e%2fme"));
        Assertions.assertTrue(StorageManagerUtil.isValidDirName("a/b"));
        Assertions.assertTrue(StorageManagerUtil.isValidDirName("a/b/c"));
        Assertions.assertFalse(StorageManagerUtil.isValidDirName("a/b/c/.."));
    }

    @Test
    void isValidExtension() {
        Assertions.assertFalse(StorageManagerUtil.isValidExtension(null));
        Assertions.assertFalse(StorageManagerUtil.isValidExtension(""));
        Assertions.assertTrue(StorageManagerUtil.isValidExtension("extension"));
        Assertions.assertTrue(StorageManagerUtil.isValidExtension("txt"));
        Assertions.assertTrue(StorageManagerUtil.isValidExtension("t_t"));
        Assertions.assertFalse(StorageManagerUtil.isValidExtension("t..t"));
        Assertions.assertFalse(StorageManagerUtil.isValidExtension("t t"));
        Assertions.assertFalse(StorageManagerUtil.isValidExtension("(1)"));
        Assertions.assertFalse(StorageManagerUtil.isValidExtension("r_?n"));
        Assertions.assertFalse(StorageManagerUtil.isValidExtension("d_../ex"));
        Assertions.assertFalse(StorageManagerUtil.isValidExtension("%2e%2e/e"));
        Assertions.assertFalse(StorageManagerUtil.isValidExtension("ex..%2f_n"));
        Assertions.assertFalse(StorageManagerUtil.isValidExtension("dir_na%2e%2e%2fme"));
    }

    @Test
    void testMustBeValidFilename() {
        Exception ex = null;

        //  VALID FILENAME
        try {
            StorageManagerUtil.mustBeValidFilename("file.txt");
        } catch (EntRuntimeException e) {
            ex = e;
        }
        Assertions.assertNull(ex);

        // INVALID FILENAME
        try {
            StorageManagerUtil.mustBeValidFilename(".txt");
        } catch (EntRuntimeException e) {
            ex = e;
        }
        
        Assertions.assertNotNull(ex);
        Assertions.assertEquals("Invalid filename detected: \".txt\"", ex.getMessage());
    }

    @Test
    void testMustBeValidDirName() {
        Exception ex = null;

        //  VALID DIR NAME
        try {
            StorageManagerUtil.mustBeValidDirName("./dir");
        } catch (EntRuntimeException e) {
            ex = e;
        }
        Assertions.assertNull(ex);

        // INVALID FILENAME
        try {
            StorageManagerUtil.mustBeValidDirName("../dir");
        } catch (EntRuntimeException e) {
            ex = e;
        }

        Assertions.assertNotNull(ex);
        Assertions.assertEquals("Invalid directory name detected: \"../dir\"", ex.getMessage());
    }


    @Test
    void testDoesPathContainsPath() throws IOException {
        Assertions.assertTrue(StorageManagerUtil.doesPathContainsPath("/etc", "/etc/x"));
        Assertions.assertTrue(StorageManagerUtil.doesPathContainsPath("/etc/", "/etc/x"));
        Assertions.assertTrue(StorageManagerUtil.doesPathContainsPath("/etc", "/etc//x"));
        Assertions.assertFalse(StorageManagerUtil.doesPathContainsPath("/etc", "/etcx/x"));
        Assertions.assertFalse(StorageManagerUtil.doesPathContainsPath("/etc", "/etc /x"));
        Assertions.assertTrue(StorageManagerUtil.doesPathContainsPath("/etc", "/etc/./x"));
        Assertions.assertTrue(StorageManagerUtil.doesPathContainsPath("/etc", "/etc//x"));
        Assertions.assertTrue(StorageManagerUtil.doesPathContainsPath("/etc", "/etc/zz/../x"));
        Assertions.assertTrue(StorageManagerUtil.doesPathContainsPath("/etc", "/etc/zz/./../x"));
        Assertions.assertTrue(StorageManagerUtil.doesPathContainsPath("/etc", "/etc/../etc/x"));
        Assertions.assertFalse(StorageManagerUtil.doesPathContainsPath("/etc", "/etc/../etc /x"));
        Assertions.assertTrue(StorageManagerUtil.doesPathContainsPath("/etc", "/etc/../etc/ x"));
        // "..." is a proper file/dir name
        Assertions.assertTrue(StorageManagerUtil.doesPathContainsPath("/etc", "/etc/..."));
        // "..." is a proper fil/dir name
        Assertions.assertTrue(StorageManagerUtil.doesPathContainsPath("/etc", "/etc/.../etc/x"));
        Assertions.assertFalse(StorageManagerUtil.doesPathContainsPath("/etc", "/etc/../../etc/x"));
        Assertions.assertFalse(StorageManagerUtil.doesPathContainsPath("/etc", "/etc/../../z/etc/x"));
        Assertions.assertFalse(StorageManagerUtil.doesPathContainsPath("/etc", "/etc/../..//etc/x"));
        Assertions.assertFalse(StorageManagerUtil.doesPathContainsPath("/etc", "/etx/x"));
        Assertions.assertFalse(StorageManagerUtil.doesPathContainsPath("/etc", "/etx/../x"));
        Assertions.assertFalse(StorageManagerUtil.doesPathContainsPath("/a/b/c", "/a/b/c", false));
        Assertions.assertFalse(StorageManagerUtil.doesPathContainsPath("/a/b/c/", "/a/b/c", false));
        Assertions.assertTrue(StorageManagerUtil.doesPathContainsPath("/a/b/c", "/a/b/c", true));
        Assertions.assertTrue(StorageManagerUtil.doesPathContainsPath("/a/b/c/", "/a/b/c", true));
        Assertions.assertTrue(StorageManagerUtil.doesPathContainsPath("/a/b/../b/c/", "/a/b/c", true));
        Assertions.assertFalse(StorageManagerUtil.doesPathContainsPath(null, null, true));
        Assertions.assertFalse(StorageManagerUtil.doesPathContainsPath("..", "..", true));
    }

    @Test
    void testIsSamePath() {
        Assertions.assertTrue(StorageManagerUtil.isSamePath("a/b/c", "a/b/c/"));
        Assertions.assertTrue(StorageManagerUtil.isSamePath("a/b/../b/c", "a/b/c/"));
        Assertions.assertFalse(StorageManagerUtil.isSamePath(null, "a/b/c/"));
        Assertions.assertFalse(StorageManagerUtil.isSamePath("..", "a/b/c/"));
        Assertions.assertFalse(StorageManagerUtil.isSamePath("a/b/c/", null));
        Assertions.assertFalse(StorageManagerUtil.isSamePath("a/b/c/", ".."));
        Assertions.assertFalse(StorageManagerUtil.isSamePath(null, null));
        Assertions.assertFalse(StorageManagerUtil.isSamePath("../etc", "../etc"));
        Assertions.assertFalse(StorageManagerUtil.isSamePath("a/b/../../../b/c", "a/b/../../../b/c"));
    }
}

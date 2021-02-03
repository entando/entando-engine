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
package com.agiletec.aps.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.commons.io.input.NullInputStream;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.hamcrest.CoreMatchers;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FileTextReaderTest {

    private static final String A_TMP_FILE = "a-tmp-file";

    @Test
    void testShouldCreateAProperTempFile() throws IOException {
        assertNotNull(
                FileTextReader.createTempFile(A_TMP_FILE, new NullInputStream(100))
        );
    }

    @Test
    void testCreateTempFileShouldBlockPathTraversal() {
        EntRuntimeException entException = Assertions.assertThrows(EntRuntimeException.class, () -> {
            FileTextReader.createTempFile("../" + A_TMP_FILE, new NullInputStream(100));
        });
        assertThat(entException.getMessage(), CoreMatchers.startsWith("Path validation failed"));
    }
    
}
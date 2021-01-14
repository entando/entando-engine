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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.commons.io.input.NullInputStream;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;

import java.io.IOException;
import org.junit.jupiter.api.Test;

public class FileTextReaderTest {

    private static final String A_TMP_FILE = "a-tmp-file";

    @Test
    public void testShouldCreateAProperTempFile() throws IOException {
        assertNotNull(
                FileTextReader.createTempFile(A_TMP_FILE, new NullInputStream(100))
        );
    }

    @Test
    public void testCreateTempFileShouldBlockPathTraversal() {
        try {
            FileTextReader.createTempFile("../" + A_TMP_FILE, new NullInputStream(100));
            fail("Shouldn't reach this point");
        } catch (EntRuntimeException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.startsWith("Path validation failed"));
        } catch (Throwable t) {
            fail("Shouldn't reach this point");
        }
    }
    
}
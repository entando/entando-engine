package org.entando.entando.ent.exception;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class EntExceptionTest {

    @Test
    public void testEntException_plain() {
        EntException ex;

        // PLAIN

        try {
            throw new EntException("something");
        } catch (EntException e) {
            ex = e;
        }

        Assert.assertEquals("something", ex.getMessage());
        Assert.assertNull("something else", ex.getCause());

        try {
            ex = null;
            throw new EntException("something", new RuntimeException("something else"));
        } catch (EntException e) {
            ex = e;
        }

        Assert.assertEquals("something", ex.getMessage());
        Assert.assertEquals(java.lang.RuntimeException.class, ex.getCause().getClass());
        Assert.assertEquals("something else", ex.getCause().getMessage());

        try {
            ex = null;
            throw new EntException(new RuntimeException("something else"));
        } catch (EntException e) {
            ex = e;
        }

        Assert.assertEquals(org.entando.entando.ent.exception.EntException.class, ex.getClass());
        Assert.assertEquals("java.lang.RuntimeException: something else", ex.getMessage());

        try {
            ex = null;
            throw new EntException("something", new RuntimeException("something else"), true, false);
        } catch (EntException e) {
            ex = e;
        }

        Assert.assertEquals(org.entando.entando.ent.exception.EntException.class, ex.getClass());
        Assert.assertEquals("something", ex.getMessage());
        Assert.assertEquals(java.lang.RuntimeException.class, ex.getCause().getClass());
        Assert.assertEquals("something else", ex.getCause().getMessage());
    }

    @Test
    public void testEntException_formatted() {
        EntException ex;

        try {
            ex = null;
            throw new EntException("something %s", "new");
        } catch (EntException e) {
            ex = e;
        }

        Assert.assertNotNull("something else", ex);
        Assert.assertEquals("something new", ex.getMessage());
        Assert.assertNull("something else", ex.getCause());

        try {
            ex = null;
            throw new EntException("something %s", "new", new RuntimeException("something else"));
        } catch (EntException e) {
            ex = e;
        }

        Assert.assertNotNull("something else", ex);
        Assert.assertEquals("something new", ex.getMessage());
        Assert.assertEquals(java.lang.RuntimeException.class, ex.getCause().getClass());
        Assert.assertEquals("something else", ex.getCause().getMessage());
    }

    @Test
    public void testEntException_formatted_and_terminated() {
        EntException ex;

        try {
            ex = null;
            throw new EntException("something %s", "new");
        } catch (EntException e) {
            ex = e;
        }

        Assert.assertNotNull("something else", ex);
        Assert.assertEquals("something new", ex.getMessage());
        Assert.assertNull("something else", ex.getCause());

        try {
            ex = null;
            throw new EntException("something %s", "new", new RuntimeException("something else"));
        } catch (EntException e) {
            ex = e;
        }

        Assert.assertNotNull("something else", ex);
        Assert.assertEquals("something new", ex.getMessage());
        Assert.assertTrue(ex.getCause() instanceof java.lang.RuntimeException);

        // IEntException.TERMINATOR is interpreted and skipped if at the end of the parameters
        try {
            ex = null;
            throw new EntException("something %s %s", "new", new RuntimeException("something else"),
                    IEntException.TERM);
        } catch (EntException e) {
            ex = e;
        }

        Assert.assertNotNull("something else", ex);
        Assert.assertEquals("something new java.lang.RuntimeException: something else", ex.getMessage());
        Assert.assertNull("java.lang.RuntimeException", ex.getCause());

        // IEntException.TERMINATOR is only interpreted if it's at the end of the parameters
        try {
            ex = null;
            throw new EntException("something %s", IEntException.TERM,
                    new RuntimeException("something else"), IEntException.TERM);
        } catch (EntException e) {
            ex = e;
        }

        Assert.assertNotNull("something else", ex);
        Assert.assertThat(ex.getMessage(),
                CoreMatchers.startsWith("something org.entando.entando.ent.exception.IEntException$"));
        Assert.assertNull("java.lang.RuntimeException", ex.getCause());
    }
}
package org.entando.entando.ent.exception;

import jdk.nashorn.internal.AssertsEnabled;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class IEntExceptionTest {

    @Test
    public void testExtractActualArgs() {
        Object[] res;

        res = extractActualArgs();
        Assert.assertEquals(0, res.length);

        res = extractActualArgs(1, 2, 3);
        Assert.assertEquals(3, res.length);

        res = extractActualArgs(1, 2, 3, new Exception("x"));
        Assert.assertEquals(3, res.length);

        res = extractActualArgs(1, 2, 3, new Exception("x"), IEntException.TERM);
        Assert.assertEquals(4, res.length);
    }


    @Test
    public void testExtractActualCause() {
        Throwable res;

        res = extractActualCause();
        Assert.assertNull(res);

        res = extractActualCause(1,2,3);
        Assert.assertNull(res);

        res = extractActualCause(1,2,3, new Exception("x"));
        Assert.assertNotNull(res);

        res = extractActualCause(1, 2, 3, new Exception("x"), IEntException.TERM);
        Assert.assertNull(res);
    }

    private Object[] extractActualArgs(Object... args) {
        return IEntException.extractActualArgs(args);
    }

    private Throwable extractActualCause(Object... args) {
        return IEntException.extractActualCause(args);
    }
}
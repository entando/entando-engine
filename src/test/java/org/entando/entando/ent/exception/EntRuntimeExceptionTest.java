import org.entando.entando.ent.exception.EntRuntimeException;
import org.entando.entando.ent.exception.IEntException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class EntRuntimeExceptionTest {

    @Test
    public void testEntRuntimeException_plain() {
        EntRuntimeException ex;

        // PLAIN

        try {
            throw new EntRuntimeException("something");
        } catch (EntRuntimeException e) {
            ex = e;
        }

        Assert.assertEquals("something", ex.getMessage());
        Assert.assertNull(ex.getCause());

        try {
            ex = null;
            throw new EntRuntimeException("something", new RuntimeException("something else"));
        } catch (EntRuntimeException e) {
            ex = e;
        }

        Assert.assertEquals("something", ex.getMessage());
        Assert.assertEquals(java.lang.RuntimeException.class, ex.getCause().getClass());
        Assert.assertEquals("something else", ex.getCause().getMessage());
    }

    @Test
    public void testEntRuntimeException_formatted() {
        EntRuntimeException ex;

        try {
            ex = null;
            throw new EntRuntimeException("something %s", "new");
        } catch (EntRuntimeException e) {
            ex = e;
        }

        Assert.assertNotNull("something else", ex);
        Assert.assertEquals("something new", ex.getMessage());
        Assert.assertNull(ex.getCause());

        try {
            ex = null;
            throw new EntRuntimeException("something %s", "new", new RuntimeException("something else"));
        } catch (EntRuntimeException e) {
            ex = e;
        }

        Assert.assertNotNull("something else", ex);
        Assert.assertEquals("something new", ex.getMessage());
        Assert.assertEquals(java.lang.RuntimeException.class, ex.getCause().getClass());
        Assert.assertEquals("something else", ex.getCause().getMessage());

        try {
            ex = null;
            throw new EntRuntimeException(new RuntimeException("something else"));
        } catch (EntRuntimeException e) {
            ex = e;
        }

        Assert.assertEquals(org.entando.entando.ent.exception.EntRuntimeException.class, ex.getClass());
        Assert.assertEquals("java.lang.RuntimeException: something else", ex.getMessage());

        try {
            ex = null;
            throw new EntRuntimeException("something", new RuntimeException("something else"), true, false);
        } catch (EntRuntimeException e) {
            ex = e;
        }

        Assert.assertEquals(org.entando.entando.ent.exception.EntRuntimeException.class, ex.getClass());
        Assert.assertEquals("something", ex.getMessage());
        Assert.assertEquals(java.lang.RuntimeException.class, ex.getCause().getClass());
        Assert.assertEquals("something else", ex.getCause().getMessage());
    }

    @Test
    public void testEntRuntimeException_formatted_and_terminated() {
        EntRuntimeException ex;

        try {
            ex = null;
            throw new EntRuntimeException("something %s", "new");
        } catch (EntRuntimeException e) {
            ex = e;
        }

        Assert.assertNotNull("something else", ex);
        Assert.assertEquals("something new", ex.getMessage());
        Assert.assertNull(ex.getCause());

        try {
            ex = null;
            throw new EntRuntimeException("something %s", "new", new RuntimeException("something else"));
        } catch (EntRuntimeException e) {
            ex = e;
        }

        Assert.assertNotNull("something else", ex);
        Assert.assertEquals("something new", ex.getMessage());
        Assert.assertEquals(java.lang.RuntimeException.class, ex.getCause().getClass());

        // IEntException.TERMINATOR is interpreted and skipped if at the end of the parameters
        try {
            ex = null;
            throw new EntRuntimeException("something %s %s", "new", new RuntimeException("something else"),
                    IEntException.TERM);
        } catch (EntRuntimeException e) {
            ex = e;
        }

        Assert.assertNotNull("something else", ex);
        Assert.assertEquals("something new java.lang.RuntimeException: something else", ex.getMessage());
        Assert.assertNull(ex.getCause());

        // IEntException.TERMINATOR is only interpreted if it's at the end of the parameters
        try {
            ex = null;
            throw new EntRuntimeException("something %s", IEntException.TERM,
                    new RuntimeException("something else"), IEntException.TERM);
        } catch (EntRuntimeException e) {
            ex = e;
        }

        Assert.assertNotNull("something else", ex);
        Assert.assertThat(ex.getMessage(),
                CoreMatchers.startsWith("something org.entando.entando.ent.exception.IEntException$"));
        Assert.assertNull(ex.getCause());
    }
}
package org.entando.entando.ent.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogger.SanitizationLevel;
import org.entando.entando.ent.util.EntLogging.SanitizedLogger;
import org.junit.jupiter.api.Test;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.SubstituteLogger;

class EntLoggingTest {

    public static final String HEY = "Hey\nThere\rHow\tAre\nYou\rDoing?\t.";
    public static final String HEY_EXP = "Hey\nThere\rHow\tAre\nYou\rDoing?\t.";
    public static final String HEY_EXP_ESC = "Hey_There_How_Are_You_Doing?_.";

    @Test
    void testNonSanitizedLog() {
        LoggerMock mock = new LoggerMock();
        runBaseChecks(EntLogFactory.from(SanitizationLevel.NO_SANITIZATION, mock), mock, HEY_EXP, false);
    }

    @Test
    void testSanitizedLog() {
        LoggerMock mock = new LoggerMock();
        runBaseChecks(EntLogFactory.from(SanitizationLevel.BASIC_SANITIZATION, mock), mock, HEY_EXP_ESC, false);
        runBaseChecks(SanitizedLogger.from(mock), mock, HEY_EXP_ESC, false);
    }

    @Test
    void testFullySanitizedLog() {
        LoggerMock mock = new LoggerMock();
        runBaseChecks(EntLogFactory.from(SanitizationLevel.FULL_SANITIZATION, mock), mock, HEY_EXP_ESC, true);
    }

    public void runBaseChecks(EntLogger sl, LoggerMock mock,
            String expectedHey, boolean affectsSpecials) {

        mock.rst();
        sl.trace(HEY);
        assertEquals("|" + expectedHey, mock.logged);

        mock.rst();
        sl.debug("{}", HEY);
        assertEquals("|{}" + "|" + expectedHey, mock.logged);

        mock.rst();
        sl.error("{}{}{}", "A.", HEY, "B.");
        assertEquals("|{}{}{}|A." + "|" + expectedHey + "|B.", mock.logged);

        mock.rst();
        sl.error("SOME_HEADER:\n{}{}{}", "A.", HEY, "B.");
        assertEquals((affectsSpecials)
                        ? "|SOME_HEADER:_{}{}{}|A." + "|" + expectedHey + "|B."
                        : "|SOME_HEADER:\n{}{}{}|A." + "|" + expectedHey + "|B."
                , mock.logged);

        mock.rst();
        sl.error("SOME_HEADER:\n{}{}{}", "A.", HEY, "B.", new Exception(HEY));
        assertEquals((affectsSpecials)
                        ? "|SOME_HEADER:_{}{}{}|A." + "|" + expectedHey + "|B." + "|java.lang.Exception: " + HEY
                        : "|SOME_HEADER:\n{}{}{}|A." + "|" + expectedHey + "|B." + "|java.lang.Exception: " + HEY
                , mock.logged);
    }

    @Test
    void testModifiers() {
        LoggerMock mock = new LoggerMock();
        EntLogger rln, rlb, rlf;

        rln = EntLogFactory.from(SanitizationLevel.NO_SANITIZATION, mock);
        assertSame(rln, rln.withNoSan());
        rlb = rln.withFullSan();
        assertNotSame(rlb, rln.withBasicSan());
        assertSame(rlb, rln.withFullSan());

        rlb = EntLogFactory.from(SanitizationLevel.BASIC_SANITIZATION, mock);
        assertSame(rlb, rlb.withBasicSan());
        rlf = rlb.withFullSan();
        assertNotSame(rlf, rlb.withBasicSan());
        assertSame(rlf, rlb.withFullSan());

        rlf = EntLogFactory.from(SanitizationLevel.FULL_SANITIZATION, mock);
        assertSame(rlf, rlf.withFullSan());
        rlb = rlf.withBasicSan();
        assertNotSame(rlb, rlf.withFullSan());
        assertSame(rlb, rlf.withBasicSan());
    }

    @Test
    void testBase() {
        LoggerMock mock = new LoggerMock();
        EntLogger rl = EntLogFactory.from(SanitizationLevel.NO_SANITIZATION, mock);
        Marker m = MarkerFactory.getMarker("x");

        assertEquals("LoggerMock", rl.getName());

        mock.level = 0;
        runTestBaseExpectations(mock, rl, m);
        mock.level = 1;
        runTestBaseExpectations(mock, rl, m);
        mock.level = 2;
        runTestBaseExpectations(mock, rl, m);
        mock.level = 3;
        runTestBaseExpectations(mock, rl, m);
        mock.level = 4;
        runTestBaseExpectations(mock, rl, m);
    }

    private void runTestBaseExpectations(LoggerMock mock, EntLogger rl, Marker m) {
        int currLev;

        switch (mock.level) {
            case 0:
                assertTrue(rl.isTraceEnabled());
                assertTrue(rl.isTraceEnabled(m));
            case 1:
                assertTrue(rl.isDebugEnabled());
                assertTrue(rl.isDebugEnabled(m));
            case 2:
                assertTrue(rl.isInfoEnabled());
                assertTrue(rl.isInfoEnabled(m));
            case 3:
                assertTrue(rl.isWarnEnabled());
                assertTrue(rl.isWarnEnabled(m));
            case 4:
                assertTrue(rl.isErrorEnabled());
                assertTrue(rl.isErrorEnabled(m));
        }

        switch (mock.level) {
            case 5:
                assertFalse(rl.isErrorEnabled());
                assertFalse(rl.isErrorEnabled(m));
            case 4:
                assertFalse(rl.isWarnEnabled());
                assertFalse(rl.isWarnEnabled(m));
            case 3:
                assertFalse(rl.isInfoEnabled());
                assertFalse(rl.isInfoEnabled(m));
            case 2:
                assertFalse(rl.isDebugEnabled());
                assertFalse(rl.isDebugEnabled(m));
            case 1:
                assertFalse(rl.isTraceEnabled());
                assertFalse(rl.isTraceEnabled(m));
        }

        // TRACE
        currLev = 0;

        mock.rst();
        rl.trace("A");
        assertEquals((mock.level > currLev) ? "" : "|A", mock.logged);
        mock.rst();
        rl.trace("A{}", "B");
        assertEquals((mock.level > currLev) ? "" : "|A{}|B", mock.logged);
        mock.rst();
        rl.trace("A{}{}", "B", "C");
        assertEquals((mock.level > currLev) ? "" : "|A{}{}|B|C", mock.logged);
        mock.rst();
        rl.trace("A{}{}{}", "B", "C", "D");
        assertEquals((mock.level > currLev) ? "" : "|A{}{}{}|B|C|D", mock.logged);
        mock.rst();
        rl.trace("A", new Exception("X"));
        assertEquals((mock.level > currLev) ? "" : "|A|java.lang.Exception: X", mock.logged);

        mock.rst();
        rl.trace(m, "A");
        assertEquals((mock.level > currLev) ? "" : "|x|A", mock.logged);
        mock.rst();
        rl.trace(m, "A{}", "B");
        assertEquals((mock.level > currLev) ? "" : "|x|A{}|B", mock.logged);
        mock.rst();
        rl.trace(m, "A{}{}", "B", "C");
        assertEquals((mock.level > currLev) ? "" : "|x|A{}{}|B|C", mock.logged);
        mock.rst();
        rl.trace(m, "A{}{}{}", "B", "C","D");
        assertEquals((mock.level > currLev) ? "" : "|x|A{}{}{}|B|C|D", mock.logged);
        mock.rst();
        rl.trace(m, "A", new Exception("X"));
        assertEquals((mock.level > currLev) ? "" : "|x|A|java.lang.Exception: X", mock.logged);

        // DEBUG
        currLev = 1;

        mock.rst();
        rl.debug("A");
        assertEquals((mock.level > currLev) ? "" : "|A", mock.logged);
        mock.rst();
        rl.debug("A{}", "B");
        assertEquals((mock.level > currLev) ? "" : "|A{}|B", mock.logged);
        mock.rst();
        rl.debug("A{}{}", "B", "C");
        assertEquals((mock.level > currLev) ? "" : "|A{}{}|B|C", mock.logged);
        mock.rst();
        rl.debug("A{}{}{}", "B", "C", "D");
        assertEquals((mock.level > currLev) ? "" : "|A{}{}{}|B|C|D", mock.logged);
        mock.rst();
        rl.debug("A", new Exception("X"));
        assertEquals((mock.level > currLev) ? "" : "|A|java.lang.Exception: X", mock.logged);

        mock.rst();
        rl.debug(m, "A");
        mock.rst();
        rl.debug(m, "A{}", "B");
        assertEquals((mock.level > currLev) ? "" : "|x|A{}|B", mock.logged);
        mock.rst();
        rl.debug(m, "A{}{}", "B", "C");
        assertEquals((mock.level > currLev) ? "" : "|x|A{}{}|B|C", mock.logged);
        mock.rst();
        rl.debug(m, "A{}{}{}", "B", "C", "D");
        assertEquals((mock.level > currLev) ? "" : "|x|A{}{}{}|B|C|D", mock.logged);
        mock.rst();
        rl.debug(m, "A", new Exception("X"));
        assertEquals((mock.level > currLev) ? "" : "|x|A|java.lang.Exception: X", mock.logged);

        // INFO
        currLev = 2;

        mock.rst();
        rl.info("A");
        assertEquals((mock.level > currLev) ? "" : "|A", mock.logged);
        mock.rst();
        rl.info("A{}", "B");
        mock.rst();
        rl.info("A{}{}", "B", "C");
        assertEquals((mock.level > currLev) ? "" : "|A{}{}|B|C", mock.logged);
        mock.rst();
        rl.info("A{}{}{}", "B", "C", "D");
        assertEquals((mock.level > currLev) ? "" : "|A{}{}{}|B|C|D", mock.logged);
        mock.rst();
        rl.info("A", new Exception("X"));
        assertEquals((mock.level > currLev) ? "" : "|A|java.lang.Exception: X", mock.logged);

        mock.rst();
        rl.info(m, "A");
        assertEquals((mock.level > currLev) ? "" : "|x|A", mock.logged);
        mock.rst();
        rl.info(m, "A{}", "B");
        assertEquals((mock.level > currLev) ? "" : "|x|A{}|B", mock.logged);
        mock.rst();
        rl.info(m, "A{}{}", "B", "C");
        assertEquals((mock.level > currLev) ? "" : "|x|A{}{}|B|C", mock.logged);
        mock.rst();
        rl.info(m, "A{}{}{}", "B", "C", "D");
        assertEquals((mock.level > currLev) ? "" : "|x|A{}{}{}|B|C|D", mock.logged);
        mock.rst();
        rl.info(m, "A", new Exception("X"));
        assertEquals((mock.level > currLev) ? "" : "|x|A|java.lang.Exception: X", mock.logged);

        // WARN
        currLev = 3;

        mock.rst();
        rl.warn("A");
        assertEquals((mock.level > currLev) ? "" : "|A", mock.logged);
        mock.rst();
        rl.warn("A{}", "B");
        assertEquals((mock.level > currLev) ? "" : "|A{}|B", mock.logged);
        mock.rst();
        rl.warn("A{}{}", "B", "C");
        assertEquals((mock.level > currLev) ? "" : "|A{}{}|B|C", mock.logged);
        mock.rst();
        rl.warn("A{}{}{}", "B", "C", "D");
        assertEquals((mock.level > currLev) ? "" : "|A{}{}{}|B|C|D", mock.logged);
        mock.rst();
        rl.warn("A", new Exception("X"));
        assertEquals((mock.level > currLev) ? "" : "|A|java.lang.Exception: X", mock.logged);

        mock.rst();
        rl.warn(m, "A");
        assertEquals((mock.level > currLev) ? "" : "|x|A", mock.logged);
        mock.rst();
        rl.warn(m,"A{}", "B");
        assertEquals((mock.level > currLev) ? "" : "|x|A{}|B", mock.logged);
        mock.rst();
        rl.warn(m, "A{}{}", "B", "C");
        assertEquals((mock.level > currLev) ? "" : "|x|A{}{}|B|C", mock.logged);
        mock.rst();
        rl.warn(m, "A{}{}{}", "B", "C", "D");
        assertEquals((mock.level > currLev) ? "" : "|x|A{}{}{}|B|C|D", mock.logged);
        mock.rst();
        rl.warn(m, "A", new Exception("X"));
        assertEquals((mock.level > currLev) ? "" : "|x|A|java.lang.Exception: X", mock.logged);

        // ERROR
        currLev = 4;

        mock.rst();
        rl.error("A");
        assertEquals((mock.level > currLev) ? "" : "|A", mock.logged);
        mock.rst();
        rl.error("A{}", "B");
        assertEquals((mock.level > currLev) ? "" : "|A{}|B", mock.logged);
        mock.rst();
        rl.error("A{}{}", "B", "C");
        assertEquals((mock.level > currLev) ? "" : "|A{}{}|B|C", mock.logged);
        mock.rst();
        rl.error("A{}{}{}", "B", "C", "D");
        assertEquals((mock.level > currLev) ? "" : "|A{}{}{}|B|C|D", mock.logged);
        mock.rst();
        rl.error("A", new Exception("X"));
        assertEquals((mock.level > currLev) ? "" : "|A|java.lang.Exception: X", mock.logged);

        mock.rst();
        rl.error(m, "A");
        assertEquals((mock.level > currLev) ? "" : "|x|A", mock.logged);
        mock.rst();
        rl.error(m, "A{}", "B");
        assertEquals((mock.level > currLev) ? "" : "|x|A{}|B", mock.logged);
        mock.rst();
        rl.error(m, "A{}{}", "B", "C");
        assertEquals((mock.level > currLev) ? "" : "|x|A{}{}|B|C", mock.logged);
        mock.rst();
        rl.error(m, "A{}{}{}", "B", "C", "D");
        assertEquals((mock.level > currLev) ? "" : "|x|A{}{}{}|B|C|D", mock.logged);
        mock.rst();
        rl.error(m, "A", new Exception("X"));
        assertEquals((mock.level > currLev) ? "" : "|x|A|java.lang.Exception: X", mock.logged);
    }

    @Test
    void testFactory() {
        EntLogger logger;

        logger = EntLogFactory.getLogger(SanitizationLevel.NO_SANITIZATION, this.getClass());
        assertEquals(SanitizationLevel.NO_SANITIZATION, logger.getSanitizationLevel());

        logger = EntLogFactory.getSanitizedLogger(this.getClass());
        assertEquals(SanitizationLevel.BASIC_SANITIZATION, logger.getSanitizationLevel());

        logger = EntLogFactory.getLogger(SanitizationLevel.BASIC_SANITIZATION, this.getClass());
        assertEquals(SanitizationLevel.BASIC_SANITIZATION, logger.getSanitizationLevel());

        logger = EntLogFactory.getLogger(SanitizationLevel.FULL_SANITIZATION, this.getClass());
        assertEquals(SanitizationLevel.FULL_SANITIZATION, logger.getSanitizationLevel());
    }

    @Test
    void testCase01() {
        LoggerMock mock = new LoggerMock();
        SanitizedLogger.from(mock).error("A{}", HEY);
        assertEquals("|A{}" + "|" + HEY_EXP_ESC, mock.logged);
    }

    static class LoggerMock extends SubstituteLogger {

        public String logged = null;
        public int level = 0;

        public LoggerMock() {
            super("LoggerMock", null, false);
        }

        private void delegate_print(Object... arguments) {
            String s = "";
            for (Object e : arguments) {
                if (e instanceof Marker) {
                    s = s.concat("|" + ((Marker) e).getName());
                } else if (e instanceof Object[]) {
                    for (Object ee : (Object[]) e) {
                        s = s.concat("|" + ee.toString());
                    }
                } else {
                    s = s.concat("|" + e.toString());
                }
            }
            logged = s;
        }

        public void rst() {
            this.logged = "";
        }

        @Override
        public String getName() {
            return "LoggerMock";
        }

        @Override
        public boolean isTraceEnabled() {
            return level <= 0;
        }

        @Override
        public void trace(String msg) {
            delegate_print(msg);
        }

        @Override
        public void trace(String format, Object arg) {
            delegate_print(format, arg);
        }

        @Override
        public void trace(String format, Object arg1, Object arg2) {
            delegate_print(format, arg1, arg2);
        }

        @Override
        public void trace(String format, Object... arguments) {
            delegate_print(format, arguments);
        }

        @Override
        public void trace(String msg, Throwable t) {
            delegate_print(msg, t);
        }

        @Override
        public boolean isTraceEnabled(Marker marker) {
            return level <= 0;
        }

        @Override
        public void trace(Marker marker, String msg) {
            delegate_print(marker, msg);
        }

        @Override
        public void trace(Marker marker, String format, Object arg) {
            delegate_print(marker, format, arg);
        }

        @Override
        public void trace(Marker marker, String format, Object arg1, Object arg2) {
            delegate_print(marker, format, arg1, arg2);
        }

        @Override
        public void trace(Marker marker, String format, Object... argArray) {
            delegate_print(marker, format, argArray);
        }

        @Override
        public void trace(Marker marker, String msg, Throwable t) {
            delegate_print(marker, msg, t);
        }

        @Override
        public boolean isDebugEnabled() {
            return level <= 1;
        }

        @Override
        public void debug(String msg) {
            delegate_print(msg);
        }

        @Override
        public void debug(String format, Object arg) {
            delegate_print(format, arg);
        }

        @Override
        public void debug(String format, Object arg1, Object arg2) {
            delegate_print(format, arg1, arg2);
        }

        @Override
        public void debug(String format, Object... arguments) {
            delegate_print(format, arguments);
        }

        @Override
        public void debug(String msg, Throwable t) {
            delegate_print(msg, t);
        }

        @Override
        public boolean isDebugEnabled(Marker marker) {
            return level <= 1;
        }

        @Override
        public void debug(Marker marker, String msg) {
            delegate_print(marker, msg);
        }

        @Override
        public void debug(Marker marker, String format, Object arg) {
            delegate_print(marker, format, arg);
        }

        @Override
        public void debug(Marker marker, String format, Object arg1, Object arg2) {
            delegate_print(marker, format, arg1, arg2);
        }

        @Override
        public void debug(Marker marker, String format, Object... arguments) {
            delegate_print(marker, format, arguments);
        }

        @Override
        public void debug(Marker marker, String msg, Throwable t) {
            delegate_print(marker, msg, t);
        }

        @Override
        public boolean isInfoEnabled() {
            return level <= 2;
        }

        @Override
        public void info(String msg) {
            delegate_print(msg);
        }

        @Override
        public void info(String format, Object arg) {
            delegate_print(format, arg);
        }

        @Override
        public void info(String format, Object arg1, Object arg2) {
            delegate_print(format, arg1, arg2);
        }

        @Override
        public void info(String format, Object... arguments) {
            delegate_print(format, arguments);
        }

        @Override
        public void info(String msg, Throwable t) {
            delegate_print(msg, t);
        }

        @Override
        public boolean isInfoEnabled(Marker marker) {
            return level <= 2;
        }

        @Override
        public void info(Marker marker, String msg) {
            delegate_print(marker, msg);
        }

        @Override
        public void info(Marker marker, String format, Object arg) {
            delegate_print(marker, format, arg);
        }

        @Override
        public void info(Marker marker, String format, Object arg1, Object arg2) {
            delegate_print(marker, format, arg1, arg2);
        }

        @Override
        public void info(Marker marker, String format, Object... arguments) {
            delegate_print(marker, format, arguments);
        }

        @Override
        public void info(Marker marker, String msg, Throwable t) {
            delegate_print(marker, msg, t);
        }

        @Override
        public boolean isWarnEnabled() {
            return level <= 3;
        }

        @Override
        public void warn(String msg) {
            delegate_print(msg);
        }

        @Override
        public void warn(String format, Object arg) {
            delegate_print(format, arg);
        }

        @Override
        public void warn(String format, Object... arguments) {
            delegate_print(format, arguments);
        }

        @Override
        public void warn(String format, Object arg1, Object arg2) {
            delegate_print(format, arg1, arg2);
        }

        @Override
        public void warn(String msg, Throwable t) {
            delegate_print(msg, t);
        }

        @Override
        public boolean isWarnEnabled(Marker marker) {
            return level <= 3;
        }

        @Override
        public void warn(Marker marker, String msg) {
            delegate_print(marker, msg);
        }

        @Override
        public void warn(Marker marker, String format, Object arg) {
            delegate_print(marker, format, arg);
        }

        @Override
        public void warn(Marker marker, String format, Object arg1, Object arg2) {
            delegate_print(marker, format, arg1, arg2);
        }

        @Override
        public void warn(Marker marker, String format, Object... arguments) {
            delegate_print(marker, format, arguments);
        }

        @Override
        public void warn(Marker marker, String msg, Throwable t) {
            delegate_print(marker, msg, t);
        }

        @Override
        public boolean isErrorEnabled() {
            return level <= 4;
        }

        @Override
        public void error(String msg) {
            delegate_print(msg);
        }

        @Override
        public void error(String format, Object arg) {
            delegate_print(format, arg);
        }

        @Override
        public void error(String format, Object arg1, Object arg2) {
            delegate_print(format, arg1, arg2);
        }

        @Override
        public void error(String format, Object... arguments) {
            delegate_print(format, arguments);
        }

        @Override
        public void error(String msg, Throwable t) {
            delegate_print(msg, t);
        }

        @Override
        public boolean isErrorEnabled(Marker marker) {
            return level <= 4;
        }

        @Override
        public void error(Marker marker, String msg) {
            delegate_print(marker, msg);
        }

        @Override
        public void error(Marker marker, String format, Object arg) {
            delegate_print(marker, format, arg);
        }

        @Override
        public void error(Marker marker, String format, Object arg1, Object arg2) {
            delegate_print(marker, format, arg1, arg2);
        }

        @Override
        public void error(Marker marker, String format, Object... arguments) {
            delegate_print(marker, format, arguments);
        }

        @Override
        public void error(Marker marker, String msg, Throwable t) {
            delegate_print(marker, msg, t);
        }
    }
}

package com.agiletec.aps.system;

import com.agiletec.aps.system.ApsSystemUtils.ApsDeepDebug;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import static org.junit.jupiter.api.Assertions.*;

class ApsSystemUtilsTest {

    @Test
    void testDeepDebug() {
        try (MockedStatic<ApsSystemUtils> mock = Mockito.mockStatic(ApsSystemUtils.class, InvocationOnMock::callRealMethod)) {
            mock.when(ApsSystemUtils::getDeepDebugFF).thenReturn("");
            assertFalse(ApsDeepDebug.print("test print"));
        }
    }

    @Test
    void testDeepDebug2() {
        try (MockedStatic<ApsSystemUtils> mock = Mockito.mockStatic(ApsSystemUtils.class, InvocationOnMock::callRealMethod)) {
            mock.when(ApsSystemUtils::getDeepDebugFF).thenReturn("true");
            assertEquals(ApsSystemUtils.getDeepDebugFF(), "true");
            assertTrue(ApsDeepDebug.print("test print"));
        }
    }

    @Test
    void test_isTagEnabled() {
        assertFalse(ApsSystemUtils.isTagEnabled(null, "TAG1"));
        assertTrue(ApsSystemUtils.isTagEnabled("TAG1,TAG2", "TAG1"));
        assertFalse(ApsSystemUtils.isTagEnabled("TAG1,TAG2", "TAG11"));
        assertFalse(ApsSystemUtils.isTagEnabled("TAG1_,TAG2", "TAG1"));

        assertTrue(ApsSystemUtils.isTagEnabled("TAG1,TAG2", "TAG2"));
        assertTrue(ApsSystemUtils.isTagEnabled("TAG1,TAG2,TAG3", "TAG2"));
        assertTrue(ApsSystemUtils.isTagEnabled("CAT", "CAT:TAG2"));
        assertTrue(ApsSystemUtils.isTagEnabled("TAG1,TAG2,CAT", "CAT:TAG2"));
        assertTrue(ApsSystemUtils.isTagEnabled("TAG1,TAG2,CAT", "CAT"));
    }

    @Test
    void test_isFeatureEnabled() {
        try (MockedStatic<ApsSystemUtils> utilsMock = Mockito.mockStatic(ApsSystemUtils.class, InvocationOnMock::callRealMethod)) {
            utilsMock.when(ApsSystemUtils::getFeatureFlags).thenReturn("A-FEATURE");
            assertTrue(ApsSystemUtils.isFeatureEnabled("A-FEATURE"));
            assertTrue(ApsSystemUtils.isFeatureEnabled("A-FEATURE:SUB-FEATURE"));
            assertFalse(ApsSystemUtils.isFeatureEnabled("A-FEATURE_"));
        }
    }
}

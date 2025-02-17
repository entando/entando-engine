package com.agiletec.aps.system;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.agiletec.aps.system.ApsSystemUtils.ApsDeepDebug;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

class ApsSystemUtilsTest {

    @Test
    void testDeepDebug() {
        System.setProperty("org.entando.deepDebug", "");
        assertFalse(ApsDeepDebug.print("test print"));
        assertTrue(ApsDeepDebug.print(null, ApsDeepDebug.FORCE_TAG_PREFIX, "test print"));
    }

    @Test
    void testDeepDebug2() {
        System.setProperty("org.entando.deepDebug", "true");
        assertTrue(ApsDeepDebug.print("test print"));
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

    @Test
    void tmp() {
        String baseName =new String(
                "Delibera_N.7'Comitato_Tecnico_d'Innovazionè_10-12-2024".getBytes(StandardCharsets.UTF_8)
                , StandardCharsets.UTF_16
        );
        System.out.println(baseName);

        String purgedName = baseName.replaceAll("[^ _.a-zA-Z0-9-àèéìòùÀÈÉÌÒÙ']", "");
        System.out.println("XXXXXXXXXXXXXXXXXXX");
        System.out.println(purgedName);
        System.out.println("XXXXXXXXXXXXXXXXXXX");
    }
}

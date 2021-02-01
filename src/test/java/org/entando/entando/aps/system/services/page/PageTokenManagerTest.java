package org.entando.entando.aps.system.services.page;

import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.security.SecureRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PageTokenManagerTest {

    public static final String TEST_PAGE_CODE = "test_page";

    @Mock
    ConfigInterface mockedConfigManager;

    @Test
    void testTokenEncryptDecrypt() throws Exception {
        PageTokenManager pageTokenManager = new PageTokenManager();
        Mockito.doReturn("ZDQdIPZ0XOc8izJJCiIv").when(mockedConfigManager).getParam("page_preview_hash");
        pageTokenManager.setConfigManager(mockedConfigManager);
        pageTokenManager.init();
        String token = pageTokenManager.encrypt(TEST_PAGE_CODE);
        Assertions.assertNotNull(token);
        String code = pageTokenManager.decrypt(token);
        Assertions.assertNotNull(code);
        Assertions.assertEquals(TEST_PAGE_CODE, code);
    }

    @Mock
    SecureRandom secureRandomMock1;
    @Mock
    SecureRandom secureRandomMock2;

    @Test
    void testMkRandomString() {
        Assertions.assertEquals(33, PageTokenManager.mkRandomString(33).length());
        Mockito.doReturn(0).when(secureRandomMock1).nextInt(Mockito.anyInt());
        Assertions.assertEquals("AAAAAAAAAAAAAAA", PageTokenManager.mkRandomString(secureRandomMock1, 15));
        Mockito.doReturn(60).when(secureRandomMock2).nextInt(Mockito.anyInt());
        Assertions.assertEquals("999999999999999", PageTokenManager.mkRandomString(secureRandomMock2, 15));
    }
}
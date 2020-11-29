package org.entando.entando.aps.system.services.page;

import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.SecureRandom;

@RunWith(MockitoJUnitRunner.class)
public class PageTokenManagerTest {

    public static final String TEST_PAGE_CODE = "test_page";

    @Mock
    ConfigInterface mockedConfigManager;

    @Test
    public void testTokenEncryptDecrypt() throws Exception {
        PageTokenManager pageTokenManager = new PageTokenManager();
        Mockito.doReturn("ZDQdIPZ0XOc8izJJCiIv").when(mockedConfigManager).getParam("page_preview_hash");
        pageTokenManager.setConfigManager(mockedConfigManager);
        pageTokenManager.init();
        String token = pageTokenManager.encrypt(TEST_PAGE_CODE);
        Assert.assertNotNull(token);
        String code = pageTokenManager.decrypt(token);
        Assert.assertNotNull(code);
        Assert.assertEquals(TEST_PAGE_CODE, code);
    }

    @Mock
    SecureRandom secureRandomMock1;
    @Mock
    SecureRandom secureRandomMock2;

    @Test
    public void testMkRandomString() {
        Assert.assertEquals(33, PageTokenManager.mkRandomString(33).length());
        Mockito.doReturn(0).when(secureRandomMock1).nextInt(Mockito.anyInt());
        Assert.assertEquals("AAAAAAAAAAAAAAA", PageTokenManager.mkRandomString(secureRandomMock1, 15));
        Mockito.doReturn(60).when(secureRandomMock2).nextInt(Mockito.anyInt());
        Assert.assertEquals("999999999999999", PageTokenManager.mkRandomString(secureRandomMock2, 15));
    }
}
/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
 */
package org.entando.entando.aps.system.services.oauth2;

import com.agiletec.aps.system.common.FieldSearchFilter;
import org.entando.entando.ent.exception.EntException;
import com.agiletec.aps.util.DateConverter;
import java.util.ArrayList;
import java.util.List;
import org.entando.entando.aps.system.services.oauth2.model.ConsumerRecordVO;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

/**
 * @author E.Santoboni
 */
@ExtendWith(MockitoExtension.class)
class OAuthConsumerManagerTest {

    @Mock
    private IOAuthConsumerDAO consumerDAO;

    @InjectMocks
    private OAuthConsumerManager consumerManager;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getConsumer() throws Exception {
        ConsumerRecordVO record = this.createMockConsumer("key_1", "secret", false);
        when(this.consumerDAO.getConsumer(Mockito.anyString())).thenReturn(record);
        ConsumerRecordVO extracted = this.consumerManager.getConsumerRecord("key");
        Assertions.assertEquals(record, extracted);
        Mockito.verify(consumerDAO, Mockito.times(1)).getConsumer(Mockito.anyString());
    }

    @Test
    void failGetConsumer() throws Exception {
        Assertions.assertThrows(EntException.class, () -> {
            Mockito.doThrow(RuntimeException.class).when(this.consumerDAO).getConsumer(Mockito.anyString());
            try {
                ConsumerRecordVO extracted = this.consumerManager.getConsumerRecord("key");
            } catch (EntException e) {
                throw e;
            } finally {
                Mockito.verify(consumerDAO, Mockito.times(1)).getConsumer(Mockito.anyString());
            }
        });
    }

    @Test
    void addConsumer() throws Exception {
        ConsumerRecordVO record = this.createMockConsumer("key_1", "secret", false);
        this.consumerManager.addConsumer(record);
        Mockito.verify(consumerDAO, Mockito.times(1)).addConsumer(Mockito.any(ConsumerRecordVO.class));
    }

    @Test
    void failAddConsumer() throws Exception {
        Assertions.assertThrows(EntException.class, () -> {
            ConsumerRecordVO record = this.createMockConsumer("key_2", "secret", false);
            Mockito.doThrow(RuntimeException.class).when(this.consumerDAO).addConsumer(record);
            try {
                this.consumerManager.addConsumer(record);
            } catch (EntException e) {
                throw e;
            } finally {
                Mockito.verify(consumerDAO, Mockito.times(1)).addConsumer(Mockito.any(ConsumerRecordVO.class));
            }
        });
    }

    @Test
    void updateConsumer() throws Exception {
        ConsumerRecordVO record = this.createMockConsumer("key_1", "secret", false);
        this.consumerManager.updateConsumer(record);
        Mockito.verify(consumerDAO, Mockito.times(1)).updateConsumer(Mockito.any(ConsumerRecordVO.class));
    }

    @Test
    void failUpdateConsumer() throws Exception {
        Assertions.assertThrows(EntException.class, () -> {
            ConsumerRecordVO record = this.createMockConsumer("key_2", "secret", false);
            Mockito.doThrow(RuntimeException.class).when(this.consumerDAO).updateConsumer(record);
            try {
                this.consumerManager.updateConsumer(record);
            } catch (EntException e) {
                throw e;
            } finally {
                Mockito.verify(consumerDAO, Mockito.times(1)).updateConsumer(Mockito.any(ConsumerRecordVO.class));
            }
        });
    }

    @Test
    void deleteConsumer() throws Exception {
        this.consumerManager.deleteConsumer("key_test_1");
        Mockito.verify(consumerDAO, Mockito.times(1)).deleteConsumer(Mockito.anyString());
    }

    @Test
    void failDeleteConsumer() throws Exception {
        Assertions.assertThrows(EntException.class, () -> {
            Mockito.doThrow(RuntimeException.class).when(this.consumerDAO).deleteConsumer(Mockito.anyString());
            try {
                this.consumerManager.deleteConsumer("key_test_2");
            } catch (EntException e) {
                throw e;
            } finally {
                Mockito.verify(consumerDAO, Mockito.times(1)).deleteConsumer(Mockito.anyString());
            }
        });
    }

    @Test
    void getConsumerKeys() throws Exception {
        List<String> mockKeys = new ArrayList<>();
        mockKeys.add("key_1");
        when(this.consumerDAO.getConsumerKeys(Mockito.any(FieldSearchFilter[].class))).thenReturn(mockKeys);
        List<String> keys = this.consumerManager.getConsumerKeys(new FieldSearchFilter[]{});
        Assertions.assertNotNull(keys);
        Assertions.assertEquals(1, keys.size());
        Mockito.verify(consumerDAO, Mockito.times(1)).getConsumerKeys(Mockito.any(FieldSearchFilter[].class));
    }

    @Test
    void failGetConsumerKeys_2() throws Exception {
        Assertions.assertThrows(EntException.class, () -> {
            Mockito.doThrow(RuntimeException.class).when(this.consumerDAO).getConsumerKeys(Mockito.any(FieldSearchFilter[].class));
            try {
                List<String> keys = this.consumerManager.getConsumerKeys(new FieldSearchFilter[]{});
            } catch (EntException e) {
                throw e;
            } finally {
                Mockito.verify(consumerDAO, Mockito.times(1)).getConsumerKeys(Mockito.any(FieldSearchFilter[].class));
            }
        });
    }

    @Test
    void loadClient() throws Exception {
        ConsumerRecordVO record = this.createMockConsumer("key_1", "secret", false);
        when(this.consumerDAO.getConsumer(Mockito.anyString())).thenReturn(record);
        ClientDetails extracted = this.consumerManager.loadClientByClientId("key_1");
        Assertions.assertNotNull(extracted);
        Mockito.verify(consumerDAO, Mockito.times(1)).getConsumer(Mockito.anyString());
    }

    @Test
    void loadClientNotFound() throws Exception {
        Assertions.assertThrows(ClientRegistrationException.class, () -> {
            ConsumerRecordVO record = this.createMockConsumer("key_1", "secret", true);
            when(this.consumerDAO.getConsumer(Mockito.anyString())).thenReturn(record);
            try {
                ClientDetails extracted = this.consumerManager.loadClientByClientId("key_1");
            } catch (ClientRegistrationException e) {
                throw e;
            } finally {
                Mockito.verify(consumerDAO, Mockito.times(1)).getConsumer(Mockito.anyString());
            }
        });
    }

    @Test
    void loadClientNotFound_2() throws Exception {
        Assertions.assertThrows(ClientRegistrationException.class, () -> {
            when(this.consumerDAO.getConsumer(Mockito.anyString())).thenReturn(null);
            try {
                ClientDetails extracted = this.consumerManager.loadClientByClientId("key_1");
            } catch (ClientRegistrationException e) {
                throw e;
            } finally {
                Mockito.verify(consumerDAO, Mockito.times(1)).getConsumer(Mockito.anyString());
            }
        });
    }

    @Test
    void loadClientNotFound_3() throws Exception {
        Assertions.assertThrows(ClientRegistrationException.class, () -> {
            when(this.consumerDAO.getConsumer(Mockito.anyString())).thenThrow(RuntimeException.class);
            try {
                ClientDetails extracted = this.consumerManager.loadClientByClientId("key_1");
            } catch (ClientRegistrationException e) {
                throw e;
            } finally {
                Mockito.verify(consumerDAO, Mockito.times(1)).getConsumer(Mockito.anyString());
            }
        });
    }

    private ConsumerRecordVO createMockConsumer(String key, String secret, boolean expired) {
        ConsumerRecordVO consumer = new ConsumerRecordVO();
        consumer.setAuthorizedGrantTypes("password");
        consumer.setCallbackUrl("http://test.test");
        consumer.setDescription("Test Description");
        if (expired) {
            consumer.setExpirationDate(DateConverter.parseDate("2000", "yyyy"));
        }
        consumer.setKey(key);
        consumer.setName("Test Name");
        consumer.setScope("trust");
        consumer.setSecret(secret);
        return consumer;
    }

}

/*
 * Copyright 2021-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package com.agiletec.aps.system.common.notify;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.IManager;
import com.agiletec.aps.system.services.page.Page;
import com.agiletec.aps.system.services.page.events.PageChangedEvent;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class NotifyManagerTest {
    
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ListableBeanFactory beanFactory;

    @InjectMocks
    private ExtNotifyManager notifyManager;
    
    @Test
    void testEvent_1() throws Exception {
        Page mockPage = Mockito.mock(Page.class);
        Mockito.when(mockPage.getCode()).thenReturn("testPage");
        Map<String, String> properties = new HashMap<>();
        properties.put("operationCode", String.valueOf(PageChangedEvent.INSERT_OPERATION_CODE));
        properties.put("pageCode", mockPage.getCode());
        PageChangedEvent event = new PageChangedEvent(SystemConstants.PAGE_EVENT_CHANNEL, properties);
        Assertions.assertEquals(SystemConstants.PAGE_EVENT_CHANNEL, event.getChannel());
        String message = event.getMessage();
        Assertions.assertNotNull(message);
        Map<String, String> extracted = PageChangedEvent.getProperties(message);
        Assertions.assertEquals(2, extracted.size());
        Assertions.assertEquals(String.valueOf(PageChangedEvent.INSERT_OPERATION_CODE), extracted.get("operationCode"));
        Assertions.assertEquals(mockPage.getCode(), extracted.get("pageCode"));
        notifyManager.publishEvent(event);
        Mockito.verify(eventPublisher, Mockito.times(1)).publishEvent(event);
    }

    @Test
    void testEvent_2() throws Exception {
        Mockito.when(beanFactory.getBeanNamesForType(Mockito.any(Class.class))).thenReturn(new String[]{"beanName"});
        ObserverService observer = Mockito.mock(ObserverService.class);
        Mockito.when(beanFactory.getBean("beanName")).thenReturn(observer);
        PageChangedEvent event = new PageChangedEvent();
        notifyManager.notify(event);
        Mockito.verify(beanFactory, Mockito.times(1)).getBeanNamesForType(Mockito.any(Class.class));
        Mockito.verify(observer, Mockito.times(1)).update(event);
    }

    @Test
    void testEvent_3() throws Exception {
        ApsEvent event = new ApsEvent("test-channel", new HashMap<String, String>()) {
                @Override
                public void notify(IManager im) {
                    return;
                }
                @Override
                public Class getObserverInterface() {
                    return null;
                }
            };
        Assertions.assertEquals("test-channel", event.getChannel());
        String message = event.getMessage();
        Assertions.assertNotNull(message);
        Assertions.assertEquals("{\"event\":{}}", message);
        notifyManager.publishEvent(event);
        Mockito.verify(eventPublisher, Mockito.times(1)).publishEvent(event);
        notifyManager.notify(event);
        Mockito.verify(beanFactory, Mockito.never()).getBeanNamesForType(Mockito.any(Class.class));
    }
    
    static class ExtNotifyManager extends NotifyManager {
        @Override
        protected void notify(ApsEvent event) {
            super.notify(event);
        }
    }
    
}

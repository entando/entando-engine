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

import com.agiletec.aps.system.services.page.IPage;
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
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class NotifyManagerTest {
    
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private NotifyManager notifyManager;

    @Test
    void testEvent() throws Exception {
        IPage mockPage = Mockito.mock(IPage.class);
        Mockito.when(mockPage.getCode()).thenReturn("testPage");
        Map<String, String> properties = new HashMap<>();
        properties.put("operationCode", String.valueOf(PageChangedEvent.INSERT_OPERATION_CODE));
        properties.put("pageCode", mockPage.getCode());
        PageChangedEvent event = new PageChangedEvent("page", properties);
        Assertions.assertEquals("page", event.getChannel());
        String message = event.getMessage();
        Assertions.assertNotNull(message);
        Map<String, String> extracted = PageChangedEvent.getProperties(message);
        Assertions.assertEquals(2, extracted.size());
        Assertions.assertEquals(String.valueOf(PageChangedEvent.INSERT_OPERATION_CODE), extracted.get("operationCode"));
        Assertions.assertEquals(mockPage.getCode(), extracted.get("pageCode"));
        notifyManager.publishEvent(event);
        Mockito.verify(eventPublisher, Mockito.times(1)).publishEvent(event);
    }

}

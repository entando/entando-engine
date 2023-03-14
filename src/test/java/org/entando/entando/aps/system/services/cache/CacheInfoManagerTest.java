/*
 * Copyright 2018-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.cache;

import com.agiletec.aps.system.services.page.Page;
import com.agiletec.aps.system.services.page.events.PageChangedEvent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.mockito.*;
import org.springframework.cache.*;

import java.util.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author E.Santoboni
 */
@ExtendWith(MockitoExtension.class)
class CacheInfoManagerTest {
	
	@Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @Mock
    private Cache.ValueWrapper valueWrapperForExpirationTime;
	
	@Mock
    private Cache.ValueWrapper valueWrapperForGroups;
	
    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

	@InjectMocks
    private CacheInfoManager cacheInfoManager;
	
	@BeforeEach
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Map<String, Date> map = new HashMap<>();
		Mockito.lenient().when(valueWrapperForExpirationTime.get()).thenReturn(map);
		Mockito.lenient().when(cache.get(Mockito.startsWith(ICacheInfoManager.EXPIRATIONS_CACHE_NAME_PREFIX))).thenReturn(valueWrapperForExpirationTime);
		Map<String, List<String>> groupsMap = new HashMap<>();
		List<String> list_a = Arrays.asList("key_a1", "key_a2", "key_a3");
		List<String> list_b = Arrays.asList("key_b1", "key_b2", "key_b3", "key_b4");
		groupsMap.put("group_1", new ArrayList<>(list_a));
		groupsMap.put("group_2", new ArrayList<>(list_b));
		Mockito.lenient().when(valueWrapperForGroups.get()).thenReturn(groupsMap);
		Mockito.lenient().when(cache.get(Mockito.startsWith(ICacheInfoManager.GROUP_CACHE_NAME_PREFIX))).thenReturn(valueWrapperForGroups);
		Mockito.lenient().when(cacheManager.getCache(Mockito.anyString())).thenReturn(this.cache);
	}
	
    @Test
    void setExpirationTimeInMinutes() {
		String targetCache = "targetCacheName1";
		String cacheKey = "testkey1";
		cacheInfoManager.putInCache(targetCache, cacheKey, "Some value");
        cacheInfoManager.setExpirationTime(targetCache, cacheKey, 1);
		boolean expired = cacheInfoManager.isExpired(targetCache, cacheKey);
		Assertions.assertFalse(expired);
    }
	
    @Test
    void setExpirationTimeInSeconds() throws Throwable {
		String targetCache = "targetCacheName2";
		String cacheKey = "testkey2";
		cacheInfoManager.putInCache(targetCache, cacheKey, "Some other value");
        cacheInfoManager.setExpirationTime(targetCache, cacheKey, 1L);
		boolean expired = cacheInfoManager.isExpired(targetCache, cacheKey);
		Assertions.assertFalse(expired);
		synchronized (this) {
    		this.wait(2000);
		}
		boolean expired2 = cacheInfoManager.isExpired(targetCache, cacheKey);
		Assertions.assertTrue(expired2);
    }
	
    @Test
    void updateFromPageChanged() {
		PageChangedEvent event = new PageChangedEvent();
		Page page = new Page();
		page.setCode("code");
		event.setPage(page);
        cacheInfoManager.updateFromPageChanged(event);
		Mockito.verify(cache, Mockito.times(1)).get(Mockito.anyString());
		Mockito.verify(cache, Mockito.times(0)).put(Mockito.anyString(), Mockito.any(Map.class));
		Object requiredMap = cacheInfoManager.getFromCache(ICacheInfoManager.CACHE_INFO_MANAGER_CACHE_NAME, 
				ICacheInfoManager.GROUP_CACHE_NAME_PREFIX + ICacheInfoManager.CACHE_INFO_MANAGER_CACHE_NAME);
		Assertions.assertTrue(requiredMap instanceof Map);
		Assertions.assertNotNull(requiredMap);
		Assertions.assertEquals(2, ((Map) requiredMap).size());
    }
	
	@Test
    void destroy() {
		cacheInfoManager.destroy();
		Mockito.verify(cacheManager, Mockito.times(0)).getCacheNames();
		Mockito.verify(cacheManager, Mockito.times(4)).getCache(Mockito.anyString());
		Mockito.verify(cache, Mockito.times(2)).clear();
	}
	
	@Test
    void flushAll() {
		cacheInfoManager.flushAll();
		Mockito.verify(cacheManager, Mockito.times(1)).getCacheNames();
		Mockito.verify(cacheManager, Mockito.times(0)).getCache(Mockito.anyString());
		Mockito.verify(cache, Mockito.times(0)).clear();
	}
	
	@Test
    void flushAllWithCaches() {
		List<String> cacheNames = new ArrayList<>();
		cacheNames.add("cache1");
		cacheNames.add("cache2");
		Mockito.when(cacheManager.getCacheNames()).thenReturn(cacheNames);
		cacheInfoManager.flushAll();
		Mockito.verify(cacheManager, Mockito.times(1)).getCacheNames();
		Mockito.verify(cacheManager, Mockito.times(4)).getCache(Mockito.anyString());
		Mockito.verify(cache, Mockito.times(2)).clear();
	}
	
	@Test
    void flushEntry() {
		String targetCache = "targetCacheName3";
		String cacheKey = "testkey3";
		cacheInfoManager.flushEntry(targetCache, cacheKey);
		Mockito.verify(cacheManager, Mockito.times(1)).getCache(Mockito.eq(targetCache));
		Mockito.verify(cache, Mockito.times(1)).evict(Mockito.eq(cacheKey));
	}
	
	@Test
    void putInCache() {
		String targetCache = "targetCacheName3";
		String cacheKey = "testkey3";
		cacheInfoManager.putInCache(targetCache, cacheKey, "Some value");
		Mockito.verify(cacheManager, Mockito.times(1)).getCache(targetCache);
		Mockito.verify(cache, Mockito.times(1)).put(cacheKey, "Some value");
	}
	
	@Test
    void putInCacheWithGroups() {
		String targetCache = "targetCacheName3";
		String cacheKey = "testkey3";
		String[] groups = new String[]{"group_1", "group_2"};
		cacheInfoManager.putInCache(targetCache, cacheKey, "Some value", groups);
		Mockito.verify(cacheManager, Mockito.times(1)).getCache(targetCache);
		Mockito.verify(cache, Mockito.times(1)).put(cacheKey, "Some value");
		Mockito.verify(cacheManager, Mockito.times(1)).getCache(ICacheInfoManager.CACHE_INFO_MANAGER_CACHE_NAME);
	}
	
	@Test
    void putInGroup() {
		String targetCache = "targetCacheName4";
		String cacheKey = "testkey4";
		String[] groups = new String[]{"group_1", "group_2"};
		cacheInfoManager.putInGroup(targetCache, cacheKey, groups);
		Mockito.verify(cacheManager, Mockito.times(0)).getCache(targetCache);
		Mockito.verify(cache, Mockito.times(0)).put(Mockito.eq(cacheKey), Mockito.anyString());
		Mockito.verify(cacheManager, Mockito.times(1)).getCache(ICacheInfoManager.CACHE_INFO_MANAGER_CACHE_NAME);
	}
	
	@Test
    void flushGroup_1() {
		this.flushGroup("group_1", 3);
	}
	
	@Test
    void flushGroup_2() {
		this.flushGroup("group_2", 4);
	}
	
    private void flushGroup(String groupName, int expectedEvict) {
		String targetCache = "targetCacheName5";
		cacheInfoManager.flushGroup(targetCache, groupName);
		Mockito.verify(cacheManager, Mockito.times(1)).getCache(ICacheInfoManager.CACHE_INFO_MANAGER_CACHE_NAME);
		Mockito.verify(cacheManager, Mockito.times(expectedEvict)).getCache(targetCache);
		Mockito.verify(cache, Mockito.times(expectedEvict)).evict(Mockito.any(Object.class));
		Mockito.verify(cache, Mockito.times(1)).put(Mockito.startsWith(ICacheInfoManager.GROUP_CACHE_NAME_PREFIX), Mockito.any(Object.class));
	}
	
}

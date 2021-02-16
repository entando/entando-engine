/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.entando.entando.aps.system.services.seomapping.cache;

import com.agiletec.aps.system.common.AbstractCacheWrapper;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import org.entando.entando.ent.exception.EntException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.services.seomapping.FriendlyCodeVO;
import org.entando.entando.aps.system.services.seomapping.ISeoMappingDAO;
import org.entando.entando.aps.system.services.seomapping.ISeoMappingManager;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.springframework.cache.Cache;

/**
 * @author E.Santoboni
 */
public class SeoMappingCacheWrapper extends AbstractCacheWrapper implements ISeoMappingCacheWrapper {

	private static final EntLogger _logger =  EntLogFactory.getSanitizedLogger(SeoMappingCacheWrapper.class);

    @Override
    public void release(ISeoMappingDAO seoMappingDAO) {
        Cache cache = this.getCache();
        this.releaseCachedObjects(cache, MAPPING_BY_CODE_CACHE_KEYS, MAPPING_BY_CODE_CACHE_KEY_PREFIX);
        List<String> objectTypes = seoMappingDAO.getObjectTypes();
        for (String objectType : objectTypes) {
            this.releaseCachedObjects(cache, this.getMappingKeys(objectType), this.getMappingKeyPrefix(objectType));
        }
        cache.evict(DRAFT_PAGES_MAPPING);
    }
    
    @Override
    public void release() {
        // nothing to do
    }

	@Override
	public void initCache(IPageManager pageManager, ISeoMappingDAO seoMappingDAO, boolean initDraftPageMapping) throws EntException {
		try {
            Map<String, FriendlyCodeVO> mapping = seoMappingDAO.loadMapping();
            Map<String, Map<String, FriendlyCodeVO>> objectMapping = new HashMap<>();
			Iterator<FriendlyCodeVO> codesIter = mapping.values().iterator();
			while (codesIter.hasNext()) {
				FriendlyCodeVO currentCode = codesIter.next();
                String type = currentCode.getObjectType();
                Map<String, FriendlyCodeVO> mappingForType = objectMapping.get(type);
                if (null == mappingForType) {
                    mappingForType = new HashMap<>();
                    objectMapping.put(type, mappingForType);
                }
                mappingForType.put(currentCode.getObjectCode(), currentCode);
			}
            Cache cache = this.getCache();
            this.insertAndCleanVoObjectsOnCache(cache, mapping, MAPPING_BY_CODE_CACHE_KEYS, MAPPING_BY_CODE_CACHE_KEY_PREFIX);
            Iterator<String> typesIter = objectMapping.keySet().iterator();
            while (typesIter.hasNext()) {
                String objectType = typesIter.next();
                this.insertAndCleanVoObjectsOnCache(cache, objectMapping.get(objectType), this.getMappingKeys(objectType), this.getMappingKeyPrefix(objectType));
            }
            if (initDraftPageMapping) {
                this.createDraftPagesMapping(pageManager, cache);
            }
		} catch (Exception e) {
			_logger.error("Error loading seo mapper", e);
			throw new EntException("Error loading seo mapper", e);
		}
	}
    
    protected Map<String, String> createDraftPagesMapping(IPageManager pageManager, Cache cache) {
        Map<String, String> mapping = new HashMap<>();
        this.createDraftPagesMapping(pageManager, pageManager.getDraftRoot(), mapping);
        cache.put(DRAFT_PAGES_MAPPING, mapping);
        return mapping;
    }
    
    protected void createDraftPagesMapping(IPageManager pageManager, IPage current, Map<String, String> mapping) {
        if (null == current) {
            return;
        }
        String friendlyCode = current.getMetadata().getFriendlyCode();
        if (!StringUtils.isBlank(friendlyCode)) {
            mapping.put(friendlyCode, current.getCode());
        }
        String[] children = current.getChildrenCodes();
        if (null != children) {
            for (int i = 0; i < children.length; i++) {
                this.createDraftPagesMapping(pageManager, pageManager.getDraftPage(children[i]), mapping);
            }
        }
    }
    
    protected void releaseCachedObjects(Cache cache, String listKey, String prefixKey) {
		List<String> codes = (List<String>) this.get(cache, listKey, List.class);
		if (null != codes) {
			for (String code : codes) {
				cache.evict(prefixKey + code);
			}
			cache.evict(listKey);
		}
	}
    
    protected void insertAndCleanVoObjectsOnCache(Cache cache, Map<String, ?> objects, String listKey, String cacheKeyPrefix) {
        List<String> oldCodes = (List<String>) this.get(cache, listKey, List.class);
        List<String> oldCodesClone = (null != oldCodes) ? new ArrayList<>(oldCodes) : null;
        List<String> codes = new ArrayList<>();
        Iterator<String> iter = objects.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            cache.put(cacheKeyPrefix + key, objects.get(key));
            if (null != oldCodesClone) {
                oldCodesClone.remove(key);
            }
            codes.add(key);
        }
        cache.put(listKey, codes);
        if (null != oldCodesClone) {
            for (String code : oldCodesClone) {
                cache.evict(cacheKeyPrefix + code);
            }
        }
    }
    
	@Override
	public FriendlyCodeVO getMappingByFriendlyCode(String friendlyCode) {
        return this.get(this.getCache(), MAPPING_BY_CODE_CACHE_KEY_PREFIX + friendlyCode, FriendlyCodeVO.class);
	}

    @Override
    public FriendlyCodeVO getMappingByPageCode(String pageCode) {
        return this.get(this.getCache(), this.getMappingKeyPrefix(ISeoMappingManager.TYPE_PAGE) + pageCode, FriendlyCodeVO.class);
	}
    
    @Override
    public FriendlyCodeVO getMappingByType(String typeCode, String objectCode) {
        return this.get(this.getCache(), this.getMappingKeyPrefix(typeCode) + objectCode, FriendlyCodeVO.class);
	}
    
    private String getMappingKeys(String type) {
        return MAPPING_BY_TYPE_CACHE_KEYS + "_" + type;
    }
    private String getMappingKeyPrefix(String type) {
        return this.getMappingKeys(type) + "_";
    }
    
    
    /*
	public static final String SEO_MAPPER_CACHE_NAME = "Entando_SeoMappingManager";
	public static final String MAPPING_BY_TYPE_CACHE_KEYS = "SeoMappingManager_byType";
    */
    
    /*
    @Override
    public ContentFriendlyCode getMappingByContentId(String contentId) {
        return this.get(this.getCache(), MAPPING_BY_CONTENT_CACHE_KEY_PREFIX + contentId, ContentFriendlyCode.class);
	}
    */
    @Override
    public String getDraftPageReference(String friendlyCode) {
        Cache cache = this.getCache();
        Map<String,String> mapping = this.get(cache, DRAFT_PAGES_MAPPING, Map.class);
        return mapping.get(friendlyCode);
    }

    @Override
    public void updateDraftPageReference(String friendlyCode, String pageCode) {
        Cache cache = this.getCache();
        Map<String,String> mapping = this.get(cache, DRAFT_PAGES_MAPPING, Map.class);
        mapping.entrySet().removeIf(e -> e.getValue().equals(pageCode));
        if (!StringUtils.isBlank(friendlyCode)) {
            mapping.put(friendlyCode, pageCode);
        }
        cache.put(DRAFT_PAGES_MAPPING, mapping);
    }

    @Override
    protected String getCacheName() {
        return ISeoMappingCacheWrapper.SEO_MAPPER_CACHE_NAME;
    }
	
}

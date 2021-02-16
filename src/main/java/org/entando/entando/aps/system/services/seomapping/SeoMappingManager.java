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
package org.entando.entando.aps.system.services.seomapping;

import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.common.FieldSearchFilter;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.PageMetadata;
import com.agiletec.aps.system.services.page.events.PageChangedEvent;
import com.agiletec.aps.system.services.page.events.PageChangedObserver;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.services.seomapping.cache.ISeoMappingCacheWrapper;
import org.entando.entando.aps.system.services.seomapping.event.SeoChangedEvent;
import org.entando.entando.ent.exception.EntException;

import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

/**
 * @author E.Santoboni, E.Mezzano
 */
public class SeoMappingManager extends AbstractService implements ISeoMappingManager, PageChangedObserver {

	private static final EntLogger logger =  EntLogFactory.getSanitizedLogger(SeoMappingManager.class);
	
	private ISeoMappingDAO seoMappingDAO;
	private IPageManager pageManager;
    private ISeoMappingCacheWrapper cacheWrapper;

	@Override
	public void init() throws Exception {
		this.getCacheWrapper().initCache(this.getPageManager(), this.getSeoMappingDAO(), true);
		logger.debug("{} ready. initialized",this.getClass().getName());
	}

    @Override
    protected void release() {
        this.getCacheWrapper().release();
        super.release();
    }

	@Override
	public void updateFromPageChanged(PageChangedEvent event) {
		IPage page = event.getPage();
        String eventType = event.getEventType();
        if (null == page || 
                PageChangedEvent.EVENT_TYPE_JOIN_WIDGET.equals(eventType) || 
                PageChangedEvent.EVENT_TYPE_MOVE_WIDGET.equals(eventType) || 
                PageChangedEvent.EVENT_TYPE_REMOVE_WIDGET.equals(eventType)) {
            return;
        }
        PageMetadata seoMetadata = page.getMetadata();
        String friendlyCode = (event.getOperationCode() == PageChangedEvent.REMOVE_OPERATION_CODE)
                ? null : seoMetadata.getFriendlyCode();
        this.getCacheWrapper().updateDraftPageReference(friendlyCode, page.getCode());
        if (!PageChangedEvent.EVENT_TYPE_SET_PAGE_OFFLINE.equals(event.getEventType())
                && !PageChangedEvent.EVENT_TYPE_SET_PAGE_ONLINE.equals(event.getEventType())) {
            return;
        }
        try {
            this.getSeoMappingDAO().deleteMappingForPage(page.getCode());
            if (PageChangedEvent.REMOVE_OPERATION_CODE != event.getOperationCode() && !StringUtils.isEmpty(friendlyCode)) {
                FriendlyCodeVO vo = new FriendlyCodeVO(seoMetadata.getFriendlyCode(), ISeoMappingManager.TYPE_PAGE, page.getCode());
                this.getSeoMappingDAO().updateMapping(vo);
            }
			SeoChangedEvent seoEvent = new SeoChangedEvent();
			seoEvent.setOperationCode(SeoChangedEvent.PAGE_CHANGED_EVENT);
			this.notifyEvent(seoEvent);
            this.getCacheWrapper().initCache(this.getPageManager(), this.getSeoMappingDAO(), false);
		} catch (Throwable t) {
			logger.error("Error updating mapping from page changed", t);
		}
	}
    
	@Override
	public List<String> searchFriendlyCode(FieldSearchFilter[] filters) throws EntException {
		List<String> codes = null;
		try {
			codes = this.getSeoMappingDAO().searchFriendlyCode(filters);
		} catch (Exception e) {
			logger.error("Error searching Friendly Codes", e);
			throw new EntException("Error searching Friendly Codes", e);
		}
		return codes;
	}

    @Override
    public String getDraftPageReference(String friendlyCode) {
        return this.getCacheWrapper().getDraftPageReference(friendlyCode);
    }
	
	@Override
	public FriendlyCodeVO getReference(String friendlyCode) {
		return this.getCacheWrapper().getMappingByFriendlyCode(friendlyCode);
	}
	
	protected ISeoMappingDAO getSeoMappingDAO() {
		return seoMappingDAO;
	}
	public void setSeoMappingDAO(ISeoMappingDAO seoMappingDAO) {
		this.seoMappingDAO = seoMappingDAO;
	}
	
    protected IPageManager getPageManager() {
        return pageManager;
    }
    public void setPageManager(IPageManager pageManager) {
        this.pageManager = pageManager;
    }
    
    protected ISeoMappingCacheWrapper getCacheWrapper() {
        return cacheWrapper;
    }
    public void setCacheWrapper(ISeoMappingCacheWrapper cacheWrapper) {
        this.cacheWrapper = cacheWrapper;
    }
	
}

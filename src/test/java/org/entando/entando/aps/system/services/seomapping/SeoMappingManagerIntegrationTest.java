/*
 * Copyright 2020-Present Entando Inc. (http://www.entando.com) All rights reserved.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.Page;
import com.agiletec.aps.system.services.page.PageMetadata;
import com.agiletec.aps.system.services.page.PageTestUtil;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import com.agiletec.aps.util.ApsProperties;
import java.util.Date;
import java.util.Set;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SeoMappingManagerIntegrationTest extends BaseTestCase {
    
    private IPageManager pageManager;
    private ISeoMappingManager seoMappingManager;
    private IWidgetTypeManager widgetTypeManager;
    
    @Test
    void testCreateFriendlyCode_3() throws Exception {
        String code1 = "test1";
        String code2 = "test2";
        try {
            this.addPage(code1, "service", "friendly1");
            super.waitNotifyingThread();
            this.addPage(code2, "service", "friendly2");
            synchronized (this) {
                this.wait(500);
            }
            super.waitNotifyingThread();
            FriendlyCodeVO friendlyCodeVO1 = this.seoMappingManager.getReference("friendly1");
            assertNotNull(friendlyCodeVO1);
            assertEquals(code1, friendlyCodeVO1.getObjectCode());
            FriendlyCodeVO friendlyCodeVO2 = this.seoMappingManager.getReference("friendly2");
            assertNotNull(friendlyCodeVO2);
            assertEquals(code2, friendlyCodeVO2.getObjectCode());
            
            IPage page = this.pageManager.getOnlinePage(code1);
            page.getMetadata().setFriendlyCode("friendly2_bis");
            this.pageManager.updatePage(page);
            this.pageManager.setPageOnline(code1);
            synchronized (this) {
                this.wait(500);
            }
            super.waitNotifyingThread();
            friendlyCodeVO1 = this.seoMappingManager.getReference("friendly1");
            assertNull(friendlyCodeVO1);
            friendlyCodeVO1 = this.seoMappingManager.getReference("friendly2_bis");
            assertNotNull(friendlyCodeVO1);
            assertEquals(code1, friendlyCodeVO1.getObjectCode());
        } catch (Exception e) {
            throw e;
        } finally {
            this.pageManager.setPageOffline(code2);
            this.pageManager.deletePage(code2);
            this.pageManager.setPageOffline(code1);
            this.pageManager.deletePage(code1);
        }
    }
    
    private void addPage(String code, String parentCode, String friendlyCode) throws Exception {
        IPage parentPage = pageManager.getDraftPage(parentCode);
        String parentForNewPage = parentPage.getParentCode();
        PageModel pageModel = parentPage.getMetadata().getModel();
        PageMetadata metadata = this.createSeoPageMetadata(pageModel,
                true, "pagina temporanea", null, null, false, null, null, friendlyCode);
        ApsProperties config = PageTestUtil.createProperties("actionPath", "/myJsp.jsp", "param1", "value1");
        Widget widgetToAdd = PageTestUtil.createWidget("formAction", config, this.widgetTypeManager);
        Widget[] widgets = new Widget[pageModel.getFrames().length]; 
        widgets[0] = widgetToAdd;
        Page pageToAdd = PageTestUtil.createPage(code, parentForNewPage, "free", metadata, widgets);
        this.pageManager.addPage(pageToAdd);
        this.pageManager.setPageOnline(code);
    }
    
	private PageMetadata createSeoPageMetadata(PageModel pageModel, boolean showable, String defaultTitle, String mimeType,
			String charset, boolean useExtraTitles, Set<String> extraGroups, Date updatedAt, String friendlyCode) {
        PageMetadata metadata = new PageMetadata();
		metadata.setModel(pageModel);
        metadata.setFriendlyCode(friendlyCode);
		metadata.setShowable(showable);
		metadata.setTitle("it", defaultTitle);
		if (extraGroups != null) {
			metadata.setExtraGroups(extraGroups);
		}
		metadata.setMimeType(mimeType);
		metadata.setCharset(charset);
		metadata.setUseExtraTitles(useExtraTitles);
		metadata.setExtraGroups(extraGroups);
		metadata.setUpdatedAt(updatedAt);
		return metadata;
	}
    
    @BeforeEach
    private void init() throws Exception {
        try {
            this.seoMappingManager = (ISeoMappingManager) this.getService(SystemConstants.SEO_MAPPING_MANAGER);
            this.pageManager = (IPageManager) this.getService(SystemConstants.PAGE_MANAGER);
            this.widgetTypeManager = (IWidgetTypeManager) this.getService(SystemConstants.WIDGET_TYPE_MANAGER);
        } catch (Throwable t) {
            throw new Exception(t);
        }
    }
    
}

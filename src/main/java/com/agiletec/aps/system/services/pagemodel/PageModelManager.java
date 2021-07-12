/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package com.agiletec.aps.system.services.pagemodel;

import com.agiletec.aps.system.common.*;
import com.agiletec.aps.system.common.model.dao.SearcherDaoPaginatedResult;
import org.entando.entando.ent.exception.EntException;
import com.agiletec.aps.system.services.pagemodel.cache.IPageModelManagerCacheWrapper;
import com.agiletec.aps.system.services.pagemodel.events.PageModelChangedEvent;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.services.guifragment.GuiFragmentUtilizer;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;

import java.util.*;
import java.util.regex.*;

/**
 * The manager of the page template.
 */
public class PageModelManager extends AbstractService implements IPageModelManager, GuiFragmentUtilizer {

    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(PageModelManager.class);
    private IPageModelDAO pageModelDao;
    private IPageModelManagerCacheWrapper cacheWrapper;

    @Override
    public void init() throws Exception {
        this.getCacheWrapper().initCache(this.getPageModelDAO());
        logger.debug("{} ready. initialized", this.getClass().getName());
    }
    
    @Override
    protected void release() {
        this.getCacheWrapper().release();
        super.release();
    }

    /**
     * Restituisce il modello di pagina con il codice dato
     *
     * @param name Il nome del modelo di pagina
     * @return Il modello di pagina richiesto
     */
    @Override
    public PageModel getPageModel(String name) {
        return this.getCacheWrapper().getPageModel(name);
    }

    /**
     * Restituisce la Collection completa di modelli.
     *
     * @return la collection completa dei modelli disponibili in oggetti
     * PageModel.
     */
    @Override
    public Collection<PageModel> getPageModels() {
        return this.getCacheWrapper().getPageModels();
    }

    @Override
    public void addPageModel(PageModel pageModel) throws EntException {
        if (null == pageModel) {
            logger.debug("Null page template can be add");
            return;
        }
        try {
            this.getPageModelDAO().addModel(pageModel);
            this.getCacheWrapper().addPageModel(pageModel);
            this.notifyPageModelChangedEvent(pageModel, PageModelChangedEvent.INSERT_OPERATION_CODE);
        } catch (Throwable t) {
            logger.error("Error adding page templates", t);
            throw new EntException("Error adding page templates", t);
        }
    }

    @Override
    public void updatePageModel(PageModel pageModel) throws EntException {
        if (null == pageModel) {
            logger.debug("Null page template can be update");
            return;
        }
        try {
            PageModel pageModelToUpdate = this.getCacheWrapper().getPageModel(pageModel.getCode());
            if (null == pageModelToUpdate) {
                logger.debug("Page template {} does not exist", pageModel.getCode());
                return;
            }
            this.getPageModelDAO().updateModel(pageModel);
            //pageModelToUpdate.setDefaultWidget(pageModel.getDefaultWidget());
            pageModelToUpdate.setDescription(pageModel.getDescription());
            //pageModelToUpdate.setFrames(pageModel.getFrames());
            pageModelToUpdate.setConfiguration(pageModel.getConfiguration());
            pageModelToUpdate.setMainFrame(pageModel.getMainFrame());
            pageModelToUpdate.setPluginCode(pageModel.getPluginCode());
            pageModelToUpdate.setTemplate(pageModel.getTemplate());
            this.getCacheWrapper().updatePageModel(pageModelToUpdate);
            this.notifyPageModelChangedEvent(pageModelToUpdate, PageModelChangedEvent.UPDATE_OPERATION_CODE);
        } catch (Throwable t) {
            logger.error("Error updating page template {}", pageModel.getCode(), t);
            throw new EntException("Error updating page template " + pageModel.getCode(), t);
        }
    }

    @Override
    public void deletePageModel(String code) throws EntException {
        try {
            PageModel model = this.getPageModel(code);
            this.getPageModelDAO().deleteModel(code);
            this.getCacheWrapper().deletePageModel(code);
            this.notifyPageModelChangedEvent(model, PageModelChangedEvent.REMOVE_OPERATION_CODE);
        } catch (Throwable t) {
            logger.error("Error deleting page templates", t);
            throw new EntException("Error deleting page templates", t);
        }
    }

    private void notifyPageModelChangedEvent(PageModel pageModel, int operationCode) {
        PageModelChangedEvent event = new PageModelChangedEvent();
        event.setPageModel(pageModel);
        event.setOperationCode(operationCode);
        this.notifyEvent(event);
    }

    @Override
    public List getGuiFragmentUtilizers(String guiFragmentCode) throws EntException {
        List<PageModel> utilizers = new ArrayList<>();
        try {
            for (PageModel pModel : this.getPageModels()) {
                String template = pModel.getTemplate();
                if (StringUtils.isNotBlank(template)) {
                    Pattern pattern = Pattern.compile("<@wp\\.fragment.*code=\"" + guiFragmentCode + "\".*/>", Pattern.MULTILINE);
                    Matcher matcher = pattern.matcher(template);
                    if (matcher.find()) {
                        utilizers.add(pModel);
                    }
                }
            }
        } catch (Throwable t) {
            logger.error("Error extracting utilizers", t);
            throw new EntException("Error extracting utilizers", t);
        }
        return utilizers;
    }

    protected IPageModelManagerCacheWrapper getCacheWrapper() {
        return cacheWrapper;
    }

    public void setCacheWrapper(IPageModelManagerCacheWrapper cacheWrapper) {
        this.cacheWrapper = cacheWrapper;
    }

    protected IPageModelDAO getPageModelDAO() {
        return pageModelDao;
    }

    public void setPageModelDAO(IPageModelDAO pageModelDAO) {
        this.pageModelDao = pageModelDAO;
    }

    @Override
    public SearcherDaoPaginatedResult<PageModel> searchPageModels(List<FieldSearchFilter> filtersList) throws EntException {
        SearcherDaoPaginatedResult<PageModel> pagedResult = null;
        try {
            FieldSearchFilter[] filters = null;
            if (null != filtersList) {
                filters = filtersList.toArray(new FieldSearchFilter[0]);
            }
            List<PageModel> pageModels = new ArrayList<>();
            int count = this.getPageModelDAO().count(filters);

            List<String> pageModelCodes = this.getPageModelDAO().search(filters);
            for (String code : pageModelCodes) {
                pageModels.add(this.getPageModel(code));
            }
            pagedResult = new SearcherDaoPaginatedResult<>(count, pageModels);
        } catch (Throwable t) {
            logger.error("Error searching groups", t);
            throw new EntException("Error searching groups", t);
        }
        return pagedResult;
    }
}

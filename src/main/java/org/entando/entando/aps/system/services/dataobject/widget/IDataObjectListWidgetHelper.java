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

package org.entando.entando.aps.system.services.dataobject.widget;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.common.entity.helper.IEntityFilterBean;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;
import com.agiletec.aps.system.exception.ApsSystemException;
import java.util.List;
import org.entando.entando.aps.system.services.dataobject.IDataObjectManager;
import org.entando.entando.aps.system.services.dataobject.helper.IDataTypeListFilterBean;

/**
 * Interfaccia base per l'implementazione del bean helper della showlet di erogatore lista dataObject. La classe è a servizio sia delle
 * funzioni dell'area di amministrazione che di front-end.
 *
 * @author E.Santoboni
 */
public interface IDataObjectListWidgetHelper extends org.entando.entando.aps.system.services.dataobject.helper.IDataTypeListHelper {

    String[] allowedMetadataFilterKeys = {
            IDataObjectManager.ENTITY_TYPE_CODE_FILTER_KEY,
            IDataObjectManager.DATA_OBJECT_DESCR_FILTER_KEY,
            IDataObjectManager.DATA_OBJECT_STATUS_FILTER_KEY,
            IDataObjectManager.DATA_OBJECT_CREATION_DATE_FILTER_KEY,
            IDataObjectManager.DATA_OBJECT_MODIFY_DATE_FILTER_KEY,
            IDataObjectManager.DATA_OBJECT_ONLINE_FILTER_KEY
    };
    String[] allowedMetadataUserFilterOptionKeys = {
            UserFilterOptionBean.KEY_CATEGORY,
            UserFilterOptionBean.KEY_FULLTEXT
    };
    String WIDGET_PARAM_CONTENT_TYPE = "contentType";
    String WIDGET_PARAM_USER_FILTERS = "userFilters";
    String WIDGET_PARAM_CATEGORIES = "categories";
    String WIDGET_PARAM_OR_CLAUSE_CATEGORY_FILTER = "orClauseCategoryFilter";
    String WIDGET_PARAM_FILTERS = "filters";
    String WIDGET_PARAM_TITLE = "title";
    String WIDGET_PARAM_PAGE_LINK = "pageLink";
    String WIDGET_PARAM_PAGE_LINK_DESCR = "linkDescr";
    /**
     * @deprecated Use {@link #WIDGET_PARAM_CONTENT_TYPE} instead
     */
    String SHOWLET_PARAM_CONTENT_TYPE = WIDGET_PARAM_CONTENT_TYPE;
    /**
     * @deprecated Use {@link #WIDGET_PARAM_USER_FILTERS} instead
     */
    String SHOWLET_PARAM_USER_FILTERS = WIDGET_PARAM_USER_FILTERS;
    @Deprecated(/**
     * to maintain compatibility with versions prior to 2.2.0.1
    */
    )
    String SHOWLET_PARAM_CATEGORY = "category";
    /**
     * @deprecated Use {@link #WIDGET_PARAM_CATEGORIES} instead
     */
    String SHOWLET_PARAM_CATEGORIES = WIDGET_PARAM_CATEGORIES;
    /**
     * @deprecated Use {@link #WIDGET_PARAM_OR_CLAUSE_CATEGORY_FILTER} instead
     */
    String SHOWLET_PARAM_OR_CLAUSE_CATEGORY_FILTER = WIDGET_PARAM_OR_CLAUSE_CATEGORY_FILTER;
    /**
     * @deprecated Use {@link #WIDGET_PARAM_FILTERS} instead
     */
    String SHOWLET_PARAM_FILTERS = WIDGET_PARAM_FILTERS;
    /**
     * @deprecated Use {@link #WIDGET_PARAM_TITLE} instead
     */
    String SHOWLET_PARAM_TITLE = WIDGET_PARAM_TITLE;
    /**
     * @deprecated Use {@link #WIDGET_PARAM_PAGE_LINK} instead
     */
    String SHOWLET_PARAM_PAGE_LINK = WIDGET_PARAM_PAGE_LINK;
    /**
     * @deprecated Use {@link #WIDGET_PARAM_PAGE_LINK_DESCR} instead
     */
    String SHOWLET_PARAM_PAGE_LINK_DESCR = WIDGET_PARAM_PAGE_LINK_DESCR;

    /**
     * Restituisce la lista di identificativi di dataObject in base ai parametri di ricerca. I parametri utilizzati per la ricerca, per
     * ciascuno di essi vengono estratti con questo ordine di importanza: hanno la precedenza i parametri specificati all'intrno del tag
     * jsp, nel caso uno di essi sia nullo esso viene ricercato nei parametri di configurazione della showlet.
     *
     * @param bean Il contenitore delle informazioni base sulla interrogazione da eseguire.
     * @param reqCtx Il contesto della richiesta.
     * @return La lista di identificativi di dataObject in base ai parametri di ricerca.
     * @throws Throwable In caso di errore.
     */
    List<String> getContentsId(IDataObjectListTagBean bean, RequestContext reqCtx) throws Throwable;

    /**
     * Restituisce l'insieme dei filtri in base al parametro di configurazione della showlet detentore dei filtri. Il parametro è nella
     * forma di: (key=KEY;value=VALUE;attributeFilter=TRUE|FALSE;start=START;end=END;like=TRUE|FALSE)+..<OTHER_FILTERS>
     *
     * @param dataType Il tipo di dataObject al quale i filtri vanno applicati.
     * @param filtersShowletParam Il parametro della showlet nella forma corretta detentore dei filtri.
     * @param reqCtx Il contesto della richiesta.
     * @return L'insieme dei filtri dato dall'interpretazione del parametro.
     */
    EntitySearchFilter[] getFilters(String dataType, String filtersShowletParam, RequestContext reqCtx);

    /**
     * Costruisce e restituisce un filtro in base ai parametri specificati. Il metodo è a servizio del sottoTag DataObjectListFilterTag di
     * DataObjectListTag.
     *
     * @param dataObjectType Il Tipo di dataObject corrispondente al filtro da costruire.
     * @param bean Il contenitore delle informazioni sul filtro da costruire.
     * @param reqCtx Il contesto della richiesta corrente.
     * @return Il nuovo filtro costruito in base ai parametri specificati.
     * @deprecated From Entando 3.0 version 3.0.1. Use getFilter(String, IEntityFilterBean, RequestContext) method
     */
    EntitySearchFilter getFilter(String dataObjectType, IDataTypeListFilterBean bean, RequestContext reqCtx);

    EntitySearchFilter getFilter(String dataObjectType, IEntityFilterBean bean, RequestContext reqCtx);

    /**
     * @deprecated From Entando 3.0 version 3.0.1. Use getUserFilterOption(String, IEntityFilterBean, RequestContext) method
     */
    UserFilterOptionBean getUserFilterOption(String dataObjectType, IDataTypeListFilterBean bean, RequestContext reqCtx);

    UserFilterOptionBean getUserFilterOption(String dataObjectType, IEntityFilterBean bean, RequestContext reqCtx);

    /**
     * Restituisce il parametro da inserire nella configurazione della showlet. Il parametro è nella forma di:
     * (key=KEY;value=VALUE;attributeFilter=TRUE|FALSE;start=START;end=END;like=TRUE|FALSE)+..<OTHER_FILTERS>
     *
     * @param filters I filtri tramite il quale ricavare il parametro.
     * @return Il parametro da inserire nella configurazione della showlet.
     * @deprecated From Entando 2.0 version 2.4.1. Use getFilterParam(EntitySearchFilter[]) method
     */
    String getShowletParam(EntitySearchFilter[] filters);

    /**
     * Return tle list of the front-end user filter options configured into showlet parameters.
     *
     * @param bean The container of the base informations.
     * @param reqCtx The request context.
     * @return The list of the filter options.
     * @throws ApsSystemException in case of error.
     */
    List<UserFilterOptionBean> getConfiguredUserFilters(IDataObjectListTagBean bean, RequestContext reqCtx)
            throws ApsSystemException;

}

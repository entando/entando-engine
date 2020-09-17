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
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.aps.system.services.dataobject.helper.PublicDataTypeAuthorizationInfo;
import org.entando.entando.aps.system.services.dataobjectdispenser.DataObjectRenderizationInfo;

/**
 * Interfaccia base per le classi helper per le showlet erogatori dataObject.
 *
 * @author E.Santoboni
 */
public interface IDataObjectViewerHelper {

    /**
     * Restituisce il dataObject da visualizzare nella showlet.
     *
     * @param dataObjectId L'identificativo del dataObject ricavato dal tag.
     * @param modelId Il modello del dataObject ricavato dal tag.
     * @param reqCtx Il contesto della richiesta.
     * @return Il dataObject da visualizzare nella showlet.
     * @throws EntException In caso di errore
     */
    public String getRenderedDataObject(String dataObjectId, String modelId, RequestContext reqCtx) throws EntException;

    public String getRenderedDataObject(String dataObjectId, String modelId, boolean publishExtraTitle, RequestContext reqCtx) throws EntException;

    public DataObjectRenderizationInfo getRenderizationInfo(String dataObjectId, String modelId, boolean publishExtraTitle, RequestContext reqCtx) throws EntException;

    public PublicDataTypeAuthorizationInfo getAuthorizationInfo(String dataObjectId, RequestContext reqCtx) throws EntException;

}

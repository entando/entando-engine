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
package org.entando.entando.aps.system.services.widgettype;

import com.agiletec.aps.util.ApsProperties;
import org.entando.entando.ent.exception.EntException;

import java.util.List;

/**
 * Interfaccia base per i Servizi gestori dei tipi di widget (WidgetType)
 * definiti nel sistema.
 *
 * @author E.Santoboni
 */
public interface IWidgetTypeManager {

    /**
     * Restituisce la definizione di un tipo di widget in base al codice.
     *
     * @param widgetTypeCode Il codice univoco del tipo
     * @return La definizione del tipo di widget
     */
    public WidgetType getWidgetType(String widgetTypeCode);

    /**
     * Restituisce la lista completa (ordinata per descrizione) dei tipi di
     * widget.
     *
     * @return la lista completa dei widget (ordinata per la descrizione del
     * tipo) disponibili in oggetti WidgetType.
     */
    public List<WidgetType> getWidgetTypes();

    /**
     * Add a widget type into the catalogue
     *
     * @param widgetType The type to add
     * @throws EntException in case of error
     */
    public void addWidgetType(WidgetType widgetType) throws EntException;

    /**
     * Delete a widget type from the catalogue
     *
     * @param widgetTypeCode The code of the type to delete
     * @throws EntException in case of error
     */
    public void deleteWidgetType(String widgetTypeCode) throws EntException;

    /**
     * Update a Widget type on the catalogue.
     *
     * @deprecated
     * @param widgetTypeCode The code of the widget type to update.
     * @param titles The titles of the widget type to update.
     * @param defaultConfig The configuration of the widget type to update.
     * @param mainGroup The main group of the widget type to update.
     * @param configUi The config UI of the widget type to update.
     * @param bundleId The bundle id of the widget type to update.
     * @throws EntException in case of error
     */
    @Deprecated
    void updateWidgetType(String widgetTypeCode, ApsProperties titles, ApsProperties defaultConfig, String mainGroup,
                                 String configUi, String bundleId, Boolean readonlyPageWidgetConfig) throws EntException;
    /**
     * Update a Widget type on the catalogue.
     *
     * @deprecated
     * @param widgetTypeCode The code of the widget type to update.
     * @param titles The titles of the widget type to update.
     * @param defaultConfig The configuration of the widget type to update.
     * @param mainGroup The main group of the widget type to update.
     * @param configUi The config UI of the widget type to update.
     * @param bundleId The bundle id of the widget type to update.
     * @param readonlyPageWidgetConfig The Boolean field to set if Page Widget config is readonly
     * @param widgetCategory The category of the widget type to update.
     * @throws EntException in case of error
     */
    @Deprecated
    void updateWidgetType(String widgetTypeCode, ApsProperties titles, ApsProperties defaultConfig, String mainGroup,
                                 String configUi, String bundleId, Boolean readonlyPageWidgetConfig, String widgetCategory) throws EntException;
    /**
     * Update a Widget type on the catalogue.
     *
     * @param widgetTypeCode The code of the widget type to update.
     * @param titles The titles of the widget type to update.
     * @param defaultConfig The configuration of the widget type to update.
     * @param mainGroup The main group of the widget type to update.
     * @param configUi The config UI of the widget type to update.
     * @param bundleId The bundle id of the widget type to update.
     * @param readonlyPageWidgetConfig The Boolean field to set if Page Widget config is readonly
     * @param widgetCategory The category of the widget type to update.
     * @param icon The icon of the widget type to update.
     * @throws EntException in case of error
     */
    void updateWidgetType(String widgetTypeCode, ApsProperties titles, ApsProperties defaultConfig, String mainGroup,
                          String configUi, String bundleId, Boolean readonlyPageWidgetConfig, String widgetCategory,
                          String icon) throws EntException;

}

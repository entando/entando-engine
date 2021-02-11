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

import com.agiletec.aps.system.common.AbstractDAO;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.util.ApsProperties;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Access Object per i tipi di widget (WidgetType).
 *
 * @author M.Diana - E.Santoboni
 */
public class WidgetTypeDAO extends AbstractDAO implements IWidgetTypeDAO {

    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(WidgetTypeDAO.class);
    private static final String ERROR_UPDATE_WIDGET = "Error updating the widget type";
    private static final String ERROR_LOADING_WIDGETS = "Error loading widgets";
    private static final String ERROR_LOADING_WIDGET  = "Error loading the widget type";

    private ILangManager langManager;

    private static final String ALL_WIDGET_TYPES
            = "SELECT code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup, configui, bundleid, readonlypagewidgetconfig, widgetcategory, icon FROM widgetcatalog";

    private static final String ADD_WIDGET_TYPE
            = "INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup, configui, bundleid, readonlypagewidgetconfig, widgetcategory, icon) "
            + "VALUES (? , ? , ? , ? , ? , ? , ? , ?, ?, ?, ?, ?, ?)";

    private static final String DELETE_WIDGET_TYPE
            = "DELETE FROM widgetcatalog WHERE code = ? AND locked = ? ";

    private static final String UPDATE_WIDGET_TYPE
            = "UPDATE widgetcatalog SET titles = ? , defaultconfig = ? , maingroup = ?, configui = ?, bundleid = ?, readonlypagewidgetconfig = ?, widgetcategory = ?, icon = ? WHERE code = ? ";

    private static final String GET_WIDGET_TYPE
            = "SELECT code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup, configui, bundleid, readonlypagewidgetconfig, widgetcategory, icon FROM widgetcatalog WHERE code = ?";

    @Override
    public Map<String, WidgetType> loadWidgetTypes() {
        Connection conn = null;
        Statement stat = null;
        ResultSet res = null;
        Map<String, WidgetType> widgetTypes = new HashMap<>();
        try {
            conn = this.getConnection();
            stat = conn.createStatement();
            res = stat.executeQuery(ALL_WIDGET_TYPES);
            while (res.next()) {
                WidgetType widgetType = this.createWidgetTypeFromResultSet(res);
                widgetTypes.put(widgetType.getCode(), widgetType);
            }
        } catch (EntException | SQLException e) {
            logger.error( ERROR_LOADING_WIDGETS , e);
            throw new EntRuntimeException(ERROR_LOADING_WIDGETS, e);
        } finally {
            closeDaoResources(res, stat, conn);
        }
        return widgetTypes;
    }

    @Override
    public WidgetType getWidgetType(String widgetTypeCode) throws EntException{
        Connection conn = null;
        PreparedStatement stat = null;
        ResultSet res = null;
        WidgetType widgetType = null;
        try {
            conn = this.getConnection();
            stat = conn.prepareStatement(GET_WIDGET_TYPE);
            stat.setString(1, widgetTypeCode);

            res = stat.executeQuery();
            while (res.next()) {
                widgetType = this.createWidgetTypeFromResultSet(res);
            }
        } catch (EntException | SQLException e) {
            logger.error(ERROR_LOADING_WIDGET, e);
            throw new EntException(ERROR_LOADING_WIDGET, e);
        } finally {
            closeDaoResources(res, stat, conn);
        }
        return widgetType;
    }

    protected WidgetType createWidgetTypeFromResultSet(ResultSet res) throws EntException {
        WidgetType widgetType = new WidgetType();
        String code = null;
        try {
            code = res.getString(1);
            widgetType.setCode(code);
            String xmlTitles = res.getString(2);
            ApsProperties titles = new ApsProperties();
            titles.loadFromXml(xmlTitles);
            widgetType.setTitles(titles);
            String xml = res.getString(3);
            if (null != xml && xml.trim().length() > 0) {
                WidgetTypeDOM showletTypeDom = new WidgetTypeDOM(xml, this.getLangManager().getLangs());
                widgetType.setTypeParameters(showletTypeDom.getParameters());
                widgetType.setAction(showletTypeDom.getAction());
            }
            widgetType.setPluginCode(res.getString(4));
            widgetType.setParentTypeCode(res.getString(5));
            String config = res.getString(6);
            if (null != config && config.trim().length() > 0) {
                ApsProperties defaultConfig = new ApsProperties();
                defaultConfig.loadFromXml(config);
                widgetType.setConfig(defaultConfig);
            }

            if ((null != widgetType.getConfig() && null == widgetType.getParentTypeCode())) {
                throw new EntException("Default configuration found in the type '"
                        + code + "' with no parent type assigned");
            }
            int isLocked = res.getInt(7);
            widgetType.setLocked(isLocked == 1);
            String mainGroup = res.getString(8);
            if (null != mainGroup && mainGroup.trim().length() > 0) {
                widgetType.setMainGroup(mainGroup.trim());
            }
            String configUi = res.getString(9);
            if (StringUtils.isNotEmpty(configUi)) {
                widgetType.setConfigUi(configUi);
            }
            String bundleId = res.getString(10);
            if (StringUtils.isNotEmpty(bundleId)) {
                widgetType.setBundleId(bundleId);
            }
            int isReadonlyPageWidgetConfig = res.getInt(11);
            widgetType.setReadonlyPageWidgetConfig(isReadonlyPageWidgetConfig == 1);
            String widgetCategory = res.getString(12);
            widgetType.setWidgetCategory(widgetCategory);
            String icon = res.getString(13);
            widgetType.setIcon(icon);

        } catch (Throwable t) {
            logger.error("Error parsing the Widget Type '{}'", code, t);
            throw new EntException("Error in the parsing in the Widget Type '" + code + "'", t);
        }
        return widgetType;
    }

    @Override
    public void addWidgetType(WidgetType widgetType) {
        Connection conn = null;
        PreparedStatement stat = null;
        try {
            conn = this.getConnection();
            conn.setAutoCommit(false);
            stat = conn.prepareStatement(ADD_WIDGET_TYPE);
            //(code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, readonlydefaulconfig, widgetcategory)
            stat.setString(1, widgetType.getCode());
            stat.setString(2, widgetType.getTitles().toXml());
            if (null != widgetType.getTypeParameters()) {
                WidgetTypeDOM showletTypeDom = new WidgetTypeDOM(widgetType.getTypeParameters(), widgetType.getAction());
                stat.setString(3, showletTypeDom.getXMLDocument());
            } else {
                stat.setNull(3, Types.VARCHAR);
            }
            stat.setString(4, widgetType.getPluginCode());
            stat.setString(5, widgetType.getParentTypeCode());
            if (null != widgetType.getConfig()) {
                stat.setString(6, widgetType.getConfig().toXml());
            } else {
                stat.setNull(6, Types.VARCHAR);
            }
            if (widgetType.isLocked()) {
                stat.setInt(7, 1);
            } else {
                stat.setInt(7, 0);
            }
            stat.setString(8, widgetType.getMainGroup());
            stat.setString(9, widgetType.getConfigUi());
            stat.setString(10, widgetType.getBundleId());
            if (widgetType.isReadonlyPageWidgetConfig()) {
                stat.setInt(11, 1);
            } else {
                stat.setInt(11, 0);
            }
            stat.setString(12, widgetType.getWidgetCategory());
            stat.setString(13, widgetType.getIcon());
            stat.executeUpdate();
            conn.commit();
        } catch (Throwable t) {
            this.executeRollback(conn);
            logger.error("Error while adding a new widget type", t);
            throw new RuntimeException("Error while adding a new widget type", t);
        } finally {
            closeDaoResources(null, stat, conn);
        }
    }

    @Override
    public void deleteWidgetType(String widgetTypeCode) {
        Connection conn = null;
        PreparedStatement stat = null;
        try {
            conn = this.getConnection();
            conn.setAutoCommit(false);
            stat = conn.prepareStatement(DELETE_WIDGET_TYPE);
            stat.setString(1, widgetTypeCode);
            stat.setInt(2, 0);
            stat.executeUpdate();
            conn.commit();
        } catch (Throwable t) {
            this.executeRollback(conn);
            logger.error("Error deleting widget type '{}'", widgetTypeCode, t);
            throw new RuntimeException("Error deleting widget type", t);
        } finally {
            closeDaoResources(null, stat, conn);
        }
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void updateWidgetType(String widgetTypeCode, ApsProperties titles, ApsProperties defaultConfig, String mainGroup,
                                 String configUi, String bundleId, Boolean readonlyPageWidgetConfig) {
        String widgetCategory;
        String icon;

        try {
            widgetCategory = getWidgetType(widgetTypeCode).getWidgetCategory();
            icon = getWidgetType(widgetTypeCode).getIcon();
        } catch (EntException | RuntimeException e) {
            throw new EntRuntimeException(ERROR_UPDATE_WIDGET, e);
        }

        updateWidgetType(widgetTypeCode, titles, defaultConfig, mainGroup,
                configUi,  bundleId, readonlyPageWidgetConfig , widgetCategory, icon);
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void updateWidgetType(String widgetTypeCode, ApsProperties titles, ApsProperties defaultConfig, String mainGroup,
                                 String configUi, String bundleId, Boolean readonlyPageWidgetConfig, String widgetCategory) {

        String icon;

        try {
            icon = getWidgetType(widgetTypeCode).getIcon();
        } catch (EntException | RuntimeException e) {
            throw new EntRuntimeException(ERROR_UPDATE_WIDGET, e);
        }

        updateWidgetType(widgetTypeCode, titles, defaultConfig, mainGroup,
                configUi,  bundleId, readonlyPageWidgetConfig , widgetCategory, icon);
    }

    public void updateWidgetType(String widgetTypeCode, ApsProperties titles, ApsProperties defaultConfig, String mainGroup,
                                 String configUi, String bundleId, Boolean readonlyPageWidgetConfig, String widgetCategory,
                                 String icon) {
        Connection conn = null;
        PreparedStatement stat = null;
        try {
            conn = this.getConnection();
            conn.setAutoCommit(false);
            stat = conn.prepareStatement(UPDATE_WIDGET_TYPE);
            stat.setString(1, titles.toXml());
            if (null == defaultConfig || defaultConfig.isEmpty()) {
                stat.setNull(2, Types.VARCHAR);
            } else {
                stat.setString(2, defaultConfig.toXml());
            }
            stat.setString(3, mainGroup);
            stat.setString(4, configUi);
            stat.setString(5, bundleId);

            if (Boolean.TRUE.equals(readonlyPageWidgetConfig)) {
                stat.setInt(6, 1);
            } else {
                stat.setInt(6, 0);
            }

            stat.setString(7, widgetCategory);
            stat.setString(8, icon);
            stat.setString(9, widgetTypeCode);

            stat.executeUpdate();
            conn.commit();
        } catch (EntException | SQLException | IOException e) {
            this.executeRollback(conn);
            logger.error("{} {}", ERROR_UPDATE_WIDGET, widgetTypeCode, e);
            throw new EntRuntimeException(ERROR_UPDATE_WIDGET, e);
        } finally {
            closeDaoResources(null, stat, conn);
        }
    }


    protected ILangManager getLangManager() {
        return langManager;
    }

    public void setLangManager(ILangManager langManager) {
        this.langManager = langManager;
    }

}

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
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un tipo di oggetto visuale che può essere inserito in una pagina,
 * in uno dei frames specificati dal modello di pagina. A questa
 * rappresentazione corrisponde una jsp che implementa effettivamente l'oggetto
 * visuale.
 *
 * @author M.Diana - E.Santoboni
 */
public class WidgetType implements Serializable {

    /**
     * Il codice del tipo di widget.
     */
    private String code;

    private ApsProperties titles;

    /**
     * La lista dei parametri previsti per il tipo di widget.
     */
    private List<WidgetTypeParameter> parameters;

    /**
     * Il nome della action specifica che gestisce questo tipo di widget. null
     * se non vi è nessun action specifica.
     */
    private String action;

    /**
     * The code of the plugin owner of widget type.
     */
    private String pluginCode;

    private String parentTypeCode;

    private WidgetType parentType;

    private ApsProperties config;

    private boolean locked;

    private String mainGroup;

    private String configUi;

    private String bundleId;


    /**
     * The readonlyPageWidgetConfig boolean field is read during the widget configuration, if It's false the user
     * can override the widget type default configuration in the instance of the widget
     */
    private boolean readonlyPageWidgetConfig;

    /**
     * The widgetCategory string field is used to group widget types
     */
    private String widgetCategory;

    /**
     * The icon string field is used to save the icon to show for the widget in app-builder
     */
    private String icon;


    public final static String WIDGET_LOCATION = "aps/jsp/widgets/";

    @Override
    public WidgetType clone() {
        WidgetType clone = new WidgetType();
        clone.setAction(this.getAction());
        clone.setCode(this.getCode());
        if (null != this.getConfig()) {
            clone.setConfig(this.getConfig().clone());
        }
        clone.setLocked(this.isLocked());
        clone.setParentType(this.getParentType());
        clone.setParentTypeCode(this.getParentTypeCode());
        clone.setPluginCode(this.getPluginCode());
        clone.setIcon(this.getIcon());
        if (null != this.getTitles()) {
            clone.setTitles(this.getTitles().clone());
        }
        if (null != this.getTypeParameters()) {
            List<WidgetTypeParameter> params = new ArrayList<WidgetTypeParameter>();
            for (int i = 0; i < this.getTypeParameters().size(); i++) {
                params.add(this.getTypeParameters().get(i).clone());
            }
            clone.setTypeParameters(params);
        }
        clone.setMainGroup(this.getMainGroup());
        if (this.isReadonlyPageWidgetConfig()){
            clone.setReadonlyPageWidgetConfig(this.readonlyPageWidgetConfig);
        }
        else {
            clone.setReadonlyPageWidgetConfig(false);
        }
        clone.setWidgetCategory(this.widgetCategory);
        return clone;
    }

    /**
     * Restituisce il codice del tipo di widget.
     *
     * @return Il codice del tipo di widget
     */
    public String getCode() {
        return code;
    }

    /**
     * Imposta il codice del tipo di widget.
     *
     * @param code Il codice del tipo di widget
     */
    public void setCode(String code) {
        this.code = code;
    }

    public ApsProperties getTitles() {
        return titles;
    }

    public void setTitles(ApsProperties titles) {
        this.titles = titles;
    }

    /**
     * Restituisce la lista dei parametri previsti per il tipo di widget.
     *
     * @return La lista di parametri in oggetti del tipo WidgetTypeParameter.
     */
    public List<WidgetTypeParameter> getTypeParameters() {
        return parameters;
    }

    public boolean hasTypeParameter(WidgetTypeParameter param) {
        for (WidgetTypeParameter parameter : getTypeParameters()) {
            if (parameter.getName().equals(param.getName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Imposta la lista dei parametri previsti per il tipo di widget. La lista
     * deve essere composta da oggetti del tipo WidgetTypeParameter.
     *
     * @param typeParameters The parameters to set.
     */
    public void setTypeParameters(List<WidgetTypeParameter> typeParameters) {
        this.parameters = typeParameters;
    }

    public boolean hasParameter(String paramName) {
        if (null == this.getTypeParameters()) {
            return false;
        }
        boolean startWith = false;
        boolean endWith = false;
        if (paramName.endsWith("%")) {
            paramName = paramName.substring(0, paramName.length() - 1);
            startWith = true;
        }
        if (paramName.startsWith("%")) {
            paramName = paramName.substring(1);
            endWith = true;
        }
        for (int i = 0; i < this.getTypeParameters().size(); i++) {
            WidgetTypeParameter param = this.getTypeParameters().get(i);
            String name = (null != param) ? param.getName() : null;
            if (null == name) {
                continue;
            }
            if (startWith && endWith && name.contains(paramName)) {
                return true;
            } else if (startWith && name.startsWith(paramName)) {
                return true;
            } else if (endWith && name.endsWith(paramName)) {
                return true;
            } else if (paramName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Restituisce il nome della action specifica che gestisce questo tipo di
     * widget.
     *
     * @return Il nome della action specifica, null se non vi è nessun action
     * specifica.
     */
    public String getAction() {
        return action;
    }

    /**
     * Setta il nome della action specifica che gestisce questo tipo di widget.
     *
     * @param action Il nome della action specifica.
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Return the code of the plugin owner of widget type. The field is null if
     * the showlet type belong to Entando Core.
     *
     * @return The plugin code.
     */
    public String getPluginCode() {
        return pluginCode;
    }

    /**
     * Set the code of the plugin owner of widget type.
     *
     * @param pluginCode The plugin code.
     */
    public void setPluginCode(String pluginCode) {
        this.pluginCode = pluginCode;
    }

    public String getParentTypeCode() {
        return parentTypeCode;
    }

    protected void setParentTypeCode(String parentTypeCode) {
        this.parentTypeCode = parentTypeCode;
    }

    public WidgetType getParentType() {
        return parentType;
    }

    public void setParentType(WidgetType parentType) {
        this.parentType = parentType;
        if (null != parentType) {
            this.setParentTypeCode(parentType.getCode());
        }
    }

    public ApsProperties getConfig() {
        return config;
    }

    public void setConfig(ApsProperties config) {
        this.config = config;
    }

    public boolean isLogic() {
        return (null != this.getParentType());
    }

    public boolean isUserType() {
        return (this.isLogic() && !this.isLocked() || (!this.isLogic() && !this.isLocked()));
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isReadonlyPageWidgetConfig() {
        return readonlyPageWidgetConfig;
    }

    public void setReadonlyPageWidgetConfig(boolean readonlyPageWidgetConfig) {
        this.readonlyPageWidgetConfig = readonlyPageWidgetConfig;
    }

    public String getWidgetCategory() {
        return widgetCategory;
    }

    public void setWidgetCategory(String widgetCategory) {
        this.widgetCategory = widgetCategory;
    }

    public String getMainGroup() {
        return mainGroup;
    }

    public void setMainGroup(String mainGroup) {
        this.mainGroup = mainGroup;
    }

    public String getConfigUi() {
        return configUi;
    }

    public void setConfigUi(String configUi) {
        this.configUi = configUi;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getJspPath() {
        WidgetType widgetType = (this.isLogic()) ? this.getParentType() : this;
        return getJspPath(widgetType.getCode(), widgetType.getPluginCode());
    }

    public static String getJspPath(String code, String pluginCode) {
        StringBuilder jspPath = new StringBuilder("/WEB-INF/");
        boolean isWidgetPlugin = (null != pluginCode && pluginCode.trim().length() > 0);
        if (isWidgetPlugin) {
            jspPath.append("plugins/").append(pluginCode.trim()).append("/");
        }
        jspPath.append(WIDGET_LOCATION).append(code).append(".jsp");
        return jspPath.toString();
    }

    public static boolean existsJsp(ServletContext srvCtx, String code, String pluginCode) throws IOException {
        String jspPath = getJspPath(code, pluginCode);
		String folderPath = srvCtx.getRealPath("/");
		boolean existsJsp = (new File(folderPath + jspPath)).exists();
		if (existsJsp) {
			return true;
		}
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resolver.getResources("file:**" + jspPath);
		for (int i = 0; i < resources.length; i++) {
			Resource resource = resources[i];
			if (resource.exists()) {
				return true;
			}
		}
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((config == null) ? 0 : config.hashCode());
        result = prime * result + (locked ? 1231 : 1237);
        result = prime * result + ((mainGroup == null) ? 0 : mainGroup.hashCode());
        result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
        result = prime * result + ((parentType == null) ? 0 : parentType.hashCode());
        result = prime * result + ((parentTypeCode == null) ? 0 : parentTypeCode.hashCode());
        result = prime * result + ((pluginCode == null) ? 0 : pluginCode.hashCode());
        result = prime * result + ((titles == null) ? 0 : titles.hashCode());
        result = prime * result + (readonlyPageWidgetConfig ? 1231 : 1237);
        result = prime * result + ((widgetCategory == null) ? 0 : widgetCategory.hashCode());
        result = prime * result + ((icon == null) ? 0 : icon.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        WidgetType other = (WidgetType) obj;
        if (action == null) {
            if (other.action != null) {
                return false;
            }
        } else if (!action.equals(other.action)) {
            return false;
        }
        if (code == null) {
            if (other.code != null) {
                return false;
            }
        } else if (!code.equals(other.code)) {
            return false;
        }
        if (config == null) {
            if (other.config != null) {
                return false;
            }
        } else if (!config.equals(other.config)) {
            return false;
        }
        if (locked != other.locked) {
            return false;
        }
        if (mainGroup == null) {
            if (other.mainGroup != null) {
                return false;
            }
        } else if (!mainGroup.equals(other.mainGroup)) {
            return false;
        }
        if (parameters == null) {
            if (other.parameters != null) {
                return false;
            }
        } else if (!parameters.equals(other.parameters)) {
            return false;
        }
        if (parentType == null) {
            if (other.parentType != null) {
                return false;
            }
        } else if (!parentType.equals(other.parentType)) {
            return false;
        }
        if (parentTypeCode == null) {
            if (other.parentTypeCode != null) {
                return false;
            }
        } else if (!parentTypeCode.equals(other.parentTypeCode)) {
            return false;
        }
        if (pluginCode == null) {
            if (other.pluginCode != null) {
                return false;
            }
        } else if (!pluginCode.equals(other.pluginCode)) {
            return false;
        }
        if (titles == null) {
            if (other.titles != null) {
                return false;
            }
        } else if (!titles.equals(other.titles)) {
            return false;
        }
        if (readonlyPageWidgetConfig != other.readonlyPageWidgetConfig) {
            return false;
        }
        if (widgetCategory == null) {
            if (other.widgetCategory != null) {
                return false;
            }
        } else if (!widgetCategory.equals(other.widgetCategory)) {
            return false;
        }
        if (icon == null) {
            if (other.icon != null) {
                return false;
            }
        } else if (!icon.equals(other.icon)) {
            return false;
        }
        return true;
    }

}

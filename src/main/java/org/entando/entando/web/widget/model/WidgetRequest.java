/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.web.widget.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Map;

public class WidgetRequest implements Serializable {

    @NotBlank(message = "widgettype.code.notBlank")
    private String code;

    @NotEmpty(message = "widgettype.titles.notBlank")
    private Map<String, String> titles;

    @NotBlank(message = "widgettype.group.notBlank")
    private String group;

    //@NotBlank(message = "widgettype.customUi.notBlank")
    private String customUi;

    private String bundleId;

    private Map<String, Object> configUi; //NOSONAR
    
    private List<WidgetParameter> params = new ArrayList<>();
    
    private String configUiName;

    private String parentCode;

    private Map<String, String> paramsDefaults;

    private Boolean readonlyPageWidgetConfig;

    private String widgetCategory;

    private String icon;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, String> getTitles() {
        return titles;
    }

    public void setTitles(Map<String, String> titles) {
        this.titles = titles;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getCustomUi() {
        return customUi;
    }

    public void setCustomUi(String customUi) {
        this.customUi = customUi;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public Map<String, Object> getConfigUi() {
        return configUi;
    }

    public void setConfigUi(Map<String, Object> configUi) {
        this.configUi = configUi;
    }

    public List<WidgetParameter> getParams() {
        return params;
    }
    public void setParams(List<WidgetParameter> params) {
        this.params = params;
    }

    public String getConfigUiName() {
        return configUiName;
    }
    public void setConfigUiName(String configUiName) {
        this.configUiName = configUiName;
    }
    
    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }
    /**
     * @deprecated Use getParentCode method
     */
    @Deprecated
    public String getParentType() {
        return this.getParentCode();
    }
    /**
     * @deprecated Use setParentCode method
     */
    @Deprecated
    public void setParentType(String parentType) {
        this.setParentCode(parentType);
    }

    public Map<String, String> getParamsDefaults() {
        return paramsDefaults;
    }

    public void setParamsDefaults(Map<String, String> paramsDefaults) {
        this.paramsDefaults = paramsDefaults;
    }
    
    /**
     * @deprecated Use getConfig method
     */
    @Deprecated
    public Map<String, String> getConfig() {
        return this.getParamsDefaults();
    }
    /**
     * @deprecated Use setParamsDefaults method
     */
    @Deprecated
    public void setConfig(Map<String, String> config) {
        this.setParamsDefaults(config);
    }

    public Boolean isReadonlyPageWidgetConfig() {
        return readonlyPageWidgetConfig;
    }

    public void setReadonlyPageWidgetConfig(Boolean readonlyPageWidgetConfig) {
        this.readonlyPageWidgetConfig = readonlyPageWidgetConfig;
    }

    public String getWidgetCategory() {
        return widgetCategory;
    }

    public void setWidgetCategory(String widgetCategory) {
        this.widgetCategory = widgetCategory;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public static class WidgetParameter implements Serializable {

        public WidgetParameter() { }
        public WidgetParameter(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        private String name;
        private String description;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        
    }
    
}

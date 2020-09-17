/*
 * Copyright 2015-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.init;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.entando.entando.aps.system.init.model.Component;
import org.entando.entando.aps.system.init.util.ComponentLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.entando.entando.ent.exception.EntException;

/**
 * @author E.Santoboni
 */
public class ComponentManager implements IComponentManager {

    private static final Logger _logger = LoggerFactory.getLogger(ComponentManager.class);

    public void init() throws Exception {
        this.loadComponents();
        _logger.debug("{} ready.", this.getClass().getName());
    }

    @Override
    public void refresh() {
        try {
            this.loadComponents();
        } catch (Throwable t) {
            _logger.error("Error reloading components definitions", t);
            throw new RuntimeException("Error reloading components definitions", t);
        }
    }

    protected void loadComponents() throws EntException {
        try {
            ComponentLoader loader
                    = new ComponentLoader(this.getLocationPatterns(), this.getPostProcessClasses());
            Map<String, Component> componentMap = loader.getComponents();
            List<Component> components = new ArrayList<Component>();
            components.addAll(componentMap.values());
            List<Component> orderedComponents = this.getOrderedComponents(components);
            this.setComponents(orderedComponents);
        } catch (Throwable t) {
            _logger.error("Error loading components definitions", t);
            throw new EntException("Error loading components definitions", t);
        }
    }

    private List<Component> getOrderedComponents(List<Component> components) {
        List<Component> ordered = new ArrayList<Component>();
        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);
            boolean added = false;
            for (int j = 0; j < ordered.size(); j++) {
                Component current = ordered.get(j);
                if (null != current.getDependencies() && current.getDependencies().contains(component.getCode())) {
                    ordered.add(j, component);
                    added = true;
                    break;
                }
            }
            if (!added) {
                ordered.add(component);
            }
        }
        return ordered;
    }

    @Override
    public List<Component> getCurrentComponents() {
        return this.getComponents();
    }

    @Override
    public boolean isComponentInstalled(String componentCode) {
        return (null != this.getInstalledComponent(componentCode));
    }

    @Override
    public Component getInstalledComponent(String componentCode) {
        List<Component> components = this.getComponents();
        if (null != components) {
            for (int i = 0; i < components.size(); i++) {
                Component component = components.get(i);
                if (null != component && component.getCode().equals(componentCode)) {
                    return component;
                }
            }
        }
        return null;
    }

    protected String getLocationPatterns() {
        if (null == this._locationPatterns) {
            return DEFAULT_LOCATION_PATTERN;
        }
        return _locationPatterns;
    }

    public void setLocationPatterns(String locationPatterns) {
        this._locationPatterns = locationPatterns;
    }

    protected List<Component> getComponents() {
        return _components;
    }

    protected void setComponents(List<Component> components) {
        this._components = components;
    }

    protected Map<String, String> getPostProcessClasses() {
        return _postProcessClasses;
    }

    public void setPostProcessClasses(Map<String, String> postProcessClasses) {
        this._postProcessClasses = postProcessClasses;
    }

    /**
     * @return The class loader of the installer which contains all the new
     * loaded classes and resources plus the old ones loaded by the parent
     * application class loader
     */
    public static ClassLoader getComponentInstallerClassLoader() {
        return _componentInstallerClassLoader;
    }

    public static void setComponentInstallerClassLoader(ClassLoader _classLoader) {
        _componentInstallerClassLoader = _classLoader;
    }
    
    private String _locationPatterns;

    private List<Component> _components;
    private Map<String, String> _postProcessClasses;

    public static final String DEFAULT_LOCATION_PATTERN = "classpath*:component/**/**component.xml";


    /**
     * The class loader of the installer which contains all the new loaded
     * classes and resources plus the old ones loaded by the parent application
     * class loader
     */
    private static ClassLoader _componentInstallerClassLoader;

}

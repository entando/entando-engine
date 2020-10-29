package org.entando.entando.web.component;

import java.util.Map;

public class ComponentAnalysis {

    private Map<String, ComponentAnalysisState> components;

    public ComponentAnalysis() {
    }

    public ComponentAnalysis(Map<String, ComponentAnalysisState> components) {
        this.components = components;
    }

    public Map<String, ComponentAnalysisState> getComponents() {
        return components;
    }

    public ComponentAnalysis setComponents(Map<String, ComponentAnalysisState> components) {
        this.components = components;
        return this;
    }




}

/*
 * Copyright 2020-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.analysis.component_existence;

import org.entando.entando.aps.system.services.IComponentExistsService;
import org.entando.entando.ent.exception.EntRuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentExistenceAnalysis {

    public static class ComponentExistenceAnalysisResult {
        public Map<Object, ComponentStatusMap> getMap() {
            return map;
        }

        Map<Object, ComponentStatusMap> map = new HashMap<>();
    }

    public static class ComponentStatusMap {
        public Map<String, Status> getMap() {
            return map;
        }

        Map<String, Status> map = new HashMap<>();
    }

    public enum Status {
        NEW, DIFF, EQUAL
    }

    public static class ServiceParams {
        public final IComponentExistsService service;
        public final List<String> idCodesList;

        public ServiceParams(IComponentExistsService service, List<String> idCodesList) {
            this.service = service;
            this.idCodesList = idCodesList;
        }
    }

    public ComponentExistenceAnalysisResult run(ComponentExistenceAnalysisResult result, List<ServiceParams> servicesParams) {
        //-
        for (ServiceParams serviceParams : servicesParams) {
            IComponentExistsService service = serviceParams.service;
            List<String> idCodesToCheck = serviceParams.idCodesList;

            ComponentStatusMap componentStatusMap = result.map.getOrDefault(service, new ComponentStatusMap());

            for (String idCodeToCheck : idCodesToCheck) {
                if (doesComponentExistsOnService(service, idCodeToCheck)) {
                    componentStatusMap.map.put(idCodeToCheck, Status.DIFF);
                } else {
                    componentStatusMap.map.put(idCodeToCheck, Status.NEW);
                }
            }

            result.map.put(service, componentStatusMap);
        }
        return result;
    }

    protected boolean doesComponentExistsOnService(IComponentExistsService service, String idCode) {
        //-
        try {
            if (service == null) {
                throw new EntRuntimeException("Null service type detected");
            }
            return service.exists(idCode);
        } catch (Exception ex) {
            throw new EntRuntimeException("Error detected checking component existence " + idCode, ex);
        }
    }
}

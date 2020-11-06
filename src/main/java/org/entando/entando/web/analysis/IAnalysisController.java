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
package org.entando.entando.web.analysis;

import org.entando.entando.aps.system.services.IComponentExistsService;
import org.entando.entando.aps.system.services.analysis.component_existence.ComponentExistenceAnalysis;
import org.entando.entando.aps.system.services.analysis.component_existence.ComponentExistenceAnalysis.ServiceParams;
import org.entando.entando.web.analysis.AnalysisResponse.Status;
import org.entando.entando.web.common.model.SimpleRestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

public interface IAnalysisController {

    default ResponseEntity<SimpleRestResponse<AnalysisResponse>> runComponentExistenceAnalysisDefaultImpl(
            ComponentExistenceAnalysis analyzer,
            Map<String, List<String>> idCodesByObjectType) {
        //-
        Set<String> objectTypes = idCodesByObjectType.keySet();

        List<ServiceParams> servicesParams = convertDtoParamsToServiceParams(idCodesByObjectType);

        ComponentExistenceAnalysis.ComponentExistenceAnalysisResult analysisResult = analyzer.run(new ComponentExistenceAnalysis.ComponentExistenceAnalysisResult(), servicesParams);
        Map<String, Map<String, String>> report = convertServiceResultToDtoResult(analysisResult, objectTypes);

        return new ResponseEntity<>(new SimpleRestResponse<>(new AnalysisResponse(report)), HttpStatus.OK);
    }

    default List<ServiceParams> convertDtoParamsToServiceParams(Map<String, List<String>> idCodesByObjectType) {
        //-
        List<ServiceParams> servicesParams = new ArrayList<>();
        Set<String> objectTypes = idCodesByObjectType.keySet();
        for (String objectType : objectTypes) {
            IComponentExistsService service = mapComponentTypeToService(objectType);
            servicesParams.add(
                    new ServiceParams(
                            service, idCodesByObjectType.get(objectType)
                    )
            );
        }
        return servicesParams;
    }

    default Map<String, Map<String, String>> convertServiceResultToDtoResult(
            ComponentExistenceAnalysis.ComponentExistenceAnalysisResult analysisResult,
            Set<String> objectTypes) {
        //-
        Map<String, Map<String, String>> report = new HashMap<>();

        HashMap<Object, String> serviceToObjectType = new HashMap<>();
        for (String objectType : objectTypes) {
            Object service = mapComponentTypeToService(objectType);
            serviceToObjectType.put(
                    service, objectType
            );
        }

        for (Object service : analysisResult.getMap().keySet()) {
            ComponentExistenceAnalysis.ComponentStatusMap componentsStatus = analysisResult.getMap().get(service);
            String objectType = serviceToObjectType.get(service);

            Map<String, String> dtoComponentStatus = new HashMap<>();

            for (String idCode : componentsStatus.getMap().keySet()) {
                ComponentExistenceAnalysis.Status status = componentsStatus.getMap().get(idCode);
                String dtoStatus = getDtoStatus(status).toString();
                dtoComponentStatus.put(idCode, dtoStatus);
            }

            report.put(objectType, dtoComponentStatus);
        }
        return report;
    }

    default Status getDtoStatus(ComponentExistenceAnalysis.Status status) {
        Status result;
        switch (status) {
            case NEW:
                result = Status.NEW;
                break;
            case DIFF:
                result = Status.DIFF;
                break;
            case EQUAL:
                result = Status.EQUAL;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + status);
        }
        return result;
    }

    IComponentExistsService mapComponentTypeToService(String objectType);
}

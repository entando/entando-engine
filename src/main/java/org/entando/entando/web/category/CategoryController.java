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
package org.entando.entando.web.category;

import com.agiletec.aps.system.services.role.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.entando.entando.aps.system.services.category.ICategoryService;
import org.entando.entando.aps.system.services.category.model.CategoryDto;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.web.category.validator.CategoryValidator;
import org.entando.entando.web.common.annotation.RestAccessControl;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.PagedRestResponse;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.common.model.RestResponse;
import org.entando.entando.web.common.model.SimpleRestResponse;
import org.entando.entando.web.component.ComponentUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author E.Santoboni
 */
@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(getClass());
    public static final String COMPONENT_ID = "category";

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private CategoryValidator categoryValidator;

    protected ICategoryService getCategoryService() {
        return categoryService;
    }

    public void setCategoryService(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public CategoryValidator getCategoryValidator() {
        return categoryValidator;
    }

    public void setCategoryValidator(CategoryValidator categoryValidator) {
        this.categoryValidator = categoryValidator;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<List<CategoryDto>, Map<String, String>>> getCategories(@RequestParam(value = "parentCode", required = false, defaultValue = "home") String parentCode) {
        logger.debug("getting category tree for parent {}", parentCode);
        List<CategoryDto> result = this.getCategoryService().getTree(parentCode);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("parentCode", parentCode);
        return new ResponseEntity<>(new RestResponse<>(result, metadata), HttpStatus.OK);
    }

    @RestAccessControl(permission = { Permission.ENTER_BACKEND })
    @RequestMapping(value = "/{categoryCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<CategoryDto>> getCategory(@PathVariable String categoryCode) {
        logger.debug("getting category {}", categoryCode);
        CategoryDto category = this.getCategoryService().getCategory(categoryCode);
        return new ResponseEntity<>(new SimpleRestResponse<>(category), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.ENTER_BACKEND)
    @RequestMapping(value = "/{categoryCode}/references/{holder}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedRestResponse<?>> getCategoryReferences(@PathVariable String categoryCode, @PathVariable String holder, RestListRequest requestList) {
        logger.debug("getting category references - {}", categoryCode);
        PagedMetadata<?> result = this.getCategoryService().getCategoryReferences(categoryCode, holder, requestList);
        return new ResponseEntity<>(new PagedRestResponse<>(result), HttpStatus.OK);
    }

    @ApiOperation("Retrieve categories usage count")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RestAccessControl(permission = Permission.MANAGE_PAGES)
    @RequestMapping(value = "/{code}/usage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<ComponentUsage>> getComponentUsage(@PathVariable String code) {
        logger.trace("get {} usage by code {}", COMPONENT_ID, code);
        ComponentUsage usage = ComponentUsage.builder()
                .type(COMPONENT_ID)
                .code(code)
                .usage(categoryService.getComponentUsage(code))
                .build();
        return new ResponseEntity<>(new SimpleRestResponse<>(usage), HttpStatus.OK);
    }


    @RestAccessControl(permission = Permission.MANAGE_CATEGORIES)
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<CategoryDto>> addCategory(@Valid @RequestBody CategoryDto categoryRequest, BindingResult bindingResult) throws EntException {
        //field validations
        this.getCategoryValidator().validate(categoryRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        //business validations
        this.getCategoryValidator().validatePostReferences(categoryRequest, bindingResult);
        CategoryDto category = this.getCategoryService().addCategory(categoryRequest);
        return new ResponseEntity<>(new SimpleRestResponse<>(category), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_CATEGORIES)
    @RequestMapping(value = "/{categoryCode}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<CategoryDto, Map<String, String>>> updateCategory(@PathVariable String categoryCode, @Valid @RequestBody CategoryDto categoryRequest, BindingResult bindingResult) {
        logger.debug("updating category {} with request {}", categoryCode, categoryRequest);
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        this.getCategoryValidator().validatePutReferences(categoryCode, categoryRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        CategoryDto category = this.getCategoryService().updateCategory(categoryRequest);
        Map<String, String> metadata = new HashMap<>();
        return new ResponseEntity<>(new RestResponse<>(category, metadata), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_CATEGORIES)
    @RequestMapping(value = "/{categoryCode}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<Map<String, String>>> deleteCategory(@PathVariable String categoryCode) {
        logger.debug("Deleting category -> {}", categoryCode);
        this.getCategoryService().deleteCategory(categoryCode);
        Map<String, String> payload = new HashMap<>();
        payload.put("code", categoryCode);
        return new ResponseEntity<>(new SimpleRestResponse<>(payload), HttpStatus.OK);
    }

}

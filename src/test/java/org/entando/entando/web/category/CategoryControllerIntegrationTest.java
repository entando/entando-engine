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

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.category.Category;
import com.agiletec.aps.system.services.category.ICategoryManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.FileTextReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import org.assertj.core.api.Assert;
import org.entando.entando.aps.system.services.category.CategoryTestHelper;
import org.entando.entando.aps.system.services.category.ICategoryService;
import org.entando.entando.aps.system.services.category.model.CategoryDto;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.analysis.AnalysisControllerDiffAnalysisEngineTestsStubs;
import org.entando.entando.web.category.validator.CategoryValidator;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

class CategoryControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ICategoryManager categoryManager;

    @Autowired
    private CategoryController controller;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testGetCategories() throws Exception {
        ResultActions result = mockMvc
                .perform(get("/categories"));
        result.andExpect(status().isOk());
        testCors("/categories");
    }

    @Test
    void testGetValidCategoryTree() throws Exception {
        ResultActions result = mockMvc
                .perform(get("/categories")
                        .param("parentCode", "home"));
        result.andExpect(status().isOk());
    }

    @Test
    void testGetInvalidCategoryTree() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        this.executeGet("invalid_code", accessToken, status().isNotFound());
        ResultActions result = mockMvc
                .perform(get("/categories")
                        .param("parentCode", "invalid_code")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isNotFound());
    }

    @Test
    void testGetValidCategory() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        this.executeGet("cat1", accessToken, status().isOk());
    }

    @Test
    void testGetInvalidCategory() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        this.executeGet("invalid_code", accessToken, status().isNotFound());
    }

    @Test
    void testAddCategory() throws Exception {
        String categoryCode = "test_cat";
        try {
            Assertions.assertNotNull(this.categoryManager.getCategory("cat1"));
            Assertions.assertNull(this.categoryManager.getCategory(categoryCode));
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);
            this.executePostByFile("1_POST_invalid_2.json", accessToken, status().isBadRequest());
            this.executePostByFile("1_POST_invalid_3.json", accessToken, status().isNotFound());
            this.executePostByFile("1_POST_valid.json", accessToken, status().isOk());
            Assertions.assertNotNull(this.categoryManager.getCategory(categoryCode));
            this.executeDelete(categoryCode, accessToken, status().isOk());
            Assertions.assertNull(this.categoryManager.getCategory(categoryCode));
        } finally {
            if (categoryManager.getCategory(categoryCode) != null) {
                this.categoryManager.deleteCategory(categoryCode);
            }
        }
    }

    @Test
    void testUpdateCategory() throws Exception {
        String categoryCode = "test_cat2";
        try {
            Assertions.assertNotNull(this.categoryManager.getCategory("cat1"));
            Assertions.assertNull(this.categoryManager.getCategory(categoryCode));
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);
            ResultActions result = this.executePostByFile("2_POST_valid.json", accessToken, status().isOk());
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.payload.code", is(categoryCode)));

            result = this.executePut("2_PUT_invalid_1.json", categoryCode, accessToken, status().isBadRequest());
            result.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            result.andExpect(
                    jsonPath("$.errors[0].code", is(CategoryValidator.ERRCODE_PARENT_CATEGORY_CANNOT_BE_CHANGED)));

            result = this.executePut("2_PUT_invalid_2.json", categoryCode, accessToken, status().isBadRequest());
            result.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.errors[0].code", is("53")));

            result = this.executePut("2_PUT_valid.json", "home", accessToken, status().isBadRequest());
            result.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.errors[0].code", is(CategoryValidator.ERRCODE_URINAME_MISMATCH)));

            result = this.executePut("2_PUT_valid.json", categoryCode, accessToken, status().isOk());
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.payload.code", is(categoryCode)));
            Category modified = this.categoryManager.getCategory(categoryCode);
            Assertions.assertNotNull(modified);
            Assertions.assertTrue(modified.getTitle("en").startsWith("New "));
            Assertions.assertTrue(modified.getTitle("it").startsWith("Nuovo "));
            result = this.executeDelete(categoryCode, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.code", is(categoryCode)));
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            Assertions.assertNull(this.categoryManager.getCategory(categoryCode));
        } finally {
            if (categoryManager.getCategory(categoryCode) != null) {
                this.categoryManager.deleteCategory(categoryCode);
            }
        }
    }

    @Test
    void testDeleteCategory() throws Exception {
        String categoryCode = "test_cat";
        try {
            Assertions.assertNotNull(this.categoryManager.getCategory("cat1"));
            Assertions.assertNull(this.categoryManager.getCategory(categoryCode));
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);
            ResultActions result = this.executePostByFile("1_POST_valid.json", accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.code", is(categoryCode)));
            Assertions.assertNotNull(this.categoryManager.getCategory(categoryCode));
            result = this.executeDelete(categoryCode, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.code", is(categoryCode)));
            Assertions.assertNull(this.categoryManager.getCategory(categoryCode));
            result = this.executeDelete("invalid_category", accessToken, status().isNotFound());
        } finally {
            if (categoryManager.getCategory(categoryCode) != null) {
                this.categoryManager.deleteCategory(categoryCode);
            }
        }
    }

    @Test
    void testDeleteCategoryWithChildren() throws Exception {
        String parentCategoryCode = "parent_category";
        String childCategoryCode = "child_category";

        CategoryDto parentCategory = new CategoryDto();
        parentCategory.setCode(parentCategoryCode);
        parentCategory.setParentCode("home");
        parentCategory.setTitles(Collections.singletonMap("en", "Parent Title"));

        CategoryDto childCategory = new CategoryDto();
        childCategory.setCode(childCategoryCode);
        childCategory.setParentCode(parentCategoryCode);
        childCategory.setTitles(Collections.singletonMap("en", "Parent Title"));

        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            mockMvc.perform(post("/categories")
                    .content(MAPPER.writeValueAsString(parentCategory))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk());

            mockMvc.perform(post("/categories")
                    .content(MAPPER.writeValueAsString(childCategory))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk());

            mockMvc.perform(delete("/categories/{code}", parentCategoryCode)
                    .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isBadRequest());

            mockMvc.perform(delete("/categories/{code}", childCategoryCode)
                    .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk());

            mockMvc.perform(delete("/categories/{code}", parentCategoryCode)
                    .header("Authorization", "Bearer " + accessToken))
                    .andDo(resultPrint())
                    .andExpect(status().isOk());

        } finally {
            if (categoryManager.getCategory(childCategoryCode) != null) {
                this.categoryManager.deleteCategory(childCategoryCode);
            }

            if (categoryManager.getCategory(parentCategoryCode) != null) {
                this.categoryManager.deleteCategory(parentCategoryCode);
            }
        }
    }

    @Test
    void testGetCategoryReferences() throws Exception {
        Assertions.assertNotNull(this.categoryManager.getCategory("cat1"));
        Assertions.assertNull(this.categoryManager.getCategory("test_test"));
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = this.mockOAuthInterceptor(user);
        ResultActions result = this.executeReference("cat1", accessToken, SystemConstants.GROUP_MANAGER, status().isNotFound());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.errors[0].code", is(CategoryValidator.ERRCODE_CATEGORY_NO_REFERENCES)));
    }

    @Test
    void shouldReturnCategoryUsageCount() throws Exception {
        Assertions.assertNotNull(this.categoryManager.getCategory("cat1"));
        Assertions.assertNull(this.categoryManager.getCategory("test_test"));
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = this.mockOAuthInterceptor(user);
        String code = "cat1";

        mockMvc.perform(get("/categories/{categoryCode}/usage", new Object[]{code})
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.type", is(CategoryController.COMPONENT_ID)))
                .andExpect(jsonPath("$.payload.code", is(code)))
                .andExpect(jsonPath("$.payload.usage", is(0)));
    }

    @Test
    void testGetCategoryWithAdminPermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        this.executeGet("cat1", accessToken, status().isOk());
    }

    @Test
    void testGetCategoryWithoutPermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("normal_user", "0x24").build();
        String accessToken = mockOAuthInterceptor(user);
        this.executeGet("cat1", accessToken, status().isForbidden());
    }

    @Test
    void testGetCategoryWithEnterBackendPermission() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("normal_user", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "admin", Permission.ENTER_BACKEND).build();
        String accessToken = mockOAuthInterceptor(user);
        this.executeGet("cat1", accessToken, status().isOk());
    }

    @Test
    void testDeleteRootCategory() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();

        String accessToken = mockOAuthInterceptor(user);
        mockMvc.perform(get("/categories"))
                .andDo(resultPrint())
                .andExpect(status().isOk());

        Assertions.assertNotNull(this.categoryManager.getCategory("home"));
        this.executeDelete("home", accessToken, status().isBadRequest())
                .andDo(resultPrint())
                .andExpect(jsonPath("$.errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is(CategoryValidator.ERRCODE_ROOT_CATEGORY_CANNOT_BE_DELETED)))
                .andExpect(jsonPath("$.errors[0].message",
                        is("The Category 'home' cannot be deleted because it is the root")));
    }


    @Test
    void testComponentExistenceAnalysis() throws Exception {

        AnalysisControllerDiffAnalysisEngineTestsStubs.testComponentEngineAnalysisResult(
                AnalysisControllerDiffAnalysisEngineTestsStubs.COMPONENT_CATEGORIES,
                "cat1",
                AnalysisControllerDiffAnalysisEngineTestsStubs.STATUS_DIFF,
                new ContextOfControllerTests(mockMvc, MAPPER)
        );

        // should return NEW for NON existing component
        AnalysisControllerDiffAnalysisEngineTestsStubs.testComponentEngineAnalysisResult(
                AnalysisControllerDiffAnalysisEngineTestsStubs.COMPONENT_CATEGORIES,
                "AN_NONEXISTENT_CODE",
                AnalysisControllerDiffAnalysisEngineTestsStubs.STATUS_NEW,
                new ContextOfControllerTests(mockMvc, MAPPER)
        );
    }


    private ResultActions executeGet(String categoryCode, String accessToken, ResultMatcher rm) throws Exception {
        ResultActions result = mockMvc
                .perform(get("/categories/{categoryCode}", new Object[]{categoryCode})
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(rm);
        return result;
    }

    private ResultActions executePostByFile(String filename, String accessToken, ResultMatcher rm) throws Exception {
        return executePost(readJsonFile(filename), accessToken, rm);
    }

    private String readJsonFile(String filename) throws EntException, IOException {
        InputStream isJsonPost = this.getClass().getResourceAsStream(filename);
        return FileTextReader.getText(isJsonPost);
    }


    private ResultActions executePost(String jsonPost, String accessToken, ResultMatcher rm) throws Exception {
        ResultActions result = mockMvc
                .perform(post("/categories").content(jsonPost)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken))
                        .andDo(resultPrint());
        return result.andExpect(rm);
    }

    private ResultActions executePut(String filename, String categoryCode, String accessToken, ResultMatcher rm)
            throws Exception {
        String jsonPut = readJsonFile(filename);
        ResultActions result = mockMvc
                .perform(put("/categories/{categoryCode}", new Object[]{categoryCode})
                        .content(jsonPut)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(rm);
        return result;
    }

    private ResultActions executeDelete(String categoryCode, String accessToken, ResultMatcher rm) throws Exception {
        ResultActions result = mockMvc
                .perform(delete("/categories/{categoryCode}", new Object[]{categoryCode})
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(rm);
        return result;
    }

    private ResultActions executeReference(String categoryCode, String accessToken, String managerName,
            ResultMatcher rm) throws Exception {
        ResultActions result = mockMvc
                .perform(get("/categories/{categoryCode}/references/{holder}", new Object[]{categoryCode, managerName})
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(rm);
        return result;
    }

    @Test
    void addExistingCategoryShouldReturnTheReceivedCategory() throws Exception {

        CategoryDto expected = null;

        try {
            String jsonPost = readJsonFile("1_POST_valid.json");
            expected = MAPPER.readValue(jsonPost, CategoryDto.class);

            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);
            this.executePostByFile("1_POST_valid.json", accessToken, status().is2xxSuccessful());
            ResultActions resultActions = this
                    .executePostByFile("1_POST_valid.json", accessToken, status().is2xxSuccessful());

            CategoryTestHelper.assertCategories(expected, resultActions);
        } finally {
            if (categoryManager.getCategory(expected.getCode()) != null) {
                this.categoryManager.deleteCategory(expected.getCode());
            }
        }
    }

    @Test
    void addExistingGroupWithDifferentParentCodeOrTitlesShouldReturn409() throws Exception {

        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            this.executePostByFile("1_POST_valid.json", accessToken, status().is2xxSuccessful());

            // change parent code
            String cat1Json = readJsonFile("1_POST_valid.json").replace("cat1", "home");
            this.executePost(cat1Json, accessToken, status().isConflict());

            // change titles
            String cat2Json = readJsonFile("1_POST_valid.json").replace("en_title", "new_title");
            this.executePost(cat2Json, accessToken, status().isConflict());
        } finally {
            if (categoryManager.getCategory("test_cat") != null) {
                this.categoryManager.deleteCategory("test_cat");
            }
        }
    }
}

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
package org.entando.entando.web.filebrowser;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.services.user.UserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.analysis.AnalysisControllerDiffAnalysisEngineTestsStubs;
import org.entando.entando.web.filebrowser.model.FileBrowserFileRequest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

class FileBrowserControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private IStorageManager storageManager;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    void testCheckRequest() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/fileBrowser")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        testCors("/fileBrowser");
    }

    @Test
    void testBrowseRootFolder() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/fileBrowser")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(2)));
        result.andExpect(jsonPath("$.payload[0].name", is("public")));
        result.andExpect(jsonPath("$.payload[0].protectedFolder", is(false)));
        result.andExpect(jsonPath("$.payload[1].name", is("protected")));
        result.andExpect(jsonPath("$.payload[1].protectedFolder", is(true)));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.metaData.size()", is(2)));
        result.andExpect(jsonPath("$.metaData.currentPath", is("")));
        result.andExpect(jsonPath("$.metaData.prevPath", is(CoreMatchers.nullValue())));
    }

    @Test
    void testBrowseFolder_1() throws Exception {
        this.storageManager.deleteDirectory("cms", false);
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/fileBrowser").param("protectedFolder", "false")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andDo(resultPrint());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(2)));
        result.andExpect(jsonPath("$.payload[0].name", is("conf")));
        result.andExpect(jsonPath("$.payload[0].protectedFolder", is(false)));
        result.andExpect(jsonPath("$.payload[0].directory", is(true)));
        result.andExpect(jsonPath("$.payload[1].name", is("entando_logo.jpg")));
        result.andExpect(jsonPath("$.payload[1].protectedFolder", is(false)));
        result.andExpect(jsonPath("$.payload[1].directory", is(false)));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.metaData.size()", is(3)));
        result.andExpect(jsonPath("$.metaData.currentPath", is("")));
        result.andExpect(jsonPath("$.metaData.prevPath", is(CoreMatchers.nullValue())));
        result.andExpect(jsonPath("$.metaData.protectedFolder", is(false)));
    }

    @Test
    void testBrowseFolder_2() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/fileBrowser").param("currentPath", "conf")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isBadRequest());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
        result.andExpect(jsonPath("$.errors[0].code", is("3")));

        result = mockMvc
                .perform(get("/fileBrowser").param("currentPath", "conf").param("protectedFolder", "false")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(2)));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.metaData.size()", is(3)));
        result.andExpect(jsonPath("$.metaData.currentPath", is("conf")));
        result.andExpect(jsonPath("$.metaData.prevPath", is("")));
        result.andExpect(jsonPath("$.metaData.protectedFolder", is(false)));
    }

    @Test
    void testBrowseFolder_3() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/fileBrowser").param("currentPath", "conf/unexisting").param("protectedFolder", "false")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
        result.andExpect(jsonPath("$.metaData.size()", is(0)));
    }

    @Test
    void testGetFile_1() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/fileBrowser/file").param("currentPath", "conf/systemParams.properties")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.payload.protectedFolder", is(false)));
        result.andExpect(jsonPath("$.payload.filename", Matchers.is("systemParams.properties")));
        result.andExpect(jsonPath("$.payload.path", Matchers.is("conf/systemParams.properties")));
        result.andExpect(jsonPath("$.payload.base64", Matchers.notNullValue()));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.metaData.size()", is(1)));
        result.andExpect(jsonPath("$.metaData.prevPath", is("conf")));
    }

    @Test
    void testGetFile_2() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ResultActions result = mockMvc
                .perform(get("/fileBrowser/file").param("currentPath", "conf/unexisting.txt")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.payload", Matchers.hasSize(0)));
        result.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
        result.andExpect(jsonPath("$.metaData.size()", is(0)));
    }

    @Test
    void testAddFile_1() throws Exception {
        Assertions.assertFalse(this.storageManager.exists("test_folder/", false));
        this.storageManager.createDirectory("test_folder/", false);
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);
            String body = this.createBody("test.txt", "test_folder/test.txt", false, "test test");
            ResultActions result = this.executeFilePost(body, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.size()", is(3)));
            result.andExpect(jsonPath("$.payload.protectedFolder", is(false)));
            result.andExpect(jsonPath("$.payload.path", is("test_folder/test.txt")));
            result.andExpect(jsonPath("$.payload.filename", is("test.txt")));
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.metaData.size()", is(1)));
            result.andExpect(jsonPath("$.metaData.prevPath", is("test_folder")));

            ResultActions result_error = this.executeFilePost(body, accessToken, status().isConflict());
            result_error.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            result_error.andExpect(jsonPath("$.errors[0].code", is("2")));
        } catch (Exception e) {
            throw e;
        } finally {
            this.storageManager.deleteDirectory("test_folder/", false);
        }
    }

    @Test
    void testAddFile_2() throws Exception {
        String folderName = "test_folder_2";
        boolean protectedFolder = true;
        Assertions.assertFalse(this.storageManager.exists(folderName, protectedFolder));
        this.storageManager.createDirectory(folderName, protectedFolder);
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            String body1 = this.createBody("wrong.txt", folderName + "/test.txt", protectedFolder, "test test");
            ResultActions result1 = this.executeFilePost(body1, accessToken, status().isConflict());
            result1.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));

            String body2 = this.createBody("test.txt", folderName + "/subfolder/test.txt", protectedFolder, "test test");
            ResultActions result2 = this.executeFilePost(body2, accessToken, status().isOk()); //subfolder is created automatically
            result2.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));

            String body3 = this.createBody("", folderName + "/test.txt", protectedFolder, "test test");
            ResultActions result3 = this.executeFilePost(body3, accessToken, status().isBadRequest());
            result3.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            result3.andExpect(jsonPath("$.errors[0].code", is("52")));

            String body4 = this.createBody("test.txt", folderName + "/test.txt", protectedFolder, null);
            ResultActions result4 = this.executeFilePost(body4, accessToken, status().isBadRequest());
            result4.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            result4.andExpect(jsonPath("$.errors[0].code", is("51")));

            String body = this.createBody("test.txt", folderName + "/test.txt", protectedFolder, "test test");
            ResultActions result = this.executeFilePost(body, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.size()", is(3)));
            result.andExpect(jsonPath("$.payload.protectedFolder", is(protectedFolder)));
            result.andExpect(jsonPath("$.payload.path", is(folderName + "/test.txt")));
            result.andExpect(jsonPath("$.payload.filename", is("test.txt")));
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.metaData.size()", is(1)));
            result.andExpect(jsonPath("$.metaData.prevPath", is(folderName)));
        } catch (Exception e) {
            throw e;
        } finally {
            this.storageManager.deleteDirectory(folderName, protectedFolder);
        }
    }

    @Test
    void testAddFileAndCreateParentFolderIfNotExists() throws Exception {
        String folderName = "test_folder_3";
        boolean protectedFolder = false;
        Assertions.assertFalse(this.storageManager.exists(folderName, protectedFolder));

        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            String body = this.createBody("test.txt", folderName + "/test.txt", protectedFolder, "test test");
            ResultActions result = this.executeFilePost(body, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.size()", is(3)));
            result.andExpect(jsonPath("$.payload.protectedFolder", is(protectedFolder)));
            result.andExpect(jsonPath("$.payload.path", is(folderName + "/test.txt")));
            result.andExpect(jsonPath("$.payload.filename", is("test.txt")));
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.metaData.size()", is(1)));
            result.andExpect(jsonPath("$.metaData.prevPath", is(folderName)));
        } catch (Exception e) {
            throw e;
        } finally {
            this.storageManager.deleteDirectory(folderName, protectedFolder);
        }
    }

    @Test
    void testAddFileWithoutExtension() throws Exception {
        Assertions.assertFalse(this.storageManager.exists("test_folder/", false));
        this.storageManager.createDirectory("test_folder/", false);
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);
            String body = this.createBody("test", "test_folder/test", false, "test test");
            ResultActions result = this.executeFilePost(body, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.size()", is(3)));
            result.andExpect(jsonPath("$.payload.protectedFolder", is(false)));
            result.andExpect(jsonPath("$.payload.path", is("test_folder/test")));
            result.andExpect(jsonPath("$.payload.filename", is("test")));
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.metaData.size()", is(1)));
            result.andExpect(jsonPath("$.metaData.prevPath", is("test_folder")));

            ResultActions result_error = this.executeFilePost(body, accessToken, status().isConflict());
            result_error.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            result_error.andExpect(jsonPath("$.errors[0].code", is("2")));
        } catch (Exception e) {
            throw e;
        } finally {
            this.storageManager.deleteDirectory("test_folder/", false);
        }
    }

    @Test
    void testUpdateFile() throws Exception {
        String folderName = "test_folder_3";
        boolean protectedFolder = true;
        Assertions.assertFalse(this.storageManager.exists(folderName, protectedFolder));
        this.storageManager.createDirectory(folderName, protectedFolder);
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            String body = this.createBody("test.txt", folderName + "/test.txt", protectedFolder, "test test");
            this.executeFilePost(body, accessToken, status().isOk());
            Assertions.assertTrue(this.storageManager.exists(folderName + "/test.txt", protectedFolder));
            String text = this.storageManager.readFile(folderName + "/test.txt", protectedFolder);
            Assertions.assertEquals("test test", text);

            String body1 = this.createBody("wrong.txt", folderName + "/test.txt", protectedFolder, "Modified test test");
            ResultActions result1 = this.executeFilePut(body1, accessToken, status().isConflict());
            result1.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));

            String body2 = this.createBody("test.txt", folderName + "/subfolder/test.txt", protectedFolder, "Modified test test");
            ResultActions result2 = this.executeFilePut(body2, accessToken, status().isNotFound());
            result2.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));

            String body3 = this.createBody("", folderName + "/test.txt", protectedFolder, "Modified test test");
            ResultActions result3 = this.executeFilePut(body3, accessToken, status().isBadRequest());
            result3.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            result3.andExpect(jsonPath("$.errors[0].code", is("52")));

            text = this.storageManager.readFile(folderName + "/test.txt", protectedFolder);
            Assertions.assertEquals("test test", text);

            String body4 = this.createBody("test_test.txt", folderName + "/test_test.txt", protectedFolder, "Modified test test");
            ResultActions result4 = this.executeFilePut(body4, accessToken, status().isNotFound());
            result4.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));

            String body5 = this.createBody("test.txt", folderName + "/test.txt", protectedFolder, null);
            ResultActions result5 = this.executeFilePut(body5, accessToken, status().isBadRequest());
            result5.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));

            String bodyPut = this.createBody("test.txt", folderName + "/test.txt", protectedFolder, "Modified test test");
            ResultActions result = this.executeFilePut(bodyPut, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.size()", is(3)));
            result.andExpect(jsonPath("$.payload.protectedFolder", is(protectedFolder)));
            result.andExpect(jsonPath("$.payload.path", is(folderName + "/test.txt")));
            result.andExpect(jsonPath("$.payload.filename", is("test.txt")));
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.metaData.size()", is(1)));
            result.andExpect(jsonPath("$.metaData.prevPath", is(folderName)));

            text = this.storageManager.readFile(folderName + "/test.txt", protectedFolder);
            Assertions.assertEquals("Modified test test", text);
        } catch (Exception e) {
            throw e;
        } finally {
            this.storageManager.deleteDirectory(folderName, protectedFolder);
        }
    }

    @Test
    void testDeleteFile_1() throws Exception {
        String folderName = "test_folder_4";
        boolean protectedFolder = true;
        Assertions.assertFalse(this.storageManager.exists(folderName, protectedFolder));
        this.storageManager.createDirectory(folderName, protectedFolder);
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            String body = this.createBody("testDelete.txt", folderName + "/testDelete.txt", protectedFolder, "test test");
            this.executeFilePost(body, accessToken, status().isOk());
            Assertions.assertTrue(this.storageManager.exists(folderName + "/testDelete.txt", protectedFolder));
            String text = this.storageManager.readFile(folderName + "/testDelete.txt", protectedFolder);
            Assertions.assertEquals("test test", text);

            ResultActions result = mockMvc
                    .perform(delete("/fileBrowser/file").param("currentPath", folderName + "/testDelete.txt").param("protectedFolder", "true")
                            .header("Authorization", "Bearer " + accessToken));
            result.andExpect(status().isOk());
            result.andExpect(jsonPath("$.payload.protectedFolder", is(true)));
            result.andExpect(jsonPath("$.payload.filename", Matchers.is("testDelete.txt")));
            result.andExpect(jsonPath("$.payload.path", Matchers.is(folderName + "/testDelete.txt")));
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.metaData.size()", is(1)));
            result.andExpect(jsonPath("$.metaData.prevPath", is(folderName)));
        } catch (Exception e) {
            throw e;
        } finally {
            this.storageManager.deleteDirectory(folderName, protectedFolder);
        }
    }

    @Test
    void testDeleteFile_2() throws Exception {
        String folderName = "test_folder_5";
        boolean protectedFolder = true;
        Assertions.assertFalse(this.storageManager.exists(folderName, protectedFolder));
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            ResultActions result = mockMvc
                    .perform(delete("/fileBrowser/file").param("currentPath", folderName + "/testDelete2.txt").param("protectedFolder", "true")
                            .header("Authorization", "Bearer " + accessToken));
            result.andExpect(status().isOk());
            result.andExpect(jsonPath("$.payload.protectedFolder", is(true)));
            result.andExpect(jsonPath("$.payload.filename", Matchers.is("testDelete2.txt")));
            result.andExpect(jsonPath("$.payload.path", Matchers.is(folderName + "/testDelete2.txt")));
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.metaData.size()", is(1)));
            result.andExpect(jsonPath("$.metaData.prevPath", is(folderName)));
        } catch (Exception e) {
            throw e;
        } finally {
            this.storageManager.deleteDirectory(folderName, protectedFolder);
        }
    }

    private String createBody(String filename, String path, boolean isProtected, String content) throws Exception {
        FileBrowserFileRequest request = new FileBrowserFileRequest();
        request.setFilename(filename);
        if (null != content) {
            request.setBase64(content.getBytes());
        }
        request.setPath(path);
        request.setProtectedFolder(isProtected);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(request);
    }

    private ResultActions executeFilePost(String body, String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(post("/fileBrowser/file")
                        .content(body).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }

    private ResultActions executeFilePut(String body, String accessToken, ResultMatcher rm) throws Exception {
        ResultActions result = mockMvc
                .perform(put("/fileBrowser/file")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(rm);
        return result;
    }

    @Test
    void testAddDeleteDirectory() throws Exception {
        Assertions.assertFalse(this.storageManager.exists("test_folder", false));
        this.storageManager.createDirectory("test_folder", false);
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);
            String body = "{\"protectedFolder\":false,\"path\":\"test_folder/subfolder\"}";//this.createBody("test.txt", "test_folder/test.txt", false, "test test");
            ResultActions result = this.executeDirectoryPost(body, accessToken, status().isOk());
            result.andExpect(jsonPath("$.payload.size()", is(2)));
            result.andExpect(jsonPath("$.payload.protectedFolder", is(false)));
            result.andExpect(jsonPath("$.payload.path", is("test_folder/subfolder")));
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.metaData.size()", is(1)));
            result.andExpect(jsonPath("$.metaData.prevPath", is("test_folder")));

            String bodyFile = this.createBody("test.txt", "test_folder/subfolder/test.txt", false, "test test");
            this.executeFilePost(bodyFile, accessToken, status().isOk());

            ResultActions result_error = this.executeDirectoryPost(body, accessToken, status().isConflict());
            result_error.andExpect(jsonPath("$.errors", Matchers.hasSize(1)));
            result_error.andExpect(jsonPath("$.errors[0].code", is("2")));

            ResultActions result2 = mockMvc
                    .perform(delete("/fileBrowser/directory")
                            .param("currentPath", "test_folder/subfolder")
                            .param("protectedFolder", "false")
                            .header("Authorization", "Bearer " + accessToken));
            result2.andExpect(status().isOk());
            result2.andExpect(jsonPath("$.payload.protectedFolder", is(false)));
            result2.andExpect(jsonPath("$.payload.path", Matchers.is("test_folder/subfolder")));
            result2.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result2.andExpect(jsonPath("$.metaData.size()", is(1)));
            result2.andExpect(jsonPath("$.metaData.prevPath", is("test_folder")));
        } catch (Exception e) {
            throw e;
        } finally {
            this.storageManager.deleteDirectory("test_folder/", false);
        }
    }

    @Test
    void testComponentExistenceAnalysis() throws Exception {

        AnalysisControllerDiffAnalysisEngineTestsStubs.testComponentEngineAnalysisResult(
                AnalysisControllerDiffAnalysisEngineTestsStubs.COMPONENT_RESOURCES,
                "conf",
                AnalysisControllerDiffAnalysisEngineTestsStubs.STATUS_DIFF,
                new ContextOfControllerTests(mockMvc, mapper)
        );

        // should return NEW for NON existing component
        AnalysisControllerDiffAnalysisEngineTestsStubs.testComponentEngineAnalysisResult(
                AnalysisControllerDiffAnalysisEngineTestsStubs.COMPONENT_RESOURCES,
                "AN_NONEXISTENT_CODE",
                AnalysisControllerDiffAnalysisEngineTestsStubs.STATUS_NEW,
                new ContextOfControllerTests(mockMvc, mapper)
        );
    }

    @Test
    void testDeleteFileWithSpace() throws Exception {
        String folderName = "test_folder_6";
        boolean protectedFolder = true;
        Assertions.assertFalse(this.storageManager.exists(folderName, protectedFolder));
        this.storageManager.createDirectory(folderName, protectedFolder);
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            String body = this.createBody("test Delete.txt", folderName + "/test Delete.txt",
                    protectedFolder, "test test");
            this.executeFilePost(body, accessToken, status().isOk());
            Assertions.assertTrue(
                    this.storageManager.exists(folderName + "/test Delete.txt", protectedFolder));
            String text = this.storageManager.readFile(folderName + "/test Delete.txt",
                    protectedFolder);
            Assertions.assertEquals("test test", text);

            ResultActions result = mockMvc
                    .perform(delete("/fileBrowser/file")
                            .param("currentPath", folderName + "/test Delete.txt")
                            .param("protectedFolder", "true")
                            .header("Authorization", "Bearer " + accessToken));
            result.andExpect(status().isOk());
            result.andExpect(jsonPath("$.payload.protectedFolder", is(true)));
            result.andExpect(jsonPath("$.payload.filename", Matchers.is("test Delete.txt")));
            result.andExpect(jsonPath("$.payload.path", Matchers.is(folderName + "/test Delete.txt")));
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.metaData.size()", is(1)));
            result.andExpect(jsonPath("$.metaData.prevPath", is(folderName)));
        } catch (Exception e) {
            throw e;
        } finally {
            this.storageManager.deleteDirectory(folderName, protectedFolder);
        }
    }

    @Test
    void testDeleteFileWithParentheses() throws Exception {
        String folderName = "test_folder_7";
        boolean protectedFolder = true;
        Assertions.assertFalse(this.storageManager.exists(folderName, protectedFolder));
        this.storageManager.createDirectory(folderName, protectedFolder);
        try {
            UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
            String accessToken = mockOAuthInterceptor(user);

            String body = this.createBody("test (Delete).txt", folderName + "/test (Delete).txt",
                    protectedFolder, "test test");
            this.executeFilePost(body, accessToken, status().isOk());
            Assertions.assertTrue(
                    this.storageManager.exists(folderName + "/test (Delete).txt", protectedFolder));
            String text = this.storageManager.readFile(folderName + "/test (Delete).txt",
                    protectedFolder);
            Assertions.assertEquals("test test", text);

            ResultActions result = mockMvc
                    .perform(delete("/fileBrowser/file")
                            .param("currentPath", folderName + "/test (Delete).txt")
                            .param("protectedFolder", "true")
                            .header("Authorization", "Bearer " + accessToken));
            result.andExpect(status().isOk());
            result.andExpect(jsonPath("$.payload.protectedFolder", is(true)));
            result.andExpect(jsonPath("$.payload.filename", Matchers.is("test (Delete).txt")));
            result.andExpect(jsonPath("$.payload.path", Matchers.is(folderName + "/test (Delete).txt")));
            result.andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
            result.andExpect(jsonPath("$.metaData.size()", is(1)));
            result.andExpect(jsonPath("$.metaData.prevPath", is(folderName)));
        } catch (Exception e) {
            throw e;
        } finally {
            this.storageManager.deleteDirectory(folderName, protectedFolder);
        }
    }

    private ResultActions executeDirectoryPost(String body, String accessToken, ResultMatcher expected)
            throws Exception {
        ResultActions result = mockMvc
                .perform(post("/fileBrowser/directory")
                        .content(body).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }

}

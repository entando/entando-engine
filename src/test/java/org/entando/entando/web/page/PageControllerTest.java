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
package org.entando.entando.web.page;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.services.authorization.AuthorizationManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.Page;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.FileTextReader;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.entando.entando.aps.system.services.page.PageAuthorizationService;
import org.entando.entando.aps.system.services.page.PageService;
import org.entando.entando.aps.system.services.page.model.PageDto;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.web.AbstractControllerTest;
import org.entando.entando.web.page.model.PageCloneRequest;
import org.entando.entando.web.page.model.PagePositionRequest;
import org.entando.entando.web.page.model.PageRequest;
import org.entando.entando.web.page.validator.PageValidator;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 *
 * @author paddeo
 */
@ExtendWith(MockitoExtension.class)
class PageControllerTest extends AbstractControllerTest {

    @Mock
    IPageManager pageManager;

    @Mock
    private PageService pageService;

    @Mock
    private PageAuthorizationService authorizationService;

    @Mock
    private AuthorizationManager authorizationManager;

    @InjectMocks
    private PageController controller;

    private static Validator validator;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        entandoOauth2Interceptor.setAuthorizationManager(authorizationManager);
        when(authorizationManager.isAuthOnPermission(any(UserDetails.class), any(String.class))).thenReturn(true);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .addInterceptors(entandoOauth2Interceptor)
                .setHandlerExceptionResolvers(createHandlerExceptionResolver())
                .build();
        PageValidator pageValidator = new PageValidator();
        pageValidator.setPageManager(pageManager);
        this.controller.setPageValidator(pageValidator);
    }

    @Test
    void shouldLoadAPageTree() throws Exception {
        // NOTE: the test only tests the interface logic, the business logic is tested at service level

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES)
                .build();
        String accessToken = mockOAuthInterceptor(user);

        String mockJsonResult = "[\n"
                + "        {\n"
                + "            \"code\": \"notfound\",\n"
                + "            \"status\": \"draft\",\n"
                + "            \"displayedInMenu\": true,\n"
                + "            \"pageModel\": \"service\",\n"
                + "            \"charset\": null,\n"
                + "            \"contentType\": null,\n"
                + "            \"parentCode\": \"service\",\n"
                + "            \"seo\": false,\n"
                + "            \"titles\": {\"en\": \"Page not found\",\n"
                + "                \"it\": \"Pagina non trovata\"\n"
                + "                },\n"
                + "            \"ownerGroup\": \"free\",\n"
                + "            \"joinGroups\": [],\n"
                + "            \"position\": 4\n"
                + "        },\n"
                + "        {\n"
                + "            \"code\": \"errorpage\",\n"
                + "            \"status\": \"draft\",\n"
                + "            \"displayedInMenu\": true,\n"
                + "            \"pageModel\": \"service\",\n"
                + "            \"charset\": null,\n"
                + "            \"contentType\": null,\n"
                + "            \"parentCode\": \"service\",\n"
                + "            \"seo\": false,\n"
                + "            \"titles\": {\n"
                + "                \"en\": \"Error page\",\n"
                + "                \"it\": \"Pagina di errore\"\n"
                + "                },\n"
                + "            \"ownerGroup\": \"free\",\n"
                + "            \"joinGroups\": [],\n"
                + "            \"position\": 5\n"
                + "        },\n"
                + "        {\n"
                + "            \"code\": \"login\",\n"
                + "            \"status\": \"draft\",\n"
                + "            \"displayedInMenu\": true,\n"
                + "            \"pageModel\": \"service\",\n"
                + "            \"charset\": null,\n"
                + "            \"contentType\": null,\n"
                + "            \"parentCode\": \"service\",\n"
                + "            \"seo\": false,\n"
                + "            \"titles\": {\n"
                + "                    \"en\": \"Login\",\n"
                + "                \"it\": \"Pagina di login\"\n"
                + "                },\n"
                + "            \"ownerGroup\": \"free\",\n"
                + "            \"joinGroups\": [],\n"
                + "            \"position\": 6\n"
                + "        },\n"
                + "        {\n"
                + "            \"code\": \"hello_page\",\n"
                + "            \"status\": \"draft\",\n"
                + "            \"displayedInMenu\": true,\n"
                + "            \"pageModel\": \"service\",\n"
                + "            \"charset\": \"utf8\",\n"
                + "            \"contentType\": \"text/html\",\n"
                + "            \"parentCode\": \"service\",\n"
                + "            \"seo\": false,\n"
                + "            \"titles\": {\n"
                + "                    \"en\": \"My Title\",\n"
                + "                \"it\": \"Mio Titolo\"\n"
                + "                },\n"
                + "            \"ownerGroup\": \"free\",\n"
                + "            \"joinGroups\": [\n"
                + "                \"free\",\n"
                + "                \"administrators\"\n"
                + "            ],\n"
                + "            \"position\": 7\n"
                + "        }\n"
                + "    ]";
        List<PageDto> mockResult = (List<PageDto>) this.createMetadataList(mockJsonResult);

        doReturn(mockResult).when(pageService).getPages(any(String.class), isNull(), isNull());
        doCallRealMethod().when(authorizationService).filterList(any(UserDetails.class), eq(mockResult));
        Mockito.lenient().doReturn(true).when(authorizationService).isAuth(any(UserDetails.class), any(String.class));
        doReturn(true).when(authorizationService).isAuth(any(UserDetails.class), any(PageDto.class), any(Boolean.class));

        ResultActions result = mockMvc.perform(
                get("/pages").
                        param("parentCode", "service")
                        .header("Authorization", "Bearer " + accessToken)
        );

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.errors", hasSize(0)));
        result.andExpect(jsonPath("$.payload", hasSize(4)));
        result.andExpect(jsonPath("$.payload[0].code", org.hamcrest.Matchers.equalTo("notfound")));
    }

    @Test
    void shouldLoadPageTreeForOwnerGroupAndExtraGroups() throws Exception {
        // NOTE: the test only tests the interface logic, the business logic is tested at service level

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        String mockJsonResult = FileTextReader.getText(
                this.getClass().getResourceAsStream("/controllers/pages/essential-page-tree.json"));
        List<PageDto> mockResult = this.createMetadataList(mockJsonResult);

        doReturn(mockResult).when(pageService).getPages(
                any(String.class),
                eq("a_group"),
                eq(Arrays.asList("GROUP1","GROUP2"))
        );

        doCallRealMethod().when(authorizationService).filterList(any(UserDetails.class), eq(mockResult));
        Mockito.lenient().doReturn(true).when(authorizationService).isAuth(any(UserDetails.class), any(String.class));
        doReturn(true).when(authorizationService).isAuth(any(UserDetails.class), any(PageDto.class), any(Boolean.class));

        ResultActions result = mockMvc.perform(
                get("/pages").
                        param("parentCode", "service").
                        param("forLinkingToOwnerGroup", "a_group").
                        param("forLinkingToExtraGroups", "GROUP1,GROUP2").
                        header("Authorization", "Bearer " + accessToken)
        );

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.errors", hasSize(0)));
        result.andExpect(jsonPath("$.payload", hasSize(2)));
        result.andExpect(jsonPath("$.payload[0].code", org.hamcrest.Matchers.equalTo("parent")));
    }

    @Test
    void shouldValidatePutPathMismatch() throws Exception {

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        String mockJsonResult = "{\n"
                + "            \"code\": \"hello_page\",\n"
                + "            \"status\": \"draft\",\n"
                + "            \"displayedInMenu\": true,\n"
                + "            \"pageModel\": \"service\",\n"
                + "            \"charset\": \"utf8\",\n"
                + "            \"contentType\": \"text/html\",\n"
                + "            \"parentCode\": \"service\",\n"
                + "            \"seo\": false,\n"
                + "            \"titles\": {\n"
                + "                    \"en\": \"My Title\",\n"
                + "                \"it\": \"Mio Titolo\"\n"
                + "                },\n"
                + "            \"ownerGroup\": \"free\",\n"
                + "            \"joinGroups\": [\n"
                + "                \"free\",\n"
                + "                \"administrators\"\n"
                + "            ],\n"
                + "            \"position\": 7\n"
                + "        }";
        PageDto mockResult = (PageDto) this.createMetadata(mockJsonResult, PageDto.class);
        Mockito.lenient().when(pageService.updatePage(any(String.class), any(PageRequest.class))).thenReturn(mockResult);
        when(authorizationService.isAuthOnGroup(any(UserDetails.class), any(String.class))).thenReturn(true);
        ResultActions result = mockMvc.perform(
                put("/pages/{pageCode}", "wrong_page")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockJsonResult)
                .header("Authorization", "Bearer " + accessToken)
        );

        String response = result.andReturn().getResponse().getContentAsString();
        result.andExpect(status().isBadRequest());
        result.andExpect(jsonPath("$.errors", hasSize(1)));
        result.andExpect(jsonPath("$.errors[0].code", is(PageValidator.ERRCODE_URINAME_MISMATCH)));

    }

    @Test
    void shouldBeUnauthorized() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withGroup(Group.FREE_GROUP_NAME)
                .build();
        String accessToken = mockOAuthInterceptor(user);

        ResultActions result = mockMvc.perform(
                get("/pages/{parentCode}", "mock_page")
                .header("Authorization", "Bearer " + accessToken)
        );

        String response = result.andReturn().getResponse().getContentAsString();
        result.andExpect(status().isForbidden());
    }

    @Test
    void shouldValidatePostConflict() throws EntException, Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        PageRequest page = new PageRequest();
        page.setCode("existing_page");
        page.setPageModel("existing_model");
        page.setParentCode("existing_parent");
        page.setOwnerGroup("existing_group");
        Mockito.lenient().when(authorizationService.isAuth(any(UserDetails.class), any(String.class))).thenReturn(true);
        when(this.controller.getPageValidator().getPageManager().getDraftPage(any(String.class))).thenReturn(new Page());
        ResultActions result = mockMvc.perform(
                post("/pages")
                .content(convertObjectToJsonBytes(page))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isConflict());
        String response = result.andReturn().getResponse().getContentAsString();
        result.andExpect(jsonPath("$.errors", hasSize(1)));
        result.andExpect(jsonPath("$.errors[0].code", is(PageValidator.ERRCODE_PAGE_ALREADY_EXISTS)));
    }

    @Test
    void shouldValidateDeleteOnlinePage() throws EntException, Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        when(authorizationService.isAuthOnGroup(any(UserDetails.class), any(String.class))).thenReturn(true);
        when(this.controller.getPageValidator().getPageManager().getOnlinePage(any(String.class))).thenReturn(new Page());
        ResultActions result = mockMvc.perform(
                delete("/pages/{pageCode}", "online_page")
                .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isBadRequest());
        String response = result.andReturn().getResponse().getContentAsString();
        result.andExpect(jsonPath("$.errors", hasSize(1)));
        result.andExpect(jsonPath("$.errors[0].code", is(PageValidator.ERRCODE_ONLINE_PAGE)));
    }

    @Test
    void shouldValidateDeletePageWithChildren() throws EntException, Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        Page page = new Page();
        page.setCode("page_with_children");
        page.addChildCode("child");
        when(authorizationService.isAuthOnGroup(any(UserDetails.class), any(String.class))).thenReturn(true);
        when(this.controller.getPageValidator().getPageManager().getDraftPage(any(String.class))).thenReturn(page);
        ResultActions result = mockMvc.perform(
                delete("/pages/{pageCode}", "page_with_children")
                .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isBadRequest());
        String response = result.andReturn().getResponse().getContentAsString();
        result.andExpect(jsonPath("$.errors", hasSize(1)));
        result.andExpect(jsonPath("$.errors[0].code", is(PageValidator.ERRCODE_PAGE_HAS_CHILDREN)));
    }

    @Test
    void shouldValidateMovePageInvalidRequest() throws EntException, Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        PagePositionRequest request = new PagePositionRequest();
        request.setCode("page_to_move");
        request.setParentCode(null);
        request.setPosition(0);
        Mockito.lenient().when(authorizationService.isAuth(any(UserDetails.class), any(String.class))).thenReturn(true);
        ResultActions result = mockMvc.perform(
                put("/pages/{pageCode}/position", "page_to_move")
                .content(convertObjectToJsonBytes(request))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isBadRequest());
        String response = result.andReturn().getResponse().getContentAsString();
        result.andExpect(jsonPath("$.errors", hasSize(1)));
        result.andExpect(jsonPath("$.errors[0].code", is("NotBlank")));
    }
    
    @Test
    void shouldValidateMovePageNameMismatch() throws EntException, Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        PagePositionRequest request = new PagePositionRequest();
        request.setCode("WRONG");
        request.setParentCode("new_parent_page");
        request.setPosition(1);
        
        Mockito.lenient().when(authorizationService.isAuth(any(UserDetails.class), any(String.class))).thenReturn(true);
        when(this.controller.getPageValidator().getPageManager().getDraftPage("new_parent_page")).thenReturn(new Page());
        
        ResultActions result = mockMvc.perform(
                put("/pages/{pageCode}/position", "page_to_move")
                        .content(convertObjectToJsonBytes(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is(PageValidator.ERRCODE_URINAME_MISMATCH)));
    }

    @Test
    void shouldValidateMovePageInvalidPosition() throws EntException, Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        PagePositionRequest request = new PagePositionRequest();
        request.setCode("page_to_move");
        request.setParentCode("new_parent_page");
        request.setPosition(0);
        
        Mockito.lenient().when(authorizationService.isAuth(any(UserDetails.class), any(String.class))).thenReturn(true);
        Mockito.lenient().when(this.controller.getPageValidator().getPageManager().getDraftPage("new_parent_page")).thenReturn(new Page());
        
        ResultActions result = mockMvc.perform(
                put("/pages/{pageCode}/position", "page_to_move")
                        .content(convertObjectToJsonBytes(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is(PageValidator.ERRCODE_CHANGE_POSITION_INVALID_REQUEST)));
    }

    @Test
    void shouldValidateMovePageMissingParent() throws EntException, Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        PagePositionRequest request = new PagePositionRequest();
        request.setCode("page_to_move");
        request.setParentCode("new_parent_page");
        request.setPosition(0);

        Mockito.lenient().when(authorizationService.isAuth(any(UserDetails.class), any(String.class))).thenReturn(true);

        ResultActions result = mockMvc.perform(
                put("/pages/{pageCode}/position", "page_to_move")
                        .content(convertObjectToJsonBytes(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is(PageValidator.ERRCODE_CHANGE_POSITION_INVALID_REQUEST)));
    }

    @Test
    void shouldValidateMovePageGroupMismatch() throws EntException, Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        PagePositionRequest request = new PagePositionRequest();
        request.setCode("page_to_move");
        request.setParentCode("new_parent_page");
        request.setPosition(1);

        Page pageToMove = new Page();
        pageToMove.setCode("page_to_move");
        pageToMove.setParentCode("old_parent_page");
        pageToMove.setGroup("page_to_move_group");

        Page newParent = new Page();
        newParent.setCode("new_parent_page");
        newParent.setParentCode("another_parent_page");
        newParent.setGroup("another_group");
        Mockito.lenient().when(authorizationService.isAuth(any(UserDetails.class), any(String.class))).thenReturn(true);
        when(this.controller.getPageValidator().getPageManager().getDraftPage("page_to_move")).thenReturn(pageToMove);
        when(this.controller.getPageValidator().getPageManager().getDraftPage("new_parent_page")).thenReturn(newParent);
        ResultActions result = mockMvc.perform(
                put("/pages/{pageCode}/position", "page_to_move")
                .content(convertObjectToJsonBytes(request))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isUnprocessableEntity());
        String response = result.andReturn().getResponse().getContentAsString();
        result.andExpect(jsonPath("$.errors", hasSize(1)));
        result.andExpect(jsonPath("$.errors[0].code", is(PageValidator.ERRCODE_GROUP_MISMATCH)));
    }
    
    @Test
    void shouldValidateMoveFreePageUnderReservedPage() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES)
                .build();
        String accessToken = mockOAuthInterceptor(user);

        PagePositionRequest request = new PagePositionRequest();
        request.setCode("page_to_move");
        request.setParentCode("new_parent_page");
        request.setPosition(1);

        Page pageToMove = new Page();
        pageToMove.setCode("page_to_move");
        pageToMove.setParentCode("old_parent_page");
        pageToMove.setGroup("free");

        Page newParent = new Page();
        newParent.setCode("new_parent_page");
        newParent.setGroup("reserved");

        Mockito.lenient().when(authorizationService.isAuth(any(UserDetails.class), any(String.class))).thenReturn(true);
        when(this.controller.getPageValidator().getPageManager().getDraftPage("page_to_move")).thenReturn(pageToMove);
        when(this.controller.getPageValidator().getPageManager().getDraftPage("new_parent_page")).thenReturn(newParent);

        ResultActions result = mockMvc.perform(
                put("/pages/{pageCode}/position", "page_to_move")
                        .content(convertObjectToJsonBytes(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is(PageValidator.ERRCODE_GROUP_MISMATCH)));
    }

    @Test
    void shouldValidateMovePageStatusMismatch() throws EntException, Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES)
                .build();
        String accessToken = mockOAuthInterceptor(user);

        PagePositionRequest request = new PagePositionRequest();
        request.setCode("page_to_move");
        request.setParentCode("new_parent_page");
        request.setPosition(1);

        PageM pageToMove = new PageM(true);
        pageToMove.setCode("page_to_move");
        pageToMove.setParentCode("old_parent_page");
        pageToMove.setGroup("valid_group");

        PageM newParent = new PageM(false);
        newParent.setCode("new_parent_page");
        newParent.setParentCode("another_parent_page");
        newParent.setGroup("valid_group");
        Mockito.lenient().when(authorizationService.isAuth(any(UserDetails.class), any(String.class))).thenReturn(true);
        when(this.controller.getPageValidator().getPageManager().getDraftPage("page_to_move")).thenReturn(pageToMove);
        when(this.controller.getPageValidator().getPageManager().getDraftPage("new_parent_page")).thenReturn(newParent);
        ResultActions result = mockMvc.perform(
                put("/pages/{pageCode}/position", "page_to_move")
                .content(convertObjectToJsonBytes(request))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isBadRequest());
        String response = result.andReturn().getResponse().getContentAsString();
        result.andExpect(jsonPath("$.errors", hasSize(1)));
        result.andExpect(jsonPath("$.errors[0].code", is(PageValidator.ERRCODE_STATUS_PAGE_MISMATCH)));
    }

    @Test
    void shouldAllowCreatingReservedPageUnderFreePage() throws Exception {

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization("test_group", "managePages", Permission.MANAGE_PAGES)
                .build();
        String accessToken = mockOAuthInterceptor(user);

        Page parentPage = Mockito.mock(Page.class);
        when(parentPage.getGroup()).thenReturn(Group.FREE_GROUP_NAME);

        PageRequest page = new PageRequest();
        page.setCode("reserved_page");
        page.setPageModel("test_model");
        page.setParentCode("free_page");
        page.setOwnerGroup("test_group");

        when(pageManager.getDraftPage("reserved_page")).thenReturn(null);
        when(pageManager.getDraftPage("free_page")).thenReturn(parentPage);

        when(authorizationService.getAuthorizationManager()).thenReturn(authorizationManager);
        when(authorizationManager.isAuthOnGroup(any(UserDetails.class), any(String.class))).thenReturn(true);

        mockMvc.perform(post("/pages")
                        .content(convertObjectToJsonBytes(page))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        Mockito.verify(pageService).addPage(any());
    }

    @Test
    void shouldAllowCreatingReservedPageHavingSameGroupOfParentPage() throws Exception {

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization("test_group", "managePages", Permission.MANAGE_PAGES)
                .build();
        String accessToken = mockOAuthInterceptor(user);

        Page parentPage = Mockito.mock(Page.class);
        when(parentPage.getGroup()).thenReturn("test_group");

        PageRequest page = new PageRequest();
        page.setCode("reserved_page");
        page.setPageModel("test_model");
        page.setParentCode("parent_reserved_page");
        page.setOwnerGroup("test_group");

        when(pageManager.getDraftPage("reserved_page")).thenReturn(null);
        when(pageManager.getDraftPage("parent_reserved_page")).thenReturn(parentPage);

        when(authorizationService.getAuthorizationManager()).thenReturn(authorizationManager);
        when(authorizationManager.isAuthOnGroup(any(UserDetails.class), any(String.class))).thenReturn(true);

        mockMvc.perform(post("/pages")
                        .content(convertObjectToJsonBytes(page))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        Mockito.verify(pageService).addPage(any());
    }

    @Test
    void shouldDenyCreatingPageWhenParentGroupMismatch() throws Exception {

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization("test_group", "managePages", Permission.MANAGE_PAGES)
                .withAuthorization("different_group", "managePages", Permission.MANAGE_PAGES)
                .build();
        String accessToken = mockOAuthInterceptor(user);

        Page parentPage = Mockito.mock(Page.class);
        when(parentPage.getGroup()).thenReturn("different_group");

        PageRequest page = new PageRequest();
        page.setCode("reserved_page");
        page.setPageModel("test_model");
        page.setParentCode("parent_reserved_page");
        page.setOwnerGroup("test_group");

        when(pageManager.getDraftPage("reserved_page")).thenReturn(null);
        when(pageManager.getDraftPage("parent_reserved_page")).thenReturn(parentPage);

        mockMvc.perform(post("/pages")
                        .content(convertObjectToJsonBytes(page))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldAllowCloningReservedPageInFreePage() throws Exception {

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization("test_group", "managePages", Permission.MANAGE_PAGES)
                .build();
        String accessToken = mockOAuthInterceptor(user);

        Page parentPage = Mockito.mock(Page.class);
        when(parentPage.getGroup()).thenReturn(Group.FREE_GROUP_NAME);
        Page pageToClone = Mockito.mock(Page.class);
        when(pageToClone.getGroup()).thenReturn("test_group");

        PageCloneRequest pageCloneRequest = new PageCloneRequest();
        pageCloneRequest.setParentCode("parent_page");
        pageCloneRequest.setNewPageCode("cloned_page");
        pageCloneRequest.setTitles(Map.of("en", "testAddClone en"));

        when(authorizationService.isAuthOnGroup(any(), any())).thenReturn(true);
        when(pageManager.getDraftPage("cloned_page")).thenReturn(null);
        when(pageManager.getDraftPage("page_to_clone")).thenReturn(pageToClone);
        when(pageManager.getDraftPage("parent_page")).thenReturn(parentPage);

        mockMvc.perform(post("/pages/{code}/clone", "page_to_clone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonBytes(pageCloneRequest))
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(pageService).clonePage(any(), any(), any());
    }

    @Test
    void shouldAllowCloningReservedPageInParentHavingSameGroup() throws Exception {

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization("test_group", "managePages", Permission.MANAGE_PAGES)
                .build();
        String accessToken = mockOAuthInterceptor(user);

        Page parentPage = Mockito.mock(Page.class);
        when(parentPage.getGroup()).thenReturn("test_group");
        Page pageToClone = Mockito.mock(Page.class);
        when(pageToClone.getGroup()).thenReturn("test_group");

        PageCloneRequest pageCloneRequest = new PageCloneRequest();
        pageCloneRequest.setParentCode("parent_page");
        pageCloneRequest.setNewPageCode("cloned_page");
        pageCloneRequest.setTitles(Map.of("en", "testAddClone en"));

        when(authorizationService.isAuthOnGroup(any(), any())).thenReturn(true);
        when(pageManager.getDraftPage("cloned_page")).thenReturn(null);
        when(pageManager.getDraftPage("page_to_clone")).thenReturn(pageToClone);
        when(pageManager.getDraftPage("parent_page")).thenReturn(parentPage);

        mockMvc.perform(post("/pages/{code}/clone", "page_to_clone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonBytes(pageCloneRequest))
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(pageService).clonePage(any(), any(), any());
    }

    @Test
    void shouldDenyCloningFreePageInReservedPage() throws Exception {

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization("test_group", "managePages", Permission.MANAGE_PAGES)
                .build();
        String accessToken = mockOAuthInterceptor(user);

        Page parentPage = Mockito.mock(Page.class);
        when(parentPage.getGroup()).thenReturn("test_group");
        Page pageToClone = Mockito.mock(Page.class);
        when(pageToClone.getGroup()).thenReturn(Group.FREE_GROUP_NAME);

        PageCloneRequest pageCloneRequest = new PageCloneRequest();
        pageCloneRequest.setParentCode("parent_page");
        pageCloneRequest.setNewPageCode("cloned_page");
        pageCloneRequest.setTitles(Map.of("en", "testAddClone en"));

        when(authorizationService.isAuthOnGroup(any(), any())).thenReturn(true);
        when(pageManager.getDraftPage("page_to_clone")).thenReturn(pageToClone);
        when(pageManager.getDraftPage("parent_page")).thenReturn(parentPage);

        mockMvc.perform(post("/pages/{code}/clone", "page_to_clone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonBytes(pageCloneRequest))
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldDenyCloningReservedPageInParentPageHavingDifferentGroup() throws Exception {

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization("test_group", "managePages", Permission.MANAGE_PAGES)
                .withAuthorization("different_group", "managePages", Permission.MANAGE_PAGES)
                .build();
        String accessToken = mockOAuthInterceptor(user);

        Page parentPage = Mockito.mock(Page.class);
        when(parentPage.getGroup()).thenReturn("test_group");
        Page pageToClone = Mockito.mock(Page.class);
        when(pageToClone.getGroup()).thenReturn("different_group");

        PageCloneRequest pageCloneRequest = new PageCloneRequest();
        pageCloneRequest.setParentCode("parent_page");
        pageCloneRequest.setNewPageCode("cloned_page");
        pageCloneRequest.setTitles(Map.of("en", "testAddClone en"));

        when(authorizationService.isAuthOnGroup(any(), any())).thenReturn(true);
        when(pageManager.getDraftPage("page_to_clone")).thenReturn(pageToClone);
        when(pageManager.getDraftPage("parent_page")).thenReturn(parentPage);

        mockMvc.perform(post("/pages/{code}/clone", "page_to_clone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonBytes(pageCloneRequest))
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    private List<PageDto> createMetadataList(String json) throws IOException, JsonParseException, JsonMappingException {
        ObjectMapper mapper = new ObjectMapper();
        List<PageDto> result = mapper.readValue(json, new TypeReference<List<PageDto>>() {
        });
        return result;
    }

    private class PageM extends Page {

        public PageM(boolean isOnline) {
            this.setOnline(isOnline);
        }
    }
}

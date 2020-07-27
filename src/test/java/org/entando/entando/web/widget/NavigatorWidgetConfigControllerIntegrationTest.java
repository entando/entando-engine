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
package org.entando.entando.web.widget;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.entando.entando.web.widget.model.NavigatorConfigDto;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.CoreMatchers.is;

import com.agiletec.aps.system.services.page.widget.NavigatorExpression;
import java.util.ArrayList;
import java.util.List;
import org.entando.entando.web.widget.model.NavigatorExpressionDto;

public class NavigatorWidgetConfigControllerIntegrationTest extends AbstractControllerIntegrationTest {
    
    private ObjectMapper mapper = new ObjectMapper();
    /*
    @Test
    public void testGetExpressions_1() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES).build();
        String accessToken = mockOAuthInterceptor(user);
        String navSpec = "current.children + code(homepage).subtree(2) + code(administrators_page).path + super(1)";
        NavigatorConfigDto dto = new NavigatorConfigDto();
        dto.setNavSpec(navSpec);
        ResultActions result = this.executeCall(accessToken, dto, "expressions", status().isOk());
        System.out.println("---------");
        System.out.println(result.andReturn().getResponse().getContentAsString());
        System.out.println("---------");
        result.andExpect(jsonPath("$.payload.expressions", Matchers.hasSize(4)));
        result.andExpect(jsonPath("$.metaData.size()", is(0)));
        result.andExpect(jsonPath("$.errors.size()", is(0)));
    }
    
    @Test
    public void testGetExpressions_2() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES).build();
        String accessToken = mockOAuthInterceptor(user);
        String navSpec = "current.children + code(homepage).subtree(2) + code(administrators_page).invalid + super(1)";
        NavigatorConfigDto dto = new NavigatorConfigDto();
        dto.setNavSpec(navSpec);
        this.executeCall(accessToken, dto, "expressions", status().is5xxServerError());
    }
    */
    @Test
    public void testGetNavSpec_1() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES).build();
        String accessToken = mockOAuthInterceptor(user);
        /*
{"spec":"code","specSuperLevel":-1,"specAbsLevel":-1,"targetCode":"homepage","operator":"subtree","operatorSubtreeLevel":2},
{"spec":"current","specSuperLevel":-1,"specAbsLevel":-1,"targetCode":null,"operator":"children","operatorSubtreeLevel":-1},
{"spec":"code","specSuperLevel":-1,"specAbsLevel":-1,"targetCode":"administrators_page","operator":"path","operatorSubtreeLevel":-1},
{"spec":"super","specSuperLevel":1,"specAbsLevel":-1,"targetCode":null,"operator":null,"operatorSubtreeLevel":-1}]
        */
        NavigatorConfigDto config = this.getExpressionsForTests();
        ResultActions result = this.executeCall(accessToken, config, "navspec", status().isOk());
        System.out.println("---------");
        System.out.println(result.andReturn().getResponse().getContentAsString());
        System.out.println("---------");
        result.andExpect(jsonPath("$.payload.expressions", Matchers.hasSize(5)));
        result.andExpect(jsonPath("$.metaData.size()", is(0)));
        result.andExpect(jsonPath("$.errors.size()", is(0)));
        
        String navSpec = "code(homepage).subtree(2) + current.children + code(administrators_page).path + super(1) + abs(2).children";
        result.andExpect(jsonPath("$.payload.navSpec", is(navSpec)));
    }
    
    private NavigatorConfigDto getExpressionsForTests() {
        List<NavigatorExpressionDto> list = new ArrayList<>();
        NavigatorExpressionDto dto1 = new NavigatorExpressionDto();
        dto1.setSpec(NavigatorExpression.SPEC_PAGE_CODE);
        dto1.setTargetCode("homepage");
        dto1.setOperator(NavigatorExpression.OPERATOR_SUBTREE_CODE);
        dto1.setOperatorSubtreeLevel(2);
        list.add(dto1);
        NavigatorExpressionDto dto2 = new NavigatorExpressionDto();
        dto2.setSpec(NavigatorExpression.SPEC_CURRENT_PAGE_CODE);
        dto2.setOperator(NavigatorExpression.OPERATOR_CHILDREN_CODE);
        list.add(dto2);
        NavigatorExpressionDto dto3 = new NavigatorExpressionDto();
        dto3.setSpec(NavigatorExpression.SPEC_PAGE_CODE);
        dto3.setTargetCode("administrators_page");
        dto3.setOperator(NavigatorExpression.OPERATOR_PATH_CODE);
        list.add(dto3);
        NavigatorExpressionDto dto4 = new NavigatorExpressionDto();
        dto4.setSpec(NavigatorExpression.SPEC_SUPER_CODE);
        dto4.setSpecSuperLevel(1);
        list.add(dto4);
        NavigatorExpressionDto dto5 = new NavigatorExpressionDto();
        dto5.setSpec(NavigatorExpression.SPEC_ABS_CODE);
        dto5.setSpecAbsLevel(2);
        dto5.setOperator(NavigatorExpression.OPERATOR_CHILDREN_CODE);
        list.add(dto5);
        
        NavigatorConfigDto config = new NavigatorConfigDto();
        config.setExpressions(list);
        return config;
    }
    
    @Test
    public void testGetNavSpec_2() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES).build();
        String accessToken = mockOAuthInterceptor(user);
        NavigatorConfigDto config = new NavigatorConfigDto();
        ResultActions result = this.executeCall(accessToken, config, "navspec", status().isBadRequest());
        result.andExpect(jsonPath("$.payload.size()", is(0)));
        result.andExpect(jsonPath("$.metaData.size()", is(0)));
        result.andExpect(jsonPath("$.errors.size()", is(1)));
        result.andExpect(jsonPath("$.errors[0].code", is("1")));
    }
    
    @Test
    public void testGetNavSpec_3() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES).build();
        String accessToken = mockOAuthInterceptor(user);
        List<NavigatorExpressionDto> list = new ArrayList<>();
        NavigatorExpressionDto dto1 = new NavigatorExpressionDto();
        dto1.setSpec(NavigatorExpression.SPEC_PAGE_CODE);
        dto1.setTargetCode(null);
        dto1.setOperator(NavigatorExpression.OPERATOR_SUBTREE_CODE);
        dto1.setOperatorSubtreeLevel(2);
        list.add(dto1);
        NavigatorConfigDto config = new NavigatorConfigDto();
        config.setExpressions(list);
        ResultActions result = this.executeCall(accessToken, config, "navspec", status().isBadRequest());
        result.andExpect(jsonPath("$.payload.size()", is(0)));
        result.andExpect(jsonPath("$.metaData.size()", is(0)));
        result.andExpect(jsonPath("$.errors.size()", is(1)));
        result.andExpect(jsonPath("$.errors[0].code", is("2")));
    }
    
    @Test
    public void testGetNavSpec_4() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES).build();
        String accessToken = mockOAuthInterceptor(user);
        List<NavigatorExpressionDto> list = new ArrayList<>();
        NavigatorExpressionDto dto1 = new NavigatorExpressionDto();
        dto1.setSpec(NavigatorExpression.SPEC_PAGE_CODE);
        dto1.setTargetCode("invalid");
        list.add(dto1);
        NavigatorConfigDto config = new NavigatorConfigDto();
        config.setExpressions(list);
        ResultActions result = this.executeCall(accessToken, config, "navspec", status().isBadRequest());
        result.andExpect(jsonPath("$.payload.size()", is(0)));
        result.andExpect(jsonPath("$.metaData.size()", is(0)));
        result.andExpect(jsonPath("$.errors.size()", is(1)));
        result.andExpect(jsonPath("$.errors[0].code", is("3")));
    }
    
    @Test
    public void testGetNavSpec_5() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES).build();
        String accessToken = mockOAuthInterceptor(user);
        List<NavigatorExpressionDto> list = new ArrayList<>();
        NavigatorExpressionDto dto1 = new NavigatorExpressionDto();
        dto1.setSpec(NavigatorExpression.SPEC_SUPER_CODE);
        list.add(dto1);
        NavigatorConfigDto config = new NavigatorConfigDto();
        config.setExpressions(list);
        ResultActions result = this.executeCall(accessToken, config, "navspec", status().isBadRequest());
        result.andExpect(jsonPath("$.payload.size()", is(0)));
        result.andExpect(jsonPath("$.metaData.size()", is(0)));
        result.andExpect(jsonPath("$.errors.size()", is(1)));
        result.andExpect(jsonPath("$.errors[0].code", is("4")));
    }
    
    @Test
    public void testGetNavSpec_6() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES).build();
        String accessToken = mockOAuthInterceptor(user);
        List<NavigatorExpressionDto> list = new ArrayList<>();
        NavigatorExpressionDto dto1 = new NavigatorExpressionDto();
        dto1.setSpec(NavigatorExpression.SPEC_ABS_CODE);
        list.add(dto1);
        NavigatorConfigDto config = new NavigatorConfigDto();
        config.setExpressions(list);
        ResultActions result = this.executeCall(accessToken, config, "navspec", status().isBadRequest());
        result.andExpect(jsonPath("$.payload.size()", is(0)));
        result.andExpect(jsonPath("$.metaData.size()", is(0)));
        result.andExpect(jsonPath("$.errors.size()", is(1)));
        result.andExpect(jsonPath("$.errors[0].code", is("5")));
    }
    
    @Test
    public void testGetNavSpec_7() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "managePages", Permission.MANAGE_PAGES).build();
        String accessToken = mockOAuthInterceptor(user);
        List<NavigatorExpressionDto> list = new ArrayList<>();
        NavigatorExpressionDto dto1 = new NavigatorExpressionDto();
        dto1.setSpec(NavigatorExpression.SPEC_PAGE_CODE);
        dto1.setTargetCode("homepage");
        dto1.setOperator(NavigatorExpression.OPERATOR_SUBTREE_CODE);
        list.add(dto1);
        NavigatorConfigDto config = new NavigatorConfigDto();
        config.setExpressions(list);
        ResultActions result = this.executeCall(accessToken, config, "navspec", status().isBadRequest());
        result.andExpect(jsonPath("$.payload.size()", is(0)));
        result.andExpect(jsonPath("$.metaData.size()", is(0)));
        result.andExpect(jsonPath("$.errors.size()", is(1)));
        result.andExpect(jsonPath("$.errors[0].code", is("6")));
    }
    
    private ResultActions executeCall(String accessToken, NavigatorConfigDto request, String subpath, ResultMatcher resultMatcher) throws Exception {
        ResultActions result = mockMvc
                .perform(post("/widget/navigator/"+subpath)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(resultMatcher);
        return result;
    }
    
}

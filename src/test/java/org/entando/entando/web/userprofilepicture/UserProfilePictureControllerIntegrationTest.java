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
package org.entando.entando.web.userprofilepicture;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class UserProfilePictureControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Test
    public void testCreateEditDeleteProfilePicture() throws Exception {
        String accessToken = createAccessToken();
        String username = "admin";

        try {
            performCreateProfilePicture(username, accessToken)
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.username", is("admin")))
                    .andExpect(jsonPath("$.payload.versions.size()", is(4)))
                    .andExpect(jsonPath("$.payload.versions[0].dimensions", isEmptyOrNullString()))
                    .andExpect(jsonPath("$.payload.versions[0].size", is("3 Kb")))
                    .andExpect(jsonPath("$.payload.versions[0].path",
                            is("/Entando/resources/profile/admin/image_test_d0.jpeg")))
                    .andExpect(jsonPath("$.payload.versions[1].dimensions", is("90x90 px")))
                    .andExpect(jsonPath("$.payload.versions[1].size", is("1 Kb")))
                    .andExpect(jsonPath("$.payload.versions[1].path",
                            is("/Entando/resources/profile/admin/image_test_d1.jpeg")))
                    .andExpect(jsonPath("$.payload.versions[2].dimensions", is("130x130 px")))
                    .andExpect(jsonPath("$.payload.versions[2].size", is("1 Kb")))
                    .andExpect(jsonPath("$.payload.versions[2].path",
                            is("/Entando/resources/profile/admin/image_test_d2.jpeg")))
                    .andExpect(jsonPath("$.payload.versions[3].dimensions", is("150x150 px")))
                    .andExpect(jsonPath("$.payload.versions[3].size", is("2 Kb")))
                    .andExpect(jsonPath("$.payload.versions[3].path",
                            is("/Entando/resources/profile/admin/image_test_d3.jpeg")));

            performGetProfilePicture(username, accessToken)
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.username", is("admin")))
                    .andExpect(jsonPath("$.payload.versions.size()", is(4)))
                    .andExpect(jsonPath("$.payload.versions[0].dimensions", isEmptyOrNullString()))
                    .andExpect(jsonPath("$.payload.versions[0].size", is("3 Kb")))
                    .andExpect(jsonPath("$.payload.versions[0].path",
                            is("/Entando/resources/profile/admin/image_test_d0.jpeg")))
                    .andExpect(jsonPath("$.payload.versions[1].dimensions", is("90x90 px")))
                    .andExpect(jsonPath("$.payload.versions[1].size", is("1 Kb")))
                    .andExpect(jsonPath("$.payload.versions[1].path",
                            is("/Entando/resources/profile/admin/image_test_d1.jpeg")))
                    .andExpect(jsonPath("$.payload.versions[2].dimensions", is("130x130 px")))
                    .andExpect(jsonPath("$.payload.versions[2].size", is("1 Kb")))
                    .andExpect(jsonPath("$.payload.versions[2].path",
                            is("/Entando/resources/profile/admin/image_test_d2.jpeg")))
                    .andExpect(jsonPath("$.payload.versions[3].dimensions", is("150x150 px")))
                    .andExpect(jsonPath("$.payload.versions[3].size", is("2 Kb")))
                    .andExpect(jsonPath("$.payload.versions[3].path",
                            is("/Entando/resources/profile/admin/image_test_d3.jpeg")));

            performEditProfilePicture(username, accessToken)
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload.username", is("admin")))
                    .andExpect(jsonPath("$.payload.versions.size()", is(4)))
                    .andExpect(jsonPath("$.payload.versions[0].dimensions", isEmptyOrNullString()))
                    .andExpect(jsonPath("$.payload.versions[0].size", is("3 Kb")))
                    .andExpect(jsonPath("$.payload.versions[0].path",
                            is("/Entando/resources/profile/admin/image_test_d0.jpeg")))
                    .andExpect(jsonPath("$.payload.versions[1].dimensions", is("90x90 px")))
                    .andExpect(jsonPath("$.payload.versions[1].size", is("1 Kb")))
                    .andExpect(jsonPath("$.payload.versions[1].path",
                            is("/Entando/resources/profile/admin/image_test_d1.jpeg")))
                    .andExpect(jsonPath("$.payload.versions[2].dimensions", is("130x130 px")))
                    .andExpect(jsonPath("$.payload.versions[2].size", is("1 Kb")))
                    .andExpect(jsonPath("$.payload.versions[2].path",
                            is("/Entando/resources/profile/admin/image_test_d2.jpeg")))
                    .andExpect(jsonPath("$.payload.versions[3].dimensions", is("150x150 px")))
                    .andExpect(jsonPath("$.payload.versions[3].size", is("2 Kb")))
                    .andExpect(jsonPath("$.payload.versions[3].path",
                            is("/Entando/resources/profile/admin/image_test_d3.jpeg")));
        } finally {
            performDeleteProfilePicture(username, accessToken)
                    .andDo(print())
                    .andExpect(status().isOk());

            performGetProfilePicture(username, accessToken)
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payload", isEmptyOrNullString()));
        }
    }

    private ResultActions performCreateProfilePicture(String username, String accessToken) throws Exception {

        String contents = "some text very big so it has more than 1Kb size asdklasdhadsjakhdsjadjasdhjhjasd some garbage to make it bigger!!!" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x";

        String path = StringUtils.join("/userProfiles/", username, "/profilePicture");
        MockMultipartFile file = new MockMultipartFile("file", "image_test.jpeg", "application/jpeg", contents.getBytes());

        MockHttpServletRequestBuilder request = multipart(path)
                .file(file)
                .header("Authorization", "Bearer " + accessToken);
        return mockMvc.perform(request);
    }

    private ResultActions performGetProfilePicture(String username, String accessToken) throws Exception {
        String path = StringUtils.join("/userProfiles/", username, "/profilePicture");
        return mockMvc.perform(
                get(path)
                        .header("Authorization", "Bearer " + accessToken));
    }

    private ResultActions performDeleteProfilePicture(String username, String accessToken) throws Exception {
        String path = StringUtils.join("/userProfiles/", username, "/profilePicture");
        return mockMvc.perform(
                delete(path)
                        .header("Authorization", "Bearer " + accessToken));
    }

    private ResultActions performEditProfilePicture(String username, String accessToken) throws Exception {

        String contents = "some text very big so it has more than 1Kb size asdklasdhadsjakhdsjadjasdhjhjasd some garbage to make it bigger!!!" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
                "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x";

        String path = StringUtils.join("/userProfiles/", username, "/profilePicture");

        ResultActions result = mockMvc
                .perform(multipart(path)
                        .file(new MockMultipartFile("file", "image_test.jpeg", "application/jpeg", contents.getBytes()))
                        .with(request -> { request.setMethod("PUT"); return request; })
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));

        return result.andDo(print());
    }
    
    private String createAccessToken() {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "manageUserProfile", Permission.MANAGE_USER_PROFILES)
                .build();
        return mockOAuthInterceptor(user);
    }

}

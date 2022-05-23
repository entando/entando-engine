package org.entando.entando.aps.system.services.api.server;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.authorization.Authorization;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.role.Role;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.User;
import java.util.Collections;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response.Status;
import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.oauth2.IApiOAuth2TokenManager;
import org.entando.entando.aps.system.services.oauth2.model.OAuth2AccessTokenImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(MockitoExtension.class)
class ApiRestServerTest {

    @Mock
    private IAuthorizationManager authManager;
    @Mock
    private IUserManager userManager;
    @Mock
    private IApiOAuth2TokenManager tokenManager;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private ServletContext servletContext;
    @Mock
    private WebApplicationContext webApplicationContext;

    @InjectMocks
    private LegacyApiUserExtractor legacyApiUserExtractor;

    private ApiRestServer apiRestServer;

    @BeforeEach
    void setUp() {
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(session.getServletContext()).thenReturn(servletContext);
        Mockito.when(servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)).thenReturn(webApplicationContext);

        Mockito.when(webApplicationContext.getBean(SystemConstants.AUTHORIZATION_SERVICE)).thenReturn(authManager);
        Mockito.when(webApplicationContext.getBean(SystemConstants.LEGACY_API_USER_EXTRACTOR)).thenReturn(legacyApiUserExtractor);

        apiRestServer = new ApiRestServer();
    }

    @Test
    void extractOAuthParameters_adminOnRestrictedEndpoint_shouldAllowAccess() throws Exception {

        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(apiMethod.getRequiredPermission()).thenReturn("viewUsers");

        String token = "<token>";

        Mockito.when(request.getHeaders("Authorization")).thenReturn(Collections.enumeration(Collections.singletonList("Bearer " + token)));

        OAuth2AccessTokenImpl oauthToken = Mockito.mock(OAuth2AccessTokenImpl.class);
        Mockito.when(tokenManager.readAccessToken(ArgumentMatchers.any())).thenReturn(oauthToken);
        Mockito.when(oauthToken.getValue()).thenReturn(token);
        Mockito.when(oauthToken.getLocalUser()).thenReturn("admin");

        User user = new User();
        user.setUsername("admin");
        Mockito.when(userManager.getUser("admin")).thenReturn(user);

        Group group = new Group();
        group.setName("administrators");
        Role role = new Role();
        role.addPermission("superuser");
        Authorization authz = new Authorization(group, role);

        Mockito.when(authManager.getUserAuthorizations("admin")).thenReturn(Collections.singletonList(authz));
        Mockito.when(authManager.isAuthOnPermission(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> {
            apiRestServer.extractOAuthParameters(request, apiMethod, new Properties());
        });
    }

    @Test
    void extractOAuthParameters_guestOnRestrictedEndpoint_shouldDenyAccess() throws Exception {

        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(apiMethod.getRequiredPermission()).thenReturn("viewUsers");
        Mockito.when(apiMethod.getRequiredAuth()).thenReturn(true);

        Mockito.when(request.getHeaders("Authorization")).thenReturn(Collections.emptyEnumeration());

        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
            apiRestServer.extractOAuthParameters(request, apiMethod, new Properties());
        });
        Assertions.assertEquals(IApiErrorCodes.API_AUTHENTICATION_REQUIRED, exception.getErrors().get(0).getCode());
        Assertions.assertEquals(Status.UNAUTHORIZED, exception.getErrors().get(0).getStatus());
    }

    @Test
    void extractOAuthParameters_guestOnFreeEndpoint_shouldAllowAccess() throws Exception {

        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);

        Mockito.when(request.getHeaders("Authorization")).thenReturn(Collections.emptyEnumeration());

        Assertions.assertDoesNotThrow(() -> {
            apiRestServer.extractOAuthParameters(request, apiMethod, new Properties());
        });
    }

    @Test
    void extractOAuthParameters_expiredTokenOnRestrictedEndpoint_shouldDenyAccess() throws Exception {

        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(apiMethod.getRequiredPermission()).thenReturn("viewUsers");

        String token = "<token>";

        Mockito.when(request.getHeaders("Authorization")).thenReturn(Collections.enumeration(Collections.singletonList("Bearer " + token)));

        OAuth2AccessTokenImpl oauthToken = Mockito.mock(OAuth2AccessTokenImpl.class);
        Mockito.when(tokenManager.readAccessToken(ArgumentMatchers.any())).thenReturn(oauthToken);
        Mockito.when(oauthToken.getValue()).thenReturn(token);
        Mockito.when(oauthToken.isExpired()).thenReturn(true);

        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
            apiRestServer.extractOAuthParameters(request, apiMethod, new Properties());
        });
        Assertions.assertEquals(IApiErrorCodes.API_AUTHENTICATION_REQUIRED, exception.getErrors().get(0).getCode());
        Assertions.assertEquals(Status.UNAUTHORIZED, exception.getErrors().get(0).getStatus());
    }

    @Test
    void extractOAuthParameters_unauthorizedUserOnRestrictedEndpoint_shouldDenyAccess() throws Exception {

        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(apiMethod.getRequiredPermission()).thenReturn("simple-user");

        String token = "<token>";

        Mockito.when(request.getHeaders("Authorization")).thenReturn(Collections.enumeration(Collections.singletonList("Bearer " + token)));

        OAuth2AccessTokenImpl oauthToken = Mockito.mock(OAuth2AccessTokenImpl.class);
        Mockito.when(tokenManager.readAccessToken(ArgumentMatchers.any())).thenReturn(oauthToken);
        Mockito.when(oauthToken.getValue()).thenReturn(token);
        Mockito.when(oauthToken.getLocalUser()).thenReturn("simple-user");

        User user = new User();
        user.setUsername("simple-user");
        Mockito.when(userManager.getUser("simple-user")).thenReturn(user);

        Group group = new Group();
        group.setName("free");
        Role role = new Role();
        role.addPermission("viewUsers");
        Authorization authz = new Authorization(group, role);

        Mockito.when(authManager.getUserAuthorizations("simple-user")).thenReturn(Collections.singletonList(authz));
        Mockito.when(authManager.getUserRoles(user)).thenReturn(Collections.singletonList(role));

        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
            apiRestServer.extractOAuthParameters(request, apiMethod, new Properties());
        });
        Assertions.assertEquals(IApiErrorCodes.API_AUTHORIZATION_REQUIRED, exception.getErrors().get(0).getCode());
        Assertions.assertEquals(Status.FORBIDDEN, exception.getErrors().get(0).getStatus());
    }
}

package org.entando.entando.aps.system.services.api.server;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.oauth2.IApiOAuth2TokenManager;
import org.entando.entando.aps.system.services.oauth2.model.OAuth2AccessTokenImpl;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.web.common.interceptor.EntandoBearerTokenExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LegacyApiUserExtractor {

    private static final Logger log = LoggerFactory.getLogger(LegacyApiUserExtractor.class);

    private final IUserManager userManager;
    private final IAuthorizationManager authorizationManager;
    private final IApiOAuth2TokenManager tokenManager;

    public LegacyApiUserExtractor(IUserManager userManager,
            IAuthorizationManager authorizationManager,
            IApiOAuth2TokenManager tokenManager) {
        this.userManager = userManager;
        this.authorizationManager = authorizationManager;
        this.tokenManager = tokenManager;
    }

    public UserDetails getUser(HttpServletRequest request) throws ApiException {
        try {
            String accessToken = new EntandoBearerTokenExtractor().extractToken(request);
            final OAuth2AccessTokenImpl token = (OAuth2AccessTokenImpl) tokenManager.readAccessToken(accessToken);
            if (token != null) {
                // Validate the access token
                if (!token.getValue().equals(accessToken)) {
                    throw new ApiException(IApiErrorCodes.API_AUTHENTICATION_REQUIRED, "Token does not match", Response.Status.UNAUTHORIZED);
                } // check if access token is expired
                else if (token.isExpired()) {
                    throw new ApiException(IApiErrorCodes.API_AUTHENTICATION_REQUIRED, "Token expired", Response.Status.UNAUTHORIZED);
                }
                String username = token.getLocalUser();
                UserDetails user = userManager.getUser(username);
                if (user != null) {
                    user.addAuthorizations(authorizationManager.getUserAuthorizations(username));
                    UserDetails userOnSession = (UserDetails) request.getSession().getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER);
                    if (null == userOnSession || userOnSession.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
                        user.setAccessToken(accessToken);
                        request.getSession().setAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER, user);
                    }
                    return user;
                }
            } else if (accessToken != null) {
                log.warn("Token not found from access token");
            }
            return null;
        } catch (EntException ex) {
            log.error("System exception", ex);
            throw new ApiException(IApiErrorCodes.SERVER_ERROR, ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}

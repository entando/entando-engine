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

package org.entando.entando.aps.system.services.oauth2;

import java.util.List;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public interface IOAuth2TokenDAO {

    void storeAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication);

    List<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String username);

    List<OAuth2AccessToken> findTokensByClientId(String clientId);

    List<OAuth2AccessToken> findTokensByUserName(String username);

    OAuth2AccessToken readAccessToken(final String accessToken);

    void removeAccessToken(final String accessToken);

    void deleteExpiredToken(int expirationTime);

    OAuth2RefreshToken readRefreshToken(String tokenValue);

    OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken refreshToken);

    void removeAccessTokenUsingRefreshToken(final String refreshToken);

}

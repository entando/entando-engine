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

import com.agiletec.aps.system.common.FieldSearchFilter;
import org.entando.entando.ent.exception.EntException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import org.entando.entando.aps.system.services.oauth2.model.ConsumerRecordVO;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

public class OAuthConsumerManager extends AbstractOAuthManager implements IOAuthConsumerManager {

    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(OAuthConsumerManager.class);

    private IOAuthConsumerDAO consumerDAO;

    @Override
    public void init() throws Exception {
        logger.debug("{} ready", this.getClass().getName());
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        BaseClientDetails details = new BaseClientDetails();
        try {
            ConsumerRecordVO consumer = this.getConsumerDAO().getConsumer(clientId);
            if (null == consumer) {
                throw new ClientRegistrationException("Client with id '" + clientId + "' does not exists");
            }
            if (null != consumer.getExpirationDate() && consumer.getExpirationDate().before(new Date())) {
                throw new ClientRegistrationException("Client '" + clientId + "' is expired");
            }
            details.setClientId(clientId);
            if (!StringUtils.isBlank(consumer.getAuthorizedGrantTypes())) {
                details.setAuthorizedGrantTypes(Arrays.asList(consumer.getAuthorizedGrantTypes().split(",")));
            }
            if (!StringUtils.isBlank(consumer.getScope())) {
                details.setScope(Arrays.asList(consumer.getScope().split(",")));
            }
            details.setClientSecret(consumer.getSecret());
            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_CLIENT"));
            details.setAuthorities(authorities);
            if (null != consumer.getCallbackUrl()) {
                Set<String> uris = new HashSet<>();
                uris.add(consumer.getCallbackUrl());
                details.setRegisteredRedirectUri(uris);
            }
            details.setAccessTokenValiditySeconds(this.getAccessTokenValiditySeconds());
            details.setRefreshTokenValiditySeconds(this.getRefreshTokenValiditySeconds());
        } catch (ClientRegistrationException t) {
            throw t;
        } catch (Exception t) {
            logger.error("Error extracting consumer record by key {}", clientId, t);
            throw new ClientRegistrationException("Error extracting consumer record by key " + clientId, t);
        }
        return details;
    }

    @Override
    public ConsumerRecordVO getConsumerRecord(String consumerKey) throws EntException {
        ConsumerRecordVO consumer = null;
        try {
            consumer = this.getConsumerDAO().getConsumer(consumerKey);
        } catch (Exception t) {
            logger.error("Error extracting consumer record by key {}", consumerKey, t);
            throw new EntException("Error extracting consumer record by key " + consumerKey, t);
        }
        return consumer;
    }

    @Override
    public ConsumerRecordVO addConsumer(ConsumerRecordVO consumer) throws EntException {
        try {
            return this.getConsumerDAO().addConsumer(consumer);
        } catch (Throwable t) {
            logger.error("Error adding consumer", t);
            throw new EntException("Error adding consumer", t);
        }
    }

    @Override
    public ConsumerRecordVO updateConsumer(ConsumerRecordVO consumer) throws EntException {
        try {
            return this.getConsumerDAO().updateConsumer(consumer);
        } catch (Throwable t) {
            logger.error("Error updating consumer", t);
            throw new EntException("Error updating consumer", t);
        }
    }

    @Override
    public void deleteConsumer(String clientId) throws EntException {
        try {
            this.getConsumerDAO().deleteConsumer(clientId);
        } catch (Throwable t) {
            logger.error("Error deleting consumer record by key {}", clientId, t);
            throw new EntException("Error deleting consumer record by key " + clientId, t);
        }
    }

    @Override
    public List<String> getConsumerKeys(FieldSearchFilter<?>[] filters) throws EntException {
        List<String> consumerKeys = null;
        try {
            consumerKeys = this.getConsumerDAO().getConsumerKeys(filters);
        } catch (Throwable t) {
            logger.error("Error extracting consumer keys", t);
            throw new EntException("Error extracting consumer keys", t);
        }
        return consumerKeys;
    }

    protected IOAuthConsumerDAO getConsumerDAO() {
        return consumerDAO;
    }

    public void setConsumerDAO(IOAuthConsumerDAO consumerDAO) {
        this.consumerDAO = consumerDAO;
    }

    @Override
    public List<ConsumerRecordVO> getConsumers(FieldSearchFilter<?>[] filters) throws EntException {
        try {
            return consumerDAO.getConsumers(filters);
        } catch (Throwable t) {
            logger.error("Error retrieving consumers", t);
            throw new EntException("Error retrieving consumers", t);
        }
    }
}

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

import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

public class ScheduledDeleteExpiredTokenThread implements Runnable {

    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(ScheduledDeleteExpiredTokenThread.class);
    
    private IOAuth2TokenDAO tokenDAO;
    private int expirationTime;
    
    public ScheduledDeleteExpiredTokenThread(IOAuth2TokenDAO tokenDAO, int expirationTime) {
        this.tokenDAO = tokenDAO;
        this.expirationTime = expirationTime;
    }
    
    @Override
    public void run() {
        logger.debug("start delete expired access token");
        try {
            this.tokenDAO.deleteExpiredToken(this.expirationTime);
        } catch (Exception e) {
            logger.error("Error in deleteExpiredToken {}", e);
        }
        logger.debug("end delete expired access token");
    }

}

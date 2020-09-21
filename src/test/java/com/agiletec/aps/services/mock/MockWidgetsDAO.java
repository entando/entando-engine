/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package com.agiletec.aps.services.mock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

import com.agiletec.aps.system.common.AbstractDAO;
import org.entando.entando.ent.exception.EntException;

/**
 * @author M.Casari
 */
public class MockWidgetsDAO  extends AbstractDAO {

	private static final EntLogger _logger =  EntLogFactory.getSanitizedLogger(MockWidgetsDAO.class);

    /**
     * Restituisce un booleano che attesta la presenza o meno della
     * showlet con il codice dato dal parametro code.
     * @param code Codice della showlet
     * @return true se la showlet esiste, false in caso contrario.
     * @throws EntException In caso di errore nell'accesso al db.
     */
    public boolean exists(String code) throws EntException {
    	Connection conn = null;
        PreparedStatement stat = null;
        ResultSet res = null;
        boolean result = false;
        try {
        	conn = this.getConnection();
            stat = conn.prepareStatement("select pagecode from widgetconfig where pagecode=?");
            stat.setString(1, code);
            res = stat.executeQuery();
            result = res.next();
        } catch (Throwable t) {
            _logger.error("Error checking test widget",  t);
			throw new RuntimeException("Error checking test widget", t);
			//processDaoException(t, "Errore in controllo presenza showlet di test", "exists");
        } finally {
            closeDaoResources(res, stat, conn);
        }
        return result;
    }
}

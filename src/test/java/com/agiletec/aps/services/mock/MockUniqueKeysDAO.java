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
public class MockUniqueKeysDAO extends AbstractDAO {
	
	private static final EntLogger _logger =  EntLogFactory.getSanitizedLogger(MockUniqueKeysDAO.class);
	
    /**
     * @param id L'id del contatore.
     * @return chiave univoca corrente.
     * @throws EntException In caso di errore nell'accesso al db.
     */
    public int getCurrentKey(int id) throws EntException {
    	Connection conn = null;
        PreparedStatement stat = null;
        ResultSet res = null;
        int current = -1;
        try {
        	conn = this.getConnection();
            stat = conn.prepareStatement("select keyvalue from uniquekeys where id=?");
            stat.setInt(1, id);
            res = stat.executeQuery();
            if (res.next()) {
				current = res.getInt("keyvalue");
			}
        } catch (Throwable t) {
            _logger.error("Error loading unique key",  t);
			throw new RuntimeException("Error loading unique key", t);
			//processDaoException(t, "Errore in controllo presenza showlet di test", "exists");
        } finally {
            closeDaoResources(res, stat, conn);
        }
        return current;
    }
}

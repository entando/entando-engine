/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.entando.entando.aps.system.services.seomapping;

import java.util.List;

import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

import com.agiletec.aps.system.common.AbstractSearcherDAO;
import com.agiletec.aps.system.common.FieldSearchFilter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.entando.entando.aps.system.init.model.portdb.FriendlyCode;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.exception.EntRuntimeException;

/**
 * @author E.Santoboni
 */
public class SeoMappingDAO extends AbstractSearcherDAO implements ISeoMappingDAO {

	private static final EntLogger logger =  EntLogFactory.getSanitizedLogger(SeoMappingDAO.class);
    
    private static final String LOAD_MAPPING_TYPES = "SELECT DISTINCT objecttype FROM friendlycode";
    
    private static final String LOAD_MAPPINGS = 
			"SELECT id, friendlycode, objecttype, objectcode, langcode FROM " + FriendlyCode.TABLE_NAME;
    
    private static final String ADD_MAPPING = 
			"INSERT INTO " + FriendlyCode.TABLE_NAME + " (id, friendlycode, objecttype, objectcode, langcode) VALUES (?, ?, ?, ?, ?)";
    
    private static final String DELETE_MAPPING = 
            "DELETE FROM " + FriendlyCode.TABLE_NAME + " WHERE objecttype = ? AND objectcode = ?";
    
    private static final String DELETE_FROM_FRIENDLYCODE = 
            "DELETE FROM " + FriendlyCode.TABLE_NAME + " WHERE friendlycode = ?";

    private static final String EXTRACT_NEXT_ID
            = "SELECT MAX(id) FROM " + FriendlyCode.TABLE_NAME;
    
    @Override
	public Map<String, FriendlyCodeVO> loadMapping() {
		Map<String, FriendlyCodeVO> mapping = new HashMap<>();
		Connection conn = null;
		Statement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			stat = conn.createStatement();
			res = stat.executeQuery(LOAD_MAPPINGS);
			FriendlyCodeVO vo = null;
			while (res.next()) {
                int id = res.getInt(1);
				String friendlycode = res.getString(2);
				String type = res.getString(3);
				String code = res.getString(4);
				String langCode = res.getString(5);
				vo = new FriendlyCodeVO(id, friendlycode, type, code, langCode);
				mapping.put(vo.getFriendlyCode(), vo);
			}
		} catch (Exception e) {
			logger.error("Error while loading mapping", e);
			throw new EntRuntimeException("Error while loading mapping", e);
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return mapping;
	}

    @Override
    public void deleteMappingForPage(String pageCode) {
        super.executeQueryWithoutResultset(DELETE_MAPPING, ISeoMappingManager.TYPE_PAGE, pageCode);
    }

    @Override
    public List<String> getObjectTypes() {
        List<String> mapping = new ArrayList<>();
        Connection conn = null;
        Statement stat = null;
        ResultSet res = null;
        try {
            conn = this.getConnection();
            stat = conn.createStatement();
            res = stat.executeQuery(LOAD_MAPPING_TYPES);
            FriendlyCodeVO vo = null;
            while (res.next()) {
                mapping.add(res.getString(1));
            }
        } catch (Exception e) {
            logger.error("Error while loading mapping", e);
            throw new EntRuntimeException("Error while loading mapping types", e);
        } finally {
            closeDaoResources(res, stat, conn);
        }
        return mapping;
    }
    
    @Override
	public void addMapping(FriendlyCodeVO vo) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
            this.addRecord(vo, conn);
			conn.commit();
		} catch (Exception e) {
			this.executeRollback(conn);
			logger.error("Error add the mapping", e);
			throw new EntRuntimeException("Error add the mapping", e);
		} finally {
			this.closeConnection(conn);
		}
	}
    
    @Override
	public void updateMapping(FriendlyCodeVO vo) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
            super.executeQueryWithoutResultset(conn, DELETE_FROM_FRIENDLYCODE, vo.getFriendlyCode());
			this.addRecord(vo, conn);
			conn.commit();
		} catch (Exception e) {
			this.executeRollback(conn);
			logger.error("Error update the mapping", e);
			throw new EntRuntimeException("Error update the mapping", e);
		} finally {
			this.closeConnection(conn);
		}
	}
	
	protected void addRecord(FriendlyCodeVO vo, Connection conn) throws EntException {
        PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(ADD_MAPPING);
			stat.setInt(1, this.extractNextId(conn));
			stat.setString(2, vo.getFriendlyCode());
			stat.setString(3, vo.getObjectType());
			stat.setString(4, vo.getObjectCode());
			stat.setString(5, vo.getLangCode());
			stat.executeUpdate();
		} catch (Exception t) {
			logger.error("Error adding a record", t);
			throw new EntRuntimeException("Error adding a record", t);
		} finally {
			closeDaoResources(null, stat);
		}
	}
    
    protected int extractNextId(Connection conn) {
        int id = 0;
        Statement stat = null;
        ResultSet res = null;
        try {
            stat = conn.createStatement();
            res = stat.executeQuery(EXTRACT_NEXT_ID);
            res.next();
            id = res.getInt(1) + 1;
        } catch (Exception e) {
            logger.error("Error extracting next id", e);
            throw new EntRuntimeException("Error extracting next id", e);
        } finally {
            this.closeDaoResources(res, stat);
        }
        return id;
    }
    
	@Override
	public List<String> searchFriendlyCode(FieldSearchFilter[] filters) {
		return super.searchId(filters);
	}

	@Override
	protected String getTableFieldName(String metadataFieldKey) {
		return metadataFieldKey;
	}
	
	@Override
	protected String getMasterTableName() {
		return FriendlyCode.TABLE_NAME;
	}
	
	@Override
	protected String getMasterTableIdFieldName() {
		return "friendlycode";
	}
	
}
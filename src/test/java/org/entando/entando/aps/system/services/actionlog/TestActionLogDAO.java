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
package org.entando.entando.aps.system.services.actionlog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import javax.sql.DataSource;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.util.DateConverter;
import org.entando.entando.aps.system.services.actionlog.model.ActionLogRecord;
import org.entando.entando.aps.system.services.actionlog.model.ActionLogRecordSearchBean;
import org.entando.entando.aps.system.services.actionlog.model.IActionLogRecordSearchBean;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TestActionLogDAO extends BaseTestCase {
    
    @Test
	public void testGetActionRecords() {
		IActionLogRecordSearchBean bean = null;
		List<Integer> ids = this._actionLoggerDAO.getActionRecords(bean);
        //		this.compareIds(new Integer[]{}, ids);
        ActionLogRecord record1 = this._helper.createActionRecord(1, "username1", "actionName1",
                                                                  "namespace1", DateConverter.parseDate("01/01/2009 00:00", "dd/MM/yyyy HH:mm"), "params1");
        ActionLogRecord record2 = this._helper.createActionRecord(2, "username2", "actionName2",
                                                                  "namespace2", DateConverter.parseDate("01/01/2009 10:00", "dd/MM/yyyy HH:mm"), "params2");
        ActionLogRecord record3 = this._helper.createActionRecord(3, "username123", "actionName123",
                                                                  "namespace123", DateConverter.parseDate("02/01/2009 12:00", "dd/MM/yyyy HH:mm"), "params123");
        this._helper.addActionRecord(record1);
        this._helper.addActionRecord(record2);
        this._helper.addActionRecord(record3);
        //
        //		ids = this._actionLoggerDAO.getActionRecords(bean);
        //		this.compareIds(new Integer[]{1, 2, 3}, ids);

		ActionLogRecordSearchBean searchBean = this._helper.createSearchBean("name", "Name", "space", "arams", null, null);
		ids = this._actionLoggerDAO.getActionRecords(searchBean);
		this.compareIds(new Integer[]{1, 2, 3}, ids);

		searchBean = this._helper.createSearchBean("name", "Name", "space", "arams", DateConverter.parseDate("01/01/2009 10:01", "dd/MM/yyyy HH:mm"), null);
		ids = this._actionLoggerDAO.getActionRecords(searchBean);
		this.compareIds(new Integer[]{3}, ids);

		searchBean = this._helper.createSearchBean(null, null, null, null, null, DateConverter.parseDate("01/01/2009 10:01", "dd/MM/yyyy HH:mm"));
		ids = this._actionLoggerDAO.getActionRecords(searchBean);
		this.compareIds(new Integer[]{1, 2}, ids);

		searchBean = this._helper.createSearchBean(null, "Name", null, null, DateConverter.parseDate("01/01/2009 09:01", "dd/MM/yyyy HH:mm"),
				DateConverter.parseDate("01/01/2009 10:01", "dd/MM/yyyy HH:mm"));
		ids = this._actionLoggerDAO.getActionRecords(searchBean);
		this.compareIds(new Integer[]{2}, ids);

	}

	@Test
	public void testActionLogSearch() {
		IActionLogRecordSearchBean bean = null;
		List<Integer> ids = this._actionLoggerDAO.getActionRecords(bean);
		this.compareIds(new Integer[]{}, ids);
		ActionLogRecord record1 = this._helper.createActionRecord(1, "username1", "actionName1",
				"namespace1", DateConverter.parseDate("01/01/2009 00:00", "dd/MM/yyyy HH:mm"), "params1");
		ActionLogRecord record2 = this._helper.createActionRecord(2, "username2", "actionName2",
				"namespace2", DateConverter.parseDate("01/01/2009 10:00", "dd/MM/yyyy HH:mm"), "params2");
		ActionLogRecord record3 = this._helper.createActionRecord(3, "username123", "actionName123",
				"namespace123", DateConverter.parseDate("02/01/2009 12:00", "dd/MM/yyyy HH:mm"), "params123");
		this._helper.addActionRecord(record1);
		this._helper.addActionRecord(record2);
		this._helper.addActionRecord(record3);

		ActionLogRecordSearchBean searchBean = this._helper.createSearchBean(null, "Name", null, null, DateConverter.parseDate("02/01/2009 10:01", "dd/MM/yyyy HH:mm"),
				DateConverter.parseDate("02/01/2009 14:01", "dd/MM/yyyy HH:mm"));
		ids = this._actionLoggerDAO.getActionRecords(searchBean);
		this.compareIds(new Integer[]{3}, ids);
	}

	@Test
	public void testAddGetDeleteActionRecord() {
		ActionLogRecord record1 = this._helper.createActionRecord(1, "username1", "actionName1",
				"namespace1", DateConverter.parseDate("01/01/2009 00:00", "dd/MM/yyyy HH:mm"), "params1");
		ActionLogRecord record2 = this._helper.createActionRecord(2, "username2", "actionName2",
				"namespace2", DateConverter.parseDate("01/02/2009 00:00", "dd/MM/yyyy HH:mm"), "params2");

		this._actionLoggerDAO.addActionRecord(record1);
		this._actionLoggerDAO.addActionRecord(record2);
		ActionLogRecord addedRecord1 = this._actionLoggerDAO.getActionRecord(record1.getId());
		this.compareActionRecords(record1, addedRecord1);
		ActionLogRecord addedRecord2 = this._actionLoggerDAO.getActionRecord(record2.getId());
		this.compareActionRecords(record2, addedRecord2);

		this._actionLoggerDAO.deleteActionRecord(record1.getId());
		assertNull(this._actionLoggerDAO.getActionRecord(record1.getId()));

		this._actionLoggerDAO.deleteActionRecord(record2.getId());
		assertNull(this._actionLoggerDAO.getActionRecord(record2.getId()));
	}
	
	private void compareIds(Integer[] expected, List<Integer> received) {
		assertEquals(expected.length, received.size());
		for (Integer id : expected) {
			if (!received.contains(id)) {
				fail("Id \"" + id + "\" not found");
			}
		}
	}

	private void compareActionRecords(ActionLogRecord expected, ActionLogRecord received) {
		assertEquals(expected.getId(), received.getId());
		assertEquals(expected.getUsername(), received.getUsername());
		assertEquals(expected.getActionName(), received.getActionName());
		assertEquals(expected.getNamespace(), received.getNamespace());
		assertEquals(expected.getParameters(), received.getParameters());
		assertEquals(DateConverter.getFormattedDate(expected.getActionDate(), "ddMMyyyyHHmm"),
				DateConverter.getFormattedDate(received.getActionDate(), "ddMMyyyyHHmm"));
	}

    @BeforeAll
	private void init() {
		ActionLogDAO actionLoggerDAO = new ActionLogDAO();
		DataSource dataSource = (DataSource) this.getApplicationContext().getBean("servDataSource");
		actionLoggerDAO.setDataSource(dataSource);
		this._actionLoggerDAO = actionLoggerDAO;
		this._helper = new ActionLoggerTestHelper(this.getApplicationContext());
        this._helper.cleanRecords();
	}
    
    @AfterAll
	protected void destroy() throws Exception {
		this._helper.cleanRecords();
	}
    
	private IActionLogDAO _actionLoggerDAO;
	private ActionLoggerTestHelper _helper;

}

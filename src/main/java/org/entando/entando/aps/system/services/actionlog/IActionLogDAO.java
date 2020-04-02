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

import com.agiletec.aps.system.common.FieldSearchFilter;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.entando.entando.aps.system.services.actionlog.model.ActionLogRecord;
import org.entando.entando.aps.system.services.actionlog.model.IActionLogRecordSearchBean;
import org.entando.entando.aps.system.services.actionlog.model.IActivityStreamSearchBean;

/**
 * @author E.Santoboni - S.Puddu
 */
public interface IActionLogDAO {

    List<Integer> getActionRecords(IActionLogRecordSearchBean searchBean);

    List<Integer> getActivityStreamRecords(IActivityStreamSearchBean searchBean);

    List<Integer> getActionRecords(FieldSearchFilter[] filters);

    List<Integer> getActionRecords(FieldSearchFilter[] filters, List<String> userGroupCodes);

    void addActionRecord(ActionLogRecord actionRecord);

    ActionLogRecord getActionRecord(int id);

    void deleteActionRecord(int id);

    Date getLastUpdate(IActionLogRecordSearchBean searchBean);

    Date getLastUpdate(FieldSearchFilter[] filters, List<String> userGroupCodes);

    Set<Integer> extractOldRecords(Integer maxActivitySizeByGroup);

    void updateRecordDate(int id);

    int countActionLogRecords(IActionLogRecordSearchBean searchBean);

}

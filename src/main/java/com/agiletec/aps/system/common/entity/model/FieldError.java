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
package com.agiletec.aps.system.common.entity.model;

import java.io.Serializable;

/**
 * @author E.Santoboni
 */
public class FieldError implements Serializable {

    public FieldError(String fieldCode, String errorCode) {
        this.setErrorCode(errorCode);
        this.setFieldCode(fieldCode);
    }

    public String getFieldCode() {
        return _fieldCode;
    }
    protected void setFieldCode(String fieldCode) {
        this._fieldCode = fieldCode;
    }

    public String getErrorCode() {
        return _errorCode;
    }
    protected void setErrorCode(String errorCode) {
        this._errorCode = errorCode;
    }

    public String getMessage() {
        return _message;
    }
    public void setMessage(String message) {
        this._message = message;
    }

    public String getMessageKey() {
        return _messageKey;
    }
    public void setMessageKey(String messageKey) {
        this._messageKey = messageKey;
    }

    private String _fieldCode;
    private String _errorCode;
    private String _message;
    private String _messageKey;

    public static final String MANDATORY = "MANDATORY";
    public static final String INVALID = "INVALID";
    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String INVALID_MIN_LENGTH = "INVALID_MIN_LENGTH";
    public static final String INVALID_MAX_LENGTH = "INVALID_MAX_LENGTH";
    public static final String LESS_THAN_ALLOWED = "LESS_THAN_ALLOWED";
    public static final String GREATER_THAN_ALLOWED = "GREATER_THAN_ALLOWED";
    public static final String NOT_EQUALS_THAN_ALLOWED = "NOT_EQUALS_THAN_ALLOWED";

}
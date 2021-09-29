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
package org.entando.entando.web.page.model;

import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class PageCloneRequest {

    @Size(max = 30, message = "string.size.invalid")
    @NotNull(message = "page.code.notBlank")
    @Pattern(regexp = "[a-zA-Z0-9_]+", message="page.code.wrongCharacters")
    private String newPageCode;
    private String parentCode;
    private Map<String, String> titles = new HashMap<>();

    public String getNewPageCode() {
        return newPageCode;
    }

    public void setNewPageCode(String newPageCode) {
        this.newPageCode = newPageCode;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public Map<String, String> getTitles() {
        return titles;
    }

    public void setTitles(Map<String, String> titles) {
        this.titles = titles;
    }
}

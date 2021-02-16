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

import java.io.Serializable;

/**
 * @author E.Santoboni
 */
public class FriendlyCodeVO implements Serializable {

    private int id;
	private String friendlyCode;
	private String objectType;
	private String objectCode;
    private String langCode;
    
    public FriendlyCodeVO(String friendlyCode, String objectType, String objectCode) {
        this.friendlyCode = friendlyCode;
        this.objectType = objectType;
        this.objectCode = objectCode;
    }
    
    public FriendlyCodeVO(int id, String friendlyCode, String objectType, String objectCode) {
        this(friendlyCode, objectType, objectCode);
        this.id = id;
    }

    public FriendlyCodeVO(int id, String friendlyCode, String objectType, String objectCode, String langCode) {
        this(id, friendlyCode, objectType, objectCode);
        this.langCode = langCode;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public String getFriendlyCode() {
        return friendlyCode;
    }
    public void setFriendlyCode(String friendlyCode) {
        this.friendlyCode = friendlyCode;
    }

    public String getObjectType() {
        return objectType;
    }
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getObjectCode() {
        return objectCode;
    }
    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    public String getLangCode() {
        return langCode;
    }
    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }
    
}

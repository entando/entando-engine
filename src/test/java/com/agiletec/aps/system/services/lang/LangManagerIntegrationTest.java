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
package com.agiletec.aps.system.services.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import com.agiletec.aps.BaseTestCaseJunit5;
import com.agiletec.aps.system.SystemConstants;
import org.entando.entando.ent.exception.EntException;
import org.junit.jupiter.api.Test;

public class LangManagerIntegrationTest extends BaseTestCaseJunit5 {

	@Test
    public void testGetLang() throws EntException {
        ILangManager langManager = (ILangManager) this.getService(SystemConstants.LANGUAGE_MANAGER);
		Lang lang = langManager.getLang("en");
		String langCode = lang.getCode();
		String langDescr = lang.getDescr();
		assertEquals(langCode, "en");
		assertEquals(langDescr, "English");
	}

	@Test
    public void testGetDefaultLang() throws EntException {
		ILangManager langManager = (ILangManager) this.getService(SystemConstants.LANGUAGE_MANAGER);
		Lang lang = langManager.getDefaultLang();
		String langCode = lang.getCode();
		String langDescr = lang.getDescr();
		assertEquals(langCode, "it");
		assertEquals(langDescr, "Italiano");
	}

	@Test
    public void testGetLangs() throws EntException {
		ILangManager langManager = (ILangManager) this.getService(SystemConstants.LANGUAGE_MANAGER);
		List<Lang> langs = langManager.getLangs();
		assertEquals(2, langs.size());
		for (Lang lang : langs) {
			String code = lang.getCode();
			if (code.equals("it")) {
				assertEquals("Italiano", lang.getDescr());
			} else if (code.equals("en")) {
				assertEquals("English", lang.getDescr());
			} else {
				fail();
			}
		}
	}

	@Test
    public void testGetAssignableLangs() throws Throwable {
		ILangManager langManager = (ILangManager) this.getService(SystemConstants.LANGUAGE_MANAGER);
		List<Lang> assignableLangs = langManager.getAssignableLangs();
		assertTrue(!assignableLangs.isEmpty());
		Lang firstLang = (Lang) assignableLangs.get(0);
		assertEquals("om", firstLang.getCode());
		assertEquals("(Afan) Oromo", firstLang.getDescr());

		Lang lastLang = (Lang) assignableLangs.get(assignableLangs.size() - 1);
		assertEquals("zu", lastLang.getCode());
		assertEquals("Zulu", lastLang.getDescr());
	}

	@Test
    public void testAddUpdateRemoveLang() throws Throwable {
		ILangManager langManager = (ILangManager) this.getService(SystemConstants.LANGUAGE_MANAGER);
		int systemLangs = langManager.getLangs().size();
		try {
			langManager.addLang("ro");
			Lang addedLang = langManager.getLang("ro");
			assertEquals("ro", addedLang.getCode());
			assertEquals("Romanian", addedLang.getDescr());
			assertEquals(systemLangs + 1, langManager.getLangs().size());

			langManager.updateLang("ro", "New Descr Romanian Lang");
			addedLang = langManager.getLang("ro");
			assertEquals("ro", addedLang.getCode());
			assertEquals("New Descr Romanian Lang", addedLang.getDescr());
			assertEquals(systemLangs + 1, langManager.getLangs().size());

		} catch (Throwable t) {
			throw t;
		} finally {
			langManager.removeLang("ro");
			assertNull(langManager.getLang("ro"));
			assertEquals(systemLangs, langManager.getLangs().size());
		}
	}

}

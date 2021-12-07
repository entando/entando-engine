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
package com.agiletec.aps.system.services.page;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.agiletec.aps.system.services.pagemodel.PageModel;
import com.agiletec.aps.util.ApsProperties;
import org.apache.commons.lang3.time.DateUtils;

import java.util.*;

public class PageTestUtil {

	public static void comparePagesFull(IPage expected, IPage actual, boolean changed) {
		comparePages(expected, actual, changed);
		comparePageMetadata(expected.getMetadata(), actual.getMetadata(), 0);
		compareWidgets(expected.getWidgets(), actual.getWidgets());
	}

	public static void comparePages(IPage expected, IPage actual, boolean changed) {
		if (expected == null) {
			assertNull(actual);
		} else {
			assertEquals(expected.getCode(), actual.getCode());
			if (expected.getChildrenCodes() == null) {
				assertNull(actual.getChildrenCodes());
			} else {
				assertEquals(expected.getChildrenCodes().length, actual.getChildrenCodes().length);
			}
			if (expected.getChildrenCodes() == null) {
				assertNull(actual.getChildrenCodes());
			} else {
				assertEquals(expected.getChildrenCodes().length, actual.getChildrenCodes().length);
			}
			assertEquals(expected.getGroup(), actual.getGroup());
			assertEquals(expected.getTitles(), actual.getTitles());
			assertEquals(changed, actual.isChanged());
			assertEquals(expected.isOnline(), actual.isOnline());
			assertEquals(expected.isShowable(), actual.isShowable());
			assertEquals(expected.isUseExtraTitles(), actual.isUseExtraTitles());
			assertEquals(expected.getMimeType(), actual.getMimeType());
			assertEquals(expected.getCharset(), actual.getCharset());
		}
	}

	/**
	 * @param expected The expected PageMetadata to check
	 * @param actual The actual PageMetadata to check
	 * @param dateExpectation The result of the comparison between the 2
	 * updatedAt dates. 1 if expected is greater, 0 if equals, -1 if lower
	 */
	public static void comparePageMetadata(PageMetadata expected, PageMetadata actual, Integer dateExpectation) {
		if (expected == null) {
			assertNull(actual);
		} else {
			assertEquals(expected.getTitles(), actual.getTitles());
			assertEquals(expected.getExtraGroups(), actual.getExtraGroups());
			if (expected.getModelCode() == null) {
				assertNull(actual.getModelCode());
			} else {
				assertEquals(expected.getModelCode(), actual.getModelCode());
			}
			assertEquals(expected.isShowable(), actual.isShowable());
			assertEquals(expected.isUseExtraTitles(), actual.isUseExtraTitles());
			assertEquals(expected.getMimeType(), actual.getMimeType());
			assertEquals(expected.getCharset(), actual.getCharset());
			if (dateExpectation != null) {
				if (expected.getUpdatedAt() == null) {
					if (dateExpectation < 0) {
						assertNotNull(actual.getUpdatedAt());
					} else {
						assertNull(actual.getUpdatedAt());
					}
				} else {
					assertEquals(dateExpectation.intValue(), DateUtils.truncate(expected.getUpdatedAt(), Calendar.MILLISECOND).compareTo(
							DateUtils.truncate(actual.getUpdatedAt(), Calendar.MILLISECOND)));
				}
			}
		}
	}

	public static void compareWidgets(Widget[] expected, Widget[] actual) {
		if (expected == null) {
			assertNull(actual);
		} else {
			assertEquals(expected.length, actual.length);
			for (int i = 0; i < expected.length; i++) {
				assertEquals(expected[i].getConfig(), actual[i].getConfig());
				assertEquals(expected[i].getTypeCode(), actual[i].getTypeCode());
			}
		}
	}

	public static IPage getPageByCode(List<IPage> pages, String code) {
		IPage extractedPage = null;
		for (IPage page : pages) {
			if (page.getCode().equals(code)) {
				extractedPage = page;
				break;
			}
		}
		return extractedPage;
	}

	public static IPage getPageByCode(Map<String, IPage> pages, String code) {
		return pages.get(code);
	}

	public static Page createPage(String code, String parentPageCode, 
            String groupName, PageModel pageModel, PageMetadata metadata, Widget[] widgets) {
		Page page = new Page();
		page.setCode(code);
		page.setParentCode(parentPageCode);
		page.setMetadata(metadata);
		page.setGroup(groupName);
        if (null == widgets) {
            if (null != pageModel) {
                widgets = new Widget[pageModel.getFrames().length];
            } else {
                widgets = new Widget[0];
            }
        }
		page.setWidgets(widgets);
		return page;
	}

	public static PageMetadata createPageMetadata(PageModel pageModel, boolean showable, String defaultTitle, String mimeType,
			String charset, boolean useExtraTitles, Set<String> extraGroups, Date updatedAt) {
		PageMetadata metadata = new PageMetadata();
		metadata.setModelCode(pageModel.getCode());

		metadata.setShowable(showable);
		metadata.setTitle("it", defaultTitle);
		if (extraGroups != null) {
			metadata.setExtraGroups(extraGroups);
		}
		metadata.setMimeType(mimeType);
		metadata.setCharset(charset);
		metadata.setUseExtraTitles(useExtraTitles);
		metadata.setExtraGroups(extraGroups);
		metadata.setUpdatedAt(updatedAt);
		return metadata;
	}

	public static <T> T[] copyArray(T[] arrayToCopy) {
		T[] copiedArray = null;
		if (arrayToCopy != null) {
			copiedArray = Arrays.copyOf(arrayToCopy, arrayToCopy.length);
		}
		return copiedArray;
	}

	public static ApsProperties createProperties(String p1Key, String p1Value, String p2Key, String p2Value) {
		ApsProperties properties = new ApsProperties();
		properties.setProperty(p1Key, p1Value);
		properties.setProperty(p2Key, p2Value);
		return properties;
	}

	public static Widget createWidget(String widgetCode, ApsProperties config) {
		Widget widget = new Widget();
		widget.setConfig(config);
		widget.setTypeCode(widgetCode);
		return widget;
	}

	public static Widget[] getValuedWidgets(Widget[] widgets) {
		int inUse = 0;
		Widget[] widgetsInUse = {};
		if (widgets != null) {
			for (Widget current : widgets) {
				if (current != null) {
					inUse++;
				}
			}
			widgetsInUse = new Widget[inUse];
			int index = 0;
			for (Widget current : widgets) {
				if (current != null) {
					widgetsInUse[index++] = current;
				}
			}
		}
		return widgetsInUse;
	}

	public static void deletePage(Page pageToDelete, PageDAO pageDao) {
		pageDao.deletePage(pageToDelete);
	}

}

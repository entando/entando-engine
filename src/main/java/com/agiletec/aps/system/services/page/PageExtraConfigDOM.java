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

import com.agiletec.aps.util.ApsProperties;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

import org.entando.entando.ent.exception.EntException;

/**
 * Dom class for parse the xml of extra page config
 * @author E.Santoboni
 */
public class PageExtraConfigDOM {

	private static final EntLogger _logger = EntLogFactory.getSanitizedLogger(PageExtraConfigDOM.class);
	
	private static final String USE_EXTRA_TITLES_ELEMENT_NAME = "useextratitles";
	private static final String EXTRA_GROUPS_ELEMENT_NAME = "extragroups";
	private static final String EXTRA_GROUP_ELEMENT_NAME = "group";
	private static final String EXTRA_GROUP_NAME_ATTRIBUTE = "name";
	
	private static final String MIMETYPE_ELEMENT_NAME = "mimeType";
	private static final String CHARSET_ELEMENT_NAME = "charset";
    
    private static final String USE_EXTRA_DESCRIPTIONS_ELEMENT_NAME = "useextradescriptions";
    private static final String DESCRIPTIONS_ELEMENT_NAME = "descriptions";
    private static final String DESCRIPTION_PROPERTY_NAME = PageMetatag.DESCRIPTION_KEY;
    private static final String KEYWORDS_ELEMENT_NAME = "keywords";
    private static final String KEYWORDS_PROPERTY_NAME = PageMetatag.KEYWORDS_KEY;
    private static final String USE_DEFAULT_LANG_ELEMENT_NAME = "useDefaultLang";
    private static final String PROPERTY_ELEMENT_NAME = "property";
    private static final String KEY_ATTRIBUTE_NAME = "key";

    private static final String FRIENDLY_CODE_ELEMENT_NAME = "friendlycode";

    @Deprecated
    private static final String XML_CONFIG_ELEMENT_NAME = "xmlConfig";

    private static final String COMPLEX_PARAMS_ELEMENT_NAME = "complexParameters";
	
	public void addExtraConfig(PageMetadata page, String xml) throws EntException {
		Document doc = this.decodeDOM(xml);
		this.addExtraConfig(page, doc);
	}
	
	protected void addExtraConfig(PageMetadata page, Document doc) throws EntException {
		Element rootElement = doc.getRootElement();
		Element useExtraTitlesElement = rootElement.getChild(USE_EXTRA_TITLES_ELEMENT_NAME);
		if (null != useExtraTitlesElement) {
			Boolean value = Boolean.valueOf(useExtraTitlesElement.getText());
			page.setUseExtraTitles(value.booleanValue());
		}
		Element extraGroupsElement = rootElement.getChild(EXTRA_GROUPS_ELEMENT_NAME);
		if (null != extraGroupsElement) {
			List<Element> groupElements = extraGroupsElement.getChildren(EXTRA_GROUP_ELEMENT_NAME);
			for (int i=0; i<groupElements.size(); i++) {
				Element groupElement = groupElements.get(i);
				page.addExtraGroup(groupElement.getAttributeValue(EXTRA_GROUP_NAME_ATTRIBUTE));
			}
		}
		Element mimetypeElement = rootElement.getChild(MIMETYPE_ELEMENT_NAME);
		if (null != mimetypeElement) {
			String mimetype = mimetypeElement.getText();
			if (null != mimetype && mimetype.trim().length() > 0) {
				page.setMimeType(mimetype);
			}
		}
		Element charsetElement = rootElement.getChild(CHARSET_ELEMENT_NAME);
		if (null != charsetElement) {
			String charset = charsetElement.getText();
			if (null != charset && charset.trim().length() > 0) {
				page.setCharset(charset);
			}
		}
        Element useExtraDescriptionsElement = rootElement.getChild(USE_EXTRA_DESCRIPTIONS_ELEMENT_NAME);
        if (null != useExtraDescriptionsElement) {
            Boolean value = Boolean.valueOf(useExtraDescriptionsElement.getText());
            page.setUseExtraDescriptions(value.booleanValue());
        }
        Element descriptionsElement = rootElement.getChild(DESCRIPTIONS_ELEMENT_NAME);
        this.extractMultilangProperty(descriptionsElement, page.getDescriptions(), DESCRIPTION_PROPERTY_NAME);
        Element keywordsElement = rootElement.getChild(KEYWORDS_ELEMENT_NAME);
        this.extractMultilangProperty(keywordsElement, page.getKeywords(), KEYWORDS_PROPERTY_NAME);
        Element friendlyCodeElement = rootElement.getChild(FRIENDLY_CODE_ELEMENT_NAME);
        if (null != friendlyCodeElement) {
            page.setFriendlyCode(friendlyCodeElement.getText());
        }
        Element xmlConfigElement = rootElement.getChild(XML_CONFIG_ELEMENT_NAME);
        if (null != xmlConfigElement) {
            //Used to guarantee porting with previous versions of the plugin
            String xml = xmlConfigElement.getText();
            page.setComplexParameters(this.extractComplexParameters(xml));
        } else {
            Element complexParamElement = rootElement.getChild(COMPLEX_PARAMS_ELEMENT_NAME);
            if (null != complexParamElement) {
                List<Element> elements = complexParamElement.getChildren();
                page.setComplexParameters(this.extractComplexParameters(elements));
            }
        }
	}

    private void extractMultilangProperty(Element mainElement, ApsProperties propertyToFill, String propertyName) {
        if (null != mainElement) {
            List<Element> mainElements = mainElement.getChildren(PROPERTY_ELEMENT_NAME);
            for (int i = 0; i < mainElements.size(); i++) {
                Element singleElement = mainElements.get(i);
                String langCode = singleElement.getAttributeValue(KEY_ATTRIBUTE_NAME);
                String useDefaultLang = singleElement.getAttributeValue(USE_DEFAULT_LANG_ELEMENT_NAME);
                PageMetatag metatag = new PageMetatag(langCode, propertyName, singleElement.getText());
                metatag.setUseDefaultLangValue(Boolean.parseBoolean(useDefaultLang));
                propertyToFill.put(langCode, metatag);
            }
        }
    }

    /**
     * Extract the complex parameters from string
     *
     * @param xmlConfig the config
     * @return the map of complex parameters
     * @deprecated Used to guarantee porting with previous versions of the plugin
     */
    @Deprecated
    private Map<String, Map<String, PageMetatag>> extractComplexParameters(String xmlConfig) throws EntException {
        Document doc = this.decodeDOM(xmlConfig);
        List<Element> elements = doc.getRootElement().getChildren();
        return this.extractComplexParameters(elements);
    }

    protected Map<String, Map<String, PageMetatag>> extractComplexParameters(List<Element> elements) {
        Map<String, Map<String, PageMetatag>> complexParameters = new HashMap<>();
        if (null == elements) {
            return complexParameters;
        }
        for (Element paramElement : elements) {
            String elementName = paramElement.getName();
            if (elementName.equals("parameter")) {
                //Used to guarantee porting with previous versions of the plugin
                String key = paramElement.getAttributeValue("key");
                List<Element> langElements = paramElement.getChildren("property");
                if (null != langElements && langElements.size() > 0) {
                    for (Element langElement : langElements) {
                        String langCode = langElement.getAttributeValue("key");
                        Map<String, PageMetatag> langMap = this.extractLangMap(langCode, complexParameters);
                        String useDefaultLang = langElement.getAttributeValue(USE_DEFAULT_LANG_ELEMENT_NAME);
                        PageMetatag metatag = new PageMetatag(langCode, key, langElement.getText(),Boolean.parseBoolean(useDefaultLang));
                        langMap.put(key, metatag);
                    }
                } else {
                    Map<String, PageMetatag> defaultLang = this.extractLangMap("default", complexParameters);
                    PageMetatag metatag = new PageMetatag("default", key, paramElement.getText());
                    defaultLang.put(key, metatag);
                }
            } else if (elementName.equals("lang")) {
                String langCode = paramElement.getAttributeValue("code");
                Map<String, PageMetatag> langMap = this.extractLangMap(langCode, complexParameters);
                List<Element> langElements = paramElement.getChildren("meta");
                for (Element langElement : langElements) {
                    String key = langElement.getAttributeValue("key");
                    PageMetatag metatag = new PageMetatag(langCode, key, langElement.getText());
                    metatag.setKeyAttribute(langElement.getAttributeValue("attributeName"));
                    String useDefaultLang = langElement.getAttributeValue(USE_DEFAULT_LANG_ELEMENT_NAME);
                    metatag.setUseDefaultLangValue(Boolean.parseBoolean(useDefaultLang));
                    langMap.put(key, metatag);
                }
            }
        }
        return complexParameters;
    }
    
 /*
    complexParameters Structure
    .....
    <complexParameters>
    <lang code="it">
      <meta key="key5">VALUE_5_IT</meta>
      <meta key="key3" attributeName="name" useDefaultLang="false" >VALUE_3_IT</meta>
      <meta key="key2" attributeName="property" useDefaultLang="true" />
    </lang>
    <lang code="en">
      <meta key="key5">VALUE_5_IT</meta>
      <meta key="key3" attributeName="name" useDefaultLang="false" >VALUE_3_EN</meta>
      <meta key="key2" attributeName="property" useDefaultLang="true" />
    </lang>
    ...
    ...
    </complexParameters>
    .....
     */
    
    
    private Map<String, PageMetatag> extractLangMap(String code,
            Map<String, Map<String, PageMetatag>> complexParameters) {
        Map<String, PageMetatag> langMap = complexParameters.get(code);
        if (null == langMap) {
            langMap = new HashMap<>();
            complexParameters.put(code, langMap);
        }
        return langMap;
    }
	
	public String extractXml(PageMetadata page) {
		Document doc = new Document();
		Element elementRoot = new Element("config");
		doc.setRootElement(elementRoot);
		this.fillDocument(doc, page);
		return this.getXMLDocument(doc);
	}
	
	protected void fillDocument(Document doc, PageMetadata page) {
		Set<String> extraGroups = page.getExtraGroups();
		Element useExtraTitlesElement = new Element(USE_EXTRA_TITLES_ELEMENT_NAME);
		useExtraTitlesElement.setText(String.valueOf(page.isUseExtraTitles()));
		doc.getRootElement().addContent(useExtraTitlesElement);
		if (null != extraGroups && !extraGroups.isEmpty()) {
			Element extraGroupsElement = new Element(EXTRA_GROUPS_ELEMENT_NAME);
			doc.getRootElement().addContent(extraGroupsElement);
			Iterator<String> iterator = extraGroups.iterator();
			while (iterator.hasNext()) {
				String group = iterator.next();
				Element extraGroupElement = new Element(EXTRA_GROUP_ELEMENT_NAME);
				extraGroupElement.setAttribute(EXTRA_GROUP_NAME_ATTRIBUTE, group);
				extraGroupsElement.addContent(extraGroupElement);
			}
		}
		String charset = page.getCharset();
		if (null != charset && charset.trim().length() > 0) {
			Element charsetElement = new Element(CHARSET_ELEMENT_NAME);
			charsetElement.setText(charset);
			doc.getRootElement().addContent(charsetElement);
		}
		String mimeType = page.getMimeType();
		if (null != mimeType && mimeType.trim().length() > 0) {
			Element mimeTypeElement = new Element(MIMETYPE_ELEMENT_NAME);
			mimeTypeElement.setText(mimeType);
			doc.getRootElement().addContent(mimeTypeElement);
		}
        Element useExtraDescriptionsElement = new Element(USE_EXTRA_DESCRIPTIONS_ELEMENT_NAME);
        useExtraDescriptionsElement.setText(String.valueOf(page.isUseExtraDescriptions()));
        doc.getRootElement().addContent(useExtraDescriptionsElement);
        ApsProperties descriptions = page.getDescriptions();
        this.fillMultilangProperty(descriptions, doc.getRootElement(), DESCRIPTIONS_ELEMENT_NAME);
        ApsProperties keywords = page.getKeywords();
        this.fillMultilangProperty(keywords, doc.getRootElement(), KEYWORDS_ELEMENT_NAME);
        if (null != page.getFriendlyCode() && page.getFriendlyCode().trim().length() > 0) {
            Element friendlyCodeElement = new Element(FRIENDLY_CODE_ELEMENT_NAME);
            friendlyCodeElement.setText(page.getFriendlyCode().trim());
            doc.getRootElement().addContent(friendlyCodeElement);
        }
        if (null != page.getComplexParameters()) {
            Element complexConfigElement = new Element(COMPLEX_PARAMS_ELEMENT_NAME);
            this.addComplexParameters(complexConfigElement, page.getComplexParameters());
            doc.getRootElement().addContent(complexConfigElement);
        }
	}

    private void fillMultilangProperty(ApsProperties property, Element elementToFill, String elementName) {
        if (null != property && property.size() > 0) {
            Element mlElement = new Element(elementName);
            elementToFill.addContent(mlElement);
            Iterator<Object> iterator = property.keySet().iterator();
            while (iterator.hasNext()) {
                String langCode = (String) iterator.next();
                Element langElement = new Element(PROPERTY_ELEMENT_NAME);
                langElement.setAttribute(KEY_ATTRIBUTE_NAME, langCode);
                PageMetatag metatag = (PageMetatag) property.get(langCode);

                langElement.setAttribute(USE_DEFAULT_LANG_ELEMENT_NAME, String.valueOf(metatag.isUseDefaultLangValue()));
                langElement.setText(metatag.getValue());
                mlElement.addContent(langElement);
            }
        }
    }

    protected void addComplexParameters(Element elementRoot, Map<String, Map<String, PageMetatag>> parameters) {
        if (null == parameters) {
            return;
        }
        Iterator<String> iter1 = parameters.keySet().iterator();
        while (iter1.hasNext()) {
            String langCode = iter1.next();
            Map<String, PageMetatag> metas = parameters.get(langCode);
            if (langCode.equals("default")) {
                Iterator<String> iter2 = metas.keySet().iterator();
                while (iter2.hasNext()) {
                    String key2 = iter2.next();
                    Element parameterElement = new Element("parameter");
                    PageMetatag metatag = metas.get(key2);
                    parameterElement.setAttribute("key", metatag.getKey());
                    parameterElement.setText(metatag.getValue());
                    elementRoot.addContent(parameterElement);
                }
            } else {
                Element langElement = new Element("lang");
                langElement.setAttribute("code", langCode);
                Iterator<String> iter2 = metas.keySet().iterator();
                while (iter2.hasNext()) {
                    String key2 = iter2.next();
                    Element metaElement = new Element("meta");
                    PageMetatag metatag = metas.get(key2);
                    metaElement.setAttribute("key", metatag.getKey());
                    metaElement.setAttribute("attributeName", metatag.getKeyAttribute());
                    metaElement.setAttribute(USE_DEFAULT_LANG_ELEMENT_NAME, String.valueOf(metatag.isUseDefaultLangValue()));
                    metaElement.setText(metatag.getValue());
                    langElement.addContent(metaElement);
                }
                elementRoot.addContent(langElement);
            }
        }
    }
	
	private Document decodeDOM(String xml) throws EntException {
		Document doc = null;
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		StringReader reader = new StringReader(xml);
		try {
			doc = builder.build(reader);
		} catch (Throwable t) {
			_logger.error("Error while parsing xml: {} ", xml, t);
			throw new EntException("Error detected while parsing the XML", t);
		}
		return doc;
	}
	
	protected String getXMLDocument(Document doc) {
		XMLOutputter out = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		out.setFormat(format);
		return out.outputString(doc);
	}
	
}
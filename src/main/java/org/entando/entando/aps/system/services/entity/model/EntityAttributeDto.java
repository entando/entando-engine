/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.entity.model;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.attribute.*;
import com.agiletec.aps.util.DateConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author E.Santoboni
 */
public class EntityAttributeDto {
    private final EntLogger logger = EntLogFactory.getSanitizedLogger(getClass());

    private String code;

    private Object value;

    private Map<String, Object> values = new HashMap<>();

    private List<EntityAttributeDto> elements = new ArrayList<>();

    @JsonProperty("compositeelements")
    private List<EntityAttributeDto> compositeElements = new ArrayList<>();

    @JsonProperty("listelements")
    private Map<String, List<EntityAttributeDto>> listElements = new HashMap<>();

    public EntityAttributeDto() {
    }

    public EntityAttributeDto(AttributeInterface src) {
        this.setCode(src.getName());
        Object value = src.getValue();
        if (null == value) {
            return;
        }
        if (src.isSimple()) {
            if ((value instanceof String) || (value instanceof Number)) {
                this.setValue(value.toString());
            } else if (value instanceof Boolean) {
                this.setValue(value);
            } else if (value instanceof Date) {
                String stringDate = DateConverter.getFormattedDate((Date) value, SystemConstants.API_DATE_FORMAT);
                this.setValue(stringDate);
            } else if (value instanceof Map) {
                setValues(((Map<String,Object>) value).entrySet().stream()
                    .filter(e -> e.getValue() != null)
                    .map(e -> {
                        Object mapValue = e.getValue();
                        String key = e.getKey();
                        if (mapValue instanceof AttributeInterface) {
                            return new AbstractMap.SimpleEntry<>(key, new EntityAttributeDto((AttributeInterface) mapValue));
                        } else {
                            return new AbstractMap.SimpleEntry<>(key, mapValue);
                        }
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            } else if(TextAttribute.class.isAssignableFrom(src.getClass())){
                setValue(value);
                setValues(((TextAttribute)src).getTextMap().entrySet().stream()
                        .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            }
        } else if (src instanceof MonoListAttribute) {
            List<AttributeInterface> list = ((MonoListAttribute) src).getAttributes();
            list.stream().forEach(element -> this.getElements().add(new EntityAttributeDto(element)));
        } else if (src instanceof CompositeAttribute) {
            Map<String, AttributeInterface> map = ((CompositeAttribute) src).getAttributeMap();
            map.keySet().stream().forEach(key -> this.getCompositeElements().add(new EntityAttributeDto(map.get(key))));
        } else if (src instanceof ListAttribute) {
            Map<String, List<AttributeInterface>> map = ((ListAttribute) src).getAttributeListMap();
            map.keySet().stream().forEach(key -> {
                List<EntityAttributeDto> dtos = new ArrayList<>();
                List<AttributeInterface> list = map.get(key);
                list.stream().forEach(element -> dtos.add(new EntityAttributeDto(element)));
                this.getListElements().put(key, dtos);
            });
        }
    }

    public void fillEntityAttribute(AttributeInterface attribute, BindingResult bindingResult) {
        if (attribute instanceof ITextAttribute) {
            ITextAttribute textAttribute = (ITextAttribute) attribute;
            if (attribute.isMultilingual() && this.getValues() != null && !this.getValues().isEmpty()) {
                this.getValues().keySet().stream().forEach(langCode -> textAttribute.setText(this.getValues().get(langCode).toString(), langCode));
            } else if (null != this.getValue()) {
                textAttribute.setText(this.getValue().toString(), null);
            }
        }
        if (attribute instanceof NumberAttribute && (null != this.getValue())) {
            BigDecimal number = new BigDecimal(this.getValue().toString());
            ((NumberAttribute) attribute).setValue(number);
        }
        if (attribute instanceof BooleanAttribute) {
            if (this.getValue() instanceof String) {
                ((BooleanAttribute) attribute).setBooleanValue(Boolean.valueOf((String) this.getValue()));
            } else {
                ((BooleanAttribute) attribute).setBooleanValue((Boolean) this.getValue());
            }
        }
        if (attribute instanceof DateAttribute && (null != this.getValue())) {
            Date date = null;
            String dateValue = null;
            try {
                date = DateConverter.parseDate(this.getValue().toString(), SystemConstants.API_DATE_FORMAT);
            } catch (Exception e) {
                dateValue = this.getValue().toString();
            }
            ((DateAttribute) attribute).setDate(date);
            ((DateAttribute) attribute).setFailedDateString(dateValue);
        }
        if (attribute instanceof CompositeAttribute && (null != this.getCompositeElements())) {
            this.getCompositeElements().stream().forEach(i -> {
                AttributeInterface compositeElement = ((CompositeAttribute) attribute).getAttribute(i.getCode());
                i.fillEntityAttribute(compositeElement, bindingResult);
            });
        } else if (attribute instanceof MonoListAttribute && (null != this.getElements())) {
            this.getElements().stream().forEach(i -> {
                AttributeInterface prototype = ((MonoListAttribute) attribute).addAttribute();
                prototype.setName(((MonoListAttribute) attribute).getName());
                i.fillEntityAttribute(prototype, bindingResult);
            });
        } else if (attribute instanceof ListAttribute && (null != this.getListElements())) {
            ((ListAttribute) attribute).getAttributeListMap().clear();
            this.getListElements().keySet().stream().forEach(langCode -> {
                List<EntityAttributeDto> list = this.getListElements().get(langCode);
                list.stream().forEach(i -> {
                    AttributeInterface prototype = ((ListAttribute) attribute).addAttribute(langCode);
                    prototype.setName(((ListAttribute) attribute).getName());
                    i.fillEntityAttribute(prototype, bindingResult);
                });
            });
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

    public List<EntityAttributeDto> getElements() {
        return elements;
    }

    public void setElements(List<EntityAttributeDto> elements) {
        this.elements = elements;
    }

    public List<EntityAttributeDto> getCompositeElements() {
        return compositeElements;
    }

    public void setCompositeElements(List<EntityAttributeDto> compositeElements) {
        this.compositeElements = compositeElements;
    }

    public Map<String, List<EntityAttributeDto>> getListElements() {
        return listElements;
    }

    public void setListElements(Map<String, List<EntityAttributeDto>> listElements) {
        this.listElements = listElements;
    }
    
}

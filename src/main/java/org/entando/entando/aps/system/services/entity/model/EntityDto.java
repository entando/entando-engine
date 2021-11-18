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

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.BooleanAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.CompositeAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.DateAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.ListAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.MonoListAttribute;
import com.agiletec.aps.util.CheckFormatUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.web.entity.validator.EntityValidator;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.BindingResult;

public class EntityDto implements Serializable {

    @NotBlank(message = "entity.id.notBlank")
    private String id;

    @Size(min = 3, max = 3, message = "string.size.invalid")
    @NotNull(message = "entity.typeCode.notBlank")
    private String typeCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String typeDescription;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String mainGroup;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<String> groups;
    private List<EntityAttributeDto> attributes = new ArrayList<>();

    public EntityDto() {
    }

    public EntityDto(IApsEntity src) {
        this.setId(src.getId());
        this.setTypeCode(src.getTypeCode());
        this.setTypeDescription(src.getTypeDescription());
        this.setDescription(src.getDescription());
        this.setMainGroup(src.getMainGroup());
        this.setGroups(src.getGroups());
        if (null != src.getAttributeList()) {
            src.getAttributeList().stream().forEach(j -> this.getAttributes().add(new EntityAttributeDto(j)));
        }
    }

    public void fillEntity(IApsEntity prototype, BindingResult bindingResult) {
        clearAttributeData(prototype.getAttributeList());
        prototype.setId(this.getId());
        prototype.setDescription(getDescription() == null ? prototype.getDescription() : getDescription());
        prototype.setMainGroup(getMainGroup() == null ? prototype.getMainGroup() : getMainGroup());
        prototype.setTypeDescription(getTypeDescription() == null ? prototype.getTypeDescription() : getTypeDescription());
        if (null != this.getGroups()) {
            prototype.setGroups(this.getGroups());
        }
        fillAttributeData(bindingResult, prototype.getAttributeMap());
    }

    protected void clearAttributeData(List<AttributeInterface> attributes) {
        for (AttributeInterface attribute : attributes) {
            clearAttribute(attribute);
        }
    }

    protected void clearAttribute(AttributeInterface attribute) {
        clearBooleanAttribute(attribute);
        clearDateAttribute(attribute);
        clearListAttribute(attribute);
        clearMonolistAttribute(attribute);
        clearCompositeAttribute(attribute);
    }

    private void clearBooleanAttribute(AttributeInterface attribute) {
        if (BooleanAttribute.class.isAssignableFrom(attribute.getClass())) {
            BooleanAttribute booleanAttribute = (BooleanAttribute) attribute;
            booleanAttribute.setBooleanValue(null);
        }
    }

    private void clearDateAttribute(AttributeInterface attribute) {
        if (DateAttribute.class.isAssignableFrom(attribute.getClass())) {
            DateAttribute dateAttribute = (DateAttribute) attribute;
            dateAttribute.setDate(null);
        }
    }

    private void clearListAttribute(AttributeInterface attribute) {
        if (ListAttribute.class.isAssignableFrom(attribute.getClass())) {
            ListAttribute listAttribute = (ListAttribute) attribute;
            for (Entry<String, List<AttributeInterface>> entry : listAttribute.getAttributeListMap().entrySet()) {
                for (AttributeInterface element : entry.getValue()) {
                    clearAttribute(element);
                }
            }
        }
    }

    private void clearMonolistAttribute(AttributeInterface attribute) {
        if (MonoListAttribute.class.isAssignableFrom(attribute.getClass())) {
            MonoListAttribute monolistAttribute = (MonoListAttribute) attribute;
            monolistAttribute.getAttributes().clear();
        }
    }

    private void clearCompositeAttribute(AttributeInterface attribute) {
        if (CompositeAttribute.class.isAssignableFrom(attribute.getClass())) {
            CompositeAttribute compositeAttribute = (CompositeAttribute) attribute;
            for (AttributeInterface element : compositeAttribute.getAttributes()) {
                clearAttribute(element);
            }
        }
    }

    protected void fillAttributeData(BindingResult bindingResult, Map<String, AttributeInterface> attributeMap) {
        for (EntityAttributeDto attributeDto : this.getAttributes()) {
            AttributeInterface attribute = attributeMap.get(attributeDto.getCode());
            if (attribute != null) {
                fillAttribute(attribute, attributeDto, bindingResult);
            } else {
                rejectAttributeNotFound(bindingResult, attributeDto);
            }
        }
    }

    protected void rejectAttributeNotFound (BindingResult bindingResult, EntityAttributeDto attributeDto) {
        bindingResult.reject(EntityValidator.ERRCODE_ATTRIBUTE_INVALID, new String[]{attributeDto.getCode()}, "entity.attribute.code.invalid");
    }

    protected void fillAttribute(AttributeInterface attribute, EntityAttributeDto attributeDto, BindingResult bindingResult) {
        attributeDto.fillEntityAttribute(attribute, bindingResult);
        fillDateAttribute(attribute, attributeDto, bindingResult);
        fillListAttribute(attribute, attributeDto, bindingResult);
        fillMonolistAttribute(attribute, attributeDto, bindingResult);
        fillCompositeAttribute(attribute, attributeDto, bindingResult);
    }

    private void fillDateAttribute(AttributeInterface attribute, EntityAttributeDto attributeDto, BindingResult bindingResult) {
        if (DateAttribute.class.isAssignableFrom(attribute.getClass())) {
            DateAttribute dateAttribute = (DateAttribute)attribute;
            if (dateAttribute.getDate() != null) {
                return;
            }
            String value = (String) attributeDto.getValue();
            Date data = null;
            if (value != null) {
                value = value.trim();
            }
            if (CheckFormatUtil.isValidDate(value)) {
                try {
                    SimpleDateFormat dataF = new SimpleDateFormat("dd/MM/yyyy");
                    data = dataF.parse(value);
                    dateAttribute.setFailedDateString(null);
                } catch (ParseException ex) {
                    throw new RuntimeException(
                            StringUtils.join("Error while parsing the date submitted - ", value, " -"), ex);
                }
            } else {
                dateAttribute.setFailedDateString(value);
            }
            dateAttribute.setDate(data);
        }
    }

    private void fillListAttribute(AttributeInterface attribute, EntityAttributeDto attributeDto,
            BindingResult bindingResult) {
        if (ListAttribute.class.isAssignableFrom(attribute.getClass())) {
            ListAttribute listAttribute = (ListAttribute) attribute;
            for (Entry<String, List<EntityAttributeDto>> entry : attributeDto.getListElements().entrySet()) {
                int index = 0;
                for (EntityAttributeDto element : entry.getValue()) {
                    fillAttribute(listAttribute.getAttributeList(entry.getKey()).get(index), element, bindingResult);
                    index++;
                }
            }
        }
    }

    private void fillMonolistAttribute(AttributeInterface attribute, EntityAttributeDto attributeDto, BindingResult bindingResult) {
        if (MonoListAttribute.class.isAssignableFrom(attribute.getClass())) {
            MonoListAttribute monolistAttribute = (MonoListAttribute) attribute;
            int index = 0;
            for (EntityAttributeDto element : attributeDto.getElements()) {
                fillAttribute(monolistAttribute.getAttribute(index), element, bindingResult);
                index++;
            }
        }
    }

    private void fillCompositeAttribute(AttributeInterface attribute, EntityAttributeDto attributeDto, BindingResult bindingResult) {
        if (CompositeAttribute.class.isAssignableFrom(attribute.getClass())) {
            CompositeAttribute compositeAttribute = (CompositeAttribute) attribute;
            for (EntityAttributeDto element : attributeDto.getCompositeElements()) {
                for (AttributeInterface att : compositeAttribute.getAttributes()) {
                    if (element.getCode().equals(att.getName())) {
                        fillAttribute(att, element, bindingResult);
                        break;
                    }
                }
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMainGroup() {
        return mainGroup;
    }

    public void setMainGroup(String mainGroup) {
        this.mainGroup = mainGroup;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }

    public List<EntityAttributeDto> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<EntityAttributeDto> attributes) {
        this.attributes = attributes;
    }

}

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

import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.parse.IApsEntityDOM;
import com.agiletec.aps.system.services.category.Category;
import com.agiletec.aps.system.services.group.IGroupManager;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents an Entity. The structure of the entity, defined during the configuration process, is built invoking the method
 * 'addAttribute', but this procedure is reserved for the Content Service which invokes this method only during the system initialization.
 * The standard procedure to instantiate an entity, used during the normal execution of the system, is to make a request to the service: it
 * will clone the prototype of the entity previously defined in the configuration.
 *
 * @author E. Santoboni
 */
public interface IApsEntity extends Serializable {

    /**
     * Return the ID of the entity.
     *
     * @return The identification string of the entity.
     */
    String getId();

    /**
     * Associate the entity to the given ID code.
     *
     * @param id The identification string of the entity.
     */
    void setId(String id);

    /**
     * Return the description of the entity.
     *
     * @return The description of entity.
     */
    String getDescription();

    /**
     * Set up the description of the entity.
     *
     * @param description he description of entity.
     */
    void setDescription(String description);

    @Deprecated
    String getDescr();

    @Deprecated
    void setDescr(String descr);

    /**
     * Add an attribute to the list of the attributes of the entity.
     *
     * @param attribute The attribute to add.
     */
    void addAttribute(AttributeInterface attribute);

    /**
     * Return an Entity Attribute identified by the key.
     *
     * @param key The name of the attribute,
     * @return The requested attribute.
     */
    AttributeInterface getAttribute(String key);

    /**
     * Return an Entity Attribute identified by the role.
     *
     * @param roleName The role of the attribute,
     * @return The requested attribute.
     */
    AttributeInterface getAttributeByRole(String roleName);

    /**
     * Add a category to the list of the entity categories.
     *
     * @param category The category to add.
     */
    void addCategory(Category category);

    /**
     * Return the list of categories associated to the entity.
     *
     * @return A list of categories.
     */
    List<Category> getCategories();

    /**
     * Remove a category from the list of the entity categories.
     *
     * @param category The category to remove.
     */
    void removeCategory(Category category);

    /**
     * Return a Map of the attributes defined in this entity.
     *
     * @return A Map object containing all the attributes.
     */
    Map<String, AttributeInterface> getAttributeMap();

    /**
     * Return the code of the Entity Type.
     *
     * @return The code of the Entity Type.
     */
    String getTypeCode();

    /**
     * Set up the code of the Entity Type.
     *
     * @param typeCode The Entity Type code.
     */
    void setTypeCode(String typeCode);

    /**
     * Return the description of the Entity Type.
     *
     * @return The description of the Entity Type.
     */
    String getTypeDescription();

    /**
     * Set up the description of the Entity Type.
     *
     * @param typeDescription The description of the Entity Type.
     */
    void setTypeDescription(String typeDescription);

    @Deprecated
    String getTypeDescr();

    @Deprecated
    void setTypeDescr(String typeDescr);

    /**
     * Return the string that identifies the main group the entity belongs to.
     *
     * @return The main group this entity belongs to.
     */
    String getMainGroup();

    /**
     * Set up the main group the entity belongs to.
     *
     * @param mainGroup The main group this entity belongs to.
     */
    void setMainGroup(String mainGroup);

    /**
     * Return the set of codes belonging to the authorized groups.
     *
     * @return The set of codes of the additional groups.
     */
    Set<String> getGroups();

    /**
     * Add an additional group to those authorized.
     *
     * @param groupName The code of the group to add.
     */
    void addGroup(String groupName);

    /**
     * Return a list of the Entity Attributes.
     *
     * @return The list of attributes.
     */
    List<AttributeInterface> getAttributeList();

    /**
     * Set up the language to use in the rendering process of the entity and its attributes.
     *
     * @param langCode The code of the language to use in the rendering process.
     */
    void setRenderingLang(String langCode);

    /**
     * Set up the default language of the entity and its attributes.
     *
     * @param langCode The code of the default language.
     */
    void setDefaultLang(String langCode);

    /**
     * Create an object from the prototype.
     *
     * @return The object created from the prototype.
     */
    IApsEntity getEntityPrototype();

    /**
     * Set up the DOM class that generates the XML which defines the Entity.
     *
     * @param entityDom The DOM class that generates the XML
     */
    void setEntityDOM(IApsEntityDOM entityDom);

    /**
     * Return the XML string that describes the entity.
     *
     * @return The XML string describing the entity.
     */
    String getXML();

    /**
     * Disable those attributes whose deactivation code matches the given one.
     *
     * @param disablingCode The deactivation code.
     */
    void disableAttributes(String disablingCode);

    void activateAttributes();

    List<FieldError> validate(IGroupManager groupManager);

}

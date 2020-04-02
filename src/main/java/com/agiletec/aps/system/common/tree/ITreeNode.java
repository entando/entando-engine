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

package com.agiletec.aps.system.common.tree;

import com.agiletec.aps.util.ApsProperties;
import java.io.Serializable;

/**
 * Interface of a node of a tree. The node is the basic information a tree and contains all the minimum information necessary for its
 * definition.
 *
 * @author E.Santoboni
 */
public interface ITreeNode extends Serializable {

    /**
     * Return the node code.
     *
     * @return The node code.
     */
    String getCode();

    /**
     * Indicates whether the node is the root of the tree.
     *
     * @return true if the root, false otherwise.
     */
    boolean isRoot();

    /**
     * Return the parent node code.
     *
     * @return The parent node.
     */
    String getParentCode();

    void setParentCode(String parentCode);

    /**
     * Returns the ordered set codes of nodes in lower level.
     *
     * @return The set of codes of nodes in lower level.
     */
    String[] getChildrenCodes();

    /**
     * Returns the position of the node compared to the brothers nodes.
     *
     * @return The position of the node compared to the brothers nodes.
     */
    int getPosition();

    void setPosition(int position);

    /**
     * Return the group code this node belongs to
     *
     * @return The group code
     */
    String getGroup();

    /**
     * Returns a properties with the titles of the node, where the keys are the codes of language.
     *
     * @return The node titles.
     */
    ApsProperties getTitles();

    /**
     * Returns the title of the node in the specified language.
     *
     * @param langCode The code of the language.
     * @return The title of the node.
     */
    String getTitle(String langCode);

    /**
     * Set the title of the node in the specified language.
     *
     * @param langCode The code of the language.
     * @param title The title of the node to set.
     */
    void setTitle(String langCode, String title);

    /**
     * Returns the title (including the parent nodes) of the single node in the specified language.
     *
     * @param langCode The code of the language.
     * @return The full title of the node.
     */
    String getFullTitle(String langCode, ITreeNodeManager treeNodeManager);

    String getShortFullTitle(String langCode, ITreeNodeManager treeNodeManager);

    String getShortFullTitle(String langCode, String separator, ITreeNodeManager treeNodeManager);

    /**
     * Returns the title (including the parent nodes) of the single node in the specified language.
     *
     * @param langCode The code of the language.
     * @param separator The separator between the titles.
     * @return The full title of the node.
     */
    String getFullTitle(String langCode, String separator, ITreeNodeManager treeNodeManager);

    /**
     * Returns the path of the single node.The separator between the node will be '/' and the path contains the root node.
     *
     * @return the path of the single node.
     */
    String getPath(ITreeNodeManager treeNodeManager);

    /**
     * Returns the path of the single node.The array in composed by node codes from the root up to the current node
     *
     * @param separator The separator between the nodes.
     * @param addRoot Add the root node
     * @return the path of the single node.
     */
    String getPath(String separator, boolean addRoot, ITreeNodeManager treeNodeManager);

    /**
     * Returns the path array of the current node.The array in composed by node codes from the root up to the current node.
     *
     * @return the required path array of the single node.
     */
    String[] getPathArray(ITreeNodeManager treeNodeManager);

    /**
     * Returns the path array of the current node.The array in composed by node codes from the root up to the current node
     *
     * @param addRoot if true, the array starts with the code of the root node
     * @return the required path array of the single node.
     */
    String[] getPathArray(boolean addRoot, ITreeNodeManager treeNodeManager);

    /**
     * Indicates whether the node is child of the other specificated node.
     *
     * @param nodeCode The code of the node
     * @return true if the node is child of the other node, false otherwise.
     */
    boolean isChildOf(String nodeCode, ITreeNodeManager treeNodeManager);

    String getManagerBeanCode();

}

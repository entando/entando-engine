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
package org.entando.entando.aps.util;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.PageUtils;
import java.util.Collection;
import org.springframework.lang.Nullable;

public class GenericResourceUtils extends PageUtils {

    /**
     * Correct implementation of the resource compatibility algorithm
     * <p>
     * A resource is linkable by a parent resource if and only if is's actually accessible by all the potential
     * users of that can access the parent resource. Which means that the resource to link should be accessible
     * by all the groups of the parent resource, in order to be linkable.
     *
     * @param resourceGroup         the group allowed to access of the page to link
     * @param resourceExtraGroups   the extra groups allowed to access the page to link
     * @param contentGroup          the group allowed to access of the parent content
     * @param contentExtraGroups    the extra groups allowed to access of the parent content
     */
    public static boolean isResourceLinkableByContent(
            String resourceGroup, @Nullable Collection<String> resourceExtraGroups,
            String contentGroup, @Nullable Collection<String> contentExtraGroups) {

        if (!isResourceAccessibleByGroup(contentGroup, resourceGroup, resourceExtraGroups)) {
            return false;
        }

        if (contentExtraGroups != null) {
            for (String egr : contentExtraGroups) {
                if (!isResourceAccessibleByGroup(egr, resourceGroup, resourceExtraGroups)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isResourceAccessibleByGroup(String byGroup,
            String resourceOwnerGroup, Collection<String> resourceExtraGroups) {
        return byGroup.equals(Group.ADMINS_GROUP_NAME) ||
                resourceOwnerGroup.equals(Group.FREE_GROUP_NAME) ||
                resourceOwnerGroup.equals(byGroup) ||
                (resourceExtraGroups != null &&
                        (resourceExtraGroups.contains(Group.FREE_GROUP_NAME) || resourceExtraGroups.contains(byGroup))
                );
    }
}

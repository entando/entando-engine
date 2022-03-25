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
package org.entando.entando.aps.system.services.page;

import static org.entando.entando.aps.system.services.page.PageService.ERRCODE_PAGE_NOT_FOUND;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.agiletec.aps.system.services.authorization.AuthorizationManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.user.UserDetails;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.services.auth.AbstractAuthorizationService;
import org.entando.entando.aps.system.services.page.model.PageDto;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author paddeo
 */
public class PageAuthorizationService extends AbstractAuthorizationService<PageDto> {
    
    @Autowired
    private IPageManager pageManager;
    
    @Autowired
    private AuthorizationManager authorizationManager;
    
    public IPageManager getPageManager() {
        return pageManager;
    }
    
    public void setPageManager(IPageManager pageManager) {
        this.pageManager = pageManager;
    }
    
    public AuthorizationManager getAuthorizationManager() {
        return authorizationManager;
    }
    
    public void setAuthorizationManager(AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }
    
    @Override
    public boolean isAuth(UserDetails user, PageDto pageDto) {
        return this.isAuth(user, pageDto, true);
    }

    @Override
    public boolean isAuth(UserDetails user, PageDto pageDto, boolean allowFreeGroup) {
        return this.isAuth(user, pageDto.getCode(), allowFreeGroup);
    }
    
    @Override
    public boolean isAuth(UserDetails user, String pageCode) {
        return this.isAuth(user, pageCode, true);
    }

    /**
     * Checks if the owner group of a page is contained in the list of user's groups.
     * @return true if the user has write permissions on a page, false otherwise
     */
    public boolean isAuthOnGroup(UserDetails user, String pageCode) {
        IPage page = getPage(pageCode);
        return this.getAuthorizationManager().isAuthOnGroup(user, page.getGroup());
    }

    /**
     * Checks if a user can see a page according to its owner group or join groups.
     * @param allowFreeGroup flag used to return always true for free access pages
     * @return true if the user has read permissions on a page, false otherwise
     */
    @Override
    public boolean isAuth(UserDetails user, String pageCode, boolean allowFreeGroup) {
        IPage page = getPage(pageCode);
        if (page.getCode().equals(page.getParentCode())) { // root
            return true;
        }
        return this.isAuth(user, getPage(pageCode), allowFreeGroup);
    }

    private IPage getPage(String pageCode) {
        IPage page = this.getPageManager().getDraftPage(pageCode);
        if (page == null) {
            throw new ResourceNotFoundException(ERRCODE_PAGE_NOT_FOUND, "page", pageCode);
        }
        return page;
    }

    public boolean isAuth(UserDetails user, IPage page) {
        return isAuth(user, page, true);
    }

    public boolean isAuth(UserDetails user, IPage page, boolean allowFreeGroup) {
        return this.getAuthorizationManager().isAuth(user, page, allowFreeGroup);
    }
    
    @Override
    public List<PageDto> filterList(UserDetails user, List<PageDto> toBeFiltered) {
        List<PageDto> res = new ArrayList<>();
        Optional.ofNullable(toBeFiltered).ifPresent(elements -> res.addAll(elements.stream()
                .filter(elem -> this.isAuth(user, elem, false)).collect(Collectors.toList())));
        return res;
    }
    
    public List<String> getAllowedGroupCodes(UserDetails user) {
        List<String> allowedGroups = new ArrayList<>();
        List<Group> userGroups = this.getAuthorizationManager().getUserGroups(user);
        userGroups.forEach(group -> allowedGroups.add(group.getName()));
        return allowedGroups;
    }
    
}

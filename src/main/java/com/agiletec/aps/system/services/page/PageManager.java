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

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.tree.ITreeNode;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.GroupUtilizer;
import com.agiletec.aps.system.services.lang.events.LangsChangedEvent;
import com.agiletec.aps.system.services.lang.events.LangsChangedObserver;
import com.agiletec.aps.system.services.page.cache.IPageManagerCacheWrapper;
import com.agiletec.aps.system.services.page.events.PageChangedEvent;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import com.agiletec.aps.system.services.pagemodel.PageModelUtilizer;
import com.agiletec.aps.system.services.pagemodel.events.PageModelChangedEvent;
import com.agiletec.aps.system.services.pagemodel.events.PageModelChangedObserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import com.agiletec.aps.system.common.AbstractParameterizableService;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * This is the page manager service class. Pages are held in a tree-like
 * structure, to allow a hierarchical access, and stored in a map, to allow a
 * key-value type access. In the tree, the father points the son and vice versa;
 * the order between the pages in the same level is always kept.
 *
 * @author M.Diana - E.Santoboni
 */
public class PageManager extends AbstractParameterizableService implements IPageManager, GroupUtilizer, LangsChangedObserver, PageModelUtilizer, PageModelChangedObserver {

    private static final EntLogger _logger = EntLogFactory.getSanitizedLogger(PageManager.class);
    public static final String ERRMSG_ERROR_WHILE_MOVING_A_PAGE = "Error while moving a page";

    @Autowired
    @Qualifier(value = "PageManagerParameterNames")
    public transient List<String> parameterNames;

    private transient IPageManagerCacheWrapper cacheWrapper;
    private transient IPageDAO pageDao;

    @Override
    public void init() throws Exception {
        this.initCache();
        _logger.debug("{} ready. : Initialized", this.getClass().getName());
    }

    private void initCache() throws EntException {
        this.getCacheWrapper().initCache(this.getPageDAO());
    }
    
    @Override
    protected void release() {
        this.getCacheWrapper().release();
        super.release();
    }

    @Override
    public void updateFromLangsChanged(LangsChangedEvent event) {
        try {
            this.init();
        } catch (Throwable t) {
            _logger.error("Error on init method", t);
        }
    }

    /**
     * Delete a page and eventually the association with the widgets.
     *
     * @param pageCode the code of the page to delete
     * @throws EntException In case of database access error.
     */
    @Override
    public synchronized void deletePage(String pageCode) throws EntException {
        IPage page = this.getDraftPage(pageCode);
        if (null != page && page.getChildrenCodes().length <= 0) {
            try {
                this.getPageDAO().deletePage(page);
                this.getCacheWrapper().deleteDraftPage(pageCode);
            } catch (Throwable t) {
                _logger.error("Error detected while deleting page {}", pageCode, t);
                throw new EntException("Error detected while deleting a page", t);
            }
        }
        this.notifyPageChangedEvent(page, PageChangedEvent.REMOVE_OPERATION_CODE, null);
    }

    /**
     * Add a new page to the database.
     *
     * @param page The page to add
     * @throws EntException In case of database access error.
     */
    @Override
    public synchronized void addPage(IPage page) throws EntException {
        try {
            IPage parent = this.getDraftPage(page.getParentCode());
            if (null == parent) {
                _logger.error("Add page {} - Invalid parent {}", page.getCode(), page.getParentCode());
                return;
            }
            String[] childrenCodes = parent.getChildrenCodes();
            int lastPosition = (null != childrenCodes) ? childrenCodes.length : 0;
            if (null != childrenCodes && childrenCodes.length > 0) {
                IPage lastParentChild = this.getDraftPage(childrenCodes[childrenCodes.length-1]);
                if (null == lastParentChild) {
                    _logger.error("Parent page {} - Invalid last child {}", page.getParentCode(), childrenCodes[childrenCodes.length-1]);
                } else {
                    lastPosition = (lastParentChild.getPosition() > lastPosition) ? lastParentChild.getPosition() : lastPosition;
                }
            }
            page.setPosition(lastPosition+1);
            this.getPageDAO().addPage(page);
            this.getCacheWrapper().addDraftPage(page);
        } catch (Throwable t) {
            _logger.error("Error adding a page", t);
            throw new EntException("Error adding a page", t);
        }
        this.notifyPageChangedEvent(this.getDraftPage(page.getCode()), PageChangedEvent.INSERT_OPERATION_CODE, null);
    }

    /**
     * Update a page record in the database.
     *
     * @param page The modified page.
     * @throws EntException In case of database access error.
     */
    @Override
    public synchronized void updatePage(IPage page) throws EntException {
        try {
            this.getPageDAO().updatePage(page);
            this.getCacheWrapper().updateDraftPage(page);
        } catch (Throwable t) {
            _logger.error("Error updating a page", t);
            throw new EntException("Error updating a page", t);
        }
        this.notifyPageChangedEvent(page, PageChangedEvent.UPDATE_OPERATION_CODE, null);
    }

    @Override
    public synchronized void setPageOnline(String pageCode) throws EntException {
        try {
            this.getPageDAO().setPageOnline(pageCode);
            this.getCacheWrapper().setPageOnline(pageCode);
        } catch (Throwable t) {
            _logger.error("Error updating a page as online", t);
            throw new EntException("Error updating a page as online", t);
        }
        this.notifyPageChangedEvent(this.getDraftPage(pageCode), PageChangedEvent.UPDATE_OPERATION_CODE, null, PageChangedEvent.EVENT_TYPE_SET_PAGE_ONLINE);
    }

    @Override
    public synchronized void setPageOffline(String pageCode) throws EntException {
        try {
            this.getPageDAO().setPageOffline(pageCode);
            this.getCacheWrapper().setPageOffline(pageCode);
        } catch (Throwable t) {
            _logger.error("Error updating a page as offline", t);
            throw new EntException("Error updating a page as offline", t);
        }
        this.notifyPageChangedEvent(this.getDraftPage(pageCode), PageChangedEvent.UPDATE_OPERATION_CODE, null, null, PageChangedEvent.EVENT_TYPE_SET_PAGE_OFFLINE);
    }

    private void notifyPageChangedEvent(IPage page, int operationCode, Integer framePos) {
        this.notifyPageChangedEvent(page, operationCode, framePos, null, null);
    }

    private void notifyPageChangedEvent(IPage page, int operationCode, Integer framesPos, Integer destFramePos, String eventType) {
        PageChangedEvent event = buildEvent(page, operationCode, framesPos);
        Map<String, String> properties = new HashMap<>();
        Optional.ofNullable(page).ifPresent(p -> properties.put("pageCode", p.getCode()));
        properties.put("operationCode", String.valueOf(operationCode));
        Optional.ofNullable(framesPos).ifPresent(p -> properties.put("framesPos", String.valueOf(p)));
        Optional.ofNullable(destFramePos).ifPresent(p -> {
            properties.put("destFramePos", String.valueOf(p));
            event.setDestFrame(p);
        });
        Optional.ofNullable(eventType).ifPresent(p -> {
            properties.put("eventType", p);
            event.setEventType(p);
        });
        event.setMessage(properties);
        event.setChannel(SystemConstants.PAGE_EVENT_CHANNEL);
        this.notifyEvent(event);
    }

    private void notifyPageChangedEvent(IPage page, int operationCode, Integer framePos, String eventType) {
        this.notifyPageChangedEvent(page, operationCode, framePos, null, eventType);
    }

    private PageChangedEvent buildEvent(IPage page, int operationCode, Integer framePos) {
        PageChangedEvent event = new PageChangedEvent();
        event.setPage(page);
        event.setOperationCode(operationCode);
        if (null != framePos) {
            event.setFramePosition(framePos);
        }
        return event;
    }

    /**
     * Move a page.
     *
     * @param pageCode The code of the page to move.
     * @param moveUp When true the page is moved to a higher level of the tree,
     * otherwise to a lower level.
     * @return The result of the operation: false if the move request could not
     * be satisfied, true otherwise.
     * @throws EntException In case of database access error.
     */
    @Override
    public synchronized boolean movePage(String pageCode, boolean moveUp) throws EntException {
        boolean resultOperation = true;
        try {
            IPage currentPage = this.getDraftPage(pageCode);
            if (null == currentPage) {
                throw mkException(pageCode);
            }
            IPage parent = this.getDraftPage(currentPage.getParentCode());
            String[] sisterPageCodes = parent.getChildrenCodes();
            for (int i = 0; i < sisterPageCodes.length; i++) {
                String sisterPageCode = sisterPageCodes[i];
                if (sisterPageCode.equals(pageCode)) {
                    if (!verifyRequiredMovement(i, moveUp, sisterPageCodes.length)) {
                        return false;
                    } else if (moveUp) {
                        String pageDown = sisterPageCodes[i - 1];
                        this.moveUpDown(pageDown, currentPage.getCode());
                    } else {
                        String pageUp = sisterPageCodes[i + 1];
                        this.moveUpDown(currentPage.getCode(), pageUp);
                    }
                }
            }
        } catch (Throwable t) {
            _logger.error(ERRMSG_ERROR_WHILE_MOVING_A_PAGE + " {}", pageCode, t);
            throw new EntException(ERRMSG_ERROR_WHILE_MOVING_A_PAGE, t);
        }
        return resultOperation;
    }

    private EntException mkException(String pageCode) {
        return new EntException("The page '" + pageCode + "' does not exist!");
    }

    /**
     * Perform the movement of a page
     *
     * @param pageDown
     * @param pageUp
     * @throws EntException In case of database access error.
     */
    private void moveUpDown(String pageDown, String pageUp) throws EntException {
        try {
            IPage draftToMoveDown = this.getDraftPage(pageDown);
            IPage draftToMoveUp = this.getDraftPage(pageUp);
            if (null != draftToMoveDown && null != draftToMoveUp 
                    && draftToMoveDown.getParentCode().equals(draftToMoveUp.getParentCode())) {
                this.getCacheWrapper().moveUpDown(pageDown, pageUp);
                this.getPageDAO().updatePosition(pageDown, pageUp);
            } else {
                _logger.error("Movement impossible - page to move up {} - page to move down {}", pageUp, pageDown);
            }
        } catch (Throwable t) {
            _logger.error(ERRMSG_ERROR_WHILE_MOVING_A_PAGE, t);
            throw new EntException(ERRMSG_ERROR_WHILE_MOVING_A_PAGE, t);
        }
    }

    @Override
    public synchronized boolean moveWidget(String pageCode, Integer frameToMove, Integer destFrame) throws EntException {
        boolean resultOperation = true;
        try {
            IPage currentPage = this.getDraftPage(pageCode);
            if (null == currentPage) {
                throw new EntException("The page '" + pageCode + "' does not exist!");
            }
            Widget[] widgets = currentPage.getWidgets();
            Widget currentWidget = widgets[frameToMove];
            if (null == currentWidget) {
                throw new EntException("No widget found in frame '" + frameToMove + "' and page '" + pageCode + "'");
            }
            boolean movementEnabled = isMovementEnabled(frameToMove, destFrame, widgets.length);
            if (!movementEnabled) {
                return false;
            } else {
                Widget currentDest = widgets[destFrame];
                widgets[frameToMove] = currentDest;
                widgets[destFrame] = currentWidget;
                currentPage.setWidgets(widgets);
                this.getCacheWrapper().updateDraftPage(currentPage);
                this.getPageDAO().updateWidgetPosition(pageCode, frameToMove, destFrame);
            }
            this.notifyPageChangedEvent(currentPage, PageChangedEvent.EDIT_FRAME_OPERATION_CODE, frameToMove, destFrame, PageChangedEvent.EVENT_TYPE_MOVE_WIDGET);
        } catch (Throwable t) {
            _logger.error("Error while moving widget. page {} from position {} to position {}", pageCode, frameToMove, destFrame, t);
            throw new EntException("Error while moving a widget", t);
        }
        return resultOperation;
    }

    private boolean isMovementEnabled(Integer frameToMove, Integer destFrame, int dimension) {
        boolean isEnabled = true;
        if (frameToMove.intValue() == destFrame.intValue()) {
            return false;
        }
        if (frameToMove > dimension) {
            return false;
        }
        if (frameToMove < 0) {
            return false;
        }
        if (destFrame > dimension - 1) {
            return false;
        }
        if (destFrame < 0) {
            return false;
        }
        return isEnabled;
    }

    /**
     * Verify the possibility of the page to be moved elsewhere.
     *
     * @param position The position of the page to move
     * @param moveUp When true the page is moved to a higher level of the tree,
     * otherwise to a lower level.
     * @param dimension The number the number of the pages of the parent of the
     * page to move.
     * @return if true then the requested movement is possible (but not
     * performed) false otherwise.
     */
    private boolean verifyRequiredMovement(int position, boolean moveUp, int dimension) {
        boolean result = true;
        if (moveUp) {
            if (position == 0) {
                result = false;
            }
        } else if (position == (dimension - 1)) {
            result = false;
        }
        return result;
    }

    /**
     * Remove a widgets from the given page.
     *
     * @param pageCode the code of the page
     * @param pos The position in the page to free
     * @throws EntException In case of error
     * @deprecated Use {@link #removeWidget(String,int)} instead
     */
    @Override
    @Deprecated
    public synchronized void removeShowlet(String pageCode, int pos) throws EntException {
        this.removeWidget(pageCode, pos);
    }

    /**
     * Remove a widget from the given page.
     *
     * @param pageCode the code of the page
     * @param pos The position in the page to free
     * @throws EntException In case of error
     */
    @Override
    public synchronized void removeWidget(String pageCode, int pos) throws EntException {
        this.checkPagePos(pageCode, pos);
        try {
            IPage currentPage = this.getDraftPage(pageCode);
            this.getPageDAO().removeWidget(currentPage, pos);
            currentPage.getWidgets()[pos] = null;
            if (currentPage.isOnline()) {
                boolean widgetEquals = Arrays.deepEquals(currentPage.getWidgets(), this.getOnlinePage(pageCode).getWidgets());
                ((Page) currentPage).setChanged(!widgetEquals);
            }
            this.getCacheWrapper().updateDraftPage(currentPage);
            this.notifyPageChangedEvent(currentPage, PageChangedEvent.EDIT_FRAME_OPERATION_CODE, pos, PageChangedEvent.EVENT_TYPE_REMOVE_WIDGET);
        } catch (Throwable t) {
            String message = "Error removing the widget from the page '" + pageCode + "' in the frame " + pos;
            _logger.error("Error removing the widget from the page '{}' in the frame {}", pageCode, pos, t);
            throw new EntException(message, t);
        }
    }

    /**
     * @param pageCode
     * @param widget
     * @param pos
     * @throws EntException In case of error.
     * @deprecated Use {@link #joinWidget(String,Widget,int)} instead
     */
    @Override
    @Deprecated
    public synchronized void joinShowlet(String pageCode, Widget widget, int pos) throws EntException {
        this.joinWidget(pageCode, widget, pos);
    }

    /**
     * Set the widget -including its configuration- in the given page in the
     * desired position. If the position is already occupied by another widget
     * this will be substituted with the new one.
     *
     * @param pageCode the code of the page where to set the widget
     * @param widget The widget to set
     * @param pos The position where to place the widget in
     * @throws EntException In case of error.
     */
    @Override
    public synchronized void joinWidget(String pageCode, Widget widget, int pos) throws EntException {
        this.checkPagePos(pageCode, pos);
        if (null == widget || null == widget.getType()) {
            throw new EntException("Invalid null value found in either the Widget or the widgetType");
        }
        try {
            IPage currentPage = this.getDraftPage(pageCode);
            this.getPageDAO().joinWidget(currentPage, widget, pos);
            currentPage.getWidgets()[pos] = widget;
            if (currentPage.isOnline()) {
                boolean widgetEquals = Arrays
                        .deepEquals(currentPage.getWidgets(), this.getOnlinePage(pageCode).getWidgets());
                ((Page) currentPage).setChanged(!widgetEquals);
            }
            this.getCacheWrapper().updateDraftPage(currentPage);
            this.notifyPageChangedEvent(currentPage, PageChangedEvent.EDIT_FRAME_OPERATION_CODE, pos,
                    PageChangedEvent.EVENT_TYPE_JOIN_WIDGET);
        } catch (Throwable t) {
            String message = "Error during the assignation of a widget to the frame " + pos + " in the page code " + pageCode;
            _logger.error("Error during the assignation of a widget to the frame {} in the page code {}", pos, pageCode, t);
            throw new EntException(message, t);
        }
    }

    /**
     * Utility method which perform checks on the parameters submitted when
     * editing the page.
     *
     * @param pageCode The code of the page
     * @param pos The given position
     * @throws EntException In case of database access error.
     */
    private void checkPagePos(String pageCode, int pos) throws EntException {
        IPage currentPage = this.getDraftPage(pageCode);
        if (null == currentPage) {
            throw new EntException("The page '" + pageCode + "' does not exist!");
        }
        PageModel model = currentPage.getMetadata().getModel();
        if (pos < 0 || pos >= model.getFrames().length) {
            throw new EntException("The Position '" + pos + "' is not defined in the model '" + model.getDescription() + "' of the page '" + pageCode + "'!");
        }
    }

    /**
     * Return the root of the pages tree.
     *
     * @return the root page
     */
    @Deprecated
    @Override
    public IPage getRoot() {
        throw new UnsupportedOperationException("METODO NON SUPPORTATO: public IPage getRoot()");
    }

    @Override
    public IPage getOnlineRoot() {
        return this.getCacheWrapper().getOnlineRoot();
    }

    @Override
    public IPage getDraftRoot() {
        return this.getCacheWrapper().getDraftRoot();
    }

    @Override
    public IPage getOnlinePage(String pageCode) {
        return this.getCacheWrapper().getOnlinePage(pageCode);
    }

    @Override
    public IPage getDraftPage(String pageCode) {
        return this.getCacheWrapper().getDraftPage(pageCode);
    }

    @Override
    public List<IPage> searchOnlinePages(String pageCodeToken, String title, List<String> allowedGroups) throws EntException {
        try {
            return this.searchPages(pageCodeToken, title, allowedGroups,
                    () -> this.getOnlineRoot(), code -> this.getOnlinePage(code));
        } catch (Throwable t) {
            String message = "Error during searching pages online with token " + pageCodeToken;
            _logger.error("Error during searching online pages with token {}", pageCodeToken, t);
            throw new EntException(message, t);
        }
    }

    @Override
    public List<IPage> searchPages(String pageCodeToken, String title, List<String> allowedGroups) throws EntException {
        try {
            return this.searchPages(pageCodeToken, title, allowedGroups,
                    () -> this.getDraftRoot(), code -> this.getDraftPage(code));
        } catch (Throwable t) {
            String message = "Error during searching pages with token " + pageCodeToken;
            _logger.error("Error during searching pages with token {}", pageCodeToken, t);
            throw new EntException(message, t);
        }
    }

    private List<IPage> searchPages(String pageCodeToken, String title, List<String> allowedGroups,
            Supplier<IPage> rootSupplier, Function<String, IPage> childProvider) throws EntException {
        List<IPage> searchResult = new ArrayList<>();
        try {
            if (null == allowedGroups || allowedGroups.isEmpty()) {
                return searchResult;
            }
            IPage root = rootSupplier.get();
            this.searchPages(root, pageCodeToken, title, allowedGroups, searchResult, childProvider);
        } catch (Throwable t) {
            String message = "Error during searching pages with token " + pageCodeToken;
            _logger.error("Error during searching pages with token {}", pageCodeToken, t);
            throw new EntException(message, t);
        }
        return searchResult;
    }

    private void searchPages(IPage currentTarget, String pageCodeToken, String title, List<String> allowedGroups,
            List<IPage> searchResult, Function<String, IPage> childProvider) {
        if ((allowedGroup(allowedGroups, currentTarget) || currentTarget.isRoot()) &&
                (noFilter(pageCodeToken, title) ||
                filterByCode(pageCodeToken, currentTarget) ||
                filterByTitle(title, currentTarget))) {
            searchResult.add(currentTarget);
        }
        String[] childrenCodes = currentTarget.getChildrenCodes();
        for (int i = 0; i < childrenCodes.length; i++) {
            IPage child = childProvider.apply(childrenCodes[i]);
            this.searchPages(child, pageCodeToken, title, allowedGroups, searchResult, childProvider);
        }
    }

    private boolean allowedGroup(List<String> allowedGroups, IPage currentTarget) {
        return allowedGroups.contains(currentTarget.getGroup()) || allowedGroups.contains(Group.ADMINS_GROUP_NAME);
    }

    private boolean noFilter(String pageCodeToken, String title) {
        return pageCodeToken == null && title == null;
    }

    private boolean filterByCode(String pageCodeToken, IPage currentTarget) {
        return pageCodeToken != null && currentTarget.getCode().toLowerCase().contains(pageCodeToken.toLowerCase());
    }

    private boolean filterByTitle(String title, IPage currentTarget) {
        if (title != null) {
            for (Entry<Object, Object> entry : currentTarget.getTitles().entrySet()) {
                if (entry.getValue() instanceof String &&
                        ((String) entry.getValue()).toLowerCase().contains(title.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ITreeNode getNode(String code) {
        return this.getOnlinePage(code);
    }

    @Override
    public List<IPage> getGroupUtilizers(String groupName) throws EntException {
        List<IPage> pageUtilizers = new ArrayList<>();
        try {
            IPage root = this.getDraftRoot();
            this.searchUtilizers(groupName, pageUtilizers, root, true);
            List<IPage> utilizersOnline = new ArrayList<>();
            root = this.getOnlineRoot();
            this.searchUtilizers(groupName, utilizersOnline, root, false);
            for (IPage page : utilizersOnline) {
                if (!pageUtilizers.contains(page)) {
                    pageUtilizers.add(page);
                }
            }
        } catch (Throwable t) {
            String message = "Error during searching page utilizers of group " + groupName;
            _logger.error("Error during searching page utilizers of group {}", groupName, t);
            throw new EntException(message, t);
        }

        return pageUtilizers;
    }

    private void searchUtilizers(String groupName, List<IPage> utilizers, IPage page, boolean draft) {
        if (page.getGroup().equals(groupName) && !page.isOnlineInstance()) {
            utilizers.add(page);
        } else {
            Collection<String> extraGroups = page.getMetadata().getExtraGroups();
            boolean inUse = extraGroups != null && extraGroups.contains(groupName);
            if (inUse) {
                utilizers.add(page);
            }
        }
        String[] childrenCodes = page.getChildrenCodes();
        for (int i = 0; i < childrenCodes.length; i++) {
            IPage child = (draft) ? this.getDraftPage(childrenCodes[i]) : this.getOnlinePage(childrenCodes[i]);
            if (null != child) {
                this.searchUtilizers(groupName, utilizers, child, draft);
            }
        }
    }

    @Override
    public List<String> getOnlineWidgetUtilizerCodes(String widgetTypeCode) throws EntException {
        return this.getCacheWrapper().getOnlineWidgetUtilizers(widgetTypeCode);
    }

    @Override
    public List<IPage> getOnlineWidgetUtilizers(String widgetTypeCode) throws EntException {
        List<String> codes = this.getOnlineWidgetUtilizerCodes(widgetTypeCode);
        return codes.stream().map(code -> this.getOnlinePage(code)).collect(Collectors.toList());
    }

    @Override
    public List<String> getDraftWidgetUtilizerCodes(String widgetTypeCode) throws EntException {
        return this.getCacheWrapper().getDraftWidgetUtilizers(widgetTypeCode);
    }

    @Override
    public List<IPage> getDraftWidgetUtilizers(String widgetTypeCode) throws EntException {
        List<String> codes = this.getDraftWidgetUtilizerCodes(widgetTypeCode);
        return codes.stream().map(code -> this.getDraftPage(code)).collect(Collectors.toList());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getPageModelUtilizers(String pageModelCode) throws EntException {
        List<IPage> pages = new ArrayList<>();
        try {
            if (null == pageModelCode) {
                return pages;
            }
            IPage root = this.getDraftRoot();
            this.getPageModelUtilizers(root, pageModelCode, pages, true);
            root = this.getOnlineRoot();
            this.getPageModelUtilizers(root, pageModelCode, pages, false);
        } catch (Throwable t) {
            String message = "Error during searching page utilizers of page template with code " + pageModelCode;
            _logger.error("Error during searching page utilizers of page template with code {}", pageModelCode, t);
            throw new EntException(message, t);
        }
        return pages;
    }

    private void getPageModelUtilizers(IPage page, String pageModelCode, List<IPage> pageModelUtilizers, boolean draft) {
        PageMetadata pageMetadata = page.getMetadata();
        boolean usingModel = pageMetadata != null && pageMetadata.getModel() != null && pageModelCode.equals(pageMetadata.getModel().getCode());
        if (!usingModel) {
            pageMetadata = page.getMetadata();
            usingModel = pageMetadata != null && pageMetadata.getModel() != null && pageModelCode.equals(pageMetadata.getModel().getCode());
        }
        if (usingModel) {
            pageModelUtilizers.add(page);
        }
        String[] childrenCodes = page.getChildrenCodes();
        for (int i = 0; i < childrenCodes.length; i++) {
            IPage child = (draft) ? this.getDraftPage(childrenCodes[i]) : this.getOnlinePage(childrenCodes[i]);
            if (null != child) {
                this.getPageModelUtilizers(child, pageModelCode, pageModelUtilizers, draft);
            }
        }
    }

    @Override
    public synchronized void updateFromPageModelChanged(PageModelChangedEvent event) {
        try {
            if (event.getOperationCode() != PageModelChangedEvent.UPDATE_OPERATION_CODE) {
                return;
            }
            PageModel model = event.getPageModel();
            String pageModelCode = (null != model) ? model.getCode() : null;
            if (null != pageModelCode) {
                List<?> utilizers = this.getPageModelUtilizers(pageModelCode);
                if (null != utilizers && utilizers.size() > 0) {
                    this.init();
                }
            }
        } catch (Throwable t) {
            _logger.error("Error during refres pages", t);
        }
    }
    
    @Override
	public synchronized boolean movePage(IPage currentPage, IPage newParent) throws EntException {
        return this.movePage(currentPage.getCode(), newParent.getCode());
    }

	@Override
	public synchronized boolean movePage(String pageCode, String newParentCode) throws EntException {
        IPage pageToMove = this.getDraftPage(pageCode);
        IPage newParent = this.getDraftPage(newParentCode);
		boolean resultOperation = false;
        if (null == pageToMove || null == newParent) {
            _logger.error("Page to move '{}' or new parent '{}' is null", pageCode, newParentCode);
            return resultOperation;
        }
        if (pageCode.equals(newParentCode)) {
            _logger.error("Page to move '{}' and new parent '{}' are the same", pageCode, newParentCode);
            return resultOperation;
        }
        if (newParent.isChildOf(pageCode, this)) {
            _logger.error("Page to move '{}' is parent of the new parent '{}'", pageCode, newParentCode);
            return resultOperation;
        }
        _logger.debug("start move page '{}' under '{}'", pageToMove, newParent);
        try {
            this.getPageDAO().movePage(pageToMove, newParent);
            this.getCacheWrapper().movePage(pageCode, newParentCode);
            resultOperation = true;
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "movePage");
            throw new EntException("Error while moving a page under a root node", t);
        }
        return resultOperation;
    }

    @Override
    public List<IPage> loadLastUpdatedPages(int size) throws EntException {
        List<IPage> pages = new ArrayList<>();
        try {
            List<String> paceCodes = this.getPageDAO().loadLastUpdatedPages(size);
            if (null == paceCodes || paceCodes.isEmpty()) {
                return pages;
            }
            for (String pageCode : paceCodes) {
                IPage page = this.getDraftPage(pageCode);
                pages.add(page);
            }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "loadLastUpdatedPages");
            throw new EntException("Error loading loadLastUpdatedPages", t);
        }
        return pages;
    }

    @Override
    public PagesStatus getPagesStatus() {
        PagesStatus status = this.getCacheWrapper().getPagesStatus();
        PagesStatus clone = new PagesStatus();
        clone.setLastUpdate(status.getLastUpdate());
        clone.setOnline(status.getOnline());
        clone.setOnlineWithChanges(status.getOnlineWithChanges());
        clone.setUnpublished(status.getUnpublished());
        return clone;
    }

    @Override
    protected List<String> getParameterNames() {
        return parameterNames;
    }

    protected IPageManagerCacheWrapper getCacheWrapper() {
        return cacheWrapper;
    }
    public void setCacheWrapper(IPageManagerCacheWrapper cacheWrapper) {
        this.cacheWrapper = cacheWrapper;
    }

    protected IPageDAO getPageDAO() {
        return pageDao;
    }
    public void setPageDAO(IPageDAO pageDao) {
        this.pageDao = pageDao;
    }

}

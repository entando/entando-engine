/*
 * Copyright 2019-Present Entando Inc. (http://www.entando.com) All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupManager;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.Page;
import com.agiletec.aps.system.services.page.PageUtilizer;
import com.agiletec.aps.system.services.pagemodel.IPageModelManager;
import com.agiletec.aps.system.services.pagemodel.PageModel;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.tuple.Pair;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.services.IDtoBuilder;
import org.entando.entando.aps.system.services.assertionhelper.PageAssertionHelper;
import org.entando.entando.aps.system.services.mockhelper.PageMockHelper;
import org.entando.entando.aps.system.services.page.model.PageDto;
import org.entando.entando.aps.system.services.page.model.PageSearchDto;
import org.entando.entando.web.common.assembler.PageSearchMapper;
import org.entando.entando.web.common.assembler.PagedMetadataMapper;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.component.ComponentUsageEntity;
import org.entando.entando.web.page.model.PageRequest;
import org.entando.entando.web.page.model.PageSearchRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@ExtendWith(MockitoExtension.class)
class PageServiceTest {
    public static final String ADMIN_GROUP_NAME = "administrators";
    public static final String FREE_GROUP_NAME = "free";

    @Mock
    private IPageManager pageManager;
    @Mock
    private IPageModelManager pageModelManager;
    @Mock
    private IGroupManager groupManager;
    @Mock
    private IDtoBuilder<IPage, PageDto> dtoBuilder;
    @Mock
    private IPageTokenManager pageTokenManager;
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private PageUtilizer pageUtilizer;
    @Mock
    private PageSearchMapper pageSearchMapper;
    @Mock
    private PagedMetadataMapper pagedMetadataMapper;

    @InjectMocks
    private PageService pageService;

    @BeforeEach
    public void setUp() {
        Mockito.lenient().when(groupManager.getGroup("free")).thenReturn(new Group());
        Mockito.lenient().when(groupManager.getGroup("admin")).thenReturn(new Group());
    }

    @Test
    void shouldAddExtraGroup() {
        PageDto dto = new PageDto();
        dto.addJoinGroup("free");
        dto.addJoinGroup("admin");
        when(dtoBuilder.convert(any(IPage.class))).thenReturn(dto);

//        PageModel pageModel = PageMockHelper.mockServicePageModel();

        Page page = PageMockHelper.mockTestPage(PageMockHelper.PAGE_CODE);
        page.setExtraGroups(new HashSet<>(Arrays.asList(PageMockHelper.GROUP)));
        when(pageManager.getDraftPage(page.getCode())).thenReturn(page);
        when(pageModelManager.getPageModel(page.getModel().getCode())).thenReturn(page.getModel());

        PageRequest request = PageMockHelper.mockPageRequest(page);
        request.setJoinGroups(Arrays.asList(PageMockHelper.GROUP, "admin"));
        when(pageManager.getDraftPage(request.getParentCode())).thenReturn(new Page());
        PageDto pageDto = pageService.updatePage(page.getCode(), request);

        assertThat(pageDto.getJoinGroups()).containsExactlyInAnyOrder(PageMockHelper.GROUP, "admin");
    }

    @Test
    void shouldRemoveExtraGroup() {
        PageDto dto = new PageDto();
        dto.addJoinGroup("free");
        when(dtoBuilder.convert(any(IPage.class))).thenReturn(dto);

        PageModel pageModel = PageMockHelper.mockServicePageModel();
        when(pageModelManager.getPageModel(pageModel.getCode())).thenReturn(pageModel);

        Page page = PageMockHelper.mockTestPage(PageMockHelper.PAGE_CODE);
        page.setExtraGroups(new HashSet<>(Arrays.asList("free", "admin")));
        when(pageManager.getDraftPage(page.getCode())).thenReturn(page);

        PageRequest request = PageMockHelper.mockPageRequest(page);
        request.setJoinGroups(Arrays.asList("free"));
        when(pageManager.getDraftPage(request.getParentCode())).thenReturn(new Page());
        PageDto pageDto = pageService.updatePage(page.getCode(), request);

        assertThat(pageDto.getJoinGroups()).containsExactly("free");
    }

    @Test
    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    public void shouldReturnCompatiblePages() {
        String A_GROUP_THAT_IS_NOT_PRESENT = "a-group-that-is-not-present";
        String A_GROUP_THAT_IS_PRESENT = "GROUP1";
        String ANOTHER_GROUP_THAT_IS_PRESENT = "GROUP2";

        // Mocked Page tree mock generation
        Page parentPage = mkTestPage("PP", "service", ADMIN_GROUP_NAME, null);

        List<Page> pages = Arrays.asList(
                parentPage,
                mkTestPage("FR", "PP", FREE_GROUP_NAME, null),
                mkTestPage("AD", "PP", ADMIN_GROUP_NAME, null),
                mkTestPage("G1", "PP", A_GROUP_THAT_IS_PRESENT, null),
                mkTestPage("FR#", "PP", ANOTHER_GROUP_THAT_IS_PRESENT, Arrays.asList(FREE_GROUP_NAME)),
                mkTestPage("AD#", "PP", ANOTHER_GROUP_THAT_IS_PRESENT, Arrays.asList(ADMIN_GROUP_NAME)),
                mkTestPage("G1#", "PP", ANOTHER_GROUP_THAT_IS_PRESENT, Arrays.asList(A_GROUP_THAT_IS_PRESENT))
        );

        for (Page page : pages) {
            parentPage.addChildCode(page.getCode());
            PageDto dto = new PageDto();
            dto.setCode(page.getCode());
            dto.setOwnerGroup(page.getGroup());
            Set<String> eg = page.getExtraGroups();
            if (eg  != null) {
                for (String g : eg) { dto.addJoinGroup(g); }
            }
            when(dtoBuilder.convert(page)).thenReturn(dto);
            when(pageManager.getDraftPage(page.getCode())).thenReturn(page);
        }
        when(pageManager.getDraftPage(parentPage.getCode())).thenReturn(parentPage);

        // Support lambda for running the tests

        BiPredicate<Pair<String, List<String>>, List<String>> forLinkingTo = (p, exp) -> {
            try (MockedStatic<URLEncoder> urlEncoderMockedStatic = Mockito.mockStatic(URLEncoder.class)) {
                urlEncoderMockedStatic.when(() -> URLEncoder.encode(anyString(), eq("UTF-8"))).thenReturn("token");
                List<PageDto> dtoPages = pageService
                        .getPages("PP", p.getLeft(),
                                (p.getRight() != null) ? new HashSet<>(p.getRight()) : null);
                for (int i = 0; i < dtoPages.size(); i++) {
                    String es = (i < exp.size()) ? exp.get(i) : null;
                    assertThat(dtoPages.get(i).getCode()).isEqualTo(es);
                }
                assertThat(dtoPages.size()).isEqualTo(exp.size());
            }
            return true;
        };

        // Test truth tables generation
        String[] parentOwnerGroup = new String[]{                   // Parent OWNER GROUPS
                FREE_GROUP_NAME, ADMIN_GROUP_NAME, A_GROUP_THAT_IS_PRESENT, "GROUP2"
        };
        List<List<List<String>>> map = new ArrayList<>();

        List<String> pSubCase1 = Arrays.asList(FREE_GROUP_NAME);
        List<String> pSubCase2 = Arrays.asList(ADMIN_GROUP_NAME);
        List<String> pSubCase3 = Arrays.asList(A_GROUP_THAT_IS_PRESENT);
        List<String> pSubCase4 = Arrays.asList(A_GROUP_THAT_IS_NOT_PRESENT);
        List<String> pSubCase5 = Arrays.asList(FREE_GROUP_NAME, ADMIN_GROUP_NAME);
        List<String> pSubCase6 = Arrays.asList(FREE_GROUP_NAME, ADMIN_GROUP_NAME, A_GROUP_THAT_IS_PRESENT);
        List<String> pSubCase7 = Arrays.asList();
        String O;

        // PARENT OWNER: FREE
        O = FREE_GROUP_NAME;
        forLinkingTo.test(Pair.of(O, pSubCase1), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase2), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase3), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase4), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase5), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase6), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase7), Arrays.asList("FR","FR#"));

        // PARENT OWNER: ADMINISTRATORS
        O = ADMIN_GROUP_NAME;
        forLinkingTo.test(Pair.of(O, pSubCase1), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase2), Arrays.asList("PP","FR","AD","G1","FR#","AD#","G1#"));
        forLinkingTo.test(Pair.of(O, pSubCase3), Arrays.asList("FR","G1","FR#","G1#"));
        forLinkingTo.test(Pair.of(O, pSubCase4), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase5), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase6), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase7), Arrays.asList("PP","FR","AD","G1","FR#","AD#","G1#"));

        // PARENT OWNER: GROUP1
        O = A_GROUP_THAT_IS_PRESENT;
        forLinkingTo.test(Pair.of(O, pSubCase1), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase2), Arrays.asList("FR","G1","FR#","G1#"));
        forLinkingTo.test(Pair.of(O, pSubCase3), Arrays.asList("FR","G1","FR#","G1#"));
        forLinkingTo.test(Pair.of(O, pSubCase4), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase5), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase6), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase7), Arrays.asList("FR","G1","FR#","G1#"));

        // PARENT OWNER: GROUP2
        O = A_GROUP_THAT_IS_NOT_PRESENT;
        forLinkingTo.test(Pair.of(O, pSubCase1), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase2), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase3), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase4), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase5), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase6), Arrays.asList("FR","FR#"));
        forLinkingTo.test(Pair.of(O, pSubCase7), Arrays.asList("FR","FR#"));
    }

    /**********************************************************************************
     * PAGE USAGE DETAILS
     *********************************************************************************/


    @Test
    void getPageUsageForNonExistingCodeShouldReturnZero() {

        int componentUsage = pageService.getComponentUsage("non_existing");
        assertEquals(0, componentUsage);
    }

    @Test
    void getPageUsageDetailsWithPublishedPageShouldAddItself() {

        PageDto pageDto = PageMockHelper.mockPageDto();

        this.testSinglePageUsageDetails(pageDto);
    }

    @Test
    void getPageUsageDetailsWithPaginationAndWithPublishedPageShouldAddItself() {

        PageDto pageDto = PageMockHelper.mockPageDto();

        this.testPagedPageUsageDetails(pageDto);
    }


    @Test
    void getPageUsageDetailsWithInvalidCodeShouldThrowResourceNotFoundException() {

        PageDto pageDto = PageMockHelper.mockPageDto();
        mockForSinglePage(PageMockHelper.mockTestPage(PageMockHelper.PAGE_CODE), pageDto, PageMockHelper.UTILIZERS);

        Arrays.stream(new String[]{"not existing", null, ""})
                .forEach(code -> {
                    try {
                        pageService.getComponentUsageDetails(code, new PageSearchRequest(PageMockHelper.PAGE_CODE));
                        fail("ResourceNotFoundException NOT thrown with code " + code);
                    } catch (Exception e) {
                        // assertTrue(e instanceof ResourceNotFoundException); // note can be PotentialStubbingProblem (Mockito)
                    }
                });
    }


    @Test
    void getPageUsageDetailsWithDraftPageShouldNOTAddItself() {

        PageDto pageDto = PageMockHelper.mockPageDto();
        pageDto.setStatus(IPageService.STATUS_DRAFT);

        this.testSinglePageUsageDetails(pageDto);
    }


    @Test
    void getPageUsageDetailsWithPaginationAndWithDraftPageShouldNOTAddItself() {

        PageDto pageDto = PageMockHelper.mockPageDto();
        pageDto.setStatus(IPageService.STATUS_DRAFT);

        this.testPagedPageUsageDetails(pageDto);
    }


    @Test
    void getPageUsageDetailsWithNoChildrenShouldReturnItself() {

        PageDto pageDto = PageMockHelper.mockPageDto();
        pageDto.setChildren(new ArrayList<>());

        mockForSinglePage(PageMockHelper.mockTestPage(PageMockHelper.PAGE_CODE), pageDto, new String[0]);

        PagedMetadata<ComponentUsageEntity> pageUsageDetails = pageService.getComponentUsageDetails(PageMockHelper.PAGE_CODE, new PageSearchRequest(PageMockHelper.PAGE_CODE));

        PageAssertionHelper.assertUsageDetails(pageUsageDetails, new String[0], 0, 1, pageDto.getStatus());
    }


    /**
     * contains generic code to test a single paged page usage details
     *
     * @param pageDto
     * @throws Exception
     */
    private void testSinglePageUsageDetails(PageDto pageDto) {

        Page page = PageMockHelper.mockTestPage(PageMockHelper.PAGE_CODE);

        mockForSinglePage(page, pageDto, PageMockHelper.UTILIZERS);

        PagedMetadata<ComponentUsageEntity> pageUsageDetails = pageService.getComponentUsageDetails(PageMockHelper.PAGE_CODE, new PageSearchRequest(PageMockHelper.PAGE_CODE));

        PageAssertionHelper.assertUsageDetails(pageUsageDetails, pageDto.getStatus());
    }


    /**
     * contains generic code to test a single paged page usage details
     *
     * @param pageDto
     * @throws Exception
     */
    private void testPagedPageUsageDetails(PageDto pageDto) {

        Page page = PageMockHelper.mockTestPage(PageMockHelper.PAGE_CODE);
        int pageSize = 3;
        int totalSize = PageMockHelper.UTILIZERS.length +
                (pageDto.getStatus().equals(IPageService.STATUS_ONLINE) ? 1 : 0);

        mockForSinglePage(page, pageDto, PageMockHelper.UTILIZERS);

        PageSearchRequest pageSearchRequest = new PageSearchRequest(PageMockHelper.PAGE_CODE);
        pageSearchRequest.setPageSize(pageSize);

        // creates paged data
        List<Integer> pageList = Arrays.asList(1, 2);
        String[][] utilizers = {
                {PageMockHelper.UTILIZER_1, PageMockHelper.UTILIZER_2, PageMockHelper.UTILIZER_3},
                {PageMockHelper.UTILIZER_4, PageMockHelper.UTILIZER_5}
        };

        // does assertions
        IntStream.range(0, pageList.size())
                .forEach(i -> {

                    pageSearchRequest.setPage(pageList.get(i));
                    mockPagedMetadata(page, pageDto, PageMockHelper.UTILIZERS, pageList.get(i), 2, pageSize, totalSize);

                    PagedMetadata<ComponentUsageEntity> pageUsageDetails = pageService.getComponentUsageDetails(PageMockHelper.PAGE_CODE, pageSearchRequest);

                    PageAssertionHelper.assertUsageDetails(pageUsageDetails,
                            utilizers[i],
                            PageMockHelper.UTILIZERS.length,
                            pageList.get(i),
                            pageDto.getStatus());
                });
    }


    /**
     * init mock for a single paged request
     */
    private void mockForSinglePage(Page page, PageDto pageDto, String[] utilizers) {

        mockPagedMetadata(page, pageDto, utilizers, 1, 1, 100,
                utilizers.length + (pageDto.getStatus().equals(IPageService.STATUS_ONLINE) ? 1 : 0));
    }

    /**
     * init mock for a multipaged request
     */
    private void mockPagedMetadata(Page page, PageDto pageDto, String[] utilizers, int currPage, int lastPage, int pageSize, int totalSize) {

        try {
            when(pageManager.getDraftPage(page.getCode())).thenReturn(page);
            when(pageTokenManager.encrypt(page.getCode())).thenReturn(PageMockHelper.TOKEN);
            Mockito.lenient().when(dtoBuilder.convert(any(IPage.class))).thenReturn(pageDto);
            when(applicationContext.getBeanNamesForType((Class<?>) any())).thenReturn(PageMockHelper.UTILIZERS);
            when(applicationContext.getBean(anyString())).thenReturn(pageUtilizer);
            when(pageUtilizer.getPageUtilizers(page.getCode())).thenReturn(Arrays.asList(PageMockHelper.UTILIZERS));
            when(pageUtilizer.getName())
                    .thenReturn(PageMockHelper.UTILIZER_1)
                    .thenReturn(PageMockHelper.UTILIZER_2);

            PageSearchRequest pageSearchRequest = new PageSearchRequest(PageMockHelper.PAGE_CODE);
            pageSearchRequest.setPageSize(pageSize);
            PageSearchDto pageSearchDto = new PageSearchDto(pageSearchRequest, Collections.singletonList(pageDto));
            pageSearchDto.setPageSize(pageSize);
            pageSearchDto.imposeLimits();

            List<ComponentUsageEntity> componentUsageEntityList = Arrays.stream(utilizers)
                    .map(child -> new ComponentUsageEntity(ComponentUsageEntity.TYPE_PAGE, child))
                    .collect(Collectors.toList());
            if (pageDto.getStatus().equals(IPageService.STATUS_ONLINE) && currPage == lastPage) {
                componentUsageEntityList.add(new ComponentUsageEntity(ComponentUsageEntity.TYPE_PAGE, page.getCode()));
            }

            PagedMetadata pagedMetadata = new PagedMetadata(pageSearchRequest, componentUsageEntityList, totalSize);
            pagedMetadata.setPageSize(pageSize);
            pagedMetadata.setPage(currPage);
            pagedMetadata.imposeLimits();
            when(pagedMetadataMapper.getPagedResult(any(), any())).thenReturn(pagedMetadata);

        } catch (Exception e) {
            Assertions.fail("Mock Exception");
        }
    }

    /**
     * test page creation helper (for test readability)
     */
    private Page mkTestPage(String code, String parentCode, @NonNull String group, @Nullable List<String> extraGroups) {
        Page p = new Page();
        p.setOnline(true);
        p.setCode(code);
        p.setParentCode(parentCode);
        p.setGroup(group);
        if (extraGroups != null) {
            p.setExtraGroups(new HashSet<>(extraGroups));
        }
        return p;
    }
}

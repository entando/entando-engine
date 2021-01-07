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
package org.entando.entando.web.pagemodel.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.agiletec.aps.system.services.pagemodel.FrameSketch;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.web.common.validator.AbstractPaginationValidator;
import org.entando.entando.web.pagemodel.model.PageModelFrameReq;
import org.entando.entando.web.pagemodel.model.PageModelRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class PageModelValidator extends AbstractPaginationValidator {

    public static final String ERRCODE_PAGEMODEL_NOT_FOUND = "1";
    public static final String ERRCODE_CODE_EXISTS = "2";
    public static final String ERRCODE_PAGEMODEL_REFERENCES = "3";
    public static final String ERRCODE_URINAME_MISMATCH = "4";
    public static final String ERRCODE_FRAMES_POS_MISMATCH = "5";
    public static final String ERRCODE_DEFAULT_WIDGET_NOT_EXISTS = "6";
    public static final String ERRCODE_DEFAULT_WIDGET_INVALID_PARAMETER = "7";
    public static final String ERRCODE_SKETCH_XY = "8";
    public static final String ERRCODE_OVERLAPPING_FRAMES = "9";
    public static final String ERRCODE_SKETCH_NULL = "10";

    private static final String ERR_MSG_FRAMES_POS_MISMATCH = "pageModel.frames.pos.mismatch";
    private static final String ERR_MSG_CODE_MISMATCH = "pageModel.code.mismatch";
    private static final String ERR_MSG_FRAMES_SKETCH_NULL = "pageModel.frames.error.sketchNull";
    private static final String ERR_MSG_FRAMES_SKETCH_XY = "pageModel.frames.error.sketchXYFrames";
    private static final String ERR_MSG_FRAMES_OVERLAPPING ="pageModel.frames.error.sketchOverlappingFrames";

    @Override
    public boolean supports(Class<?> paramClass) {
        return PageModelRequest.class.equals(paramClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PageModelRequest request = (PageModelRequest) target;
        this.validateConfiguration(request, errors);
    }

    private void validateConfiguration(PageModelRequest request, Errors errors) {
        List<PageModelFrameReq> conf = request.getConfiguration().getFrames();

        //frame positions should start from 0 and be progressive
        List<Integer> positions = conf
                .stream()
                .sorted((e1, e2) -> Integer.compare(e1.getPos(), e2.getPos()))
                .map(PageModelFrameReq::getPos).collect(Collectors.toList());
        int firstPosition = positions.get(0);

        if (firstPosition != 0) {
            errors.reject(ERRCODE_FRAMES_POS_MISMATCH, new String[]{}, ERR_MSG_FRAMES_POS_MISMATCH);
            return;
        }
        int lastPosition = positions.get(positions.size() - 1);
        if (lastPosition != positions.size() - 1) {
            errors.reject(ERRCODE_FRAMES_POS_MISMATCH, new String[]{}, ERR_MSG_FRAMES_POS_MISMATCH);
            return;
        }
        if (positions.size() != new HashSet<Integer>(positions).size()) {
            errors.reject(ERRCODE_FRAMES_POS_MISMATCH, new String[]{}, ERR_MSG_FRAMES_POS_MISMATCH);
            return;
        }
        validateXYSketchFrames(conf, errors);
        if (errors.hasErrors()) {
            return;
        }
        validateOverlapppingFrames(conf, errors);
    }

    public void validateBodyName(String code, PageModelRequest pageModelRequest, Errors errors) {
        if (!StringUtils.equals(code, pageModelRequest.getCode())) {
            errors.rejectValue("code", ERRCODE_URINAME_MISMATCH, new String[]{code, pageModelRequest.getCode()}, ERR_MSG_CODE_MISMATCH);
        }
    }

    private void validateXYSketchFrames(final List<PageModelFrameReq> frames, Errors errors) {
        frames.forEach(frame -> {
            final FrameSketch sketch = frame.getSketch();
            if (null == sketch) {
                errors.rejectValue("code", ERRCODE_SKETCH_NULL, new String[]{frame.getDescr(), String.valueOf(frame.getPos())},  ERR_MSG_FRAMES_SKETCH_NULL);
            } else if (sketch.getX1() < 0 ||
                    sketch.getX2() < 0 ||
                    sketch.getY1() < 0 ||
                    sketch.getY2() < 0 ||
                    sketch.getY1() > sketch.getY2() ||
                    sketch.getX1() > sketch.getX2()) {
                errors.rejectValue("code", ERRCODE_SKETCH_XY, new String[]{frame.getDescr(), String.valueOf(frame.getPos())}, ERR_MSG_FRAMES_SKETCH_XY);
            }
        });
    }

    private void validateOverlapppingFrames(final List<PageModelFrameReq> frames, Errors errors) {
        frames.forEach(frame -> {
            final FrameSketch sketch = frame.getSketch();
            final Optional<PageModelFrameReq> frameByCoordinates1 = getFrameByCoordinates(frames, sketch.getX1(), sketch.getY1());
            final Optional<PageModelFrameReq> frameByCoordinates2 = getFrameByCoordinates(frames, sketch.getX2(), sketch.getY1());
            final Optional<PageModelFrameReq> frameByCoordinates3 = getFrameByCoordinates(frames, sketch.getX1(), sketch.getY2());
            final Optional<PageModelFrameReq> frameByCoordinates4 = getFrameByCoordinates(frames, sketch.getX2(), sketch.getY2());
            checkFrameByCoordinates(errors, frameByCoordinates1, frame);
            checkFrameByCoordinates(errors, frameByCoordinates2, frame);
            checkFrameByCoordinates(errors, frameByCoordinates3, frame);
            checkFrameByCoordinates(errors, frameByCoordinates4, frame);
        });

    }

    private void checkFrameByCoordinates(Errors errors, Optional<PageModelFrameReq> frameByCoordinates1, PageModelFrameReq frame) {
        if (frameByCoordinates1.isPresent() && (frameByCoordinates1.get() != frame)) {
            final PageModelFrameReq f1 = frameByCoordinates1.get();
            errors.rejectValue("code", ERRCODE_OVERLAPPING_FRAMES, new String[]{frame.getDescr(), String.valueOf(frame.getPos()), f1.getDescr(), String.valueOf(f1.getPos())}, ERR_MSG_FRAMES_OVERLAPPING);
        }
    }

    private Optional<PageModelFrameReq> getFrameByCoordinates(final List<PageModelFrameReq> frames, final int x, final int y) {
        return frames.stream().filter(frame -> (x >= frame.getSketch().getX1()
                && y >= frame.getSketch().getY1()
                && x <= frame.getSketch().getX2()
                && y <= frame.getSketch().getY2())).findFirst();
    }
}

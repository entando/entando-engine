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
package org.entando.entando.web.guifragment.validator;

import java.util.HashMap;
import org.entando.entando.aps.system.services.guifragment.GuiFragment;
import org.entando.entando.aps.system.services.guifragment.IGuiFragmentManager;
import org.entando.entando.web.guifragment.model.GuiFragmentRequestBody;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.MapBindingResult;

/**
 * @author E.Santoboni
 */
@ExtendWith(MockitoExtension.class)
class GuiFragmentValidatorTest {

    @Mock
    private IGuiFragmentManager guiFragmentManager;

    @InjectMocks
    private GuiFragmentValidator validator;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void validateRightFragment() throws Exception {
        when(this.guiFragmentManager.getGuiFragment("not_existing")).thenReturn(null);
        GuiFragmentRequestBody request = new GuiFragmentRequestBody("not_existing", "<h1>code</h1>");
        MapBindingResult bindingResult = new MapBindingResult(new HashMap<Object, Object>(), "fragment");
        validator.validate(request, bindingResult);
        Assertions.assertFalse(bindingResult.hasErrors());
        Assertions.assertEquals(0, bindingResult.getErrorCount());
    }

    @Test
    void validateExistingFragment() throws Exception {
        GuiFragment existing = new GuiFragment();
        existing.setCode("existing");
        when(this.guiFragmentManager.getGuiFragment("existing")).thenReturn(existing);
        GuiFragmentRequestBody request = new GuiFragmentRequestBody("existing", "<h1>code</h1>");
        MapBindingResult bindingResult = new MapBindingResult(new HashMap<Object, Object>(), "fragment");
        validator.validate(request, bindingResult);
        Assertions.assertTrue(bindingResult.hasErrors());
        Assertions.assertEquals(1, bindingResult.getErrorCount());
    }

    @Test
    void validateExistingAndInvalidFragment() throws Exception {
        GuiFragment existing = new GuiFragment();
        existing.setCode("existing");
        when(this.guiFragmentManager.getGuiFragment("existing")).thenReturn(existing);
        GuiFragmentRequestBody request = new GuiFragmentRequestBody("existing", "");
        MapBindingResult bindingResult = new MapBindingResult(new HashMap<Object, Object>(), "fragment");
        validator.validate(request, bindingResult);
        Assertions.assertTrue(bindingResult.hasErrors());
        Assertions.assertEquals(2, bindingResult.getErrorCount());
    }

    @Test
    void validateInvalidFragmentCode_1() throws Exception {
        String code = "very_long";
        for (int i = 0; i < 10; i++) {
            code += code;
        }
        when(this.guiFragmentManager.getGuiFragment(code)).thenReturn(null);
        GuiFragmentRequestBody request = new GuiFragmentRequestBody(code, "<h1>prova</h1>");
        MapBindingResult bindingResult = new MapBindingResult(new HashMap<Object, Object>(), "fragment");
        validator.validate(request, bindingResult);
        Assertions.assertTrue(bindingResult.hasErrors());
        Assertions.assertEquals(1, bindingResult.getErrorCount());
    }

    @Test
    void validateInvalidFragmentCode_2() throws Exception {
        String code = "wrong_characters_&_$_123";
        when(this.guiFragmentManager.getGuiFragment(code)).thenReturn(null);
        GuiFragmentRequestBody request = new GuiFragmentRequestBody(code, "<h1>prova</h1>");
        MapBindingResult bindingResult = new MapBindingResult(new HashMap<Object, Object>(), "fragment");
        validator.validate(request, bindingResult);
        Assertions.assertTrue(bindingResult.hasErrors());
        Assertions.assertEquals(1, bindingResult.getErrorCount());
    }

}

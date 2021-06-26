/*
 * Copyright 2020-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.guifragment;

import org.entando.entando.web.guifragment.model.GuiFragmentRequestBody;

public final class FragmentTestUtil {

    private static final String CODE = "my-fragment-code";
    private static final String GUI_CODE = "<script>my_js_script</script>";

    private FragmentTestUtil() {
        // No instance - utility class
    }

    public static GuiFragmentRequestBody validFragmentRequest() {
        GuiFragmentRequestBody request = new GuiFragmentRequestBody();
        request.setCode(CODE);
        request.setGuiCode(GUI_CODE);
        return request;
    }
    
}

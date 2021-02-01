package org.entando.entando.aps.system.services.page.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.agiletec.aps.system.services.page.Page;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import org.junit.jupiter.api.Test;

class PageDtoBuilderTest {

    @Test
    void testDefaultContentTypeAndCharSet() {
        PageDtoBuilder builder = new PageDtoBuilder();

        Page page = new Page();
        page.setModel(new PageModel());
        PageDto dto = builder.convert(page);

        assertEquals(dto.getCharset(), "utf8");
        assertEquals(dto.getContentType(), "text/html");
    }

}

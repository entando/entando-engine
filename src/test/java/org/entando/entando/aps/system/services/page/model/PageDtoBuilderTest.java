package org.entando.entando.aps.system.services.page.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.agiletec.aps.system.services.page.Page;
import org.junit.jupiter.api.Test;

class PageDtoBuilderTest {

    @Test
    void testDefaultContentTypeAndCharSet() {
        PageDtoBuilder builder = new PageDtoBuilder();

        Page page = new Page();
        page.setModelCode("test");
        PageDto dto = builder.convert(page);

        assertEquals(dto.getCharset(), "utf8");
        assertEquals(dto.getContentType(), "text/html");
    }

}

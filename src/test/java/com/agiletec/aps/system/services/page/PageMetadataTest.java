package com.agiletec.aps.system.services.page;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PageMetadataTest {

    @Test
    void testEqualsOnClonedObject() {
        PageMetadata pageMetadata = new PageMetadata();
        pageMetadata.setModelCode("model1");
        PageMetadata clonedPageMetadata = pageMetadata.clone();
        Assertions.assertEquals(clonedPageMetadata, pageMetadata);
        Assertions.assertEquals(clonedPageMetadata.hashCode(), pageMetadata.hashCode());
    }

    @Test
    void testEqualsDifferentModel() {
        PageMetadata pageMetadata1 = new PageMetadata();
        pageMetadata1.setModelCode("model1");
        PageMetadata pageMetadata2 = new PageMetadata();
        pageMetadata2.setModelCode("model2");
        Assertions.assertNotEquals(pageMetadata1, pageMetadata2);
    }

    @Test
    void testEqualsDifferentModelAndFirstNull() {
        PageMetadata pageMetadata1 = new PageMetadata();
        PageMetadata pageMetadata2 = new PageMetadata();
        pageMetadata2.setModelCode("model2");
        Assertions.assertNotEquals(pageMetadata1, pageMetadata2);
    }
}

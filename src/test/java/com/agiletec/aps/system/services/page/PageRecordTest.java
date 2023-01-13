package com.agiletec.aps.system.services.page;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PageRecordTest {

    @Test
    void testIsChangedDifferentWidgetsSize() {
        PageRecord pageRecord = new PageRecord();
        pageRecord.setMetadataDraft(new PageMetadata());
        pageRecord.setMetadataOnline(new PageMetadata());
        pageRecord.setWidgetsDraft(new Widget[]{});
        pageRecord.setWidgetsOnline(new Widget[]{getWidget("widget")});
        Assertions.assertTrue(pageRecord.isChanged());
    }

    @Test
    void testIsChangedDifferentWidgetsCode() {
        PageRecord pageRecord = new PageRecord();
        pageRecord.setMetadataDraft(new PageMetadata());
        pageRecord.setMetadataOnline(new PageMetadata());
        pageRecord.setWidgetsDraft(new Widget[]{getWidget("widget1")});
        pageRecord.setWidgetsOnline(new Widget[]{getWidget("widget2")});
        Assertions.assertTrue(pageRecord.isChanged());
    }

    private Widget getWidget(String code) {
        Widget widget = new Widget();
        widget.setTypeCode(code);
        return widget;
    }
}

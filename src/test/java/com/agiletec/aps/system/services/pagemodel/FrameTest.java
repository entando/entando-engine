package com.agiletec.aps.system.services.pagemodel;

import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.system.services.pagemodel.Frame.JAXBDefaultWidget;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.aps.system.services.widgettype.WidgetTypeManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FrameTest {

    @Mock
    private WidgetTypeManager widgetTypeManager;

    @Test
    void testGetDefaultWidget() {
        WidgetType widgetType = new WidgetType();
        widgetType.setCode("my-widget");

        JAXBDefaultWidget jaxbDefaultWidget = new JAXBDefaultWidget();
        jaxbDefaultWidget.setCode(widgetType.getCode());
        Mockito.when(widgetTypeManager.getWidgetType(jaxbDefaultWidget.getCode())).thenReturn(widgetType);

        Frame frame = new Frame();
        frame.setWidgetTypeManager(widgetTypeManager);
        frame.setJaxbDefaultWidget(jaxbDefaultWidget);
        Widget widget = frame.getDefaultWidget();

        Assertions.assertEquals(widgetType.getCode(), widget.getTypeCode());
    }

    @Test
    void testEqualsSameObject() {
        Frame frame = new Frame();
        Assertions.assertEquals(frame, frame);
    }

    @Test
    void testEqualsNullObject() {
        Assertions.assertNotEquals(new Frame(), (Frame) null);
    }

    @Test
    void testEqualsClonedObject() {
        FrameSketch frameSketch = new FrameSketch();
        frameSketch.setCoords(0, 0, 1, 1);
        Frame frame = new Frame();
        frame.setSketch(frameSketch);
        frame.setDefaultWidget(new Widget());
        Frame clonedFrame = frame.clone();
        Assertions.assertEquals(clonedFrame, frame);
        Assertions.assertEquals(clonedFrame.hashCode(), frame.hashCode());
    }

    @Test
    void testEqualsDifferentPos() {
        Frame frame1 = new Frame();
        Frame frame2 = new Frame();
        frame1.setPos(0);
        frame2.setPos(1);
        Assertions.assertNotEquals(frame1, frame2);
    }

    @Test
    void testEqualsDifferentMainFrame() {
        Frame frame1 = new Frame();
        Frame frame2 = new Frame();
        frame1.setMainFrame(true);
        Assertions.assertNotEquals(frame1, frame2);
    }

    @Test
    void testEqualsDifferentDescription() {
        Frame frame1 = new Frame();
        Frame frame2 = new Frame();
        frame1.setDescription("Description 1");
        frame2.setDescription("Description 2");
        Assertions.assertNotEquals(frame1, frame2);
    }

    @Test
    void testEqualsDifferentDefaultWidget() {
        Frame frame1 = new Frame();
        Frame frame2 = new Frame();
        Widget defaultWidget1 = new Widget();
        defaultWidget1.setTypeCode("widget1");
        Widget defaultWidget2 = new Widget();
        defaultWidget2.setTypeCode("widget2");
        frame1.setDefaultWidget(defaultWidget1);
        frame2.setDefaultWidget(defaultWidget2);
        Assertions.assertNotEquals(frame1, frame2);
    }

    @Test
    void testEqualsDifferentJaxbDefaultWidget() {
        Frame frame1 = new Frame();
        Frame frame2 = new Frame();
        JAXBDefaultWidget defaultWidget1 = new JAXBDefaultWidget();
        defaultWidget1.setCode("widget1");
        JAXBDefaultWidget defaultWidget2 = new JAXBDefaultWidget();
        defaultWidget2.setCode("widget2");
        frame1.setJaxbDefaultWidget(defaultWidget1);
        frame2.setJaxbDefaultWidget(defaultWidget2);
        Assertions.assertNotEquals(frame1, frame2);
    }

    @Test
    void testEqualsDifferentSketch() {
        Frame frame1 = new Frame();
        Frame frame2 = new Frame();
        FrameSketch frameSketch1 = new FrameSketch();
        frameSketch1.setCoords(0, 0, 1, 1);
        FrameSketch frameSketch2 = new FrameSketch();
        frameSketch2.setCoords(0, 0, 2, 2);
        frame1.setSketch(frameSketch1);
        frame2.setSketch(frameSketch2);
        Assertions.assertNotEquals(frame1, frame2);
    }
}

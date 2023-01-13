package com.agiletec.aps.system.services.pagemodel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FrameSketchTest {

    @Test
    void testEqualsSameObject() {
        FrameSketch frameSketch = new FrameSketch();
        Assertions.assertEquals(frameSketch, frameSketch);
    }

    @Test
    void testEqualsNullObject() {
        Assertions.assertNotEquals(new FrameSketch(), (FrameSketch) null);
    }

    @Test
    void testEqualsDifferentX1() {
        FrameSketch frameSketch1 = new FrameSketch();
        frameSketch1.setCoords(0, 0, 1, 1);
        FrameSketch frameSketch2 = new FrameSketch();
        frameSketch2.setCoords(5, 0, 1, 1);
        Assertions.assertNotEquals(frameSketch1, frameSketch2);
    }

    @Test
    void testEqualsDifferentY1() {
        FrameSketch frameSketch1 = new FrameSketch();
        frameSketch1.setCoords(0, 0, 1, 1);
        FrameSketch frameSketch2 = new FrameSketch();
        frameSketch2.setCoords(0, 5, 1, 1);
        Assertions.assertNotEquals(frameSketch1, frameSketch2);
    }

    @Test
    void testEqualsDifferentX2() {
        FrameSketch frameSketch1 = new FrameSketch();
        frameSketch1.setCoords(0, 0, 1, 1);
        FrameSketch frameSketch2 = new FrameSketch();
        frameSketch2.setCoords(0, 0, 5, 1);
        Assertions.assertNotEquals(frameSketch1, frameSketch2);
    }

    @Test
    void testEqualsDifferentY2() {
        FrameSketch frameSketch1 = new FrameSketch();
        frameSketch1.setCoords(0, 0, 1, 1);
        FrameSketch frameSketch2 = new FrameSketch();
        frameSketch2.setCoords(0, 0, 1, 5);
        Assertions.assertNotEquals(frameSketch1, frameSketch2);
    }
}

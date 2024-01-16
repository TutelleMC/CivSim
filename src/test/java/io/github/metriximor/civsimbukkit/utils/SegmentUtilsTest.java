package io.github.metriximor.civsimbukkit.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.*;
import org.junit.jupiter.api.Test;

class SegmentUtilsTest {
    // More tests at https://www.desmos.com/calculator/sfsty752zk
    @Test
    void testIntersectIgnoresEndpoints() {
        assertFalse(SegmentUtils.intersect(new Point(0, 0), new Point(1, 0), new Point(1, 0), new Point(2, 0)));
        assertFalse(SegmentUtils.intersect(new Point(0, 0), new Point(1, 0), new Point(1, 0), new Point(1, 1)));
    }

    @Test
    void testIntersectWorksOnParallelTouching() {
        assertTrue(SegmentUtils.intersect(new Point(0, 0), new Point(10, 0), new Point(5, 0), new Point(15, 0)));
        assertTrue(SegmentUtils.intersect(new Point(0, 0), new Point(20, 0), new Point(5, 0), new Point(15, 0)));
    }

    @Test
    void testIntersectionWorksOnARegression() {
        assertTrue(SegmentUtils.intersect(new Point(0, 0), new Point(10, 0), new Point(10, 0), new Point(5, 0)));
    }

    @Test
    void testIntersectIgnoresEndpointsYaxis() {
        assertFalse(SegmentUtils.intersect(new Point(0, 0), new Point(0, 1), new Point(0, 1), new Point(0, 2)));
    }

    @Test
    void testIntersectWorksOnParallelTouchingYaxis() {
        assertTrue(SegmentUtils.intersect(new Point(0, 0), new Point(0, 10), new Point(0, 5), new Point(0, 15)));
        assertTrue(SegmentUtils.intersect(new Point(0, 0), new Point(0, 20), new Point(0, 5), new Point(0, 15)));
    }

    @Test
    void testIntersectionWorksOnARegressionYaxis() {
        assertTrue(SegmentUtils.intersect(new Point(0, 0), new Point(0, 10), new Point(0, 10), new Point(0, 5)));
    }

    @Test
    void testIntersectOverlappingDiagonal() {
        assertTrue(SegmentUtils.intersect(new Point(0, 0), new Point(10, 10), new Point(5, 5), new Point(15, 15)));
        assertFalse(SegmentUtils.intersect(new Point(0, 0), new Point(10, 10), new Point(10, 10), new Point(15, 15)));
        assertTrue(SegmentUtils.intersect(new Point(0, 0), new Point(20, 20), new Point(5, 5), new Point(15, 15)));
    }

    @Test
    void testIntersectionWorksOnARegularCase() {
        assertTrue(SegmentUtils.intersect(new Point(0, 0), new Point(5, 5), new Point(5, 0), new Point(0, 5)));
        assertTrue(SegmentUtils.intersect(new Point(0, 0), new Point(10, 0), new Point(5, -5), new Point(5, 5)));
        assertTrue(SegmentUtils.intersect(
                new Point(2020, 5921), new Point(2001, 5924), new Point(2020, 5920), new Point(2001, 5925)));
    }
}

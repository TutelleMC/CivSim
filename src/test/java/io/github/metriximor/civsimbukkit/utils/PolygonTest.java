package io.github.metriximor.civsimbukkit.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class PolygonTest {
    private static final Point ZERO_ZERO = new Point(0, 0);
    private static final Point ONE_ONE = new Point(1, 1);
    private static final Point ONE_ZERO = new Point(1, 0);
    private static final List<Point> THREE_POINTS = List.of(ZERO_ZERO, ONE_ONE, ONE_ZERO);

    @Test
    void testPointsBuilderWorks() {
        assertNull(Polygon.build(List.of(new Point())));
        assertNotNull(Polygon.build(THREE_POINTS));
    }

    @Test
    void testPointListOfXAndYWorks() {
        assertNull(Polygon.build(List.of(1), List.of()));
        assertNull(Polygon.build(List.of(1, 2, 3), List.of()));
        assertNull(Polygon.build(List.of(), List.of(1, 2, 3)));
        assertNull(Polygon.build(List.of(1, 2, 3, 4, 5), List.of(1, 2, 3)));
        assertNotNull(Polygon.build(List.of(1, 2, 3), List.of(1, 2, 3)));
    }

    @Test
    void testGetListsWork() {
        var polygon = Polygon.build(THREE_POINTS);
        assertNotNull(polygon);
        var polygonFromList = Polygon.build(polygon.getListX(), polygon.getListY());
        assertNotNull(polygonFromList);
        assertEquals(polygon.getListX(), polygonFromList.getListX());
        assertEquals(polygon.getListY(), polygonFromList.getListY());
    }

    @Test
    void testCalculateAreaWorks() {
        var polygon = Polygon.build(THREE_POINTS);
        assertNotNull(polygon);
        assertEquals(0.5, polygon.area());
        polygon.addPoint(540, 2782);
        assertEquals(1390.5, polygon.area());
        polygon.addPoint(-545, -821);
        assertEquals(537815.5, polygon.area());
    }
}

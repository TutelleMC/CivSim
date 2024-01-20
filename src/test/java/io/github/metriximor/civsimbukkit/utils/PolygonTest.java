package io.github.metriximor.civsimbukkit.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
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

    @Test
    void testGetPointsInSquareGridSimpleTriangle() {
        var polygon = Polygon.build(THREE_POINTS);
        assert polygon != null;
        var result = polygon.getPointsInSquareGrid().collect(Collectors.toSet());
        assertEquals(new HashSet<>(THREE_POINTS), result);
    }

    @Test
    void testGetPointsInSquareGridComplexShape() {
        var polygon = Polygon.build(List.of(
                ZERO_ZERO,
                new Point(10, 0),
                new Point(10, 30),
                new Point(0, 30),
                new Point(0, 20),
                new Point(5, 20),
                new Point(5, 10),
                new Point(0, 10)));
        var notPresent = Polygon.build(List.of(new Point(0, 19), new Point(4, 19), new Point(4, 11), new Point(0, 11)));
        assert notPresent != null;
        assert polygon != null;
        var notPresentBlocks = notPresent.getPointsInSquareGrid().collect(Collectors.toSet());
        var result = polygon.getPointsInSquareGrid().collect(Collectors.toSet());
        notPresentBlocks.forEach(
                notPresentBlock -> assertFalse(result.contains(notPresentBlock), notPresentBlock.toString()));
        assertFalse(result.contains(new Point(3, 15)));
        assertTrue(result.contains(new Point(10, 0)));
        assertTrue(result.contains(new Point(10, 15)));
        assertTrue(result.contains(new Point(5, 15)));
        assertTrue(result.contains(new Point(2, 20)));
        assertTrue(result.contains(new Point(2, 10)));
        assertTrue(result.contains(new Point(5, 30)));
        assertTrue(result.contains(new Point(5, 0)));
        assertEquals((11 * 31) - 45, result.size());
        assertEquals((19 - 11 + 1) * (4 + 1), notPresentBlocks.size());
    }
}

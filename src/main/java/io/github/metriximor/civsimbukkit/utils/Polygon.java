package io.github.metriximor.civsimbukkit.utils;

import java.awt.Point;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.NonNull;

public class Polygon extends java.awt.Polygon {
    private Polygon(final @NonNull List<Point> points) {
        super();
        points.forEach(point -> addPoint(point.x, point.y));
    }

    public static Polygon build(final @NonNull List<Point> points) {
        if (points.size() < 3) {
            return null;
        }
        return new Polygon(points);
    }

    public static Polygon build(final @NonNull List<Integer> xPoints, final @NonNull List<Integer> yPoints) {
        if (xPoints.size() != yPoints.size() || xPoints.size() < 3) {
            return null;
        }
        return new Polygon(IntStream.range(0, xPoints.size())
                .mapToObj(i -> new Point(xPoints.get(i), yPoints.get(i)))
                .toList());
    }

    public List<Integer> getListX() {
        return getVerticesOfPolygon().map(point -> point.x).toList();
    }

    public List<Integer> getListY() {
        return getVerticesOfPolygon().map(point -> point.y).toList();
    }

    public double area() {
        return Math.abs(IntStream.range(0, npoints)
                        .mapToDouble(
                                i -> xpoints[i] * ypoints[(i + 1) % npoints] - ypoints[i] * xpoints[(i + 1) % npoints])
                        .sum()
                / 2d);
    }

    private Stream<Point> getVerticesOfPolygon() {
        return IntStream.range(0, npoints).mapToObj(i -> new Point(xpoints[i], ypoints[i]));
    }

    public @NonNull Stream<Point> getPointsInSquareGrid() {
        final var bounds = getBounds();
        return IntStream.range((int) bounds.getMinX(), (int) bounds.getMaxX() + 1)
                .mapToObj(x -> IntStream.range((int) bounds.getMinY(), (int) bounds.getMaxY() + 1)
                        .filter(y -> containsInsideGrid(x, y))
                        .mapToObj(y -> new Point(x, y)))
                .flatMap(Function.identity());
    }

    public boolean containsInsideGrid(final int x, final int y) {
        boolean inside = false;
        int xOld = xpoints[npoints - 1];
        int yOld = ypoints[npoints - 1];

        for (int i = 0; i < npoints; ++i) {
            int xNew = xpoints[i];
            int yNew = ypoints[i];

            if (xNew == x && yNew == y) {
                return true;
            }

            int x1 = Math.min(xOld, xNew);
            int x2 = Math.max(xOld, xNew);
            int y1 = Math.min(yOld, yNew);
            int y2 = Math.max(yOld, yNew);
            if (x1 <= x && x <= x2) {
                long crossProduct =
                        ((long) y - (long) y1) * (long) (x2 - x1) - ((long) y2 - (long) y1) * (long) (x - x1);
                if (crossProduct == 0) {
                    if ((y1 <= y) == (y <= y2)) return true;
                } else if (crossProduct < 0 && (x1 != x)) {
                    inside = !inside;
                }
            }
            xOld = xNew;
            yOld = yNew;
        }

        return inside;
    }
}

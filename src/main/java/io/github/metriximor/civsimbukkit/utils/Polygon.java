package io.github.metriximor.civsimbukkit.utils;

import java.awt.Point;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.NonNull;

public class Polygon extends java.awt.Polygon {
    private Polygon(@NonNull final List<Point> points) {
        super();
        points.forEach(point -> addPoint(point.x, point.y));
    }

    public static Polygon build(@NonNull final List<Point> points) {
        if (points.size() < 3) {
            return null;
        }
        return new Polygon(points);
    }

    public static Polygon build(@NonNull final List<Integer> xPoints, @NonNull final List<Integer> yPoints) {
        if (xPoints.size() != yPoints.size() || xPoints.size() < 3) {
            return null;
        }
        return new Polygon(IntStream.range(0, xPoints.size())
                .mapToObj(i -> new Point(xPoints.get(i), yPoints.get(i)))
                .toList());
    }

    public List<Integer> getListX() {
        return getPointsOfPolygon().map(point -> point.x).toList();
    }

    public List<Integer> getListY() {
        return getPointsOfPolygon().map(point -> point.y).toList();
    }

    public double area() {
        return Math.abs(IntStream.range(0, npoints)
                        .mapToDouble(
                                i -> xpoints[i] * ypoints[(i + 1) % npoints] - ypoints[i] * xpoints[(i + 1) % npoints])
                        .sum()
                / 2d);
    }

    private Stream<Point> getPointsOfPolygon() {
        return IntStream.range(0, npoints).mapToObj(i -> new Point(xpoints[i], ypoints[i]));
    }

    //    private Stream<Pair<Point, Point>> getEdgesOfPolygon() {
    //        return IntStream.range(0, npoints - 1)
    //                .mapToObj(i -> Pair.of(new Point(xpoints[i], ypoints[i]), new Point(xpoints[i + 1], ypoints[i +
    // 1])));
    //    }
}

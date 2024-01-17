package io.github.metriximor.civsimbukkit.utils;

import static java.awt.geom.Line2D.linesIntersect;

import java.awt.*;
import java.awt.geom.Point2D;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public class SegmentUtils {
    public static boolean intersect(final Point p1, final Point p2, final Point q1, final Point q2) {
        final var linesIntersect = linesIntersect(p1.x, p1.y, p2.x, p2.y, q1.x, q1.y, q2.x, q2.y);
        if (!linesIntersect) {
            return false;
        }
        final var interceptionPoint = calculateInterceptionPoint(p1, p2, q1, q2);
        if (interceptionPoint == null) { // slope is 0
            return boundaryBoxesDontBorderAtASinglePoint(p1, p2, q1, q2);
        }
        //noinspection EqualsBetweenInconvertibleTypes they are tested and work fine
        return !interceptionPoint.equals(p1)
                && !interceptionPoint.equals(p2)
                && !interceptionPoint.equals(q1)
                && !interceptionPoint.equals(q2);
    }

    private static boolean boundaryBoxesDontBorderAtASinglePoint(
            @NonNull final Point p1, @NonNull final Point p2, @NonNull final Point q1, @NonNull final Point q2) {
        final Point bb1TopLeft = new Point(Math.min(p1.x, p2.x), Math.max(p1.y, p2.y));
        final Point bb1BottomRight = new Point(Math.max(p1.x, p2.x), Math.min(p1.y, p2.y));
        final Point bb2TopLeft = new Point(Math.min(q1.x, q2.x), Math.max(q1.y, q2.y));
        final Point bb2BottomRight = new Point(Math.max(q1.x, q2.x), Math.min(q1.y, q2.y));
        if (bb1TopLeft.x != bb1BottomRight.x || bb2TopLeft.x != bb2BottomRight.x) {
            if (bb1BottomRight.x == bb2TopLeft.x) return false; // To the left
            if (bb1TopLeft.x == bb2BottomRight.x) return false; // To the right
        }
        if (bb1TopLeft.y != bb1BottomRight.y || bb2TopLeft.y != bb2BottomRight.y) {
            if (bb1TopLeft.y == bb2BottomRight.y) return false; // Below
            //noinspection RedundantIfStatement this way makes more sense in my brain
            if (bb1BottomRight.y == bb2TopLeft.y) return false; // Above
        }
        return true;
    }

    @Nullable
    private static Point2D.Double calculateInterceptionPoint(
            @NonNull final Point s1, @NonNull final Point s2, @NonNull final Point d1, @NonNull final Point d2) {
        double a1 = s2.y - s1.y;
        double b1 = s1.x - s2.x;
        double c1 = a1 * s1.x + b1 * s1.y;

        double a2 = d2.y - d1.y;
        double b2 = d1.x - d2.x;
        double c2 = a2 * d1.x + b2 * d1.y;

        double delta = a1 * b2 - a2 * b1;
        if (delta == 0) {
            return null;
        }
        return new Point2D.Double(((b2 * c1 - b1 * c2) / delta), ((a1 * c2 - a2 * c1) / delta));
    }
}

package io.github.metriximor.civsimbukkit.models.nodes;

import static io.github.metriximor.civsimbukkit.utils.NamespacedKeyUtils.getKey;

import com.jeff_media.morepersistentdatatypes.DataType;
import java.awt.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.NonNull;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public interface PolygonalArea extends Node {
    NamespacedKey AREA_MARKER = getKey("node_area");
    NamespacedKey AREA_KEY_X = getKey("node_area_x");
    NamespacedKey AREA_KEY_Y = getKey("node_area_y");

    default boolean hasAreaConfigured() {
        var state = getState();
        var pdc = state.getPersistentDataContainer();
        return pdc.has(AREA_MARKER);
    }

    default boolean setArea(@NonNull final Polygon polygon) {
        var state = getState();
        var pdc = state.getPersistentDataContainer();
        var points = getPointsOfPolygon(polygon).toList();
        pdc.set(AREA_MARKER, DataType.BOOLEAN, true);
        pdc.set(
                AREA_KEY_X,
                DataType.asList(PersistentDataType.INTEGER),
                points.stream().map(point -> point.x).toList());
        pdc.set(
                AREA_KEY_Y,
                DataType.asList(PersistentDataType.INTEGER),
                points.stream().map(point -> point.y).toList());

        return state.update();
    }

    default Polygon getArea() {
        var state = getState();
        var pdc = state.getPersistentDataContainer();
        if (!hasAreaConfigured()) {
            return null;
        }
        var x_points = pdc.get(AREA_KEY_Y, DataType.asList(PersistentDataType.INTEGER));
        var y_points = pdc.get(AREA_KEY_X, DataType.asList(PersistentDataType.INTEGER));
        if (x_points == null || y_points == null || x_points.size() != y_points.size()) {
            return null;
        }
        return new Polygon(
                x_points.stream().mapToInt(Integer::intValue).toArray(),
                y_points.stream().mapToInt(Integer::intValue).toArray(),
                x_points.size());
    }

    default boolean removeArea() {
        var state = getState();
        var pdc = state.getPersistentDataContainer();
        if (!hasAreaConfigured()) {
            return false;
        }
        pdc.remove(AREA_MARKER);
        pdc.remove(AREA_KEY_X);
        pdc.remove(AREA_KEY_Y);
        return state.update();
    }

    private static Stream<Point> getPointsOfPolygon(@NonNull final Polygon poly) {
        return IntStream.range(0, poly.npoints).mapToObj(i -> new Point(poly.xpoints[i], poly.ypoints[i]));
    }
}

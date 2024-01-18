/*
 * Copyright 2019 Hannes De Valkeneer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.metriximor.civsimbukkit.services.nodes;

import static io.github.metriximor.civsimbukkit.models.BoundaryMarker.getIndexFromItemStack;
import static io.github.metriximor.civsimbukkit.utils.NamespacedKeyUtils.getKey;
import static io.github.metriximor.civsimbukkit.utils.PlayerInteractionUtils.removeAllItemsThatSatisfyCondition;
import static io.github.metriximor.civsimbukkit.utils.Result.err;
import static io.github.metriximor.civsimbukkit.utils.Result.ok;
import static io.github.metriximor.civsimbukkit.utils.SegmentUtils.intersect;
import static io.github.metriximor.civsimbukkit.utils.StringUtils.getSuccessMessage;

import io.github.metriximor.civsimbukkit.models.BoundaryMarker;
import io.github.metriximor.civsimbukkit.models.PlacedBoundaryMarker;
import io.github.metriximor.civsimbukkit.models.errors.PlaceBoundaryError;
import io.github.metriximor.civsimbukkit.models.errors.RegisterBoundaryError;
import io.github.metriximor.civsimbukkit.models.nodes.PolygonalArea;
import io.github.metriximor.civsimbukkit.repositories.Repository;
import io.github.metriximor.civsimbukkit.services.ParticleService;
import io.github.metriximor.civsimbukkit.utils.Pair;
import io.github.metriximor.civsimbukkit.utils.Polygon;
import io.github.metriximor.civsimbukkit.utils.Result;
import io.github.metriximor.civsimbukkit.utils.SegmentUtils;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PolygonalAreaFunctionality<T extends PolygonalArea> extends NodeService<T> {
    NamespacedKey PLAYER_UUID_KEY = getKey("boundary_player_key");
    String DEFINING_BOUNDARIES_MSG =
            "Place the boundary marker to start! Type %s%s/civsim boundary done%s%s when finished or %s/civsim boundary cancel%s%s to cancel"
                    .formatted(
                            ChatColor.RESET,
                            ChatColor.ITALIC,
                            ChatColor.RESET,
                            ChatColor.GREEN,
                            ChatColor.ITALIC,
                            ChatColor.RESET,
                            ChatColor.GREEN);
    double MAX_DISTANCE_BETWEEN_MARKERS = 50d; // TODO load as configurable
    double MAX_DISTANCE_BETWEEN_MARKERS_SQUARED = MAX_DISTANCE_BETWEEN_MARKERS * MAX_DISTANCE_BETWEEN_MARKERS;
    double MAX_AREA_POLYGON = 2500d; // TODO load as configurable through function or smth
    double MIN_AREA_POLYGON = 4d;
    int MAX_POLYGON_POINTS = 50; // TODO configurable

    Repository<Player, Pair<PolygonalArea, List<PlacedBoundaryMarker>>> getPolygonalAreasRepo();

    ParticleService getParticleService();

    default Optional<ItemStack> defineBoundaries(@NonNull final Player player, @NonNull final Block nodeBlock) {
        final PolygonalArea node = getNode(nodeBlock);
        if (node == null) {
            return Optional.empty();
        }
        if (getPolygonalAreasRepo().getById(player) != null) {
            return Optional.empty();
        }
        player.sendMessage(getSuccessMessage(DEFINING_BOUNDARIES_MSG));
        var boundaryMarker = new BoundaryMarker(0);
        getPolygonalAreasRepo().add(player, Pair.of(node, new ArrayList<>()));
        return Optional.of(boundaryMarker.getAsArmorStand());
    }

    default Result<ItemStack, PlaceBoundaryError> placeBoundary(
            @NonNull final Player player,
            @NonNull final ItemStack currentBoundaryItemStack,
            @NonNull final Location location) {
        final Integer index = getIndexFromItemStack(currentBoundaryItemStack).orElse(null);
        if (index == null) {
            return err(PlaceBoundaryError.NOT_A_BOUNDARY_MARKER);
        }
        final var pair = getPolygonalAreasRepo().getById(player);
        if (pair == null) {
            getLogger()
                    .severe("Player %s is not in boundaries editing mode but managed to place one at %s"
                            .formatted(player, location));
            return err(PlaceBoundaryError.NOT_IN_BOUNDARY_EDITING_MODE);
        }
        if (index >= 1) {
            final var points = new ArrayList<>(
                    pair.right().stream().map(PlacedBoundaryMarker::asPoint2d).toList());
            final var edges =
                    IntStream.range(0, points.size() - 1).mapToObj(i -> Pair.of(points.get(i), points.get(i + 1)));
            final var previousPoint = points.get(points.size() - 1);
            final var currentPoint = new Point(location.getBlockX(), location.getBlockZ());
            if (edges.anyMatch(edge -> intersect(edge.left(), edge.right(), previousPoint, currentPoint))) {
                return err(PlaceBoundaryError.SELF_INTERSECTING);
            }
        }
        if (index >= 2) {
            final var currentPoint = new Point(location.getBlockX(), location.getBlockZ());
            final var points = new ArrayList<>(
                    pair.right().stream().map(PlacedBoundaryMarker::asPoint2d).toList());
            points.add(currentPoint);
            final var polygon = Polygon.build(points);
            if (polygon == null) {
                getLogger().severe("Player %s placed 3 points but the polygon failed to build");
                return err(PlaceBoundaryError.CONTACT_ADMIN);
            }
            var area = polygon.area();
            if (area > MAX_AREA_POLYGON) {
                return err(PlaceBoundaryError.AREA_TOO_BIG);
            }
        }
        if (index >= MAX_POLYGON_POINTS) {
            return err(PlaceBoundaryError.TOO_MANY_BOUNDARY_MARKERS);
        }
        if (index > 0) {
            final var previousBoundary = pair.right().get(index - 1);
            if (previousBoundary.distanceToSquared(location) > MAX_DISTANCE_BETWEEN_MARKERS_SQUARED) {
                return err(PlaceBoundaryError.DISTANCE_TOO_BIG);
            }
            final var previousLocation = previousBoundary.getLocation().clone();
            final var particleCurrentLocation = location.clone();

            previousLocation.add(0, 0.5, 0);
            particleCurrentLocation.add(0, 0.5, 0);
            getParticleService()
                    .drawLine(getParticleKey(player), previousLocation, particleCurrentLocation, Color.PURPLE, player);
        }
        pair.right().add(new BoundaryMarker(index).placeAt(location));
        final var boundaryMarker = new BoundaryMarker(index + 1);
        return ok(boundaryMarker.getAsArmorStand());
    }

    default Result<Boolean, RegisterBoundaryError> registerBoundaries(@NonNull final Player player) {
        final var pair = getPolygonalAreasRepo().getById(player);
        if (pair == null) {
            return err(RegisterBoundaryError.NOT_REGISTERING_BOUNDARIES);
        }
        final var node = pair.left();
        final var polygon = Polygon.build(
                pair.right().stream().map(PlacedBoundaryMarker::asPoint2d).toList());
        if (polygon == null) {
            return err(RegisterBoundaryError.INVALID_POLYGON);
        }
        if (polygon.area() < MIN_AREA_POLYGON) {
            return err(RegisterBoundaryError.AREA_TOO_SMALL);
        }
        final var lastPoint = pair.right().get(pair.right().size() - 1);
        final var firstPoint = pair.right().get(0);
        if (lastPoint.distanceToSquared(firstPoint) > MAX_DISTANCE_BETWEEN_MARKERS_SQUARED) {
            return err(RegisterBoundaryError.DISTANCE_TOO_BIG);
        }
        final var points =
                pair.right().stream().map(PlacedBoundaryMarker::asPoint2d).toList();
        final var lastEdgeIntersects = IntStream.range(0, pair.right().size() - 1)
                .mapToObj(i -> Pair.of(points.get(i), points.get(i + 1)))
                .anyMatch(edge -> SegmentUtils.intersect(
                        edge.left(), edge.right(), lastPoint.asPoint2d(), firstPoint.asPoint2d()));
        if (lastEdgeIntersects) {
            return err(RegisterBoundaryError.LAST_SEGMENT_INTERSECTS);
        }
        handleExitingEditingMode(player, pair);
        return ok(node.setArea(polygon));
    }

    default boolean cancelBoundarySelection(@NonNull final Player player) {
        final var pair = getPolygonalAreasRepo().getById(player);
        if (pair == null) {
            return false;
        }
        handleExitingEditingMode(player, pair);
        return true;
    }

    private void handleExitingEditingMode(
            @NonNull final Player player, @NonNull final Pair<PolygonalArea, List<PlacedBoundaryMarker>> pair) {
        pair.right().stream()
                .map(PlacedBoundaryMarker::getLocation)
                .flatMap(location -> location.getNearbyEntitiesByType(ArmorStand.class, 1).stream())
                .forEach(Entity::remove);
        getPolygonalAreasRepo().remove(player);
        getParticleService().removeAll(getParticleKey(player));
        removeAllItemsThatSatisfyCondition(player, BoundaryMarker::isBoundaryMarker);
    }

    private static String getParticleKey(@NonNull final Player player) {
        return "boundary_edit_%s".formatted(player.getUniqueId().toString());
    }
}

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
import static io.github.metriximor.civsimbukkit.models.BoundaryMarker.isBoundaryMarker;
import static io.github.metriximor.civsimbukkit.utils.Result.err;
import static io.github.metriximor.civsimbukkit.utils.Result.ok;
import static io.github.metriximor.civsimbukkit.utils.StringUtils.getSuccessMessage;

import io.github.metriximor.civsimbukkit.models.BoundaryMarker;
import io.github.metriximor.civsimbukkit.models.PlacedBoundaryMarker;
import io.github.metriximor.civsimbukkit.models.errors.AddBoundaryError;
import io.github.metriximor.civsimbukkit.models.nodes.PolygonalArea;
import io.github.metriximor.civsimbukkit.repositories.Repository;
import io.github.metriximor.civsimbukkit.services.ParticleService;
import io.github.metriximor.civsimbukkit.utils.Pair;
import io.github.metriximor.civsimbukkit.utils.Polygon;
import io.github.metriximor.civsimbukkit.utils.Result;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PolygonalAreaFunctionality<T extends PolygonalArea> extends NodeService<T> {
    String DEFINING_BOUNDARIES_MSG =
            "Place down the boundary marker to start! Type %s/civsim boundary done%s when finished"
                    .formatted(ChatColor.ITALIC, ChatColor.RESET);
    double MAX_DISTANCE_BETWEEN_MARKERS = 50d; // TODO load as configurable
    double MAX_DISTANCE_BETWEEN_MARKERS_SQUARED = MAX_DISTANCE_BETWEEN_MARKERS * MAX_DISTANCE_BETWEEN_MARKERS;
    double MAX_AREA_POLYGON = 2500d; // TODO load as configurable through function or smth
    int MAX_POLYGON_POINTS = 50; // TODO configurable

    Repository<Player, Pair<PolygonalArea, List<PlacedBoundaryMarker>>> getPolygonalAreasRepo();

    ParticleService getParticleService();

    default Optional<ItemStack> defineBoundaries(@NonNull final Player player, @NonNull final Block block) {
        final PolygonalArea node = getNode(block);
        if (node == null) {
            return Optional.empty();
        }
        player.sendMessage(getSuccessMessage(DEFINING_BOUNDARIES_MSG));
        var boundaryMarker = new BoundaryMarker(0);
        getPolygonalAreasRepo().add(player, Pair.of(node, new ArrayList<>()));
        return Optional.of(boundaryMarker.getAsArmorStand());
    }

    default Result<ItemStack, AddBoundaryError> addBoundary(
            @NonNull final Player player,
            @NonNull final ItemStack currentBoundaryItemStack,
            @NonNull final Location location) {
        if (!isBoundaryMarker(currentBoundaryItemStack)) {
            return err(AddBoundaryError.NOT_A_BOUNDARY_MARKER);
        }
        final Integer index = getIndexFromItemStack(currentBoundaryItemStack).orElse(null);
        if (index == null) {
            return err(AddBoundaryError.NO_INDEX);
        }
        final var pair = getPolygonalAreasRepo().getById(player);
        if (pair == null) {
            getLogger()
                    .severe("Player %s is not in boundaries editing mode but managed to place one at %s"
                            .formatted(player, location));
            return err(AddBoundaryError.NOT_IN_BOUNDARY_EDITING_MODE);
        }
        if (index > 0) {
            final var previousBoundary = pair.right().get(index - 1);
            if (previousBoundary.distanceToSquared(location) > MAX_DISTANCE_BETWEEN_MARKERS_SQUARED) {
                return err(AddBoundaryError.DISTANCE_TOO_BIG);
            }
            final var previousLocation = previousBoundary.getLocation().clone();
            final var particleCurrentLocation = location.clone();

            previousLocation.add(0, 0.5, 0);
            particleCurrentLocation.add(0, 0.5, 0);
            getParticleService()
                    .drawLine(getParticleKey(player), previousLocation, particleCurrentLocation, Color.PURPLE, player);
        }
        if (index >= 3) {
            final var points = new ArrayList<>(
                    pair.right().stream().map(PlacedBoundaryMarker::asPoint2d).toList());
            points.add(new Point(location.getBlockX(), location.getBlockZ()));
            final var polygon = Polygon.build(points);
            if (polygon == null) {
                getLogger().severe("Player %s placed 3 points but the polygon failed to build");
                return err(AddBoundaryError.CONTACT_ADMIN);
            }
            var area = polygon.area();
            if (area > MAX_AREA_POLYGON) {
                return err(AddBoundaryError.AREA_TOO_BIG);
            }
        }
        if (index >= MAX_POLYGON_POINTS) {
            return err(AddBoundaryError.TOO_MANY_BOUNDARY_MARKERS);
        }
        pair.right().add(new BoundaryMarker(index).place(location));
        final var boundaryMarker = new BoundaryMarker(index + 1);
        return ok(boundaryMarker.getAsArmorStand());
    }

    default boolean registerBoundaries(@NonNull final Player player) {
        final var pair = getPolygonalAreasRepo().getById(player);
        if (pair == null) {
            return false;
        }
        final var node = pair.left();
        final var polygon = Polygon.build(
                pair.right().stream().map(PlacedBoundaryMarker::asPoint2d).toList());
        if (polygon == null) {
            return false;
        }
        getPolygonalAreasRepo().remove(player);
        getParticleService().removeAll(getParticleKey(player));
        return node.setArea(polygon);
    }

    private static String getParticleKey(@NonNull final Player player) {
        return "boundary_edit_%s".formatted(player.getUniqueId().toString());
    }
}

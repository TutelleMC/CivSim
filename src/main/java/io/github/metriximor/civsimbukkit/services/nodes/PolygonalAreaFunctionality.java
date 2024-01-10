package io.github.metriximor.civsimbukkit.services.nodes;

import static io.github.metriximor.civsimbukkit.services.BoundaryMarker.getIndexFromItemStack;
import static io.github.metriximor.civsimbukkit.services.BoundaryMarker.isBoundaryMarker;
import static io.github.metriximor.civsimbukkit.utils.StringUtils.getSuccessMessage;

import io.github.metriximor.civsimbukkit.models.nodes.PolygonalArea;
import io.github.metriximor.civsimbukkit.repositories.Repository;
import io.github.metriximor.civsimbukkit.services.BoundaryMarker;
import io.github.metriximor.civsimbukkit.utils.Pair;
import io.github.metriximor.civsimbukkit.utils.Polygon;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PolygonalAreaFunctionality<T extends PolygonalArea> extends NodeService<T> {
    String DEFINING_BOUNDARIES_MSG =
            "Place down the boundary marker to start! Type %s/civsim boundary done%s when finished"
                    .formatted(ChatColor.ITALIC, ChatColor.RESET);
    double MAX_DISTANCE_BETWEEN_MARKERS = 50d * 50d; // TODO load as configurable
    double MAX_AREA_POLYGON = 2500d; // TODO load as configurable through function or smth
    int MAX_POLYGON_POINTS = 50; // TODO configurable

    Repository<Player, Pair<PolygonalArea, List<BoundaryMarker>>> getPolygonalAreasRepo();

    default Optional<ItemStack> defineBoundaries(@NonNull final Player player, @NonNull final Block block) {
        final PolygonalArea node = getNode(block);
        if (node == null) {
            return Optional.empty();
        }
        player.sendMessage(getSuccessMessage(DEFINING_BOUNDARIES_MSG));
        var boundaryMarker = new BoundaryMarker(0);
        getPolygonalAreasRepo().add(player, new Pair<>(node, new ArrayList<>(List.of(boundaryMarker))));
        return Optional.of(boundaryMarker.getAsArmorStand());
    }

    default Optional<ItemStack> addBoundary(
            @NonNull final Player player,
            @NonNull final ItemStack currentBoundaryItemStack,
            @NonNull final Location location) {
        if (!isBoundaryMarker(currentBoundaryItemStack)) {
            return Optional.empty();
        }
        final Integer index = getIndexFromItemStack(currentBoundaryItemStack).orElse(null);
        if (index == null) {
            return Optional.empty();
        }
        final var pair = getPolygonalAreasRepo().getById(player);
        if (pair == null) {
            return Optional.empty();
        }
        if (index > 0) {
            final var previousBoundary = pair.right().get(index - 1);
            if (previousBoundary.distanceToSquared(location) > MAX_DISTANCE_BETWEEN_MARKERS) {
                return Optional.empty();
            }
        }
        if (index >= 3) {
            final var points = new ArrayList<>(
                    pair.right().stream().map(BoundaryMarker::asPoint2d).toList());
            points.add(new Point(location.getBlockX(), location.getBlockZ()));
            final var polygon = Polygon.build(points);
            if (polygon == null || polygon.area() > MAX_AREA_POLYGON) {
                return Optional.empty();
            }
        }
        if (index >= MAX_POLYGON_POINTS) {
            return Optional.empty();
        }
        final var currentBoundary = pair.right().get(index);
        currentBoundary.place(location);
        final var boundaryMarker = new BoundaryMarker(index + 1);
        pair.right().add(boundaryMarker);
        return Optional.of(boundaryMarker.getAsArmorStand());
    }

    default boolean registerBoundaries(@NonNull final Player player) {
        final var pair = getPolygonalAreasRepo().getById(player);
        if (pair == null) {
            return false;
        }
        final var node = pair.left();
        final var polygon = Polygon.build(
                pair.right().stream().map(BoundaryMarker::asPoint2d).toList());
        if (polygon == null) {
            return false;
        }
        return node.setArea(polygon);
    }
}

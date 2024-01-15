package io.github.metriximor.civsimbukkit.models;

import static org.junit.jupiter.api.Assertions.*;

import io.github.metriximor.civsimbukkit.BukkitTest;
import org.bukkit.Location;
import org.junit.jupiter.api.Test;

class PlacedBoundaryMarkerTest extends BukkitTest {
    @Test
    void testDistanceToSquaredWorksCorrectly() {
        final var placed = new PlacedBoundaryMarker(new Location(getWorld(), 0, 0, 0));
        assertEquals(300.0, placed.distanceToSquared(new Location(getWorld(), 10, 10, 10)));
        assertTrue(placed.distanceToSquared(new Location(getWorld(), 26, 0, 0)) > (25 * 25));
    }

    @Test
    void testAsPoint2DMapsCorrectly() {
        final var placed = new PlacedBoundaryMarker(new Location(getWorld(), 1, 2, 3));
        final var result = placed.asPoint2d();
        assertEquals(placed.getLocation().getBlockX(), result.x);
        assertEquals(placed.getLocation().getBlockZ(), result.y);
    }
}

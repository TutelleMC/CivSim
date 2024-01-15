package io.github.metriximor.civsimbukkit.models;

import static org.junit.jupiter.api.Assertions.*;

import io.github.metriximor.civsimbukkit.BukkitTest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

class BoundaryMarkerTest extends BukkitTest {
    @Test
    void testConstructorWorks() {
        assertDoesNotThrow(() -> new BoundaryMarker(0));
        assertDoesNotThrow(() -> new BoundaryMarker(1));
        assertThrows(IllegalArgumentException.class, () -> new BoundaryMarker(-1));
    }

    @Test
    void testIsBoundaryMarkerReturnsFalseOnNonArmorStand() {
        assertFalse(BoundaryMarker.isBoundaryMarker(new ItemStack(Material.IRON_INGOT)));
    }

    @Test
    void testIsBoundaryMarkerReturnsFalseOnArmorStandWithNoMeta() {
        final var itemStack = new ItemStack(Material.ARMOR_STAND);
        itemStack.setItemMeta(null);
        assertFalse(BoundaryMarker.isBoundaryMarker(itemStack));
    }

    @Test
    void testIsBoundaryMarkerReturnsFalseOnARegularArmorStand() {
        final var armorStand = new ItemStack(Material.ARMOR_STAND);
        assertNotNull(armorStand.getItemMeta());
        assertFalse(BoundaryMarker.isBoundaryMarker(armorStand));
    }

    @Test
    void testGetAsArmorStandWorksCorrectly() {
        final var boundaryMarkerItem = new BoundaryMarker(0).getAsArmorStand();
        assertTrue(BoundaryMarker.isBoundaryMarker(boundaryMarkerItem));
        assertTrue(BoundaryMarker.getIndexFromItemStack(boundaryMarkerItem).isPresent());
        assertEquals(0, BoundaryMarker.getIndexFromItemStack(boundaryMarkerItem).get());
    }

    @Test
    void testGetIndexFromItemStackReturnsEmptyWhenIsNotBoundaryMarker() {
        assertTrue(BoundaryMarker.getIndexFromItemStack(new ItemStack(Material.ARMOR_STAND))
                .isEmpty());
    }

    @Test
    void testPlaceAtWorks() {
        final var boundary = new BoundaryMarker(0);
        assertNotNull(boundary.placeAt(new Location(getWorld(), 1, 1, 1)));
    }
}

package io.github.metriximor.civsimbukkit.utils;

import static io.github.metriximor.civsimbukkit.utils.NamespacedKeyUtils.getMarkerKey;
import static io.github.metriximor.civsimbukkit.utils.PlayerInteractionUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import io.github.metriximor.civsimbukkit.BukkitTest;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.Test;

class PlayerInteractionUtilsTest extends BukkitTest {
    @Test
    void giveItemToPlayerHappyPath() {
        final var player = setupPlayer();
        final var ironIngot = new ItemStack(Material.IRON_INGOT);
        assertEquals(-1, player.getInventory().first(ironIngot));
        giveItemToPlayer(player, ironIngot);
        assertNotEquals(-1, player.getInventory().first(ironIngot));
    }

    @Test
    void testRemoveAllItemsThatSatisfyConditionHappyPath() {
        final var player = setupPlayer();
        final var modifiedIronIngot = new ItemStack(Material.IRON_INGOT);
        final var ironIngot = new ItemStack(Material.IRON_INGOT);
        modifiedIronIngot.editMeta(
                meta -> meta.getPersistentDataContainer().set(getMarkerKey(), PersistentDataType.BOOLEAN, true));
        giveItemToPlayer(player, modifiedIronIngot);
        giveItemToPlayer(player, ironIngot);
        removeAllItemsThatSatisfyCondition(
                player, item -> item.getItemMeta().getPersistentDataContainer().has(getMarkerKey()));

        assertEquals(-1, player.getInventory().first(modifiedIronIngot));
        assertNotEquals(-1, player.getInventory().first(ironIngot));
    }

    @Test
    void testReplaceItemInInventoryHappyPath() {
        final var player = setupPlayer();
        final var modifiedIronIngot = new ItemStack(Material.IRON_INGOT);
        final var ironIngot = new ItemStack(Material.IRON_INGOT);
        modifiedIronIngot.editMeta(
                meta -> meta.getPersistentDataContainer().set(getMarkerKey(), PersistentDataType.BOOLEAN, true));
        giveItemToPlayer(player, modifiedIronIngot);
        final var previousIndex = player.getInventory().first(modifiedIronIngot);
        replaceItemInInventory(player, modifiedIronIngot, ironIngot);

        assertEquals(previousIndex, player.getInventory().first(ironIngot));
        assertEquals(-1, player.getInventory().first(modifiedIronIngot));
    }
}

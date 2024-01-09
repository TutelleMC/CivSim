package io.github.metriximor.civsimbukkit.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerInteractionUtils {
    public static void giveItemToPlayer(@NotNull final Player player, final ItemStack farmItem) {
        // Add item to player
        final var droppedItem = player.getInventory().addItem(farmItem);
        droppedItem.forEach((idx, item) -> player.getWorld().dropItem(player.getLocation(), item));
    }
}

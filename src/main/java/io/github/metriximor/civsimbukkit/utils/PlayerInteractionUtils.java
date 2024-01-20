package io.github.metriximor.civsimbukkit.utils;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerInteractionUtils {
    public static void giveItemToPlayer(final @NonNull Player player, final ItemStack itemStack) {
        // Add item to player
        final var droppedItem = player.getInventory().addItem(itemStack);
        droppedItem.forEach((idx, item) -> player.getWorld().dropItem(player.getLocation(), item));
    }

    public static void removeAllItemsThatSatisfyCondition(
            @NotNull final Player player, final @NonNull Function<ItemStack, Boolean> condition) {
        final var inventory = player.getInventory();
        Arrays.stream(inventory.getStorageContents())
                .filter(Objects::nonNull)
                .filter(condition::apply)
                .forEach(inventory::remove);
    }

    public static void replaceItemInInventory(
            final @NonNull Player player, final @NonNull ItemStack oldItem, final @NonNull ItemStack newItem) {
        final var inventory = player.getInventory();
        inventory.setItem(inventory.first(oldItem), newItem);
    }
}

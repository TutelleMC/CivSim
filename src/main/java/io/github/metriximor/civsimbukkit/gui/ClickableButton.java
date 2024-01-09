package io.github.metriximor.civsimbukkit.gui;

import java.util.function.Consumer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

@RequiredArgsConstructor
public class ClickableButton extends AbstractItem {
    public record Click(
            @NonNull ClickType clickType, @NonNull Player player, @NonNull InventoryClickEvent inventoryClickEvent) {}

    private final Consumer<Click> action;

    @Override
    public ItemProvider getItemProvider() {
        return new ItemBuilder(Material.COMPASS)
                .setDisplayName("%sBoundaries".formatted(ChatColor.DARK_PURPLE))
                .addLoreLines("%sClick me to start editing the boundaries of this node.".formatted(ChatColor.ITALIC));
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        action.accept(new Click(clickType, player, event));
    }
}

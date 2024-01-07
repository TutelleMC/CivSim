package io.github.metriximor.civsimbukkit.gui;

import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

@AllArgsConstructor
public class ToggleItem extends AbstractItem {
    public record ToggleCall(
            ClickType clickType, Player player, InventoryClickEvent inventoryClickEvent, Boolean enabled) {}

    private boolean enabled;
    private Consumer<ToggleCall> supplier;

    @Override
    public ItemProvider getItemProvider() {
        return enabled ? getEnabled() : getDisabled();
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        this.enabled = !enabled;
        supplier.accept(new ToggleCall(clickType, player, event, enabled));
        notifyWindows();
    }

    private ItemProvider getEnabled() {
        return new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE)
                .setDisplayName("%sEnabled".formatted(ChatColor.GREEN));
    }

    private ItemProvider getDisabled() {
        return new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("%sDisabled".formatted(ChatColor.RED));
    }
}

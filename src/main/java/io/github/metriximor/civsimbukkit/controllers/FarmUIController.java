package io.github.metriximor.civsimbukkit.controllers;

import io.github.metriximor.civsimbukkit.gui.ToggleItem;
import io.github.metriximor.civsimbukkit.services.nodes.WorkableNodeService;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

@RequiredArgsConstructor
public class FarmUIController {
    private final WorkableNodeService workableNodeService;

    public void openNodeUI(@NonNull final Player player, @NonNull final Block block) {
        if (workableNodeService.blockIsNotNode(block)) {
            player.sendMessage("%sToggling non toggleable block. Please contact an admin!".formatted(ChatColor.RED));
            return;
        }
        final boolean isEnabled = workableNodeService.isEnabled(block);

        final var wagesLore = workableNodeService
                .copyWages(block)
                .map(bill -> bill.describe().stream()
                        .map(component -> component.decorate(TextDecoration.ITALIC))
                        .toList())
                .orElse(List.of(Component.text("No wages configured!").color(NamedTextColor.RED)))
                .stream()
                .map(component -> (ComponentWrapper) new AdventureComponentWrapper(component))
                .toList();
        final var wagesItem = new SimpleItem(new ItemBuilder(Material.PAPER)
                .addLoreLines(wagesLore)
                .setDisplayName("%sWages".formatted(ChatColor.DARK_PURPLE)));

        final Gui gui = Gui.normal()
                .setStructure("T W . . . . . . .")
                .addIngredient('T', new ToggleItem(isEnabled, toggleCall -> workableNodeService.toggleNode(block)))
                .addIngredient('W', wagesItem)
                .build();
        Window.single()
                .setTitle("%sFarm Menu".formatted(ChatColor.DARK_GREEN))
                .setGui(gui)
                .build(player)
                .open();
    }
}

package io.github.metriximor.civsimbukkit.controllers;

import io.github.metriximor.civsimbukkit.gui.items.ToggleItem;
import io.github.metriximor.civsimbukkit.services.nodes.WorkableNodeService;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
        final var wages = Optional
                .ofNullable(workableNodeService.copyWages(block).orElse(new ItemStack(Material.PAPER)).lore());
        final List<ComponentWrapper> wagesLore = wages.orElse(List.of(Component.text("No wages configured!"))).stream()
                .map(AdventureComponentWrapper::new).map(list -> (ComponentWrapper) list).toList();
        final var wagesItem = new SimpleItem(new ItemBuilder(Material.PAPER).addLoreLines(wagesLore));

        final Gui gui = Gui.normal().setStructure("T W . . . . . . .")
                .addIngredient('T', new ToggleItem(isEnabled, toggleCall -> workableNodeService.toggleNode(block)))
                .addIngredient('W', wagesItem).build();
        Window.single().setTitle("%sFarm Menu".formatted(ChatColor.DARK_GREEN)).setGui(gui).build(player).open();
    }
}

package io.github.metriximor.civsimbukkit.controllers;

import io.github.metriximor.civsimbukkit.gui.ToggleItem;
import io.github.metriximor.civsimbukkit.gui.WagesItem;
import io.github.metriximor.civsimbukkit.services.nodes.WorkableNodeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
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

        final Gui gui = Gui.normal()
                .setStructure("T W . . . . . . .")
                .addIngredient('T', new ToggleItem(isEnabled, toggleCall -> workableNodeService.toggleNode(block)))
                .addIngredient(
                        'W', new WagesItem(workableNodeService.copyWages(block).orElse(null)))
                .build();
        Window.single()
                .setTitle("%sFarm Menu".formatted(ChatColor.DARK_GREEN))
                .setGui(gui)
                .build(player)
                .open();
    }
}

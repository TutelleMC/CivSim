package io.github.metriximor.civsimbukkit.controllers;

import static io.github.metriximor.civsimbukkit.utils.PlayerInteractionUtils.giveItemToPlayer;
import static io.github.metriximor.civsimbukkit.utils.StringUtils.getFailMessage;

import io.github.metriximor.civsimbukkit.gui.ClickableButton;
import io.github.metriximor.civsimbukkit.gui.ToggleableButton;
import io.github.metriximor.civsimbukkit.gui.WagesItem;
import io.github.metriximor.civsimbukkit.services.nodes.FarmNodeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

@RequiredArgsConstructor
public class FarmUIController {
    private final FarmNodeService farmNodeService;

    public void openNodeUI(@NonNull final Player player, @NonNull final Block block) {
        if (farmNodeService.blockIsNotNode(block)) {
            player.sendMessage("%sToggling non toggleable block. Please contact an admin!".formatted(ChatColor.RED));
            return;
        }
        final boolean isEnabled = farmNodeService.isEnabled(block);

        final Gui gui = Gui.normal()
                .setStructure("T W . . . . . B .")
                .addIngredient('T', new ToggleableButton(isEnabled, toggleCall -> farmNodeService.toggleNode(block)))
                .addIngredient(
                        'W', new WagesItem(farmNodeService.copyWages(block).orElse(null)))
                .addIngredient('B', new ClickableButton(click -> defineBoundary(player, block)))
                .build();
        Window.single()
                .setTitle("%sFarm Menu".formatted(ChatColor.DARK_GREEN))
                .setGui(gui)
                .build(player)
                .open();
    }

    private void defineBoundary(@NotNull Player player, @NotNull Block block) {
        var boundaryMarker = farmNodeService.defineBoundaries(player, block);
        if (boundaryMarker.isEmpty()) {
            player.sendMessage(getFailMessage("Failed to create boundary marker!"));
            return;
        }
        giveItemToPlayer(player, boundaryMarker.get());
    }
}

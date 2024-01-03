package io.github.metriximor.civsimbukkit.controllers;

import io.github.metriximor.civsimbukkit.gui.items.ToggleItem;
import io.github.metriximor.civsimbukkit.models.Node;
import io.github.metriximor.civsimbukkit.services.NodeService;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

public class UIController {
    private NodeService nodeService;
    public void openNodeUI(@NonNull final Player player,
                           @NonNull final Block block) {
        if (nodeService.blockIsNotNode(block)) {
            return;
        }
        final var node = Node.make(block);
        if (node.isEmpty()) {
            return;
        }

        final Gui gui = Gui.normal()
                .setStructure("T W . . . . . . .")
                .addIngredient('T', new ToggleItem(node.get().isEnabled()))
                .addIngredient('W', new SimpleItem(new ItemBuilder(Material.MAP)))
                .build();
        Window.single()
                .setTitle("%sFarm Menu".formatted(ChatColor.DARK_GREEN))
                .setGui(gui)
                .build(player)
                .open();
    }
}

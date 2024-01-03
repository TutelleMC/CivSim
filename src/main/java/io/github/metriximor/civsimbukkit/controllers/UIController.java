package io.github.metriximor.civsimbukkit.controllers;

import io.github.metriximor.civsimbukkit.gui.items.ToggleItem;
import io.github.metriximor.civsimbukkit.services.NodeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

@RequiredArgsConstructor
public class UIController {
	private final NodeService nodeService;
	public void openNodeUI(@NonNull final Player player, @NonNull final Block block) {
		if (nodeService.blockIsNotNode(block)) {
			player.sendMessage("%sToggling non toggleable block. Please contact an admin!".formatted(ChatColor.RED));
			return;
		}
		final boolean isEnabled = nodeService.isEnabled(block);

		final Gui gui = Gui.normal().setStructure("T W . . . . . . .")
				.addIngredient('T', new ToggleItem(isEnabled, toggleCall -> nodeService.toggleNode(block)))
				.addIngredient('W', new SimpleItem(new ItemBuilder(Material.MAP))).build();
		Window.single().setTitle("%sFarm Menu".formatted(ChatColor.DARK_GREEN)).setGui(gui).build(player).open();
	}
}

package io.github.metriximor.civsimbukkit.listeners;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import io.github.metriximor.civsimbukkit.services.ItemSetService;
import io.github.metriximor.civsimbukkit.services.NodeService;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class NodeListener implements Listener {
    private final NodeService nodeService;
    private final ItemSetService itemSetService;

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractWithNode(@NotNull final PlayerInteractEvent event) {
        final var itemInHand = event.getItem();
        final var clickedBlock = event.getClickedBlock();
        if (itemInHand == null || clickedBlock == null) {
            event.getPlayer().sendMessage("Item or block was null, ignoring");
            return;
        }

        final var node = nodeService.getNode(clickedBlock);
        if (!event.getAction().isLeftClick() || node.isEmpty()) {
            event.getPlayer().sendMessage("Not interacting with a node");
            return;
        }

        if (itemInHand.getType().equals(Material.STICK)) {
            //TODO: open UI here
            return;
        } else if (itemSetService.isItemSetItemStack(ItemSetService.SetType.WAGES, itemInHand)) {
            event.getPlayer().sendMessage("Wages registered");
            nodeService.addWages(node.get(), itemInHand);
            event.getPlayer().getInventory().remove(itemInHand);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPlaceNode(@NotNull final BlockPlaceEvent event) {
        final var itemStack = event.getItemInHand();

        if (nodeService.hasMarker(itemStack)) {
            nodeService.registerNode(event.getBlockPlaced());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onNodeBeingDestroyed(@NotNull final BlockDestroyEvent event) {
        if (nodeService.getNode(event.getBlock()).isEmpty()) {
            return;
        }
        nodeService.unregisterNode(event.getBlock());
    }
}

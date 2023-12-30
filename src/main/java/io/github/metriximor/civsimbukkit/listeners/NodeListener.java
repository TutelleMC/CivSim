package io.github.metriximor.civsimbukkit.listeners;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import io.github.metriximor.civsimbukkit.guis.FarmGUI;
import io.github.metriximor.civsimbukkit.services.NodeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class NodeListener implements Listener {
    @NonNull
    private final NodeService nodeService;

    @EventHandler(ignoreCancelled = true)
    public void onPlayerRightClickOnABlock(@NotNull final PlayerInteractEvent event) {
        final var itemInHand = event.getItem();
        final var clickedBlock = event.getClickedBlock();
        if (itemInHand == null || clickedBlock == null) {
            event.getPlayer().sendMessage("Item or block was null, ignoring");
            return;
        }

        if (!event.getAction().isLeftClick() ||
                !itemInHand.getType().equals(Material.STICK) ||
                !nodeService.isNode(clickedBlock)) {
            event.getPlayer().sendMessage("Conditions not met for opening node inventory");
            return;
        }

        event.getPlayer().sendMessage("Opening Farm GUI");
        final var farmGUI = new FarmGUI();
        farmGUI.open(event.getPlayer());
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
        if (!nodeService.isNode(event.getBlock())) {
            return;
        }
        nodeService.unregisterNode(event.getBlock());
    }
}

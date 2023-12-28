package io.github.metriximor.civsimbukkit.listeners;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import io.github.metriximor.civsimbukkit.services.NodeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
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
        if (!event.getAction().isRightClick() || isNotANode(event.getClickedBlock())) {
            return;
        }
        event.getPlayer().sendMessage("You just clicked on a farm! :)");
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
        if (isNotANode(event.getBlock())) {
            return;
        }
        nodeService.unregisterNode(event.getBlock());
    }

    private boolean isNotANode(final Block clickedBlock) {
        return clickedBlock == null || nodeService.isNode(clickedBlock);
    }
}

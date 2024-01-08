package io.github.metriximor.civsimbukkit.listeners;

import static io.github.metriximor.civsimbukkit.utils.StringUtils.getFailMessage;
import static io.github.metriximor.civsimbukkit.utils.StringUtils.getSuccessMessage;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import io.github.metriximor.civsimbukkit.controllers.FarmUIController;
import io.github.metriximor.civsimbukkit.models.BillOfMaterials;
import io.github.metriximor.civsimbukkit.services.BillOfMaterialsService;
import io.github.metriximor.civsimbukkit.services.nodes.FarmNodeService;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class NodeListener implements Listener {
    private final FarmNodeService farmNodeService;
    private final BillOfMaterialsService billOfMaterialsService;
    private final FarmUIController farmUiController;

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractWithNode(@NotNull final PlayerInteractEvent event) {
        final var itemInHand = event.getItem();
        final var clickedBlock = event.getClickedBlock();
        if (itemInHand == null || clickedBlock == null) {
            event.getPlayer().sendMessage("Item or block was null, ignoring");
            return;
        }

        if (!event.getAction().isLeftClick() || farmNodeService.blockIsNotNode(clickedBlock)) {
            event.getPlayer().sendMessage(getFailMessage("Not interacting with a node"));
            return;
        }

        if (itemInHand.getType().equals(Material.STICK)) {
            farmUiController.openNodeUI(event.getPlayer(), clickedBlock);
        } else if (billOfMaterialsService.isItemSetItemStack(BillOfMaterialsService.SetType.WAGES, itemInHand)) {
            final var wages = BillOfMaterials.fromItemStack(BillOfMaterialsService.SetType.WAGES, itemInHand);
            if (wages.isEmpty()) {
                event.getPlayer().sendMessage(getFailMessage("Wages invalid, can hold only a max of 9 stacks"));
                return;
            }
            if (farmNodeService.addWages(clickedBlock, wages.get())) {
                event.getPlayer().getInventory().remove(itemInHand);
                event.getPlayer().sendMessage(getSuccessMessage("Wages registered"));
            } else {
                event.getPlayer().sendMessage(getFailMessage("Failed to register wages"));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPlaceNode(@NotNull final BlockPlaceEvent event) {
        final var itemStack = event.getItemInHand();

        if (farmNodeService.hasMarker(itemStack)) {
            event.getPlayer().sendMessage(getSuccessMessage("You just placed a Farm!"));
            farmNodeService.registerNode(event.getBlockPlaced());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onNodeBeingDestroyed(@NotNull final BlockDestroyEvent event) {
        if (farmNodeService.blockIsNotNode(event.getBlock())) {
            return;
        }
        farmNodeService.unregisterNode(event.getBlock());
    }
}

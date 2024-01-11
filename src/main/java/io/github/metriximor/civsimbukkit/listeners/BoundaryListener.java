package io.github.metriximor.civsimbukkit.listeners;

import static io.github.metriximor.civsimbukkit.services.BoundaryMarker.isBoundaryMarker;
import static io.github.metriximor.civsimbukkit.utils.PlayerInteractionUtils.giveItemToPlayer;
import static io.github.metriximor.civsimbukkit.utils.StringUtils.getFailMessage;

import io.github.metriximor.civsimbukkit.services.BoundaryMarker;
import io.github.metriximor.civsimbukkit.services.nodes.FarmNodeService;
import java.util.logging.Logger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class BoundaryListener implements Listener {
    @NonNull
    private final Logger logger;

    @NonNull
    private final Plugin plugin;

    @NonNull
    private final FarmNodeService farmNodeService;

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPlaceBoundaryMarker(@NonNull final EntityPlaceEvent event) {
        final var player = event.getPlayer();
        if (!EntityType.ARMOR_STAND.equals(event.getEntityType()) || player == null) {
            return;
        }
        // This ugly code can be replaced with a single method call in 1.19... oh well
        final var inventory = player.getInventory();
        final var mainHand = inventory.getItemInMainHand();
        final var offHand = inventory.getItemInOffHand();
        final boolean mainHandIsBoundaryMarker = isBoundaryMarker(mainHand);
        final boolean offHandIsBoundaryMarker = isBoundaryMarker(offHand);
        if (!mainHandIsBoundaryMarker && !offHandIsBoundaryMarker) {
            return;
        } else if (mainHandIsBoundaryMarker && offHandIsBoundaryMarker) {
            player.sendMessage(
                    getFailMessage("You can't place a boundary marker when both hands have boundary markers"));
            event.setCancelled(true);
            return;
        }
        final var itemStack = mainHandIsBoundaryMarker ? mainHand : offHand;

        player.sendMessage("You are holding %s".formatted(itemStack));
        player.sendMessage(
                "It's a boundary marker! Index: %s".formatted(BoundaryMarker.getIndexFromItemStack(itemStack)));

        // When updating to 1.20, we can update this to hide for everyone. As it stands it's going to be wack
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.hideEntity(plugin, event.getEntity()));
        player.showEntity(plugin, event.getEntity());

        final var nextMarker =
                farmNodeService.addBoundary(player, itemStack, event.getBlock().getLocation());
        if (nextMarker.isEmpty()) {
            player.sendMessage(getFailMessage("Failed to place marker!"));
            event.setCancelled(true);
            return;
        }

        giveItemToPlayer(player, nextMarker.get());
    }
}

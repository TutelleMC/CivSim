package io.github.metriximor.civsimbukkit.listeners;

import static io.github.metriximor.civsimbukkit.models.BoundaryMarker.isBoundaryMarker;
import static io.github.metriximor.civsimbukkit.services.nodes.PolygonalAreaFunctionality.PLAYER_UUID_KEY;
import static io.github.metriximor.civsimbukkit.utils.NamespacedKeyUtils.getMarkerKey;
import static io.github.metriximor.civsimbukkit.utils.PlayerInteractionUtils.*;
import static io.github.metriximor.civsimbukkit.utils.StringUtils.getFailMessage;

import com.jeff_media.morepersistentdatatypes.DataType;
import io.github.metriximor.civsimbukkit.models.BoundaryMarker;
import io.github.metriximor.civsimbukkit.services.nodes.FarmNodeService;
import java.util.logging.Logger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
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
    public void onPlayerPlaceBoundaryMarker(final @NonNull EntityPlaceEvent event) {
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

        // When updating to 1.20, we can update this to hide for everyone. As it stands
        // it's going to be wack
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.hideEntity(plugin, event.getEntity()));
        player.showEntity(plugin, event.getEntity());

        final var nextMarker = farmNodeService.placeBoundary(
                player, itemStack, event.getEntity().getLocation());
        if (nextMarker.isErr()) {
            final String errorMessage =
                    switch (nextMarker.unwrapErr()) {
                        case CONTACT_ADMIN -> "Error, if you see this message please contact an admin";
                        case NOT_A_BOUNDARY_MARKER -> "Placed armor stand is not a boundary";
                        case NOT_IN_BOUNDARY_EDITING_MODE -> "You are not in boundary editing mode";
                        case DISTANCE_TOO_BIG -> "Distance between boundary markers too big";
                        case AREA_TOO_BIG -> "The area of the farm boundary is too big";
                        case TOO_MANY_BOUNDARY_MARKERS -> "You have hit the limit in number of boundary markers";
                        case SELF_INTERSECTING -> "You can't place a marker that is intersecting existing markers";
                    };
            player.sendMessage(getFailMessage(errorMessage));
            event.setCancelled(true);
            return;
        }
        replaceItemInInventory(player, itemStack, nextMarker.unwrap());
        event.getEntity().getPersistentDataContainer().set(getMarkerKey(), PersistentDataType.BYTE, (byte) 2);
        event.getEntity().getPersistentDataContainer().set(PLAYER_UUID_KEY, DataType.UUID, player.getUniqueId());
    }

    @EventHandler
    public void onPlayerLogout(final @NonNull PlayerQuitEvent event) {
        farmNodeService.cancelBoundarySelection(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(final @NonNull PlayerDeathEvent event) {
        farmNodeService.cancelBoundarySelection(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerThrowBoundaryMarkerAway(final @NonNull PlayerDropItemEvent event) {
        final var droppedItem = event.getItemDrop().getItemStack();
        if (!BoundaryMarker.isBoundaryMarker(droppedItem)) {
            return;
        }
        event.setCancelled(true);
        event.getPlayer().sendMessage(getFailMessage("Can't drop a boundary marker"));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAttackBoundaryMarker(final @NonNull EntityDamageEvent event) {
        if (!event.getEntityType().equals(EntityType.ARMOR_STAND)) {
            return;
        }
        if (!event.getEntity().getPersistentDataContainer().has(getMarkerKey())) {
            return;
        }
        final var playerUUID = event.getEntity().getPersistentDataContainer().get(PLAYER_UUID_KEY, DataType.UUID);
        final var player = Bukkit.getPlayer(playerUUID);
        if (player == null) {
            logger.severe("Attempted to find player with UUID %s but couldn't find it".formatted(playerUUID));
            return;
        }
        farmNodeService.cancelBoundarySelection(player);
    }

    @EventHandler
    public void onPlayerJoinEvent(final @NonNull PlayerJoinEvent event) {
        removeAllItemsThatSatisfyCondition(event.getPlayer(), BoundaryMarker::isBoundaryMarker);
    }
}

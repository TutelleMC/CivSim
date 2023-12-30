package io.github.metriximor.civsimbukkit.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class NodeService {
    @NonNull
    private final Logger logger;
    @NonNull
    private final Plugin plugin;

    private final Set<Block> registeredNodes = new HashSet<>();

    public boolean isNode(@NonNull final Block block) {
        if (block.getState() instanceof Barrel state) {
            return state.getPersistentDataContainer().has(getMarkerKey());
        }
        return false;
    }

    public void registerNode(@NonNull final Block block) {
        if (!(block.getState() instanceof TileState state)) {
            logger.severe("Attempted to register %s as a node despite it not being a tileable block".formatted(block));
            return;
        }
        setNodeMarker(state.getPersistentDataContainer());
        state.update();

        logger.info("Registered Node at %s".formatted(block.getLocation()));
        registeredNodes.add(block);
    }

    public void unregisterNode(@NonNull final Block block) {
        if (!isNode(block)) {
            logger.severe("Attempted to unregister block that isn't a node: %s".formatted(block));
            return;
        }
        if (!registeredNodes.remove(block)) {
            logger.severe("Failed to remove node from registered nodes: %s".formatted(block));
        }
    }

    public void addMarker(@NonNull final ItemStack itemStack) {
        itemStack.editMeta(meta -> setNodeMarker(meta.getPersistentDataContainer()));
    }

    public boolean hasMarker(@NonNull final ItemStack itemStack) {
        return itemStack.getItemMeta().getPersistentDataContainer().has(getMarkerKey());
    }

    private void setNodeMarker(final PersistentDataContainer persistentDataContainer) {
        persistentDataContainer.set(getMarkerKey(), PersistentDataType.BYTE, (byte) 1);
    }

    private NamespacedKey getMarkerKey() {
        return getKey("marker");
    }

    private NamespacedKey getKey(final String key) {
        return new NamespacedKey(plugin, key);
    }
}

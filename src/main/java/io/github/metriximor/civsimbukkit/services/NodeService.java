package io.github.metriximor.civsimbukkit.services;

import com.jeff_media.morepersistentdatatypes.DataType;
import io.github.metriximor.civsimbukkit.models.Node;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.logging.Logger;

import static io.github.metriximor.civsimbukkit.services.PersistentDataService.getWagesKey;

@RequiredArgsConstructor
public class NodeService {
    private final Logger logger;
    private final ItemSetService itemSetService;

    private final Set<Block> registeredNodes = new HashSet<>();

    public Optional<Node> getNode(final Block block) {
        return Node.make(block)
                .filter(node -> node.getState().getPersistentDataContainer().has(PersistentDataService.getMarkerKey()));
    }

    public void registerNode(@NonNull final Block block) {
        if (!(block.getState() instanceof TileState state)) {
            logger.severe("Attempted to register %s as a node despite it not being a tile-able block".formatted(block));
            return;
        }
        setNodeMarker(state.getPersistentDataContainer());
        state.update();

        logger.info("Registered Node at %s".formatted(block.getLocation()));
        registeredNodes.add(block);
    }

    public void unregisterNode(@NonNull final Block block) {
        if (!registeredNodes.remove(block)) {
            logger.severe("Failed to remove node from registered nodes: %s".formatted(block));
        }
    }

    public void addWages(@NonNull final Node node, @NonNull final ItemStack wages) {
        if (!itemSetService.isItemSetItemStack(ItemSetService.SetType.WAGES, wages)) {
            return;
        }
        logger.info("Writing wages to node %s".formatted(node));
        // Retrieve wage itemStacks from wages item
        final var wageItems = Objects.requireNonNull(
                wages.getItemMeta().getPersistentDataContainer().get(getWagesKey(),
                        DataType.asArray(new ItemStack[0], DataType.ITEM_STACK)));

        // Add wage itemStacks to node
        final var pdc = node.getState().getPersistentDataContainer();
        pdc.set(getWagesKey(), DataType.ITEM_STACK_ARRAY, wageItems);
    }

    public Optional<ItemStack> takeWages(@NonNull final Node node) {
        final var pdc = node.getState().getPersistentDataContainer();
        if (!pdc.has(getWagesKey())) {
            return Optional.empty();
        }
        final var wages = Objects.requireNonNull(pdc.get(getWagesKey(),
                DataType.asArray(new ItemStack[0], DataType.ITEM_STACK)));
        pdc.remove(getWagesKey());
        logger.info("Removing wages from node %s".formatted(node));
        return Optional.of(itemSetService.createItemSetItemStack(
                ItemSetService.SetType.WAGES,
                Arrays.stream(wages).toList()));
    }

    public void addMarker(@NonNull final ItemStack itemStack) {
        itemStack.editMeta(meta -> setNodeMarker(meta.getPersistentDataContainer()));
    }

    public boolean hasMarker(@NonNull final ItemStack itemStack) {
        return itemStack.getItemMeta().getPersistentDataContainer().has(PersistentDataService.getMarkerKey());
    }

    private void setNodeMarker(final PersistentDataContainer persistentDataContainer) {
        persistentDataContainer.set(PersistentDataService.getMarkerKey(), PersistentDataType.BYTE, (byte) 1);
    }
}

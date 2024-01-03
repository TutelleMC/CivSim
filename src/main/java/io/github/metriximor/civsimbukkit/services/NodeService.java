package io.github.metriximor.civsimbukkit.services;

import com.jeff_media.morepersistentdatatypes.DataType;
import io.github.metriximor.civsimbukkit.models.Node;
import io.github.metriximor.civsimbukkit.repositories.Repository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
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
    private final Repository<Block, Node> nodeRepository;

    public boolean blockIsNotNode(final Block block) {
        return !Node.isNode(block);
    }

    public Optional<Node> registerNode(@NonNull final Block block) {
        final var result = Node.make(block);
        result.ifPresent(node -> {
            logger.info("Registered Node at %s".formatted(block.getLocation()));
            nodeRepository.add(block, node);
        });
        return result;
    }

    public void unregisterNode(@NonNull final Block block) {
        if (!nodeRepository.remove(block)) {
            logger.severe("Failed to remove node from registered nodes: %s".formatted(block));
        }
    }

    public boolean isEnabled(@NonNull final Block block) {
        if (blockIsNotNode(block)) {
            return false;
        }
        final var node = nodeRepository.getById(block);
        if (node == null) {
            return false;
        }
        return node.isEnabled();
    }

    public boolean addWages(@NonNull final Block block, @NonNull final ItemStack wages) {
        if (!itemSetService.isItemSetItemStack(ItemSetService.SetType.WAGES, wages)) {
            return false;
        }
        final var node = nodeRepository.getById(block);
        if (node == null || node.isEnabled()) {
            return false;
        }

        // Retrieve wage itemStacks from wages item
        final var wagesPdc = wages.getItemMeta().getPersistentDataContainer();
        final var wageItems = wagesPdc.get(getWagesKey(), DataType.asList(DataType.ITEM_STACK));
        if (wageItems == null || wageItems.isEmpty()) {
            logger.warning("Wage config %s had no wages defined!".formatted(wages));
            return false;
        }

        logger.info("Writing wages to node %s".formatted(node));
        return node.setWages(wageItems);
    }

    @NonNull
    public Optional<ItemStack> takeWages(@NonNull final Block block) {
        if (blockIsNotNode(block)) {
            return Optional.empty();
        }
        final var node = nodeRepository.getById(block);
        if (node == null || node.isEnabled()) {
            return Optional.empty();
        }

        final var wages = copyWages(block);
        if (wages.isEmpty()) {
            return Optional.empty();
        }
        node.removeWages();
        return wages;
    }

    @NonNull
    public Optional<ItemStack> copyWages(@NonNull final Block block) {
        if (blockIsNotNode(block)) {
            return Optional.empty();
        }
        final var node = nodeRepository.getById(block);
        if (node == null) {
            return Optional.empty();
        }

        final var wageItems = node.getWages();
        return wageItems.map(itemStacks -> itemSetService.createItemSetItemStack(ItemSetService.SetType.WAGES, itemStacks));
    }

    public void toggleNode(@NonNull final Block block) {
        if (blockIsNotNode(block)) {
            return;
        }
        final var node = nodeRepository.getById(block);
        if (node == null) {
            return;
        }

        node.toggle();
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

package io.github.metriximor.civsimbukkit.services.nodes;

import io.github.metriximor.civsimbukkit.models.NodeType;
import io.github.metriximor.civsimbukkit.models.nodes.Node;
import io.github.metriximor.civsimbukkit.models.nodes.NodeBuilder;
import io.github.metriximor.civsimbukkit.repositories.Repository;
import io.github.metriximor.civsimbukkit.services.ItemSetService;
import io.github.metriximor.civsimbukkit.services.PersistentDataService;
import java.util.*;
import java.util.logging.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public abstract class NodeService<T extends Node> {
    @Getter(AccessLevel.PACKAGE)
    private final Logger logger;
    @Getter(AccessLevel.PACKAGE)
    private final ItemSetService itemSetService;
    private final Repository<Block, T> nodeRepository;
    private final NodeType serviceNodeType;

    public boolean blockIsNotNode(final Block block) {
        return !Node.isNode(block);
    }

    @Nullable
    public T getNode(@NonNull final Block block) {
        final T result = nodeRepository.getById(block);
        if (result != null) {
            return result;
        }
        final T node = new NodeBuilder().block(block).type(serviceNodeType).build();
        if (node == null) {
            return null;
        }
        nodeRepository.add(block, node);
        return node;
    }

    public Optional<T> registerNode(@NonNull final Block block) {
        final T result = new NodeBuilder().type(NodeType.WORKABLE).block(block).build();
        if (result == null) {
            return Optional.empty();
        }
        logger.info("Registered Node at %s".formatted(block.getLocation()));
        nodeRepository.add(block, result);
        return Optional.of(result);
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
        final var node = getNode(block);
        if (node == null) {
            return false;
        }
        return node.isEnabled();
    }

    public void toggleNode(@NonNull final Block block) {
        if (blockIsNotNode(block)) {
            return;
        }
        final var node = getNode(block);
        if (node == null) {
            return;
        }

        node.toggle();
        logger.info("Node %s was toggled to %s".formatted(node, node.isEnabled()));
    }

    // TODO move this to ItemSetService and ItemSet object
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

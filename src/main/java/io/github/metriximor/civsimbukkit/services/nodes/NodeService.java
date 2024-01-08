package io.github.metriximor.civsimbukkit.services.nodes;

import io.github.metriximor.civsimbukkit.models.nodes.Node;
import java.util.logging.Logger;
import lombok.NonNull;
import org.bukkit.block.Block;

public interface NodeService<T extends Node> {
    boolean blockIsNotNode(final Block block);

    T getNode(@NonNull final Block block);

    Logger getLogger();
}

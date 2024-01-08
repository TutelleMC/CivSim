package io.github.metriximor.civsimbukkit.models.nodes;

import io.github.metriximor.civsimbukkit.models.NodeType;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;

@NoArgsConstructor
public final class NodeBuilder {
    private NodeType type;
    private Block block;

    @NonNull
    public NodeBuilder block(@NonNull final Block block) {
        if (!(block.getState() instanceof TileState)) {
            return this;
        }
        this.block = block;
        return this;
    }

    @NonNull
    public NodeBuilder type(@NonNull final NodeType type) {
        this.type = type;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Node> T build() {
        if (block == null || type == null) {
            return null;
        }
        final var existingType = Node.tryFindType(block);
        if (existingType != null && type != existingType) {
            return null;
        }

        try {
            switch (type) {
                case WORKABLE -> {
                    return (T) new FarmNode(block, type);
                }
                case SHOP -> {
                    return (T) new ShopNode(block);
                }
            }
        } catch (ClassCastException ignored) {
        }
        return null;
    }
}

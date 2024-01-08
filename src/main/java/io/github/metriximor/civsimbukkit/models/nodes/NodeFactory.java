package io.github.metriximor.civsimbukkit.models.nodes;

import io.github.metriximor.civsimbukkit.models.NodeType;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor
public final class NodeFactory {
    public static <T extends Node> T build(@NonNull final Block block, @NonNull final NodeType type) {
        final var existingType = Node.tryFindType(block);
        if (existingType != null && type != existingType) {
            return null;
        }

        return tryBuild(block, type);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static <T extends Node> T tryBuild(@NotNull Block block, @NotNull NodeType type) {
        try {
            switch (type) {
                case FARM -> {
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

package io.github.metriximor.civsimbukkit.models;

import lombok.NonNull;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;

import java.util.Optional;

public final class Node {
    private final @NonNull Block block;

    private Node(@NonNull Block block) {
        this.block = block;
    }

    public static Optional<Node> make(final Block block) {
        if (block == null || !(block.getState() instanceof TileState)) {
            return Optional.empty();
        }
        return Optional.of(new Node(block));
    }

    public TileState getState() {
        return (TileState) block.getState();
    }
}

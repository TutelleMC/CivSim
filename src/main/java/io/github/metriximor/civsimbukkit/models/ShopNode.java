package io.github.metriximor.civsimbukkit.models;

import lombok.NonNull;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class ShopNode extends AbstractNode {
    ShopNode(@NonNull Block block) {
        super(block, NodeType.SHOP);
    }

    @Override
    public @NotNull Transaction getTransaction() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean perform() {
        throw new UnsupportedOperationException("TODO");
    }
}

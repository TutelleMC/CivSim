package io.github.metriximor.civsimbukkit.models;

import lombok.NonNull;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class ShopNode extends AbstractNode {
    private static final NodeType TYPE = NodeType.SHOP;

    ShopNode(@NonNull Block block) {
        super(block, TYPE);
    }

    @Override
    public @NotNull Transaction getTransaction() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean perform() {
        throw new UnsupportedOperationException("TODO");
    }

    public static ShopNode build(final @NonNull Block block) {
        if (!isOfType(block, TYPE)) {
            return null;
        }
        return new ShopNode(block);
    }
}

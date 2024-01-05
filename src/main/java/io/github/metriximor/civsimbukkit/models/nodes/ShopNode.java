package io.github.metriximor.civsimbukkit.models.nodes;

import io.github.metriximor.civsimbukkit.models.NodeType;
import lombok.NonNull;
import org.bukkit.block.Block;

public class ShopNode extends Node {
    ShopNode(@NonNull Block block) {
        super(block, NodeType.SHOP);
    }
}

package io.github.metriximor.civsimbukkit.models.nodes;

import io.github.metriximor.civsimbukkit.models.NodeType;
import io.github.metriximor.civsimbukkit.models.Transaction;
import java.util.List;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FarmNode extends AbstractNode implements Wages, PolygonalArea {
    FarmNode(@NonNull final Block block, @NonNull final NodeType nodeType) {
        super(block, nodeType);
    }

    @Override
    public @NotNull Transaction getTransaction() {
        return new Transaction(List.of(), List.of(), 0, 0, 0);
    }

    @Override
    public boolean perform() {
        var container = getContainer();
        return container.getInventory().addItem(new ItemStack(Material.WHEAT)).isEmpty();
    }
}

package io.github.metriximor.civsimbukkit.models;

import io.github.metriximor.civsimbukkit.models.nodes.PolygonalArea;
import io.github.metriximor.civsimbukkit.models.nodes.Wages;
import java.util.List;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FarmNode extends AbstractNode implements Wages, PolygonalArea {
    private static final NodeType TYPE = NodeType.FARM;

    FarmNode(@NonNull final Block block) {
        super(block, TYPE);
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

    public static FarmNode build(@NonNull final Block block) {
        if (!isOfType(block, TYPE)) {
            return null;
        }
        return new FarmNode(block);
    }
}

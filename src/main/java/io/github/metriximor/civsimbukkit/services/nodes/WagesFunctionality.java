package io.github.metriximor.civsimbukkit.services.nodes;

import io.github.metriximor.civsimbukkit.models.BillOfMaterials;
import io.github.metriximor.civsimbukkit.models.nodes.Wages;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.block.Block;

public interface WagesFunctionality<T extends Wages> extends NodeService<T> {
    default boolean addWages(@NonNull final Block block, @NonNull final BillOfMaterials bill) {
        final var node = getNode(block);
        if (node == null || node.isEnabled()) {
            return false;
        }

        // Retrieve wage itemStacks from wages item
        if (bill.isEmpty()) {
            getLogger().warning("Wage config %s had no wages defined!".formatted(bill));
            return false;
        }

        getLogger().info("Writing wages to node %s".formatted(node));
        return node.setWages(bill);
    }

    @NonNull
    default Optional<BillOfMaterials> takeWages(@NonNull final Block block) {
        if (blockIsNotNode(block)) {
            return Optional.empty();
        }
        final var node = getNode(block);
        if (node == null || node.isEnabled()) {
            return Optional.empty();
        }

        final var wages = copyWages(block);
        if (wages.isEmpty()) {
            return Optional.empty();
        }
        node.removeWages();
        return wages;
    }

    @NonNull
    default Optional<BillOfMaterials> copyWages(@NonNull final Block block) {
        if (blockIsNotNode(block)) {
            return Optional.empty();
        }
        final var node = getNode(block);
        if (node == null) {
            return Optional.empty();
        }

        return node.getWages();
    }
}

package io.github.metriximor.civsimbukkit.services.nodes;

import io.github.metriximor.civsimbukkit.models.BillOfMaterials;
import io.github.metriximor.civsimbukkit.models.NodeType;
import io.github.metriximor.civsimbukkit.models.nodes.FarmNode;
import io.github.metriximor.civsimbukkit.repositories.Repository;
import io.github.metriximor.civsimbukkit.services.BillOfMaterialsService;
import io.github.metriximor.civsimbukkit.services.SimulationService;
import java.util.Optional;
import java.util.logging.Logger;
import lombok.NonNull;
import org.bukkit.block.Block;

public class WorkableNodeService extends NodeService<FarmNode> {
    public WorkableNodeService(
            final Logger logger,
            final BillOfMaterialsService billOfMaterialsService,
            final Repository<Block, FarmNode> nodeRepository,
            final SimulationService simulationService) {
        super(logger, billOfMaterialsService, nodeRepository, NodeType.WORKABLE, simulationService);
    }

    public boolean addWages(@NonNull final Block block, @NonNull final BillOfMaterials bill) {
        final var node = getNode(block);
        if (node == null || node.isEnabled()) {
            return false;
        }

        // Retrieve wage itemStacks from wages item
        if (bill.isEmpty()) {
            super.getLogger().warning("Wage config %s had no wages defined!".formatted(bill));
            return false;
        }

        super.getLogger().info("Writing wages to node %s".formatted(node));
        return node.setWages(bill);
    }

    @NonNull
    public Optional<BillOfMaterials> takeWages(@NonNull final Block block) {
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
    public Optional<BillOfMaterials> copyWages(@NonNull final Block block) {
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

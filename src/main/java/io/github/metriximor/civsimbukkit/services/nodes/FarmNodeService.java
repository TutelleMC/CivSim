package io.github.metriximor.civsimbukkit.services.nodes;

import io.github.metriximor.civsimbukkit.models.NodeType;
import io.github.metriximor.civsimbukkit.models.nodes.FarmNode;
import io.github.metriximor.civsimbukkit.repositories.Repository;
import io.github.metriximor.civsimbukkit.services.BillOfMaterialsService;
import io.github.metriximor.civsimbukkit.services.SimulationService;
import java.util.logging.Logger;
import org.bukkit.block.Block;

public class FarmNodeService extends AbstractNodeService<FarmNode> implements WagesFunctionality<FarmNode> {
    public FarmNodeService(
            final Logger logger,
            final BillOfMaterialsService billOfMaterialsService,
            final Repository<Block, FarmNode> nodeRepository,
            final SimulationService simulationService) {
        super(logger, billOfMaterialsService, nodeRepository, NodeType.FARM, simulationService);
    }
}

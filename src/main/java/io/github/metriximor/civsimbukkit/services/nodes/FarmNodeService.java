package io.github.metriximor.civsimbukkit.services.nodes;

import io.github.metriximor.civsimbukkit.models.FarmNode;
import io.github.metriximor.civsimbukkit.models.NodeType;
import io.github.metriximor.civsimbukkit.repositories.InMemoryRepository;
import io.github.metriximor.civsimbukkit.repositories.Repository;
import io.github.metriximor.civsimbukkit.services.BillOfMaterialsService;
import io.github.metriximor.civsimbukkit.services.BoundaryMarker;
import io.github.metriximor.civsimbukkit.services.SimulationService;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FarmNodeService extends AbstractNodeService<FarmNode>
        implements WagesFunctionality<FarmNode>, PolygonalAreaFunctionality<FarmNode> {
    private final Repository<Player, List<BoundaryMarker>> boundariesRepo;

    public FarmNodeService(
            final Logger logger,
            final BillOfMaterialsService billOfMaterialsService,
            final Repository<Block, FarmNode> nodeRepository,
            final SimulationService simulationService) {
        super(logger, billOfMaterialsService, nodeRepository, NodeType.FARM, simulationService);
        this.boundariesRepo = new InMemoryRepository<>();
    }

    @Override
    public Repository<Player, List<BoundaryMarker>> getPolygonalAreasRepo() {
        return boundariesRepo;
    }
}

package io.github.metriximor.civsimbukkit.services.nodes;

import io.github.metriximor.civsimbukkit.models.FarmNode;
import io.github.metriximor.civsimbukkit.models.PlacedBoundaryMarker;
import io.github.metriximor.civsimbukkit.models.nodes.PolygonalArea;
import io.github.metriximor.civsimbukkit.repositories.InMemoryRepository;
import io.github.metriximor.civsimbukkit.repositories.Repository;
import io.github.metriximor.civsimbukkit.services.BillOfMaterialsService;
import io.github.metriximor.civsimbukkit.services.ParticleService;
import io.github.metriximor.civsimbukkit.services.SimulationService;
import io.github.metriximor.civsimbukkit.utils.Pair;
import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FarmNodeService extends AbstractNodeService<FarmNode>
        implements WagesFunctionality<FarmNode>, PolygonalAreaFunctionality<FarmNode> {
    @NonNull
    private final Repository<Player, Pair<PolygonalArea, List<PlacedBoundaryMarker>>> boundariesRepo;

    @Getter
    @NonNull
    private final ParticleService particleService;

    public FarmNodeService(
            final @NonNull Logger logger,
            final @NonNull BillOfMaterialsService billOfMaterialsService,
            final @NonNull Repository<Block, FarmNode> nodeRepository,
            final @NonNull SimulationService simulationService,
            final @NonNull ParticleService particleService) {
        super(logger, billOfMaterialsService, nodeRepository, simulationService);
        this.boundariesRepo = new InMemoryRepository<>();
        this.particleService = particleService;
    }

    @Override
    public Repository<Player, Pair<PolygonalArea, List<PlacedBoundaryMarker>>> getPolygonalAreasRepo() {
        return boundariesRepo;
    }

    @Override
    public FarmNode build(@NonNull Block block) {
        return FarmNode.build(block);
    }
}

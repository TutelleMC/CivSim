package io.github.metriximor.civsimbukkit.services;

import static io.github.metriximor.civsimbukkit.models.AbstractNode.isOfType;
import static io.github.metriximor.civsimbukkit.services.nodes.PolygonalAreaFunctionality.MAX_DISTANCE_BETWEEN_MARKERS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.metriximor.civsimbukkit.BukkitTest;
import io.github.metriximor.civsimbukkit.models.*;
import io.github.metriximor.civsimbukkit.models.errors.PlaceBoundaryError;
import io.github.metriximor.civsimbukkit.repositories.InMemoryRepository;
import io.github.metriximor.civsimbukkit.services.nodes.FarmNodeService;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

class FarmNodeServiceTest extends BukkitTest {
    private final Logger logger = mock(Logger.class);
    private final BillOfMaterialsService billOfMaterialsService = mock(BillOfMaterialsService.class);
    private final SimulationService simulationService = mock(SimulationService.class);
    private final InMemoryRepository<Block, FarmNode> nodeRepository = spy(new InMemoryRepository<>());
    private final ParticleService particleService = mock(ParticleService.class);
    private final FarmNodeService farmNodeService =
            new FarmNodeService(logger, billOfMaterialsService, nodeRepository, simulationService, particleService);

    @Test
    void testNodeCreatesSuccessfully() {
        final Block barrel = setupBarrelBlock();
        final FarmNode node = FarmNode.build(barrel);
        assertNotNull(node);
    }

    @Test
    void testBlockIsNotNodeDetectsNonBarrelsCorrectly() {
        final Block barrel = setupBarrelBlock();
        assertTrue(farmNodeService.blockIsNotNode(barrel));
    }

    @Test
    void testBlockIsNotNodeDetectsBarrelsCorrectly() {
        final Block barrel = setupBarrelBlock();
        farmNodeService.registerNode(barrel);
        assertFalse(farmNodeService.blockIsNotNode(barrel));
    }

    @Test
    void testRegisterNodeAddsMarker() {
        final Block barrel = setupBarrelBlock();
        assertTrue(farmNodeService.registerNode(barrel).isPresent());
    }

    @Test
    void testIsEnabledCorrectlyDetectsDisabledBarrels() {
        final Block barrel = setupBarrelBlock();
        assertFalse(farmNodeService.isEnabled(barrel));
        farmNodeService.registerNode(barrel);
        assertFalse(farmNodeService.isEnabled(barrel));
    }

    @Test
    void testGetNodeCanGetBlocksThatAreMissingFromTheRepo() {
        final Block barrel = setupBarrelBlock();
        farmNodeService.registerNode(barrel);
        assertNotNull(farmNodeService.getNode(barrel));
        doReturn(null).when(nodeRepository).getById(barrel);
        assertNotNull(farmNodeService.getNode(barrel));
        verify(nodeRepository, times(2)).add(eq(barrel), any());
    }

    @Test
    void testGetNodeReturnsNullWhenMismatchedTypesAreGotten() {
        final Block barrel = setupBarrelBlock();
        ShopNode.build(barrel);
        assertFalse(isOfType(barrel, NodeType.FARM));
        assertNull(farmNodeService.getNode(barrel));
    }

    @Test
    void testWagesWorkCorrectly() {
        final Block barrel = setupBarrelBlock();
        farmNodeService.registerNode(barrel);
        assertTrue(farmNodeService.copyWages(barrel).isEmpty());
        assertTrue(farmNodeService.takeWages(barrel).isEmpty());

        final var wages = getSampleWages();

        assertTrue(farmNodeService.addWages(barrel, wages));
        assertTrue(farmNodeService.copyWages(barrel).isPresent());
        assertTrue(farmNodeService.takeWages(barrel).isPresent());
        assertTrue(farmNodeService.copyWages(barrel).isEmpty());
    }

    @Test
    void testCantChangeWagesWhenNodeIsEnabled() {
        final Block barrel = setupBarrelBlock();
        farmNodeService.registerNode(barrel);
        final var wages = getSampleWages();

        farmNodeService.toggleNode(barrel);
        assertFalse(farmNodeService.addWages(barrel, wages));
        farmNodeService.toggleNode(barrel);
        assertTrue(farmNodeService.addWages(barrel, wages));
        assertTrue(farmNodeService.copyWages(barrel).isPresent());
        farmNodeService.toggleNode(barrel);
        assertTrue(farmNodeService.takeWages(barrel).isEmpty());
        assertTrue(farmNodeService.copyWages(barrel).isPresent());
        farmNodeService.toggleNode(barrel);
        assertTrue(farmNodeService.takeWages(barrel).isPresent());
        assertTrue(farmNodeService.copyWages(barrel).isEmpty());
    }

    @Test
    void testDefineBoundariesReturnsEmptyWhenBlockIsNotNode() {
        final var block = setupBarrelBlock();
        final var player = setupPlayer();
        assertTrue(farmNodeService.defineBoundaries(player, block).isEmpty());
    }

    @Test
    void testDefineBoundariesReturnsNonEmptyWhenBlockIsNode() {
        final var block = setupBarrelBlock();
        final var player = setupPlayer();
        farmNodeService.registerNode(block);

        assertTrue(farmNodeService.defineBoundaries(player, block).isPresent());
    }

    @Test
    void testPlaceBoundariesReturnsErrorWhenItemIsNotABoundaryMarker() {
        final var player = setupPlayer();
        final var location = new Location(getWorld(), 1, 1, 1);

        assertEquals(
                PlaceBoundaryError.NOT_A_BOUNDARY_MARKER,
                farmNodeService
                        .placeBoundary(player, new ItemStack(Material.ARMOR_STAND), location)
                        .unwrapErr());
    }

    @Test
    void testPlaceBoundariesReturnsErrorWhenPlayerIsNotInBoundaryEditingMode() {
        final var player = setupPlayer();
        final var boundary = new BoundaryMarker(0).getAsArmorStand();

        assertEquals(
                PlaceBoundaryError.NOT_IN_BOUNDARY_EDITING_MODE,
                farmNodeService
                        .placeBoundary(player, boundary, new Location(getWorld(), 1, 1, 1))
                        .unwrapErr());
    }

    @Test
    void testPlaceBoundariesAddsTheFirstBoundarySuccessfully() {
        final var player = setupPlayer();
        final var block = setupBarrelBlock();
        farmNodeService.registerNode(block);
        final var boundaryArmorStand =
                farmNodeService.defineBoundaries(player, block).orElseThrow();

        final var result = farmNodeService.placeBoundary(player, boundaryArmorStand, new Location(getWorld(), 0, 0, 0));

        assertTrue(result.isOk());
        assertNotEquals(boundaryArmorStand, result.unwrap());
    }

    @Test
    void testPlaceBoundariesReturnsErrorWhenBoundariesAreTooFarApart() {
        final var player = setupPlayer();
        final var block = setupBarrelBlock();
        farmNodeService.registerNode(block);
        final var firstBoundary =
                farmNodeService.defineBoundaries(player, block).orElseThrow();
        final var secondBoundary = farmNodeService
                .placeBoundary(player, firstBoundary, new Location(getWorld(), 0, 0, 0))
                .unwrap();

        final var result = farmNodeService.placeBoundary(
                player, secondBoundary, new Location(getWorld(), MAX_DISTANCE_BETWEEN_MARKERS + 1, 0, 0));

        assertTrue(result.isErr());
        assertEquals(PlaceBoundaryError.DISTANCE_TOO_BIG, result.unwrapErr());
    }

    @Test
    void testPlaceBoundariesReturnsErrorWhenBoundariesAreAtTheExactLimit() {
        final var player = setupPlayer();
        final var block = setupBarrelBlock();
        farmNodeService.registerNode(block);
        final var firstBoundary =
                farmNodeService.defineBoundaries(player, block).orElseThrow();
        final var secondBoundary = farmNodeService
                .placeBoundary(player, firstBoundary, new Location(getWorld(), 0, 0, 0))
                .unwrap();

        final var result = farmNodeService.placeBoundary(
                player, secondBoundary, new Location(getWorld(), MAX_DISTANCE_BETWEEN_MARKERS, 0, 0));

        assertTrue(result.isOk());
        assertNotEquals(firstBoundary, result.unwrap());
        assertNotEquals(secondBoundary, result.unwrap());
    }

    @NotNull
    private BillOfMaterials getSampleWages() {
        final var bill = new BillOfMaterials(BillOfMaterialsService.SetType.WAGES);
        bill.add(new ItemStack(Material.IRON_INGOT, 2));
        return bill;
    }
}

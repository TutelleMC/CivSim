package io.github.metriximor.civsimbukkit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.metriximor.civsimbukkit.BukkitTest;
import io.github.metriximor.civsimbukkit.models.BillOfMaterials;
import io.github.metriximor.civsimbukkit.models.NodeType;
import io.github.metriximor.civsimbukkit.models.nodes.FarmNode;
import io.github.metriximor.civsimbukkit.models.nodes.Node;
import io.github.metriximor.civsimbukkit.models.nodes.NodeFactory;
import io.github.metriximor.civsimbukkit.repositories.InMemoryRepository;
import io.github.metriximor.civsimbukkit.services.nodes.FarmNodeService;
import java.util.logging.Logger;
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
    private final FarmNodeService farmNodeService =
            new FarmNodeService(logger, billOfMaterialsService, nodeRepository, simulationService);

    @Test
    void testNodeCreatesSuccessfully() {
        final Block barrel = setupBarrelBlock();
        final Node node = NodeFactory.build(barrel, NodeType.FARM);
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
        NodeFactory.build(barrel, NodeType.SHOP);
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

    @NotNull
    private BillOfMaterials getSampleWages() {
        final var bill = new BillOfMaterials(BillOfMaterialsService.SetType.WAGES);
        bill.add(new ItemStack(Material.IRON_INGOT, 2));
        return bill;
    }
}

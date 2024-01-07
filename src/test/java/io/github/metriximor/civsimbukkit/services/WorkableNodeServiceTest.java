package io.github.metriximor.civsimbukkit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.metriximor.civsimbukkit.BukkitTest;
import io.github.metriximor.civsimbukkit.models.BillOfMaterials;
import io.github.metriximor.civsimbukkit.models.NodeType;
import io.github.metriximor.civsimbukkit.models.nodes.Node;
import io.github.metriximor.civsimbukkit.models.nodes.NodeBuilder;
import io.github.metriximor.civsimbukkit.models.nodes.WorkableNode;
import io.github.metriximor.civsimbukkit.repositories.InMemoryRepository;
import io.github.metriximor.civsimbukkit.services.nodes.WorkableNodeService;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

class WorkableNodeServiceTest extends BukkitTest {
    private final Logger logger = mock(Logger.class);
    private final BillOfMaterialsService billOfMaterialsService = mock(BillOfMaterialsService.class);
    private final SimulationService simulationService = mock(SimulationService.class);
    private final InMemoryRepository<Block, WorkableNode> nodeRepository = spy(new InMemoryRepository<>());
    private final WorkableNodeService workableNodeService =
            new WorkableNodeService(logger, billOfMaterialsService, nodeRepository, simulationService);

    @Test
    void testNodeCreatesSuccessfully() {
        final Block barrel = setupBarrelBlock();
        final Node node =
                new NodeBuilder().type(NodeType.WORKABLE).block(barrel).build();
        assertNotNull(node);
    }

    @Test
    void testBlockIsNotNodeDetectsNonBarrelsCorrectly() {
        final Block barrel = setupBarrelBlock();
        assertTrue(workableNodeService.blockIsNotNode(barrel));
    }

    @Test
    void testBlockIsNotNodeDetectsBarrelsCorrectly() {
        final Block barrel = setupBarrelBlock();
        workableNodeService.registerNode(barrel);
        assertFalse(workableNodeService.blockIsNotNode(barrel));
    }

    @Test
    void testRegisterNodeAddsMarker() {
        final Block barrel = setupBarrelBlock();
        assertTrue(workableNodeService.registerNode(barrel).isPresent());
    }

    @Test
    void testIsEnabledCorrectlyDetectsDisabledBarrels() {
        final Block barrel = setupBarrelBlock();
        assertFalse(workableNodeService.isEnabled(barrel));
        workableNodeService.registerNode(barrel);
        assertFalse(workableNodeService.isEnabled(barrel));
    }

    @Test
    void testGetNodeCanGetBlocksThatAreMissingFromTheRepo() {
        final Block barrel = setupBarrelBlock();
        workableNodeService.registerNode(barrel);
        assertNotNull(workableNodeService.getNode(barrel));
        doReturn(null).when(nodeRepository).getById(barrel);
        assertNotNull(workableNodeService.getNode(barrel));
        verify(nodeRepository, times(2)).add(eq(barrel), any());
    }

    @Test
    void testGetNodeReturnsNullWhenMismatchedTypesAreGotten() {
        final Block barrel = setupBarrelBlock();
        new NodeBuilder().block(barrel).type(NodeType.SHOP).build();
        assertNull(workableNodeService.getNode(barrel));
    }

    @Test
    void testWagesWorkCorrectly() {
        final Block barrel = setupBarrelBlock();
        workableNodeService.registerNode(barrel);
        assertTrue(workableNodeService.copyWages(barrel).isEmpty());
        assertTrue(workableNodeService.takeWages(barrel).isEmpty());

        final var wages = getSampleWages();

        assertTrue(workableNodeService.addWages(barrel, wages));
        assertTrue(workableNodeService.copyWages(barrel).isPresent());
        assertTrue(workableNodeService.takeWages(barrel).isPresent());
        assertTrue(workableNodeService.copyWages(barrel).isEmpty());
    }

    @Test
    void testCantChangeWagesWhenNodeIsEnabled() {
        final Block barrel = setupBarrelBlock();
        workableNodeService.registerNode(barrel);
        final var wages = getSampleWages();

        workableNodeService.toggleNode(barrel);
        assertFalse(workableNodeService.addWages(barrel, wages));
        workableNodeService.toggleNode(barrel);
        assertTrue(workableNodeService.addWages(barrel, wages));
        assertTrue(workableNodeService.copyWages(barrel).isPresent());
        workableNodeService.toggleNode(barrel);
        assertTrue(workableNodeService.takeWages(barrel).isEmpty());
        assertTrue(workableNodeService.copyWages(barrel).isPresent());
        workableNodeService.toggleNode(barrel);
        assertTrue(workableNodeService.takeWages(barrel).isPresent());
        assertTrue(workableNodeService.copyWages(barrel).isEmpty());
    }

    @NotNull
    private BillOfMaterials getSampleWages() {
        final var bill = new BillOfMaterials(BillOfMaterialsService.SetType.WAGES);
        bill.add(new ItemStack(Material.IRON_INGOT, 2));
        return bill;
    }
}

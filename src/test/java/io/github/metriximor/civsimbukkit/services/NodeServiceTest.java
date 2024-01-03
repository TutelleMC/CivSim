package io.github.metriximor.civsimbukkit.services;

import be.seeseemelk.mockbukkit.Coordinate;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import io.github.metriximor.civsimbukkit.models.Node;
import io.github.metriximor.civsimbukkit.repositories.InMemoryNodeRepository;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NodeServiceTest {
    private final Logger logger = mock(Logger.class);
    private final ItemSetService itemSetService = mock(ItemSetService.class);
    private final InMemoryNodeRepository nodeRepository = new InMemoryNodeRepository();
    private final NodeService nodeService = new NodeService(logger, itemSetService, nodeRepository);
    private WorldMock world;

    @BeforeEach
    void setup() {
        ServerMock server = MockBukkit.mock();
        MockBukkit.createMockPlugin("CivSimBukkit");
        this.world = server.addSimpleWorld("world");
    }

    @AfterEach
    void destroy() {
        MockBukkit.unmock();
    }

    @Test
    void testNodeCreatesSuccessfully() {
        final Block barrel = setupBarrelBlock();
        final Optional<Node> node = Node.make(barrel);
        assertTrue(node.isPresent());
    }

    @Test
    void testBlockIsNotNodeDetectsNonBarrelsCorrectly() {
        final Block barrel = setupBarrelBlock();
        assertTrue(nodeService.blockIsNotNode(barrel));
    }

    @Test
    void testBlockIsNotNodeDetectsBarrelsCorrectly() {
        final Block barrel = setupBarrelBlock();
        nodeService.registerNode(barrel);
        assertFalse(nodeService.blockIsNotNode(barrel));
    }

    @Test
    void testRegisterNodeAddsMarker() {
        final Block barrel = setupBarrelBlock();
        assertTrue(nodeService.registerNode(barrel).isPresent());
    }

    @Test
    void testIsEnabledCorrectlyDetectsDisabledBarrels() {
        final Block barrel = setupBarrelBlock();
        assertFalse(nodeService.isEnabled(barrel));
        nodeService.registerNode(barrel);
        assertFalse(nodeService.isEnabled(barrel));
    }

    @Test
    void testWagesWorkCorrectly() {
        final Block barrel = setupBarrelBlock();
        nodeService.registerNode(barrel);
        assertTrue(nodeService.copyWages(barrel).isEmpty());
        assertTrue(nodeService.takeWages(barrel).isEmpty());

        final ItemStack wages = getSampleWages();

        assertTrue(nodeService.addWages(barrel, wages));
        assertTrue(nodeService.copyWages(barrel).isPresent());
        assertTrue(nodeService.takeWages(barrel).isPresent());
        assertTrue(nodeService.copyWages(barrel).isEmpty());
    }

    @Test
    void testCantChangeWagesWhenNodeIsEnabled() {
        final Block barrel = setupBarrelBlock();
        nodeService.registerNode(barrel);
        final ItemStack wages = getSampleWages();

        nodeService.toggleNode(barrel);
        assertFalse(nodeService.addWages(barrel, wages));
        nodeService.toggleNode(barrel);
        assertTrue(nodeService.addWages(barrel, wages));
        assertTrue(nodeService.copyWages(barrel).isPresent());
        nodeService.toggleNode(barrel);
        assertTrue(nodeService.takeWages(barrel).isEmpty());
        assertTrue(nodeService.copyWages(barrel).isPresent());
        nodeService.toggleNode(barrel);
        assertTrue(nodeService.takeWages(barrel).isPresent());
        assertTrue(nodeService.copyWages(barrel).isEmpty());
    }

    @NotNull
    private ItemStack getSampleWages() {
        final List<ItemStack> paymentItems = List.of(new ItemStack(Material.IRON_INGOT, 2));
        when(itemSetService.createItemSetItemStack(any(), anyList())).thenCallRealMethod();
        when(itemSetService.isItemSetItemStack(any(), any())).thenCallRealMethod();
        return itemSetService.createItemSetItemStack(ItemSetService.SetType.WAGES, paymentItems);
    }

    @NotNull
    private Block setupBarrelBlock() {
        final Block barrel = world.createBlock(new Coordinate(0, 0, 0));
        barrel.setType(Material.BARREL);
        return barrel;
    }
}

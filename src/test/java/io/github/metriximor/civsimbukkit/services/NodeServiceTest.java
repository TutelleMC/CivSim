package io.github.metriximor.civsimbukkit.services;

import be.seeseemelk.mockbukkit.Coordinate;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import io.github.metriximor.civsimbukkit.models.Node;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NodeServiceTest {
    private final Logger logger = mock(Logger.class);
    private final ItemSetService itemSetService = mock(ItemSetService.class);
    private final NodeService nodeService = new NodeService(logger, itemSetService);
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
    void testGetNodeReturnsEmptyWhenMarkerIsMissing() {
        final Block barrel = setupBarrelBlock();
        assertTrue(nodeService.getNode(barrel).isEmpty());
    }

    @Test
    void testRegisterNodeAddsMarker() {
        final Block barrel = setupBarrelBlock();
        nodeService.registerNode(barrel);
        assertTrue(nodeService.getNode(barrel).isPresent());
    }

    @Test
    void testWagesWorkCorrectly() {
        final Node node = getNode();
        assertTrue(nodeService.copyWages(node).isEmpty());
        assertTrue(nodeService.takeWages(node).isEmpty());

        final List<ItemStack> paymentItems = getSampleWages();
        final ItemStack wages = itemSetService.createItemSetItemStack(ItemSetService.SetType.WAGES, paymentItems);

        nodeService.addWages(node, wages);
        assertTrue(nodeService.copyWages(node).isPresent());
        assertTrue(nodeService.takeWages(node).isPresent());
        assertTrue(nodeService.copyWages(node).isEmpty());
    }

    @NotNull
    private List<ItemStack> getSampleWages() {
        final List<ItemStack> paymentItems = List.of(new ItemStack(Material.IRON_INGOT, 2));
        when(itemSetService.createItemSetItemStack(any(), anyList())).thenCallRealMethod();
        when(itemSetService.isItemSetItemStack(any(), any())).thenCallRealMethod();
        return paymentItems;
    }

    @NotNull
    private Node getNode() {
        final Block barrel = setupBarrelBlock();
        nodeService.registerNode(barrel);
        return nodeService.getNode(barrel).orElseThrow();
    }

    @NotNull
    private Block setupBarrelBlock() {
        final Block barrel = world.createBlock(new Coordinate(0, 0, 0));
        barrel.setType(Material.BARREL);
        return barrel;
    }
}

package io.github.metriximor.civsimbukkit.models.nodes;

import static org.junit.jupiter.api.Assertions.*;

import io.github.metriximor.civsimbukkit.BukkitTest;
import io.github.metriximor.civsimbukkit.models.BillOfMaterials;
import io.github.metriximor.civsimbukkit.models.NodeType;
import io.github.metriximor.civsimbukkit.services.BillOfMaterialsService;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

class FarmNodeTest extends BukkitTest {
    @Test
    void testNodeConstructsSuccessfully() {
        final var block = setupBarrelBlock();
        assertNotNull(NodeFactory.build(block, NodeType.FARM));
    }

    @Test
    void testWagesWorksCorrectly() {
        final var block = setupBarrelBlock();
        final var node = setupWorkableNode(block);
        final var wages = new BillOfMaterials(BillOfMaterialsService.SetType.WAGES);
        wages.add(new ItemStack(Material.IRON_INGOT));

        assertTrue(node.getWages().isEmpty());
        assertTrue(node.setWages(wages));
        assertTrue(node.getWages().isPresent());
        assertTrue(node.removeWages());
        assertTrue(node.getWages().isEmpty());

        node.toggle();
        node.toggle();
        assertTrue(node.setWages(wages));
        node.toggle();

        assertTrue(node.getWages().isPresent());
        assertFalse(node.setWages(wages));
        assertTrue(node.getWages().isPresent());
        assertFalse(node.removeWages());
        assertTrue(node.getWages().isPresent());
    }

    @Test
    void testIsNodeReturnsFalseWhenItemIsNotANode() {
        final var block = setupBarrelBlock();
        assertFalse(AbstractNode.isNode(block));
    }

    @Test
    void testIsNodeReturnsTrueWhenItIsANode() {
        final var block = setupBarrelBlock();
        setupWorkableNode(block);
        assertTrue(AbstractNode.isNode(block));
    }

    @Test
    void testTryToFindTypeWorksCorrectly() {
        final var block = setupBarrelBlock();

        assertNull(AbstractNode.tryFindType(block));

        setupWorkableNode(block);
        assertNotNull(AbstractNode.tryFindType(block));
    }

    @Test
    void testToggleWorksCorrectly() {
        final var node = setupWorkableNode(setupBarrelBlock());

        assertFalse(node.isEnabled());
        assertTrue(node.toggle());
        assertTrue(node.isEnabled());
        assertFalse(node.toggle());
        assertFalse(node.isEnabled());
    }

    @Test
    void testPerformUpdatesInventoryCorrectly() {
        final var node = setupWorkableNode(setupBarrelBlock());

        assertTrue(node.getContainer().getInventory().isEmpty());
        assertTrue(node.perform(5));
        assertFalse(node.getContainer().getInventory().isEmpty());
    }

    private FarmNode setupWorkableNode(@NonNull final Block block) {
        return NodeFactory.build(block, NodeType.FARM);
    }
}

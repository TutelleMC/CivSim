package io.github.metriximor.civsimbukkit.models;

import static org.junit.jupiter.api.Assertions.*;

import io.github.metriximor.civsimbukkit.BukkitTest;
import io.github.metriximor.civsimbukkit.services.BillOfMaterialsService;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

class BillOfMaterialsTest extends BukkitTest {
    @Test
    void testConstructorWorksFine() {
        assertDoesNotThrow(() -> new BillOfMaterials(BillOfMaterialsService.SetType.WAGES));
    }

    @Test
    void testAddWorksCorrectly() {
        assertTrue(setupBill().add(new ItemStack(Material.IRON_INGOT, 64)));
    }

    @Test
    void testAddAllWorksCorrectly() {
        assertTrue(setupBill().addAll(List.of(new ItemStack(Material.IRON_INGOT, 64))));
        assertFalse(setupBill().addAll(List.of(new ItemStack(Material.DIAMOND_SHOVEL, 10))));

        final var listOfNulls = new ArrayList<ItemStack>();
        listOfNulls.add(null);
        assertFalse(setupBill().addAll(listOfNulls));
    }

    @Test
    void getContentListWorksCorrectly() {
        final var bill = setupBill();
        assertEquals(List.of(), bill.getContentsList());

        bill.addAll(getListOfIron());
        assertEquals(getListOfIron(), bill.getContentsList());
    }

    @Test
    void testDescribeWorksCorrectly() {
        final var bill = setupBill();
        assertEquals(List.of(), bill.describe());

        bill.addAll(getListOfIron());
        assertFalse(bill.describe().isEmpty());
    }

    @Test
    void testGetAsItemWorksCorrectly() {
        final var bill = setupBill();
        bill.addAll(getListOfIron());

        final var itemBill = bill.getAsItem();
        final var billFromItem = BillOfMaterials.fromItemStack(BillOfMaterialsService.SetType.WAGES, itemBill)
                .orElseThrow();
        assertEquals(
                bill.getContentsList(),
                billFromItem.getContentsList().stream()
                        .peek(itemStack -> itemStack.setItemMeta(null))
                        .toList());
    }

    private List<ItemStack> getListOfIron() {
        return List.of(new ItemStack(Material.IRON_INGOT, 64));
    }

    private BillOfMaterials setupBill() {
        return new BillOfMaterials(BillOfMaterialsService.SetType.WAGES);
    }
}

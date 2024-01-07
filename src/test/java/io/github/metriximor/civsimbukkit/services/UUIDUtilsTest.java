package io.github.metriximor.civsimbukkit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import io.github.metriximor.civsimbukkit.utils.UUIDUtils;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

class UUIDUtilsTest {
    record MyRecord(String name, ItemStack itemStack) {}

    @Test
    void testUUIDIsCalculatedAndConsistent() {
        final MyRecord sample = new MyRecord("name", mock(ItemStack.class));
        assertNotNull(UUIDUtils.generateUUID(sample));
        assertEquals(UUIDUtils.generateUUID(sample), UUIDUtils.generateUUID(sample));
    }

    @Test
    void testSimilarButDifferentResultsProduceDifferentKeys() {
        final MyRecord sample = new MyRecord("nAme", mock(ItemStack.class));
        final MyRecord different = new MyRecord("name", mock(ItemStack.class));
        assertNotEquals(sample, different);
    }
}

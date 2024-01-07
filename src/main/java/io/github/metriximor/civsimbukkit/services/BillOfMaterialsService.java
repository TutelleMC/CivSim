package io.github.metriximor.civsimbukkit.services;

import io.github.metriximor.civsimbukkit.models.BillOfMaterials;
import io.github.metriximor.civsimbukkit.utils.NamespacedKeyUtils;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class BillOfMaterialsService {
    public enum SetType {
        INPUT,
        OUTPUT,
        WAGES
    }

    public ItemStack createItemSetItemStack(@NonNull final SetType type, @NonNull final List<ItemStack> items) {
        final var bill = new BillOfMaterials(type);
        final var valid = bill.addAll(items);
        if (!valid) {
            return null;
        }

        return bill.getAsItem();
    }

    public boolean isItemSetItemStack(@NonNull final SetType type, @NonNull final ItemStack itemStack) {
        return itemStack.getItemMeta().getPersistentDataContainer().has(NamespacedKeyUtils.getKey(type.toString()));
    }
}

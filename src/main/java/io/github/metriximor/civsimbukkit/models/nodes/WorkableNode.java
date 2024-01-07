package io.github.metriximor.civsimbukkit.models.nodes;

import static io.github.metriximor.civsimbukkit.utils.NamespacedKeyUtils.getWagesKey;

import com.jeff_media.morepersistentdatatypes.DataType;
import io.github.metriximor.civsimbukkit.models.BillOfMaterials;
import io.github.metriximor.civsimbukkit.models.NodeType;
import io.github.metriximor.civsimbukkit.models.Transaction;
import io.github.metriximor.civsimbukkit.services.BillOfMaterialsService;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WorkableNode extends Node {
    WorkableNode(@NonNull final Block block, @NonNull final NodeType nodeType) {
        super(block, nodeType);
    }

    @Override
    public @NotNull Transaction getTransaction() {
        return new Transaction(List.of(), List.of(), 0, 0, 0);
    }

    @Override
    public boolean perform() {
        var container = getContainer();
        return container.getInventory().addItem(new ItemStack(Material.WHEAT)).isEmpty();
    }

    public boolean setWages(@NonNull final BillOfMaterials wageItems) {
        if (isEnabled()) {
            return false;
        }
        final var state = getState();
        final var pdc = state.getPersistentDataContainer();
        pdc.set(getWagesKey(), DataType.asList(DataType.ITEM_STACK), wageItems.getContentsList());
        state.update();
        return true;
    }

    @NonNull
    public Optional<BillOfMaterials> getWages() {
        final var pdc = getState().getPersistentDataContainer();
        // TODO: Make bill a serializable type to avoid this nonsense
        final var wages = pdc.get(getWagesKey(), DataType.asList(DataType.ITEM_STACK));
        if (wages == null) {
            return Optional.empty();
        }
        final var bill = new BillOfMaterials(BillOfMaterialsService.SetType.WAGES);
        if (!bill.addAll(wages)) {
            return Optional.empty();
        }
        return Optional.of(bill);
    }

    public boolean removeWages() {
        if (isEnabled()) {
            return false;
        }
        final var state = getState();
        state.getPersistentDataContainer().remove(getWagesKey());
        state.update();
        return true;
    }
}

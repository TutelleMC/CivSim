package io.github.metriximor.civsimbukkit.models.nodes;

import static io.github.metriximor.civsimbukkit.services.PersistentDataService.getWagesKey;

import com.jeff_media.morepersistentdatatypes.DataType;
import io.github.metriximor.civsimbukkit.models.NodeType;
import io.github.metriximor.civsimbukkit.models.Transaction;
import java.util.List;
import java.util.Objects;
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
    public void perform() {
        var container = getContainer();
        container.getInventory().addItem(new ItemStack(Material.WHEAT));
        container.update();
    }

    public boolean setWages(@NonNull final List<ItemStack> wageItems) {
        if (isEnabled()) {
            return false;
        }
        final var state = getState();
        final var pdc = state.getPersistentDataContainer();
        pdc.set(getWagesKey(), DataType.asList(DataType.ITEM_STACK), Objects.requireNonNull(wageItems));
        state.update();
        return true;
    }

    @NonNull
    public Optional<List<ItemStack>> getWages() {
        final var pdc = getState().getPersistentDataContainer();
        return Optional.ofNullable(pdc.get(getWagesKey(), DataType.asList(DataType.ITEM_STACK)));
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

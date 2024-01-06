package io.github.metriximor.civsimbukkit.models.nodes;

import static io.github.metriximor.civsimbukkit.services.PersistentDataService.getWagesKey;

import com.jeff_media.morepersistentdatatypes.DataType;
import io.github.metriximor.civsimbukkit.models.NodeType;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class WorkableNode extends Node {
    WorkableNode(@NonNull final Block block, @NonNull final NodeType nodeType) {
        super(block, nodeType);
        setWages(List.of());
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

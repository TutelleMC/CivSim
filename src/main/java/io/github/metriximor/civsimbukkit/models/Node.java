package io.github.metriximor.civsimbukkit.models;

import com.jeff_media.morepersistentdatatypes.DataType;
import io.github.metriximor.civsimbukkit.services.PersistentDataService;
import lombok.NonNull;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static io.github.metriximor.civsimbukkit.services.PersistentDataService.getKey;
import static io.github.metriximor.civsimbukkit.services.PersistentDataService.getWagesKey;

public final class Node {
    private static final NamespacedKey ENABLED_KEY = getKey("enabled");
    private final @NonNull Block block;

    private Node(@NonNull Block block) {
        this.block = block;
    }

    public static Optional<Node> make(final Block block) {
        if (block == null || !(block.getState() instanceof TileState state)) {
            return Optional.empty();
        }
        final var pdc = state.getPersistentDataContainer();
        if (!pdc.has(PersistentDataService.getMarkerKey())) {
            pdc.set(PersistentDataService.getMarkerKey(), PersistentDataType.BYTE, (byte) 1);
            pdc.set(ENABLED_KEY, DataType.BOOLEAN, false);
            state.update();
        }
        return Optional.of(new Node(block));
    }

    public static boolean isNode(final Block block) {
        if (block == null || !(block.getState() instanceof TileState state)) {
            return false;
        }
        final var pdc = state.getPersistentDataContainer();
        return pdc.has(PersistentDataService.getMarkerKey());
    }

    @NonNull
    public TileState getState() {
        return (TileState) block.getState();
    }

    public boolean isEnabled() {
        return Optional.ofNullable(getState().getPersistentDataContainer().get(ENABLED_KEY, DataType.BOOLEAN))
                .orElse(false);
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
        if (!pdc.has(getWagesKey())) {
            return Optional.empty();
        }
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

    public void toggle() {
        final var state = getState();
        final var pdc = state.getPersistentDataContainer();
        final var toggleStatus = isEnabled();
        pdc.set(ENABLED_KEY, DataType.BOOLEAN, !toggleStatus);
        state.update();
    }
}

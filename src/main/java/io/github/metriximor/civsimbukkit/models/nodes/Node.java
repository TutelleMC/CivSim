package io.github.metriximor.civsimbukkit.models.nodes;

import static io.github.metriximor.civsimbukkit.services.PersistentDataService.getKey;
import static io.github.metriximor.civsimbukkit.services.PersistentDataService.getMarkerKey;

import com.jeff_media.morepersistentdatatypes.DataType;
import io.github.metriximor.civsimbukkit.models.NodeType;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;

public abstract class Node {
    private static final NamespacedKey ENABLED_KEY = getKey("enabled");
    private static final NamespacedKey TYPE_KEY = getKey("type");
    private final @NonNull Block block;

    Node(@NonNull final Block block, @NonNull final NodeType nodeType) {
        if (!(block.getState() instanceof TileState state)) {
            throw new IllegalArgumentException();
        }
        final var pdc = state.getPersistentDataContainer();
        if (!pdc.has(getMarkerKey())) {
            pdc.set(getMarkerKey(), DataType.BYTE, (byte) 1);
            pdc.set(ENABLED_KEY, DataType.BOOLEAN, false);
            pdc.set(TYPE_KEY, DataType.asEnum(NodeType.class), nodeType);
            state.update();
        }
        this.block = block;
    }

    public static boolean isNode(final Block block) {
        if (block == null || !(block.getState() instanceof TileState state)) {
            return false;
        }
        final var pdc = state.getPersistentDataContainer();
        return pdc.has(getMarkerKey());
    }

    public static NodeType tryFindType(final Block block) {
        if (!isNode(block)) {
            return null;
        }
        final var state = (TileState) block.getState();
        return state.getPersistentDataContainer().get(TYPE_KEY, DataType.asEnum(NodeType.class));
    }

    @NonNull
    public TileState getState() {
        return (TileState) block.getState();
    }

    public boolean isEnabled() {
        return Optional.ofNullable(getState().getPersistentDataContainer().get(ENABLED_KEY, DataType.BOOLEAN))
                .orElse(false);
    }

    public void toggle() {
        final var state = getState();
        final var pdc = state.getPersistentDataContainer();
        final var toggleStatus = isEnabled();
        pdc.set(ENABLED_KEY, DataType.BOOLEAN, !toggleStatus);
        state.update();
    }
}

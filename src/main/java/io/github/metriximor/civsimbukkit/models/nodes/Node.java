package io.github.metriximor.civsimbukkit.models.nodes;

import static io.github.metriximor.civsimbukkit.services.PersistentDataService.getKey;
import static io.github.metriximor.civsimbukkit.services.PersistentDataService.getMarkerKey;

import com.jeff_media.morepersistentdatatypes.DataType;
import io.github.metriximor.civsimbukkit.models.NodeType;
import io.github.metriximor.civsimbukkit.models.Transaction;
import io.github.metriximor.civsimbukkit.services.UUIDService;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.TileState;
import org.jetbrains.annotations.NotNull;

public abstract class Node {
    private static final NamespacedKey ENABLED_KEY = getKey("enabled");
    private static final NamespacedKey TYPE_KEY = getKey("node_type");
    // private static final NamespacedKey INPUT_KEY = getKey("node_inputs");
    // private static final NamespacedKey OUTPUT_KEY = getKey("output_key");
    private final @NonNull Block block;
    private final @NonNull UUID uuid;

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
        this.uuid = UUIDService.generateUUID(block.getLocation());
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

    public UUID getNodeId() {
        return uuid;
    }

    @NonNull
    public TileState getState() {
        return (TileState) block.getState();
    }

    public Container getContainer() {
        return (Container) block.getState();
    }

    public boolean isEnabled() {
        return Optional.ofNullable(getState().getPersistentDataContainer().get(ENABLED_KEY, DataType.BOOLEAN))
                .orElse(false);
    }

    public boolean toggle() {
        final var state = getState();
        final var pdc = state.getPersistentDataContainer();
        final var toggleStatus = isEnabled();
        pdc.set(ENABLED_KEY, DataType.BOOLEAN, !toggleStatus);
        state.update();
        return !toggleStatus;
    }

    @NotNull
    public abstract Transaction getTransaction();

    public boolean perform(final Integer timesPerformed) {
        var result = false;
        for (int i = 0; i < timesPerformed; i += 1) {
            result = perform();
        }
        return result;
    }

    public abstract boolean perform();
}

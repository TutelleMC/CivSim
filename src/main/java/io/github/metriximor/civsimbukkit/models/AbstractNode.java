package io.github.metriximor.civsimbukkit.models;

import static io.github.metriximor.civsimbukkit.utils.NamespacedKeyUtils.getKey;
import static io.github.metriximor.civsimbukkit.utils.NamespacedKeyUtils.getMarkerKey;

import com.jeff_media.morepersistentdatatypes.DataType;
import io.github.metriximor.civsimbukkit.models.nodes.Node;
import io.github.metriximor.civsimbukkit.utils.UUIDUtils;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.TileState;

public abstract class AbstractNode implements Node {
    private static final NamespacedKey ENABLED_KEY = getKey("enabled");
    private static final NamespacedKey TYPE_KEY = getKey("node_type");
    private final @NonNull Block block;
    private final @NonNull UUID uuid;

    AbstractNode(final @NonNull Block block, final @NonNull NodeType nodeType) {
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
        this.uuid = UUIDUtils.generateUUID(block.getLocation());
    }

    public static boolean isNode(final Block block) {
        if (block == null || !(block.getState() instanceof TileState state)) {
            return false;
        }
        final var pdc = state.getPersistentDataContainer();
        return pdc.has(getMarkerKey());
    }

    public static boolean isTileState(final @NonNull Block block) {
        return block.getState() instanceof TileState;
    }

    public static NodeType tryFindType(final Block block) {
        if (!isNode(block)) {
            return null;
        }
        final var state = (TileState) block.getState();
        return state.getPersistentDataContainer().get(TYPE_KEY, DataType.asEnum(NodeType.class));
    }

    public static boolean isOfType(final @NonNull Block block, final @NonNull NodeType nodeType) {
        if (!isTileState(block)) {
            return false;
        }
        var existingType = tryFindType(block);
        return existingType == null || existingType.equals(nodeType);
    }

    @Override
    public @NonNull UUID getNodeId() {
        return uuid;
    }

    @Override
    public @NonNull TileState getState() {
        return (TileState) block.getState();
    }

    @Override
    public @NonNull Container getContainer() {
        return (Container) block.getState();
    }

    @Override
    public @NonNull Location getLocation() {
        return block.getLocation();
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

    public @NonNull abstract Transaction getTransaction();

    public boolean perform(final Integer timesPerformed) {
        var result = false;
        for (int i = 0; i < timesPerformed; i += 1) {
            result = perform();
        }
        return result;
    }
}

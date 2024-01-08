package io.github.metriximor.civsimbukkit.models.nodes;

import java.util.UUID;
import lombok.NonNull;
import org.bukkit.block.Container;
import org.bukkit.block.TileState;

public interface Node {
    @NonNull
    UUID getNodeId();

    @NonNull
    Container getContainer();

    @NonNull
    TileState getState();

    boolean isEnabled();

    boolean toggle();

    boolean perform();

    boolean perform(final Integer timesPerformed);
}

package io.github.metriximor.civsimbukkit.models.nodes;

import lombok.NonNull;
import org.bukkit.block.TileState;

public interface NodeEssentials {
    @NonNull
    TileState getState();

    boolean isEnabled();
}

package io.github.metriximor.civsimbukkit.services.nodes;

import static io.github.metriximor.civsimbukkit.utils.StringUtils.getSuccessMessage;

import io.github.metriximor.civsimbukkit.models.nodes.PolygonalArea;
import io.github.metriximor.civsimbukkit.repositories.Repository;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface PolygonalAreaFunctionality<T extends PolygonalArea> extends NodeService<T> {
    Repository<Player, List<Location>> getPolygonalAreasRepo();

    default boolean defineBoundaries(@NonNull final Player player, @NonNull final Block block) {
        final PolygonalArea node = getNode(block);
        if (node == null) {
            return false;
        }
        if (node.hasAreaConfigured()) {
            return editBoundaries(player, node);
        }
        player.sendMessage(getSuccessMessage("Place down the boundary marker to start! Type %s/civsim boundary done%s"
                .formatted(ChatColor.ITALIC, ChatColor.RESET)));

        getPolygonalAreasRepo().add(player, new ArrayList<>());
        return true;
    }

    private boolean editBoundaries(@NonNull final Player player, @NonNull final PolygonalArea node) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}

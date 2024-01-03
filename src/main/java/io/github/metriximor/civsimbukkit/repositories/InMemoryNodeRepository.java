package io.github.metriximor.civsimbukkit.repositories;

import io.github.metriximor.civsimbukkit.models.Node;
import lombok.NonNull;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class InMemoryNodeRepository implements Repository<Block, Node> {
    private final Map<Block, Node> repo = new HashMap<>();

    @Nullable
    public Node getById(@NonNull final Block block) {
        if (repo.containsKey(block)) {
            return repo.get(block);
        }
        if (Node.isNode(block)) {
            final var node = Node.make(block).orElseThrow();
            repo.put(block, node);
            return node;
        }
        return null;
    }

    @Override
    public void add(@NonNull Block key, @NonNull Node value) {
        repo.put(key, value);
    }

    @Override
    public boolean remove(@NonNull Block key) {
        return repo.remove(key) != null;
    }
}

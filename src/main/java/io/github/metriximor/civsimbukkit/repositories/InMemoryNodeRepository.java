package io.github.metriximor.civsimbukkit.repositories;

import io.github.metriximor.civsimbukkit.models.Node;
import lombok.NonNull;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryNodeRepository implements Repository<Block, Node> {
    private final Map<Block, Node> repo = new HashMap<>();

    public Optional<Node> getById(@NonNull final Block block) {
        if (repo.containsKey(block)) {
            return Optional.of(repo.get(block));
        }
        if (Node.isNode(block)) {
            final var node = Node.make(block).orElseThrow();
            repo.put(block, node);
            return Optional.of(node);
        }
        return Optional.empty();
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

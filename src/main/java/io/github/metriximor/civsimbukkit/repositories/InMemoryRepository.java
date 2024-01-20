package io.github.metriximor.civsimbukkit.repositories;

import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public class InMemoryRepository<K, V> implements Repository<K, V> {
    private final Map<K, V> repo = new HashMap<>();

    @Nullable
    public V getById(final @NonNull K key) {
        if (repo.containsKey(key)) {
            return repo.get(key);
        }
        return null;
    }

    @Override
    public void add(@NonNull K key, @NonNull V value) {
        repo.put(key, value);
    }

    @Override
    public boolean remove(@NonNull K key) {
        return repo.remove(key) != null;
    }
}

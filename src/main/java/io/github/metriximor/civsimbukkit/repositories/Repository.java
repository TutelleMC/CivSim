package io.github.metriximor.civsimbukkit.repositories;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public interface Repository<K, V> {
    @Nullable
    V getById(final @NonNull K key);

    void add(final @NonNull K key, final @NonNull V value);

    boolean remove(final @NonNull K key);
}

package io.github.metriximor.civsimbukkit.repositories;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public interface Repository<K, V> {
    @Nullable
    V getById(@NonNull final K key);

    void add(@NonNull final K key, @NonNull final V value);

    boolean remove(@NonNull final K key);
}

package io.github.metriximor.civsimbukkit.repositories;

import lombok.NonNull;

import java.util.Optional;

public interface Repository<K, V> {
    Optional<V> getById(@NonNull final K key);

    void add(@NonNull final K key, @NonNull final V value);

    boolean remove(@NonNull final K key);
}

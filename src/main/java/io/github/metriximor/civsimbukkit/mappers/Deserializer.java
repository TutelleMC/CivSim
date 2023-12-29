package io.github.metriximor.civsimbukkit.mappers;

import java.util.Optional;

public interface Deserializer<T> {
    Optional<T> deserialize(final String data);
}

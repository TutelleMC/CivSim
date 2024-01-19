package io.github.metriximor.civsimbukkit.models;

import java.awt.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class PlacedBoundaryMarker {
    private @NonNull final Location location;

    public double distanceToSquared(@NonNull final Location location) {
        return this.location.distanceSquared(location);
    }

    public double distanceToSquared(@NonNull final PlacedBoundaryMarker other) {
        return this.location.distanceSquared(other.location);
    }

    @NonNull
    public Point asPoint2d() {
        return new Point(location.getBlockX(), location.getBlockZ());
    }
}

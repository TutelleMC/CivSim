package io.github.metriximor.civsimbukkit.services;

import static java.lang.Math.round;
import static java.util.stream.IntStream.range;

import com.destroystokyo.paper.ParticleBuilder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleService {
    private final int TICKS_INTERVAL = 5; // TODO configurable

    @NonNull
    private final Map<String, List<ParticleBuilder>> registeredParticles = new ConcurrentHashMap<>();

    public ParticleService(final @NonNull Plugin plugin) {
        new ParticleSpawner().runTaskTimerAsynchronously(plugin, 0, TICKS_INTERVAL);
    }

    public boolean drawLine(
            final @NonNull String key,
            final @NonNull Location start,
            final @NonNull Location end,
            final @NonNull Color color,
            final @NonNull Player... receivers) {
        if (!start.getWorld().equals(end.getWorld())) {
            return false;
        }
        final var world = start.getWorld();
        final int lineLength = (int) round(start.distance(end)); // potentially unsafe cast
        final var particleLocations = range(0, lineLength)
                .mapToObj(i -> {
                    double t = (double) i / lineLength;
                    double x = getMidLinePointSingleAxisCoordinate(start.getX(), end.getX(), t);
                    double y = getMidLinePointSingleAxisCoordinate(start.getY(), end.getY(), t);
                    double z = getMidLinePointSingleAxisCoordinate(start.getZ(), end.getZ(), t);
                    return new Location(world, x, y, z);
                })
                .map(particleLocation -> new ParticleBuilder(Particle.REDSTONE)
                        .location(particleLocation)
                        .color(color)
                        .receivers(receivers))
                .toList();

        if (registeredParticles.containsKey(key)) {
            registeredParticles.get(key).addAll(particleLocations);
        } else {
            registeredParticles.put(key, new ArrayList<>(particleLocations));
        }
        return registeredParticles.containsKey(key);
    }

    public boolean removeAll(final @NonNull String key) {
        return registeredParticles.remove(key) != null;
    }

    private static double getMidLinePointSingleAxisCoordinate(double axisStart, double axisEnd, double t) {
        return axisStart + t * (axisEnd - axisStart);
    }

    private class ParticleSpawner extends BukkitRunnable {
        @Override
        public void run() {
            registeredParticles.values().stream()
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .forEach(ParticleBuilder::spawn);
        }
    }
}

package io.github.metriximor.civsimbukkit.services;

import io.github.metriximor.civsimbukkit.models.nodes.Node;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SimulationService {
    private final Logger logger;
    // private final CommunicationService communicationService;
    private final Plugin plugin;
    // TODO: this is a mock while the call is not implemented
    private final Map<UUID, Node> registeredNodes;

    public SimulationService(final Logger logger, final Plugin plugin) {
        this.logger = logger;
        this.plugin = plugin;
        this.registeredNodes = new HashMap<>();

        new HeartbeatTask().runTaskTimerAsynchronously(plugin, 0, TimeUnit.SECONDS.convert(Duration.ofSeconds(5)));
    }

    private class HeartbeatTask extends BukkitRunnable {
        @Override
        public void run() {
            final var nodesToUpdate = registeredNodes.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> new Random().nextInt(3) + 1));
            new UpdateWorldBasedOnHeartBeatTask(nodesToUpdate).runTask(plugin);
        }
    }

    @RequiredArgsConstructor
    private class UpdateWorldBasedOnHeartBeatTask extends BukkitRunnable {
        private final Map<UUID, Integer> performedTransactions;
        @Override
        public void run() {
            performedTransactions.forEach((id, usedStock) -> {
                final var node = registeredNodes.get(id);
                if (node.perform(usedStock)) {
                    logger.info("Node %s performed %s times".formatted(node, usedStock));
                } else {
                    logger.severe("Failed to perform node %s %s times".formatted(node, usedStock));
                }
            });
        }
    }

    public void registerTransaction(@NonNull final Node node) {
        registeredNodes.put(node.getNodeId(), node);
    }

    public void unregisterTransaction(@NonNull final Node node) {
        registeredNodes.remove(node.getNodeId());
    }

    // public boolean updateTransactionStock(@NonNull final TransactionUpdate
    // transactionUpdate) {
    // throw new UnsupportedOperationException();
    // }
}

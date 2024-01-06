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
    private final Map<UUID, Node> registeredTransactions;
    private final Set<UUID> queueToRemove;
    private final Map<UUID, Node> queueToAdd;

    public SimulationService(final Logger logger, final Plugin plugin) {
        this.logger = logger;
        this.plugin = plugin;
        this.registeredTransactions = new HashMap<>();
        this.queueToAdd = new HashMap<>();
        this.queueToRemove = new HashSet<>();

        new HeartbeatTask().runTaskTimerAsynchronously(plugin, 0, TimeUnit.SECONDS.convert(Duration.ofSeconds(5)));
    }

    private class HeartbeatTask extends BukkitRunnable {
        @Override
        public void run() {
            registeredTransactions.putAll(queueToAdd);
            queueToAdd.clear();

            final var nodesToUpdate = registeredTransactions.entrySet().stream()
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
                final var node = registeredTransactions.get(id);
                if (node == null) {
                    logger.info(
                            "Node %s performed %s times but it was disabled and couldn't".formatted(node, usedStock));
                    return;
                }
                node.perform(usedStock);
            });
            queueToRemove.forEach(registeredTransactions::remove);
            queueToRemove.clear();
        }
    }

    public boolean registerTransaction(@NonNull final Node node) {
        if (registeredTransactions.containsKey(node.getNodeId())) {
            logger.severe(
                    "Attempted to register transaction of node %s that has registered transactions".formatted(node));
            return false;
        }

        queueToAdd.put(node.getNodeId(), node);
        logger.info("Queued up node %s to have the transaction registered".formatted(node));
        return queueToAdd.containsKey(node.getNodeId());
    }

    public boolean unregisterTransaction(@NonNull final Node node) {
        if (queueToAdd.containsKey(node.getNodeId())) {
            queueToAdd.remove(node.getNodeId());
            return true;
        }

        if (!registeredTransactions.containsKey(node.getNodeId())) {
            logger.severe("Attempted to unregister transaction of node %s that has no registered transactions"
                    .formatted(node));
            return false;
        }
        logger.info("Unregistered transaction for node %s".formatted(node));
        return queueToRemove.add(node.getNodeId());
    }

    // public boolean updateTransactionStock(@NonNull final TransactionUpdate
    // transactionUpdate) {
    // throw new UnsupportedOperationException();
    // }
}

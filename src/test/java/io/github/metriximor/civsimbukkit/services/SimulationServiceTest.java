package io.github.metriximor.civsimbukkit.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.github.metriximor.civsimbukkit.BukkitTest;
import io.github.metriximor.civsimbukkit.models.NodeType;
import io.github.metriximor.civsimbukkit.models.nodes.Node;
import io.github.metriximor.civsimbukkit.models.nodes.NodeBuilder;
import java.util.Objects;
import java.util.logging.Logger;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;

class SimulationServiceTest extends BukkitTest {
    private final Logger logger = mock(Logger.class);
    private final Plugin plugin = mock(Plugin.class);

    @Test
    void testTransactionRegistrationWorksCorrectly() {
        SimulationService simulationService = new SimulationService(logger, plugin);

        final var node = setupNode();
        assertFalse(simulationService.unregisterTransaction(node));
        assertTrue(simulationService.registerTransaction(node));
        assertTrue(simulationService.unregisterTransaction(node));
    }

    @NonNull
    private Node setupNode() {
        return Objects.requireNonNull(new NodeBuilder().type(NodeType.WORKABLE).block(setupBarrelBlock()).build());
    }
}
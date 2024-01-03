package io.github.metriximor.civsimbukkit.services;

import io.github.metriximor.civsimbukkit.models.HeartBeat;
import io.github.metriximor.civsimbukkit.models.Transaction;
import io.github.metriximor.civsimbukkit.models.TransactionUpdate;
import java.util.UUID;
import java.util.logging.Logger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;

@RequiredArgsConstructor
public class SimulationService {
    private final Logger logger;
    private final CommunicationService communicationService;

    public void sendHeartBeat(@NonNull final World world) {
        final var worldTicks = world.getFullTime() / 20;

        final HeartBeat heartBeat = new HeartBeat(worldTicks);
        throw new UnsupportedOperationException();
    }

    public boolean registerTransaction(@NonNull final UUID transactionId, @NonNull final Transaction transaction) {
        throw new UnsupportedOperationException();
    }

    public boolean unregisterTransaction(@NonNull final UUID transaction) {
        throw new UnsupportedOperationException();
    }

    public boolean updateTransactionStock(@NonNull final TransactionUpdate transactionUpdate) {
        throw new UnsupportedOperationException();
    }
}

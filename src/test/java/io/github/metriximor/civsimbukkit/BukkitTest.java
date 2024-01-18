package io.github.metriximor.civsimbukkit;

import be.seeseemelk.mockbukkit.Coordinate;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BukkitTest {
    private ServerMock server;

    @Getter
    private WorldMock world;

    @BeforeEach
    void setup() {
        this.server = MockBukkit.mock();
        MockBukkit.createMockPlugin("CivSimBukkit");
        this.world = server.addSimpleWorld("world");
    }

    @AfterEach
    void destroy() {
        MockBukkit.unmock();
    }

    @NotNull
    protected Block setupBarrelBlock() {
        final Block barrel = world.createBlock(new Coordinate(0, 0, 0));
        barrel.setType(Material.BARREL);
        return barrel;
    }

    @NotNull
    protected Block setupBarrelBlock(final int x, final int y, final int z) {
        final Block barrel = world.createBlock(new Coordinate(x, y, z));
        barrel.setType(Material.BARREL);
        return barrel;
    }

    @NotNull
    protected PlayerMock setupPlayer() {
        return server.addPlayer();
    }

    protected void tick(long amountOfTicks) {
        server.getScheduler().performTicks(amountOfTicks);
    }
}

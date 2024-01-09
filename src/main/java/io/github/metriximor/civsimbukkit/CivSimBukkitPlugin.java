package io.github.metriximor.civsimbukkit;

import io.github.metriximor.civsimbukkit.commands.CivSimCommand;
import io.github.metriximor.civsimbukkit.controllers.FarmUIController;
import io.github.metriximor.civsimbukkit.listeners.NodeListener;
import io.github.metriximor.civsimbukkit.models.FarmNode;
import io.github.metriximor.civsimbukkit.repositories.InMemoryRepository;
import io.github.metriximor.civsimbukkit.repositories.Repository;
import io.github.metriximor.civsimbukkit.services.BillOfMaterialsService;
import io.github.metriximor.civsimbukkit.services.CommandsService;
import io.github.metriximor.civsimbukkit.services.SimulationService;
import io.github.metriximor.civsimbukkit.services.nodes.FarmNodeService;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

@SuppressWarnings("unused")
public class CivSimBukkitPlugin extends JavaPlugin {
    private final Logger logger = getLogger();
    private final PluginManager pluginManager = getServer().getPluginManager();

    @Override
    public void onEnable() {
        logger.info("Initializing CivSimBukkit plugin...");
        saveDefaultConfig();

        // Service Instantiation
        final BukkitScheduler scheduler = this.getServer().getScheduler();
        final BillOfMaterialsService billOfMaterialsService = new BillOfMaterialsService();
        final Repository<Block, FarmNode> workableNodeRepository = new InMemoryRepository<>();
        final SimulationService simulationService = new SimulationService(logger, this);
        final FarmNodeService farmNodeService =
                new FarmNodeService(logger, billOfMaterialsService, workableNodeRepository, simulationService);

        final CommandsService commandsService =
                new CommandsService(this, List.of(new CivSimCommand(logger, farmNodeService, billOfMaterialsService)));
        final FarmUIController farmUiController = new FarmUIController(farmNodeService);

        // Register Events
        pluginManager.registerEvents(new NodeListener(farmNodeService, billOfMaterialsService, farmUiController), this);

        logger.info("CivSimBukkit Plugin loaded successfully");
    }

    public static String getVersion() {
        final String version = CivSimBukkitPlugin.class.getPackage().getImplementationVersion();
        return version == null ? "dev" : version;
    }
}

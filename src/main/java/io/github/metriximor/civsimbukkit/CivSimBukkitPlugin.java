package io.github.metriximor.civsimbukkit;

import io.github.metriximor.civsimbukkit.commands.CivSimCommand;
import io.github.metriximor.civsimbukkit.controllers.UIController;
import io.github.metriximor.civsimbukkit.listeners.NodeListener;
import io.github.metriximor.civsimbukkit.models.Node;
import io.github.metriximor.civsimbukkit.repositories.InMemoryNodeRepository;
import io.github.metriximor.civsimbukkit.repositories.Repository;
import io.github.metriximor.civsimbukkit.services.CommandsService;
import io.github.metriximor.civsimbukkit.services.ItemSetService;
import io.github.metriximor.civsimbukkit.services.NodeService;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class CivSimBukkitPlugin extends JavaPlugin {
    private final Logger logger = getLogger();
    private final PluginManager pluginManager = getServer().getPluginManager();

    @Override
    public void onEnable() {
        logger.info("Initializing CivSimBukkit plugin...");
        saveDefaultConfig();

        // Service Instantiation
        final ItemSetService itemSetService = new ItemSetService();
        final Repository<Block, Node> nodeRepository = new InMemoryNodeRepository();
        final NodeService nodeService = new NodeService(logger, itemSetService, nodeRepository);

        final CommandsService commandsService = new CommandsService(this, List.of(
                new CivSimCommand(logger, nodeService, itemSetService)
        ));
        final UIController uiController = new UIController();

        // Register Events
        pluginManager.registerEvents(new NodeListener(nodeService, itemSetService, uiController), this);

        logger.info("CivSimBukkit Plugin loaded successfully");
    }

    public static String getVersion() {
        final String version = CivSimBukkitPlugin.class.getPackage().getImplementationVersion();
        return version == null ? "dev" : version;
    }
}

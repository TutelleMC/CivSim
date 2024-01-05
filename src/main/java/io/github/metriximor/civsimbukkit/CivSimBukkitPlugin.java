package io.github.metriximor.civsimbukkit;

import io.github.metriximor.civsimbukkit.commands.CivSimCommand;
import io.github.metriximor.civsimbukkit.controllers.UIController;
import io.github.metriximor.civsimbukkit.listeners.NodeListener;
import io.github.metriximor.civsimbukkit.models.nodes.WorkableNode;
import io.github.metriximor.civsimbukkit.repositories.InMemoryRepository;
import io.github.metriximor.civsimbukkit.repositories.Repository;
import io.github.metriximor.civsimbukkit.services.CommandsService;
import io.github.metriximor.civsimbukkit.services.ItemSetService;
import io.github.metriximor.civsimbukkit.services.nodes.WorkableNodeService;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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
        final Repository<Block, WorkableNode> workableNodeRepository = new InMemoryRepository<>();
        final WorkableNodeService workableNodeService = new WorkableNodeService(logger, itemSetService,
                workableNodeRepository);

        final CommandsService commandsService = new CommandsService(this,
                List.of(new CivSimCommand(logger, workableNodeService, itemSetService)));
        final UIController uiController = new UIController(workableNodeService);

        // Register Events
        pluginManager.registerEvents(new NodeListener(workableNodeService, itemSetService, uiController), this);

        logger.info("CivSimBukkit Plugin loaded successfully");
    }

    public static String getVersion() {
        final String version = CivSimBukkitPlugin.class.getPackage().getImplementationVersion();
        return version == null ? "dev" : version;
    }
}

package io.github.metriximor.civsimbukkit;

import io.github.metriximor.civsimbukkit.listeners.NodeListener;
import io.github.metriximor.civsimbukkit.services.CommandsService;
import io.github.metriximor.civsimbukkit.services.NodeService;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

@SuppressWarnings("unused")
public final class CivSimBukkitPlugin extends JavaPlugin {
    private final Logger logger = getLogger();
    private final PluginManager pluginManager = getServer().getPluginManager();

    @Override
    public void onEnable() {
        logger.info("Initializing CivSimBukkit plugin...");
        saveDefaultConfig();

        // Service Instantiation
        final NodeService nodeService = new NodeService(logger, this);
        final CommandsService commandsService = new CommandsService(this, nodeService);

        // Register Events
        pluginManager.registerEvents(new NodeListener(nodeService), this);

        logger.info("CivSimBukkit Plugin loaded successfully");
    }

    public static String getVersion() {
        final String version = CivSimBukkitPlugin.class.getPackage().getImplementationVersion();
        return version == null ? "dev" : version;
    }
}

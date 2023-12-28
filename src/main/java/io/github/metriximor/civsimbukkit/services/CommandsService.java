package io.github.metriximor.civsimbukkit.services;

import co.aikar.commands.PaperCommandManager;
import io.github.metriximor.civsimbukkit.commands.CivSimCommand;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;

public class CommandsService {
    private final PaperCommandManager paperCommandManager;

    public CommandsService(@NonNull final Plugin plugin,
                           @NonNull final NodeService nodeService) {
        this.paperCommandManager = new PaperCommandManager(plugin);

        paperCommandManager.enableUnstableAPI("help");

        paperCommandManager.registerCommand(new CivSimCommand(plugin, nodeService));
    }
}

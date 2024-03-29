package io.github.metriximor.civsimbukkit.services;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import java.util.List;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;

public class CommandsService {
    private final PaperCommandManager paperCommandManager;

    public CommandsService(final @NonNull Plugin plugin, final @NonNull List<BaseCommand> commands) {
        this.paperCommandManager = new PaperCommandManager(plugin);

        paperCommandManager.enableUnstableAPI("help");

        commands.forEach(paperCommandManager::registerCommand);
    }
}

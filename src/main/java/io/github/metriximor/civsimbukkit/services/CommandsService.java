package io.github.metriximor.civsimbukkit.services;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class CommandsService {
    private final PaperCommandManager paperCommandManager;

    public CommandsService(@NonNull final Plugin plugin,
                           @NonNull final List<BaseCommand> commands) {
        this.paperCommandManager = new PaperCommandManager(plugin);

        paperCommandManager.enableUnstableAPI("help");

        commands.forEach(paperCommandManager::registerCommand);
    }
}

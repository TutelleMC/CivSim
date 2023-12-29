package io.github.metriximor.civsimbukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import io.github.metriximor.civsimbukkit.CivSimBukkitPlugin;
import io.github.metriximor.civsimbukkit.models.Nodes;
import io.github.metriximor.civsimbukkit.services.NodeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

@CommandAlias("civsim|csim")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CivSimCommand extends BaseCommand {
    @NonNull
    private Plugin plugin;
    @NonNull
    private NodeService nodeService;

    @Subcommand("version")
    @Description("The version command displays the currently installed version of CivSim")
    public void onInfo(@NonNull final CommandSender sender) {
        sender.sendMessage("Version: %s, Author: Metriximor".formatted(CivSimBukkitPlugin.getVersion()));
    }

    @Subcommand("buy")
    @Description("TODO") //TODO finish this
    public void onBuy(@NonNull final Player player, @NonNull final Nodes node) {
        final var farmItem = new ItemStack(Material.BARREL, 1);

        nodeService.addMarker(farmItem);

        // Change the name
        final var displayName = Component.text("Farm").color(NamedTextColor.DARK_GREEN);
        farmItem.editMeta(meta -> meta.displayName(displayName));

        // Add item to player
        final var droppedItem = player.getInventory().addItem(farmItem);
        if (!droppedItem.isEmpty()) { // drop around the player if he can't fit it in inv
            droppedItem.forEach((idx, item) -> player.getWorld().dropItem(player.getLocation(), item));
        }

        player.sendMessage("You bought a farm: %s".formatted(farmItem));
    }

    @HelpCommand
    public static void onHelp(final CommandSender sender, final CommandHelp help) {
        help.showHelp();
    }
}

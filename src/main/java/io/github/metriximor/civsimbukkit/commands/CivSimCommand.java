package io.github.metriximor.civsimbukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import io.github.metriximor.civsimbukkit.CivSimBukkitPlugin;
import io.github.metriximor.civsimbukkit.models.Node;
import io.github.metriximor.civsimbukkit.models.Nodes;
import io.github.metriximor.civsimbukkit.services.ItemSetService;
import io.github.metriximor.civsimbukkit.services.NodeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Logger;

import static io.github.metriximor.civsimbukkit.services.ItemSetService.SetType.WAGES;

@CommandAlias("civsim|csim")
@RequiredArgsConstructor
public class CivSimCommand extends BaseCommand {
    private final Logger logger;
    private final NodeService nodeService;
    private final ItemSetService itemSetService;

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

        giveItemToPlayer(player, farmItem);

        logger.info("%s bought a farm".formatted(player.getName()));
        player.sendMessage("You bought a farm: %s".formatted(farmItem));
    }

    @Subcommand("wages")
    @Description("Wages are what specify how agents are paid when they complete a task.\n" +
            "Farms, Factories and other buildings are configured with these.")
    public class WagesClass extends BaseCommand {
        @Subcommand("new")
        public void onCreate(@NonNull final Player player, @NonNull Material material, int quantity) {
            logger.info("%s created a wages object".formatted(player.getName()));
            final var requiredItem = List.of(new ItemStack(material, quantity));
            giveItemToPlayer(player, itemSetService.createItemSetItemStack(WAGES, requiredItem));
        }

        @Subcommand("get")
        public void onGet(@NonNull final Player player) {
            final Block blockLookedAt = player.getTargetBlock(10);
            final var node = Node.make(blockLookedAt);
            if (node.isEmpty()) {
                player.sendMessage("You must be looking at a workable building block");
                return;
            }

            final var wages = nodeService.takeWages(node.get());
            if (wages.isEmpty()) {
                player.sendMessage("Node doesn't have wages");
                return;
            }
            giveItemToPlayer(player, wages.get());
        }
    }

    @HelpCommand
    public static void onHelp(final CommandSender sender, final CommandHelp help) {
        help.showHelp();
    }

    private static void giveItemToPlayer(@NotNull final Player player, final ItemStack farmItem) {
        // Add item to player
        final var droppedItem = player.getInventory().addItem(farmItem);
        if (!droppedItem.isEmpty()) { // drop around the player if he can't fit it in inv
            droppedItem.forEach((idx, item) -> player.getWorld().dropItem(player.getLocation(), item));
        }
    }
}

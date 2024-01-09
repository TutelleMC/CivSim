package io.github.metriximor.civsimbukkit.commands;

import static io.github.metriximor.civsimbukkit.services.BillOfMaterialsService.SetType.WAGES;
import static io.github.metriximor.civsimbukkit.utils.PlayerInteractionUtils.giveItemToPlayer;
import static io.github.metriximor.civsimbukkit.utils.StringUtils.getFailMessage;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import io.github.metriximor.civsimbukkit.CivSimBukkitPlugin;
import io.github.metriximor.civsimbukkit.models.BillOfMaterials;
import io.github.metriximor.civsimbukkit.models.NodeType;
import io.github.metriximor.civsimbukkit.services.BillOfMaterialsService;
import io.github.metriximor.civsimbukkit.services.nodes.FarmNodeService;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("civsim|csim|cim")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CivSimCommand extends BaseCommand {
    private final Logger logger;
    private final FarmNodeService farmNodeService;
    private final BillOfMaterialsService billOfMaterialsService;

    @Subcommand("version")
    @Description("The version command displays the currently installed version of CivSim")
    public void onInfo(@NonNull final CommandSender sender) {
        sender.sendMessage("Version: %s, Author: Metriximor".formatted(CivSimBukkitPlugin.getVersion()));
    }

    @Subcommand("buy")
    @Description("TODO") // TODO finish this
    public void onBuy(@NonNull final Player player, @NonNull final NodeType node) {
        final var farmItem = new ItemStack(Material.BARREL, 1);

        farmNodeService.addMarker(farmItem);

        // Change the name
        final var displayName = Component.text("Farm").color(NamedTextColor.DARK_GREEN);
        farmItem.editMeta(meta -> meta.displayName(displayName));

        giveItemToPlayer(player, farmItem);

        logger.info("%s bought a farm".formatted(player.getName()));
        player.sendMessage("You bought a farm: %s".formatted(farmItem));
    }

    @Subcommand("wages")
    @Description("Wages are what specify how agents are paid when they complete a task.\n"
            + "Farms, Factories and other buildings are configured with these")
    public class WagesClass extends BaseCommand {
        @Subcommand("new")
        @Description("Configure wages to be paid on a workable block")
        public void onCreate(@NonNull final Player player, @NonNull Material material, int quantity) {
            final var requiredItem = List.of(new ItemStack(material, quantity));
            final var wagesItem = billOfMaterialsService.createItemSetItemStack(WAGES, requiredItem);
            if (wagesItem == null) {
                player.sendMessage(getFailMessage("Couldn't create wages object"));
                return;
            }
            giveItemToPlayer(player, wagesItem);
            logger.info("%s created a wages object".formatted(player.getName()));
        }

        @Subcommand("remove")
        @Description("Removes the wages from the block that is being looked at")
        public void onGet(@NonNull final Player player) {
            getWagesFromNode(player, farmNodeService::takeWages);
        }

        @Subcommand("copy")
        @Description("Copies the wages from the block that is being looked at without erasing the block's wages")
        public void onCopy(@NonNull final Player player) {
            getWagesFromNode(player, farmNodeService::copyWages);
        }

        private void getWagesFromNode(final Player player, Function<Block, Optional<BillOfMaterials>> action) {
            final Block blockLookedAt = player.getTargetBlock(10);
            if (farmNodeService.blockIsNotNode(blockLookedAt)) {
                player.sendMessage(getFailMessage("You must be looking at a workable building block"));
                return;
            }

            final var wages = action.apply(blockLookedAt);
            if (wages.isEmpty()) {
                player.sendMessage(getFailMessage("Node doesn't have wages"));
                return;
            }
            giveItemToPlayer(player, wages.map(BillOfMaterials::getAsItem).get());
        }
    }

    @HelpCommand
    public static void onHelp(final CommandSender sender, final CommandHelp help) {
        help.showHelp();
    }
}

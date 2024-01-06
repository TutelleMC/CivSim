package io.github.metriximor.civsimbukkit.services.nodes;

import static io.github.metriximor.civsimbukkit.services.PersistentDataService.getWagesKey;

import com.jeff_media.morepersistentdatatypes.DataType;
import io.github.metriximor.civsimbukkit.models.NodeType;
import io.github.metriximor.civsimbukkit.models.nodes.WorkableNode;
import io.github.metriximor.civsimbukkit.repositories.Repository;
import io.github.metriximor.civsimbukkit.services.ItemSetService;
import java.util.Optional;
import java.util.logging.Logger;
import lombok.NonNull;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class WorkableNodeService extends NodeService<WorkableNode> {
    public WorkableNodeService(final Logger logger, final ItemSetService itemSetService,
            final Repository<Block, WorkableNode> nodeRepository) {
        super(logger, itemSetService, nodeRepository, NodeType.WORKABLE);
    }

    public boolean addWages(@NonNull final Block block, @NonNull final ItemStack wages) {
        if (!super.getItemSetService().isItemSetItemStack(ItemSetService.SetType.WAGES, wages)) {
            return false;
        }
        final var node = getNode(block);
        if (node == null || node.isEnabled()) {
            return false;
        }

        // Retrieve wage itemStacks from wages item
        final var wagesPdc = wages.getItemMeta().getPersistentDataContainer();
        final var wageItems = wagesPdc.get(getWagesKey(), DataType.asList(DataType.ITEM_STACK));
        if (wageItems == null || wageItems.isEmpty()) {
            super.getLogger().warning("Wage config %s had no wages defined!".formatted(wages));
            return false;
        }

        super.getLogger().info("Writing wages to node %s".formatted(node));
        return node.setWages(wageItems);
    }

    @NonNull
    public Optional<ItemStack> takeWages(@NonNull final Block block) {
        if (blockIsNotNode(block)) {
            return Optional.empty();
        }
        final var node = getNode(block);
        if (node == null || node.isEnabled()) {
            return Optional.empty();
        }

        final var wages = copyWages(block);
        if (wages.isEmpty()) {
            return Optional.empty();
        }
        node.removeWages();
        return wages;
    }

    @NonNull
    public Optional<ItemStack> copyWages(@NonNull final Block block) {
        if (blockIsNotNode(block)) {
            return Optional.empty();
        }
        final var node = getNode(block);
        if (node == null) {
            return Optional.empty();
        }

        final var wageItems = node.getWages();
        return wageItems.map(itemStacks -> super.getItemSetService()
                .createItemSetItemStack(ItemSetService.SetType.WAGES, itemStacks));
    }
}

package io.github.metriximor.civsimbukkit.services;

import com.jeff_media.morepersistentdatatypes.DataType;
import io.github.metriximor.civsimbukkit.utils.StringUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ItemSetService {
    public enum SetType {
        INPUT,
        OUTPUT,
        WAGES
    }

    public ItemStack createItemSetItemStack(@NonNull final SetType type,
                                            @NonNull final List<ItemStack> items) {
        final var wagesItemStack = new ItemStack(Material.PAPER);
        final var typeName = type.toString();

        wagesItemStack.editMeta(meta ->
                meta.displayName(Component.text(StringUtils.convertToTitleCase(typeName))
                        .decorate(TextDecoration.UNDERLINED)
                        .decorate(TextDecoration.BOLD)));

        wagesItemStack.editMeta(meta ->
                meta.getPersistentDataContainer().set(
                        PersistentDataService.getKey(typeName),
                        DataType.asList(DataType.ITEM_STACK),
                        items
                )
        );
        final List<Component> lore = new ArrayList<>(items.stream().map(itemStack -> {
                    final var itemMetaDisplayName = itemStack.getItemMeta().displayName();
                    final var displayName = itemMetaDisplayName == null ? itemStack.displayName() : itemMetaDisplayName;
                    final String name = PlainTextComponentSerializer.plainText().serialize(displayName);
                    return (Component) Component.text("%s %s".formatted(itemStack.getAmount(), name));
                })
                .toList());
        lore.add(Component.text("To use me, left click me into a workable building").decorate(TextDecoration.ITALIC));
        wagesItemStack.lore(lore);

        return wagesItemStack;
    }

    public boolean isItemSetItemStack(@NonNull final SetType type,
                                      @NonNull final ItemStack itemStack) {
        return itemStack.getItemMeta().getPersistentDataContainer().has(PersistentDataService.getKey(type.toString()));
    }
}
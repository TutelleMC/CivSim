package io.github.metriximor.civsimbukkit.models;

import static io.github.metriximor.civsimbukkit.utils.NamespacedKeyUtils.getKey;
import static io.github.metriximor.civsimbukkit.utils.NamespacedKeyUtils.getMarkerKey;
import static io.github.metriximor.civsimbukkit.utils.StringUtils.convertToTitleCaseSplitting;

import com.jeff_media.morepersistentdatatypes.DataType;
import io.github.metriximor.civsimbukkit.services.BillOfMaterialsService;
import io.github.metriximor.civsimbukkit.utils.StringUtils;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class BillOfMaterials {
    private final BillOfMaterialsService.SetType type;
    private final Inventory bill = Bukkit.createInventory(null, 9);

    @NonNull
    public static Optional<BillOfMaterials> fromItemstack(
            final BillOfMaterialsService.SetType type, final ItemStack itemStack) {
        final var bill = new BillOfMaterials(type);
        final var pdc = itemStack.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(bill.getListKey()) || !pdc.has(getMarkerKey())) {
            return Optional.empty();
        }
        final var contents = pdc.get(bill.getListKey(), DataType.asList(DataType.ITEM_STACK));
        if (contents == null) {
            return Optional.empty();
        }
        final var valid = bill.addAll(contents);
        if (!valid) {
            return Optional.empty();
        }
        return Optional.of(bill);
    }

    public boolean addAll(@NonNull final List<ItemStack> list) {
        return list.stream().allMatch(this::add);
    }

    public boolean add(@NonNull final ItemStack item) {
        return bill.addItem(item).isEmpty();
    }

    public boolean isEmpty() {
        return bill.isEmpty();
    }

    @NonNull
    public List<ItemStack> getContentsList() {
        return getContents().toList();
    }

    @NonNull
    public List<Component> describe() {
        return getContents().map(BillOfMaterials::describeQuantityAndNameOf).toList();
    }

    @NonNull
    public ItemStack getAsItem() {
        final var wagesItemStack = new ItemStack(Material.PAPER);
        final var typeName = type.toString();

        wagesItemStack.editMeta(meta -> meta.displayName(Component.text(StringUtils.convertToTitleCase(typeName))
                .decorate(TextDecoration.UNDERLINED)
                .decorate(TextDecoration.BOLD)));
        wagesItemStack.editMeta(meta -> meta.getPersistentDataContainer()
                .set(
                        getListKey(),
                        DataType.asList(DataType.ITEM_STACK),
                        getContents().toList()));
        wagesItemStack.editMeta(meta -> meta.getPersistentDataContainer().set(getMarkerKey(), DataType.BYTE, (byte) 0));

        final var lore = describe();
        lore.add(Component.text("To use me, left click me into a workable building")
                .decorate(TextDecoration.ITALIC));

        wagesItemStack.lore(lore);
        return wagesItemStack;
    }

    @NonNull
    private static Component describeQuantityAndNameOf(ItemStack itemStack) {
        final String name = convertToTitleCaseSplitting(itemStack.getType().toString(), "_");
        return Component.text("%s - %s".formatted(itemStack.getAmount(), name));
    }

    @NonNull
    private Stream<ItemStack> getContents() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(bill.iterator(), Spliterator.ORDERED), false);
    }

    @NonNull
    private NamespacedKey getListKey() {
        return getKey(type.toString());
    }
}

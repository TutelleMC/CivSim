package io.github.metriximor.civsimbukkit.models;

import static io.github.metriximor.civsimbukkit.utils.NamespacedKeyUtils.getKey;
import static io.github.metriximor.civsimbukkit.utils.NamespacedKeyUtils.getMarkerKey;

import com.jeff_media.morepersistentdatatypes.DataType;
import java.util.Optional;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class BoundaryMarker {
    private static final NamespacedKey INDEX_KEY = getKey("boundary_index");

    private final int index;

    public BoundaryMarker(final int index) {
        this.index = index;
    }

    public static boolean isBoundaryMarker(@NonNull final ItemStack boundaryMarker) {
        if (boundaryMarker.getType() != Material.ARMOR_STAND) {
            return false;
        }
        final var meta = boundaryMarker.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(getMarkerKey())
                && meta.getPersistentDataContainer().has(INDEX_KEY);
    }

    public static Optional<Integer> getIndexFromItemStack(@NonNull final ItemStack boundaryMarker) {
        if (!isBoundaryMarker(boundaryMarker)) {
            return Optional.empty();
        }
        return Optional.ofNullable(
                boundaryMarker.getItemMeta().getPersistentDataContainer().get(INDEX_KEY, DataType.INTEGER));
    }

    public PlacedBoundaryMarker place(@NonNull final Location location) {
        return new PlacedBoundaryMarker(location);
    }

    public ItemStack getAsArmorStand() {
        var armorStand = new ItemStack(Material.ARMOR_STAND);
        armorStand.editMeta(
                meta -> meta.displayName(Component.text("Boundary Marker").color(NamedTextColor.DARK_PURPLE)));
        armorStand.editMeta(meta -> meta.getPersistentDataContainer().set(getMarkerKey(), DataType.BYTE, (byte) 0));
        armorStand.editMeta(meta -> meta.getPersistentDataContainer().set(INDEX_KEY, DataType.INTEGER, index));
        return armorStand;
    }
}

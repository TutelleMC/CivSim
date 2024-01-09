package io.github.metriximor.civsimbukkit.services;

import static io.github.metriximor.civsimbukkit.utils.NamespacedKeyUtils.getKey;
import static io.github.metriximor.civsimbukkit.utils.NamespacedKeyUtils.getMarkerKey;

import com.jeff_media.morepersistentdatatypes.DataType;
import io.github.metriximor.civsimbukkit.models.nodes.PolygonalArea;
import io.github.metriximor.civsimbukkit.utils.UUIDUtils;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class BoundaryMarker {
    private static final NamespacedKey INDEX_KEY = getKey("boundary_index");

    @NonNull
    private final UUID id;

    private final int index;
    private Location location;
    private boolean placed = false;

    public BoundaryMarker(@NonNull final PolygonalArea node, final int index) {
        this.id = UUIDUtils.generateUUID(List.of(node, index));
        this.index = index;
    }

    public boolean place(@NonNull final Location location) {
        this.location = location;
        this.placed = true;
        return placed;
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

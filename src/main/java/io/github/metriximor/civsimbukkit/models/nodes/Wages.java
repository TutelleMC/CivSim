package io.github.metriximor.civsimbukkit.models.nodes;

import static io.github.metriximor.civsimbukkit.utils.NamespacedKeyUtils.getKey;

import com.jeff_media.morepersistentdatatypes.DataType;
import io.github.metriximor.civsimbukkit.models.BillOfMaterials;
import io.github.metriximor.civsimbukkit.services.BillOfMaterialsService;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.NamespacedKey;

public interface Wages extends NodeEssentials {
    NamespacedKey WAGES_KEY = getKey(BillOfMaterialsService.SetType.WAGES.toString());

    default boolean setWages(@NonNull final BillOfMaterials wageItems) {
        if (isEnabled()) {
            return false;
        }
        final var state = getState();
        final var pdc = state.getPersistentDataContainer();
        pdc.set(WAGES_KEY, DataType.asList(DataType.ITEM_STACK), wageItems.getContentsList());
        state.update();
        return true;
    }

    @NonNull
    default Optional<BillOfMaterials> getWages() {
        final var pdc = getState().getPersistentDataContainer();
        final var wages = pdc.get(WAGES_KEY, DataType.asList(DataType.ITEM_STACK));
        if (wages == null) {
            return Optional.empty();
        }
        final var bill = new BillOfMaterials(BillOfMaterialsService.SetType.WAGES);
        if (!bill.addAll(wages)) {
            return Optional.empty();
        }
        return Optional.of(bill);
    }

    default boolean removeWages() {
        if (isEnabled()) {
            return false;
        }
        final var state = getState();
        state.getPersistentDataContainer().remove(WAGES_KEY);
        state.update();
        return true;
    }
}

package io.github.metriximor.civsimbukkit.gui;

import io.github.metriximor.civsimbukkit.models.BillOfMaterials;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;

public class WagesItem extends SimpleItem {
    public WagesItem(final BillOfMaterials wagesBill) {
        super(new ItemBuilder(Material.PAPER)
                .addLoreLines(getLore(wagesBill))
                .setDisplayName("%sWages".formatted(ChatColor.DARK_PURPLE)));
    }

    private static List<ComponentWrapper> getLore(final BillOfMaterials wagesBill) {
        return Optional.ofNullable(wagesBill)
                .map(bill -> bill.describe().stream()
                        .map(component -> component.decorate(TextDecoration.ITALIC))
                        .toList())
                .orElse(List.of(Component.text("No wages configured!").color(NamedTextColor.RED)))
                .stream()
                .map(component -> (ComponentWrapper) new AdventureComponentWrapper(component))
                .toList();
    }
}

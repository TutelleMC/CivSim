package io.github.metriximor.civsimbukkit.guis;

import com.mattmx.ktgui.components.button.ButtonClickedEvent;
import com.mattmx.ktgui.components.button.GuiToggleButton;
import com.mattmx.ktgui.components.screen.GuiScreen;
import com.mattmx.ktgui.item.ItemBuilder;
import kotlin.Unit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

public class FarmGUI extends GuiScreen {
    public FarmGUI() {
        final var enabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("%sEnabled".formatted(ChatColor.GREEN));
        final var disabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("%sDisabled".formatted(ChatColor.RED));
        new GuiToggleButton(enabled.make(), disabled.make())
                .onChange(FarmGUI::onToggle)
                .slot(0)
                .childOf(this);

        setTitle("%sFarm UI".formatted(ChatColor.DARK_GREEN));
        setRows(1);
        setType(InventoryType.CHEST);
    }

    private static Unit onToggle(ButtonClickedEvent e) {
        final var name = ((GuiToggleButton) e.getButton()).enabled() ? "enabled" : "disabled";
        e.getPlayer().sendMessage("You just %s the node!".formatted(name));
        return null;
    }
}

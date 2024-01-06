package io.github.metriximor.civsimbukkit.models;

import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record Transaction(@NotNull List<ItemStack> inputs, @NotNull List<ItemStack> outputs, int stock,
        float energyCost, float happinessReduction) {

}

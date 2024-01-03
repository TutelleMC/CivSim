package io.github.metriximor.civsimbukkit.services;

import io.github.metriximor.civsimbukkit.CivSimBukkitPlugin;
import java.util.Locale;
import org.bukkit.NamespacedKey;

public class PersistentDataService {
	public static NamespacedKey getMarkerKey() {
		return getKey("marker");
	}

	public static NamespacedKey getWagesKey() {
		return getKey(ItemSetService.SetType.WAGES.toString());
	}

	public static NamespacedKey getKey(final String key) {
		try {
			final var plugin = CivSimBukkitPlugin.getPlugin(CivSimBukkitPlugin.class);
			return new NamespacedKey(plugin, key);
		} catch (IllegalArgumentException e) {
			final var plugin = "CivSimBukkit".toLowerCase(Locale.ROOT);
			return new NamespacedKey(plugin, key.toLowerCase(Locale.ROOT));
		}
	}
}

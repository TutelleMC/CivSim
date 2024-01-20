package io.github.metriximor.civsimbukkit.utils;

import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.bukkit.ChatColor;

public class StringUtils {
    public static String convertToTitleCase(final @NonNull String text) {
        return convertToTitleCaseSplitting(text, " ");
    }

    public static String convertToTitleCaseSplitting(final @NonNull String text, final @NonNull String separator) {
        if (text.isBlank()) {
            return text;
        }

        return Arrays.stream(text.split(separator))
                .map(word -> word.isEmpty()
                        ? word
                        : Character.toTitleCase(word.charAt(0))
                                + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    public static String getSuccessMessage(final @NonNull String text) {
        return "%s%s".formatted(ChatColor.GREEN, text);
    }

    public static String getFailMessage(final @NonNull String text) {
        return "%s%s".formatted(ChatColor.RED, text);
    }
}

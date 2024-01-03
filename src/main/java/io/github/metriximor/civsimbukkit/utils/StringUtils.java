package io.github.metriximor.civsimbukkit.utils;

import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.NonNull;

public class StringUtils {
    public static String convertToTitleCase(@NonNull final String text) {
        return convertToTitleCaseSplitting(text, " ");
    }
    public static String convertToTitleCaseSplitting(@NonNull final String text, @NonNull final String separator) {
        if (text.isBlank()) {
            return text;
        }

        return Arrays.stream(text.split(separator)).map(
                word -> word.isEmpty() ? word : Character.toTitleCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}

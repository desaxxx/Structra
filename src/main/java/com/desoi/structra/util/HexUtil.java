package com.desoi.structra.util;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class HexUtil {

    private static final Pattern PATTERN = Pattern.compile(
            "<(#[a-f0-9]{6}|aqua|black|blue|dark_(aqua|blue|gray|green|purple|red)|gray|gold|green|light_purple|red|white|yellow)>",
            Pattern.CASE_INSENSITIVE
    );

    public static String color(@NotNull String text) {
        if (text.isEmpty()) {
            return "";
        }

        final Matcher matcher = PATTERN.matcher(text);

        while (matcher.find()) {
            try {
                final ChatColor chatColor = ChatColor.of(matcher.group(1));

                if (chatColor != null) {
                    text = text.replace(matcher.group(), chatColor.toString());
                }
            } catch (IllegalArgumentException ignored) { }
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static Map<String, String> placeholders = new HashMap<>();

    public static String parse(@NotNull String text) {
        if (text.isEmpty()) return "";
        String out = text;
        for(String placeholder : placeholders.keySet()) {
            String value = placeholders.get(placeholder);
            out = out.replace(placeholder, value);
        }
        return color(out);
    }
}

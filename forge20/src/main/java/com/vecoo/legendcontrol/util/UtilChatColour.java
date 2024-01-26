package com.vecoo.legendcontrol.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilChatColour {

    public static final Pattern COLOUR_PATTERN = Pattern.compile("&(#\\w{6}|[\\da-zA-Z])");

    public static Component colour(String text) {
        if (text.contains("{")) {
            try {
                return Component.Serializer.fromJson(text);
            } catch (Exception ignored) {
            }
        }

        Matcher matcher = COLOUR_PATTERN.matcher(text);
        MutableComponent textComponent = Component.literal("");
        ChatFormatting nextApply = null;
        int lastEnd = 0;
        TextColor lastColor = null;

        while (matcher.find()) {
            var start = matcher.start();
            var segment = text.substring(lastEnd, start);
            var iFormattableTextComponent = attemptAppend(textComponent, segment, lastColor);

            if (nextApply != null && iFormattableTextComponent != null) {
                iFormattableTextComponent.withStyle(nextApply);
            }

            lastEnd = matcher.end();
            String colourCode = matcher.group(1);
            var colour = parseColour(colourCode);

            if (colour.isPresent()) {
                lastColor = colour.get();
                nextApply = null;
            } else {
                var byCode = ChatFormatting.getByCode(colourCode.toCharArray()[0]);

                if (byCode != null) {
                    nextApply = byCode;
                } else {
                    textComponent.append(Component.literal("&" + colourCode));
                }
            }
        }

        var segment = text.substring(lastEnd);
        var iFormattableTextComponent = attemptAppend(textComponent, segment, lastColor);

        if (nextApply != null && iFormattableTextComponent != null) {
            iFormattableTextComponent.withStyle(nextApply);
        }
        return textComponent;
    }

    public static MutableComponent attemptAppend(MutableComponent textComponent, String segment, TextColor lastColour) {
        if (segment.isEmpty()) {
            return null;
        }

        var literalText = Component.literal(segment);

        if (lastColour != null) {
            literalText.setStyle(Style.EMPTY.withColor(lastColour));
        }

        textComponent.append(literalText);
        return literalText;
    }

    public static Optional<TextColor> parseColour(String colourCode) {
        TextColor colour = TextColor.parseColor(colourCode);

        if (colour != null) {
            return Optional.of(colour);
        }

        if (colourCode.length() > 1) {
            return Optional.empty();
        }

        ChatFormatting byCode = getByCode(colourCode.toCharArray()[0]);

        if (byCode == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(TextColor.fromLegacyFormat(byCode));
    }

    public static ChatFormatting getByCode(char p_211165_0_) {
        char c0 = Character.toString(p_211165_0_).toLowerCase(Locale.ROOT).charAt(0);

        switch (c0) {
            case '0':
                return ChatFormatting.BLACK;
            case '1':
                return ChatFormatting.DARK_BLUE;
            case '2':
                return ChatFormatting.DARK_GREEN;
            case '3':
                return ChatFormatting.DARK_AQUA;
            case '4':
                return ChatFormatting.DARK_RED;
            case '5':
                return ChatFormatting.DARK_PURPLE;
            case '6':
                return ChatFormatting.GOLD;
            case '7':
                return ChatFormatting.GRAY;
            case '8':
                return ChatFormatting.DARK_GRAY;
            case '9':
                return ChatFormatting.BLUE;
            case 'a':
                return ChatFormatting.GREEN;
            case 'b':
                return ChatFormatting.AQUA;
            case 'c':
                return ChatFormatting.RED;
            case 'd':
                return ChatFormatting.LIGHT_PURPLE;
            case 'e':
                return ChatFormatting.YELLOW;
            case 'f':
                return ChatFormatting.WHITE;
            case 'k':
                return ChatFormatting.OBFUSCATED;
            case 'l':
                return ChatFormatting.BOLD;
            case 'm':
                return ChatFormatting.STRIKETHROUGH;
            case 'n':
                return ChatFormatting.UNDERLINE;
            case 'o':
                return ChatFormatting.ITALIC;
            case 'r':
                return ChatFormatting.RESET;
        }
        return null;
    }
}
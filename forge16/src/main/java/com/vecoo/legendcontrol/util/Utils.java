package com.vecoo.legendcontrol.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.UsernameCache;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Utils {
    private static Map<String, UUID> username = Utils.invertMap(UsernameCache.getMap());

    public static <K, V> Map<V, K> invertMap(Map<K, V> map) {
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    public static UUID getUUID(String player) {
        return username.get(player);
    }

    public static boolean hasUUID(String player) {
        return username.containsKey(player);
    }

    public static String formattedString(String unformattedString) {
        return unformattedString.replace("&", "\u00a7");
    }

    public static StringTextComponent formatMessage(String unformattedText) {
        return new StringTextComponent(formattedString(unformattedText));
    }

    public static void broadcast(String message, MinecraftServer server) {
        server.getPlayerList().broadcastMessage(formatMessage(message), ChatType.CHAT, Util.NIL_UUID);
    }
}
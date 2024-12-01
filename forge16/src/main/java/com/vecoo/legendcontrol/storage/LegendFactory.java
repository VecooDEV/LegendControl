package com.vecoo.legendcontrol.storage;

import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class LegendFactory {
    public static float getLegendaryChance() {
        return LegendControl.getInstance().getServerProvider().getServerStorage().getLegendaryChance();
    }

    public static String getLastLegend() {
        return LegendControl.getInstance().getServerProvider().getServerStorage().getLastLegend();
    }

    public static LinkedHashMap<UUID, String> getPlayersIP() {
        return LegendControl.getInstance().getServerProvider().getServerStorage().getPlayersIP();
    }

    public static void addLegendaryChance(float legendaryChance) {
        setLegendaryChance(Math.min(getLegendaryChance() + legendaryChance, 100F));
    }

    public static void removeLegendaryChance(float legendaryChance) {
        setLegendaryChance(Math.max(getLegendaryChance() - legendaryChance, 0F));
    }

    public static void replacePlayerIP(UUID playerUUID, String playerIP) {
        LegendControl.getInstance().getServerProvider().getServerStorage().replacePlayerIP(playerUUID, playerIP);
    }

    public static void updatePlayerIP(ServerPlayerEntity player) {
        LegendControl.getInstance().getServerProvider().getServerStorage().updatePlayerIP(player);
    }

    public static void setLegendaryChance(float legendaryChance) {
        LegendControl.getInstance().getServerProvider().getServerStorage().setLegendaryChance(legendaryChance);
    }

    public static void setLastLegend(String lastLegend) {
        LegendControl.getInstance().getServerProvider().getServerStorage().setLastLegend(lastLegend);
    }

    public static boolean hasPlayerTrust(UUID playerUUID, UUID targetUUID) {
        return LegendControl.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).getPlayersTrust().contains(targetUUID);
    }

    public static List<UUID> getPlayersTrust(UUID playerUUID) {
        return LegendControl.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).getPlayersTrust();
    }

    public static void addPlayerTrust(UUID playerUUID, UUID targetUUID) {
        LegendControl.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).addPlayerTrust(targetUUID);
    }

    public static void removePlayerTrust(UUID playerUUID, UUID targetUUID) {
        LegendControl.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).removePlayerTrust(targetUUID);
    }

    public static void removePlayersTrust(UUID playerUUID) {
        LegendControl.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).removePlayersTrust();
    }
}
package com.vecoo.legendcontrol.storage.server;

import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class LegendServerFactory {
    public static float getLegendaryChance() {
        return LegendControl.getInstance().getServerProvider().getServerStorage().getLegendaryChance();
    }

    public static String getLastLegend() {
        return LegendControl.getInstance().getServerProvider().getServerStorage().getLastLegend();
    }

    public static LinkedHashMap<UUID, String> getPlayersIP() {
        return LegendControl.getInstance().getServerProvider().getServerStorage().getPlayersIP();
    }

    public static List<UUID> getPlayersBlacklist() {
        return LegendControl.getInstance().getServerProvider().getServerStorage().getPlayersBlacklist();
    }

    public static void addLegendaryChance(float legendaryChance) {
        setLegendaryChance(Math.min(getLegendaryChance() + legendaryChance, 100F));
    }

    public static void removeLegendaryChance(float legendaryChance) {
        setLegendaryChance(Math.max(getLegendaryChance() - legendaryChance, 0F));
    }

    public static void addPlayerBlacklist(UUID playerUUID) {
        LegendControl.getInstance().getServerProvider().getServerStorage().addPlayerBlacklist(playerUUID);
    }

    public static void removePlayerBlacklist(UUID playerUUID) {
        LegendControl.getInstance().getServerProvider().getServerStorage().removePlayerBlacklist(playerUUID);
    }

    public static void removePlayersBlacklist() {
        LegendControl.getInstance().getServerProvider().getServerStorage().removePlayersBlacklist();
    }

    public static void replacePlayerIP(UUID playerUUID, String playerIP) {
        LegendControl.getInstance().getServerProvider().getServerStorage().replacePlayerIP(playerUUID, playerIP);
    }

    public static void updatePlayerIP(EntityPlayerMP player) {
        LegendControl.getInstance().getServerProvider().getServerStorage().updatePlayerIP(player);
    }

    public static void setLegendaryChance(float legendaryChance) {
        LegendControl.getInstance().getServerProvider().getServerStorage().setLegendaryChance(legendaryChance);
    }

    public static void setLastLegend(String lastLegend) {
        LegendControl.getInstance().getServerProvider().getServerStorage().setLastLegend(lastLegend);
    }
}
package com.vecoo.legendcontrol.storage.server;

import com.vecoo.legendcontrol.LegendControl;

import java.util.List;

public class ServerFactory {
    public static double getLegendaryChance() {
        return LegendControl.getInstance().getServerProvider().getServerStorage().getLegendaryChance();
    }

    public static String getLastLegend() {
        return LegendControl.getInstance().getServerProvider().getServerStorage().getLastLegend();
    }

    public static List<String> getPlayersIP() {
        return LegendControl.getInstance().getServerProvider().getServerStorage().getPlayersIP();
    }

    public static void addLegendaryChance(double legendaryChance) {
        setLegendaryChance(Math.min(getLegendaryChance() + legendaryChance, 100));
    }

    public static void removeLegendaryChance(double legendaryChance) {
        setLegendaryChance(Math.max(getLegendaryChance() - legendaryChance, 0));
    }

    public static void replacePlayerIP(String playerIP) {
        LegendControl.getInstance().getServerProvider().getServerStorage().replacePlayerIP(playerIP);
    }

    public static void setLegendaryChance(double legendaryChance) {
        LegendControl.getInstance().getServerProvider().getServerStorage().setLegendaryChance(legendaryChance);
    }

    public static void setLastLegend(String lastLegend) {
        LegendControl.getInstance().getServerProvider().getServerStorage().setLastLegend(lastLegend);
    }
}
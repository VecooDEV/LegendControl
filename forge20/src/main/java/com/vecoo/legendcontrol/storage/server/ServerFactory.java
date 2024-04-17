package com.vecoo.legendcontrol.storage.server;

import com.vecoo.legendcontrol.LegendControl;

import java.util.List;

public class ServerFactory {
    public static int getLegendaryChance() {
        return LegendControl.getInstance().getServerProvider().getServerStorage("legendaryChance").getLegendaryChance();
    }

    public static String getLastLegend() {
        return LegendControl.getInstance().getServerProvider().getServerStorage("lastLegend").getLastLegend();
    }

    public static List<String> getPlayersIP() {
        return LegendControl.getInstance().getServerProvider().getServerStorage("playersIP").getPlayersIP();
    }

    public static void addLegendaryChance(int legendaryChance) {
        setLegendaryChance(Math.min(getLegendaryChance() + legendaryChance, 100));
    }

    public static void replacePlayerIP(String playerIP) {
        LegendControl.getInstance().getServerProvider().getServerStorage("playersIP").replacePlayerIP(playerIP);
    }

    public static void setLegendaryChance(int legendaryChance) {
        LegendControl.getInstance().getServerProvider().getServerStorage("legendaryChance").setLegendaryChance(legendaryChance);
    }

    public static void setLastLegend(String lastLegend) {
        LegendControl.getInstance().getServerProvider().getServerStorage("lastLegend").setLastLegend(lastLegend);
    }
}
package com.vecoo.legendcontrol.storage.server;

import com.google.common.collect.Lists;
import com.vecoo.legendcontrol.LegendControl;

import java.util.ArrayList;
import java.util.List;

public class ServerStorage {
    private int legendaryChance;
    private String lastLegend;
    private List<String> playersIP;

    public ServerStorage(int legendaryChance, String latLegend) {
        this.legendaryChance = legendaryChance;
        this.lastLegend = latLegend;
        this.playersIP = new ArrayList<>();
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public List<String> getStorageName() {
        return Lists.newArrayList("legendaryChance", "lastLegend", "playersIP");
    }

    public int getLegendaryChance() {
        return this.legendaryChance;
    }

    public String getLastLegend() {
        return this.lastLegend;
    }

    public List<String> getPlayersIP() {
        return this.playersIP;
    }

    public void replacePlayerIP(String playerIP) {
        if (this.playersIP.size() < LegendControl.getInstance().getConfig().getMaxPlayersIP()) {
            this.playersIP.add(playerIP);
        } else {
            this.playersIP.remove(0);
            this.playersIP.add(playerIP);
        }
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public void setLegendaryChance(int legendaryChance) {
        this.legendaryChance = legendaryChance;
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public void setLastLegend(String lastLegend) {
        this.lastLegend = lastLegend;
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }
}
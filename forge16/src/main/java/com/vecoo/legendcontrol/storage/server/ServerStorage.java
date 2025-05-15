package com.vecoo.legendcontrol.storage.server;

import com.vecoo.legendcontrol.LegendControl;

public class ServerStorage {
    private float legendaryChance;
    private String lastLegend;

    public ServerStorage(float legendaryChance, String lastLegend) {
        this.legendaryChance = legendaryChance;
        this.lastLegend = lastLegend;
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public float getLegendaryChance() {
        return this.legendaryChance;
    }

    public String getLastLegend() {
        return this.lastLegend;
    }

    public void setLegendaryChance(float legendaryChance) {
        this.legendaryChance = legendaryChance;
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public void setLastLegend(String lastLegend) {
        this.lastLegend = lastLegend;
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }
}
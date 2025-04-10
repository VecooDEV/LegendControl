package com.vecoo.legendcontrol.storage.server;

import com.vecoo.legendcontrol.LegendControl;

public class ServerStorage {
    private float legendaryChance;

    public ServerStorage(float legendaryChance) {
        this.legendaryChance = legendaryChance;
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public float getLegendaryChance() {
        return this.legendaryChance;
    }

    public void setLegendaryChance(float legendaryChance) {
        this.legendaryChance = legendaryChance;
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }
}
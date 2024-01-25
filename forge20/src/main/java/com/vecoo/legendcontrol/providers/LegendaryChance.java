package com.vecoo.legendcontrol.providers;

import com.vecoo.legendcontrol.LegendControl;

public class LegendaryChance {
    private String key;
    private int legendaryChance;

    public LegendaryChance(String key, int legendaryChance) {
        this.key = key;
        this.legendaryChance = legendaryChance;
        LegendControl.getInstance().getLegendaryProvider().updateLegendaryChance(this);
    }

    public int getLegendaryChance() {
        return legendaryChance;
    }

    public void setLegendaryChance(int legendaryChance) {
        this.legendaryChance = legendaryChance;
        LegendControl.getInstance().getLegendaryProvider().updateLegendaryChance(this);
    }
}

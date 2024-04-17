package com.vecoo.legendcontrol.storage;

import com.vecoo.legendcontrol.LegendControl;

public class LegendaryChance {
    private int chance;

    public LegendaryChance(int chance) {
        this.chance = chance;
        LegendControl.getInstance().getLegendaryProvider().updateLegendaryChance(this);
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
        LegendControl.getInstance().getLegendaryProvider().updateLegendaryChance(this);
    }

    public void addChance(int chance) {
        this.chance += chance;
        LegendControl.getInstance().getLegendaryProvider().updateLegendaryChance(this);
    }
}
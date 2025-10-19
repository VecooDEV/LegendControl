package com.vecoo.legendcontrol.storage;

import com.vecoo.legendcontrol.LegendControl;
import org.jetbrains.annotations.NotNull;

public class ServerStorage {
    private float chanceLegend;
    private String lastLegend;

    public ServerStorage(float chanceLegend, @NotNull String lastLegend) {
        this.chanceLegend = chanceLegend;
        this.lastLegend = lastLegend;
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public float getChanceLegend() {
        return this.chanceLegend;
    }

    @NotNull
    public String getLastLegend() {
        return this.lastLegend;
    }

    public void setChanceLegend(float amount) {
        this.chanceLegend = amount;
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public void addChanceLegend(float amount) {
        this.chanceLegend = Math.min(getChanceLegend() + amount, 100F);
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public void removeChanceLegend(float amount) {
        this.chanceLegend = Math.max(getChanceLegend() - amount, 0F);
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public void setLastLegend(@NotNull String pokemonName) {
        this.lastLegend = pokemonName;
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }
}
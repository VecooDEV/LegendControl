package com.vecoo.legendcontrol.service;

import com.vecoo.legendcontrol.LegendControl;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;

@Getter
@ToString
public class ServerStorage {
    private float chanceLegend;
    @Nonnull
    private String lastLegend;

    public ServerStorage(float chanceLegend, @Nonnull String lastLegend) {
        this.chanceLegend = chanceLegend;
        this.lastLegend = lastLegend;
        LegendControl.getInstance().getServerService().updateStorage(this);
    }

    public void setChanceLegend(float amount) {
        this.chanceLegend = amount;
        LegendControl.getInstance().getServerService().updateStorage(this);
    }

    public void addChanceLegend(float amount) {
        this.chanceLegend = Math.min(getChanceLegend() + amount, 100F);
        LegendControl.getInstance().getServerService().updateStorage(this);
    }

    public void removeChanceLegend(float amount) {
        this.chanceLegend = Math.max(getChanceLegend() - amount, 0F);
        LegendControl.getInstance().getServerService().updateStorage(this);
    }

    public void setLastLegend(@Nonnull String pokemonName) {
        this.lastLegend = pokemonName;
        LegendControl.getInstance().getServerService().updateStorage(this);
    }
}
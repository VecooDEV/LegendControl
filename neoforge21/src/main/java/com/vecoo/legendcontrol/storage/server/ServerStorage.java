package com.vecoo.legendcontrol.storage.server;

import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.events.ChanceLegendEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ServerStorage {
    private float chanceLegend;
    private String lastLegend;

    public ServerStorage(float chanceLegend, String lastLegend) {
        this.chanceLegend = chanceLegend;
        this.lastLegend = lastLegend;
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public float getChanceLegend() {
        return this.chanceLegend;
    }

    public String getLastLegend() {
        return this.lastLegend;
    }

    public void setChanceLegend(String source, float amount, boolean update) {
        if (!NeoForge.EVENT_BUS.post(new ChanceLegendEvent(source, amount)).isCanceled()) {
            this.chanceLegend = amount;

            if (update) {
                LegendControl.getInstance().getServerProvider().updateServerStorage(this);
            }
        }
    }

    public void setLastLegend(String pokemonName, boolean update) {
        this.lastLegend = pokemonName;

        if (update) {
            LegendControl.getInstance().getServerProvider().updateServerStorage(this);
        }
    }
}
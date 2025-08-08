package com.vecoo.legendcontrol.storage.server;

import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.events.ChangeChanceLegendEvent;
import net.minecraftforge.common.MinecraftForge;

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
        ChangeChanceLegendEvent event = new ChangeChanceLegendEvent(source, amount);

        if (!MinecraftForge.EVENT_BUS.post(event)) {
            this.chanceLegend = event.getChance();

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
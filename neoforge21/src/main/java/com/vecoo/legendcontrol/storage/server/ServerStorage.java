package com.vecoo.legendcontrol.storage.server;

import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.events.ChangeChanceLegendEvent;
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
        ChangeChanceLegendEvent.setChance event = new ChangeChanceLegendEvent.setChance(source, amount);

        if (!NeoForge.EVENT_BUS.post(event).isCanceled()) {
            this.chanceLegend = event.getChance();

            if (update) {
                LegendControl.getInstance().getServerProvider().updateServerStorage(this);
            }
        }
    }

    public void addChanceLegend(String source, float amount, boolean update) {
        ChangeChanceLegendEvent.addChance event = new ChangeChanceLegendEvent.addChance(source, Math.min(getChanceLegend() + amount, 100F));

        if (!NeoForge.EVENT_BUS.post(event).isCanceled()) {
            this.chanceLegend = event.getChance();

            if (update) {
                LegendControl.getInstance().getServerProvider().updateServerStorage(this);
            }
        }
    }

    public void removeChanceLegend(String source, float amount, boolean update) {
        ChangeChanceLegendEvent.removeChance event = new ChangeChanceLegendEvent.removeChance(source, Math.min(getChanceLegend() - amount, 0F));

        if (!NeoForge.EVENT_BUS.post(event).isCanceled()) {
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
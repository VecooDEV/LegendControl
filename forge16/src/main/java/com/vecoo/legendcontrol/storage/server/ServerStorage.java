package com.vecoo.legendcontrol.storage.server;

import com.vecoo.legendcontrol.LegendControl;

import java.util.HashSet;
import java.util.UUID;

public class ServerStorage {
    private float legendaryChance;
    private String lastLegend;
    private final HashSet<UUID> legends;

    public ServerStorage(float legendaryChance, String lastLegend) {
        this.legendaryChance = legendaryChance;
        this.lastLegend = lastLegend;
        this.legends = new HashSet<>();
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public float getLegendaryChance() {
        return this.legendaryChance;
    }

    public String getLastLegend() {
        return this.lastLegend;
    }

    public HashSet<UUID> getLegends() {
        return this.legends;
    }

    public void setLegendaryChance(float legendaryChance) {
        this.legendaryChance = legendaryChance;
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public void setLastLegend(String lastLegend) {
        this.lastLegend = lastLegend;
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public void addLegends(UUID pokemonUUID) {
        this.legends.add(pokemonUUID);
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public void removeLegends(UUID pokemonUUID) {
        this.legends.remove(pokemonUUID);
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public void clearLegends() {
        this.legends.clear();
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }
}
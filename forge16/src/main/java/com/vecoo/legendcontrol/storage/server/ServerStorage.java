package com.vecoo.legendcontrol.storage.server;

import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ServerStorage {
    private float legendaryChance;
    private String lastLegend;
    private final LinkedHashMap<UUID, String> playersIP;

    public ServerStorage(float legendaryChance, String lastLegend) {
        this.legendaryChance = legendaryChance;
        this.lastLegend = lastLegend;
        this.playersIP = new LinkedHashMap<>();
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public float getLegendaryChance() {
        return this.legendaryChance;
    }

    public String getLastLegend() {
        return this.lastLegend;
    }

    public LinkedHashMap<UUID, String> getPlayersIP() {
        return this.playersIP;
    }

    public void setLegendaryChance(float legendaryChance) {
        this.legendaryChance = legendaryChance;
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public void setLastLegend(String lastLegend) {
        this.lastLegend = lastLegend;
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public void replacePlayerIP(UUID playerUUID, String playerIP) {
        if (this.playersIP.containsKey(playerUUID) || this.playersIP.containsValue(playerIP)) {
            return;
        }

        if (this.playersIP.size() < LegendControl.getInstance().getConfig().getMaxPlayersIP()) {
            this.playersIP.put(playerUUID, playerIP);
        } else {
            Iterator<Map.Entry<UUID, String>> iterator = this.playersIP.entrySet().iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
            this.playersIP.put(playerUUID, playerIP);
        }
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public void updatePlayerIP(ServerPlayerEntity player) {
        if (this.playersIP.containsKey(player.getUUID())) {
            this.playersIP.replace(player.getUUID(), player.getIpAddress());
        }
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }
}
package com.vecoo.legendcontrol.storage.server;

import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.*;

public class ServerStorage {
    private float legendaryChance;
    private String lastLegend;
    private List<UUID> playersBlacklist;
    private LinkedHashMap<UUID, String> playersIP;

    public ServerStorage(float legendaryChance, String lastLegend) {
        this.legendaryChance = legendaryChance;
        this.lastLegend = lastLegend;
        this.playersIP = new LinkedHashMap<>();
        this.playersBlacklist = new ArrayList<>();
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

    public List<UUID> getPlayersBlacklist() {
        return this.playersBlacklist;
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

    public void updatePlayerIP(EntityPlayerMP player) {
        if (this.playersIP.containsKey(player.getUniqueID())) {
            this.playersIP.replace(player.getUniqueID(), player.getPlayerIP());
        }
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public void addPlayerBlacklist(UUID playerUUID) {
        this.playersBlacklist.add(playerUUID);
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public void removePlayerBlacklist(UUID playerUUID) {
        this.playersBlacklist.remove(playerUUID);
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }

    public void removePlayersBlacklist() {
        this.playersBlacklist.clear();
        LegendControl.getInstance().getServerProvider().updateServerStorage(this);
    }
}
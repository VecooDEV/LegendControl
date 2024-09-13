package com.vecoo.legendcontrol.config;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.legendcontrol.LegendControl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServerConfig {
    private String playerStorage = "/%directory%/storage/LegendControl/players/";
    private String serverStorage = "/%directory%/storage/LegendControl/";
    private int trustLimit = 15;
    private int protectedTime = 300;
    private int locationTime = 600;
    private float baseChance = 10.0F;
    private float stepSpawnChance = 5.0F;
    private int maxPlayersIP = 0;
    private int randomTimeSpawnMin = 0;
    private int randomTimeSpawnMax = 300;
    private boolean notifyLegendarySpawn = true;
    private boolean legendaryRepeat = true;
    private boolean legendaryParticle = true;
    private boolean blacklistLegendary = false;
    private List<String> blacklistLegendaryList = Lists.newArrayList("Regieleki", "Regidrago");
    private boolean blacklistWorld = false;
    private List<Integer> blacklistWorldList = Lists.newArrayList(-1, 1);

    public String getPlayerStorage() {
        return this.playerStorage;
    }

    public String getServerStorage() {
        return this.serverStorage;
    }

    public int getTrustLimit() {
        return this.trustLimit;
    }

    public int getProtectedTime() {
        return this.protectedTime;
    }

    public float getBaseChance() {
        return this.baseChance;
    }

    public float getStepSpawnChance() {
        return this.stepSpawnChance;
    }

    public int getLocationTime() {
        return this.locationTime;
    }

    public int getMaxPlayersIP() {
        return this.maxPlayersIP;
    }

    public int getRandomTimeSpawnMin() {
        return this.randomTimeSpawnMin;
    }

    public int getRandomTimeSpawnMax() {
        return this.randomTimeSpawnMax;
    }

    public boolean isNotifyLegendarySpawn() {
        return this.notifyLegendarySpawn;
    }

    public boolean isLegendaryRepeat() {
        return this.legendaryRepeat;
    }

    public boolean isLegendaryParticle() {
        return this.legendaryParticle;
    }

    public boolean isBlacklistLegendary() {
        return this.blacklistLegendary;
    }

    public boolean isBlacklistWorld() {
        return this.blacklistWorld;
    }

    public List<String> getBlockedLegendary() {
       return this.blacklistLegendaryList;
    }

    public List<Integer> getBlockedWorld() {
        return this.blacklistWorldList;
    }

    private boolean write() {
        Gson gson = UtilGson.newGson();
        CompletableFuture<Boolean> future = UtilGson.writeFileAsync("/config/LegendControl/", "config.json", gson.toJson(this));
        return future.join();
    }

    public void init() {
        try {
            CompletableFuture<Boolean> future = UtilGson.readFileAsync("/config/LegendControl/", "config.json", el -> {
                Gson gson = UtilGson.newGson();
                ServerConfig config = gson.fromJson(el, ServerConfig.class);

                this.playerStorage = config.getPlayerStorage();
                this.serverStorage = config.getServerStorage();
                this.trustLimit = config.getTrustLimit();
                this.protectedTime = config.getProtectedTime();
                this.locationTime = config.getLocationTime();
                this.baseChance = config.getBaseChance();
                this.stepSpawnChance = config.getStepSpawnChance();
                this.maxPlayersIP = config.getMaxPlayersIP();
                this.randomTimeSpawnMin = config.getRandomTimeSpawnMin();
                this.randomTimeSpawnMax = config.getRandomTimeSpawnMax();
                this.notifyLegendarySpawn = config.isNotifyLegendarySpawn();
                this.legendaryRepeat = config.isLegendaryRepeat();
                this.legendaryParticle = config.isLegendaryParticle();
                this.blacklistLegendary = config.isBlacklistLegendary();
                this.blacklistWorld = config.isBlacklistWorld();
                this.blacklistWorldList = config.getBlockedWorld();
                this.blacklistLegendaryList = config.getBlockedLegendary();
            });
            if (!future.join()) {
                write();
            }
        } catch (Exception e) {
            LegendControl.getLogger().error("Error in config.");
        }
    }
}
package com.vecoo.legendcontrol.config;

import com.google.common.collect.Sets;
import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.legendcontrol.LegendControl;

import java.util.Set;

public class ServerConfig {
    private int locationTime = 600;
    private int despawnTime = 1800;
    private float baseChance = 10.0F;
    private float stepSpawnChance = 5.0F;
    private int randomTimeSpawnMin = 0;
    private int randomTimeSpawnMax = 300;
    private boolean legendaryParticle = true;
    private String particleName = "dragonbreath";
    private boolean notifyPersonalLegendarySpawn = true;
    private boolean notifyLegendaryCatch = true;
    private boolean notifyLegendaryDefeat = true;
    private boolean legendaryRepeat = true;
    private boolean blacklistDimensions = false;
    private boolean blacklistPlayers = false;
    private Set<Integer> blacklistDimensionList = Sets.newHashSet(-1, 1);
    private Set<String> blacklistPlayersList = Sets.newHashSet("Player");

    public float getBaseChance() {
        return this.baseChance;
    }

    public float getStepSpawnChance() {
        return this.stepSpawnChance;
    }

    public boolean isNotifyLegendaryCatch() {
        return this.notifyLegendaryCatch;
    }

    public int getLocationTime() {
        return this.locationTime;
    }

    public int getDespawnTime() {
        return this.despawnTime;
    }

    public int getRandomTimeSpawnMin() {
        return this.randomTimeSpawnMin;
    }

    public int getRandomTimeSpawnMax() {
        return this.randomTimeSpawnMax;
    }

    public String getParticleName() {
        return this.particleName;
    }

    public boolean isNotifyPersonalLegendarySpawn() {
        return this.notifyPersonalLegendarySpawn;
    }

    public boolean isLegendaryParticle() {
        return this.legendaryParticle;
    }

    public boolean isNotifyLegendaryDefeat() {
        return this.notifyLegendaryDefeat;
    }

    public boolean isLegendaryRepeat() {
        return this.legendaryRepeat;
    }

    public boolean isBlacklistDimensions() {
        return this.blacklistDimensions;
    }

    public boolean isBlacklistPlayers() {
        return this.blacklistPlayers;
    }

    public Set<Integer> getBlacklistDimensionList() {
        return this.blacklistDimensionList;
    }

    public Set<String> getBlacklistPlayersList() {
        return this.blacklistPlayersList;
    }

    private void write() {
        UtilGson.writeFileAsync("/config/LegendControl/", "config.json", UtilGson.newGson().toJson(this)).join();
    }

    public void init() {
        boolean completed = UtilGson.readFileAsync("/config/LegendControl/", "config.json", el -> {
            ServerConfig config = UtilGson.newGson().fromJson(el, ServerConfig.class);

            this.baseChance = config.getBaseChance();
            this.stepSpawnChance = config.getStepSpawnChance();
            this.notifyPersonalLegendarySpawn = config.isNotifyPersonalLegendarySpawn();
            this.locationTime = config.getLocationTime();
            this.despawnTime = config.getDespawnTime();
            this.randomTimeSpawnMin = config.getRandomTimeSpawnMin();
            this.randomTimeSpawnMax = config.getRandomTimeSpawnMax();
            this.legendaryParticle = config.isLegendaryParticle();
            this.particleName = config.getParticleName();
            this.notifyLegendaryCatch = config.isNotifyLegendaryCatch();
            this.notifyLegendaryDefeat = config.isNotifyLegendaryDefeat();
            this.legendaryRepeat = config.isLegendaryRepeat();
            this.blacklistDimensions = config.isBlacklistDimensions();
            this.blacklistPlayers = config.isBlacklistPlayers();
            this.blacklistDimensionList = config.getBlacklistDimensionList();
            this.blacklistPlayersList = config.getBlacklistPlayersList();
        }).join();

        if (!completed) {
            LegendControl.getLogger().error("[ExtraQuests] Failed init config.");
            write();
        }
    }
}
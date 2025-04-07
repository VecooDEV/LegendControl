package com.vecoo.legendcontrol.config;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.config.api.data.ConfigPath;
import com.pixelmonmod.pixelmon.api.config.api.yaml.AbstractYamlConfig;
import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigPath("config/LegendControl/config.yml")
@ConfigSerializable
public class ServerConfig extends AbstractYamlConfig {
    private int locationTime = 600;
    private float baseChance = 10.0F;
    private float stepSpawnChance = 5.0F;
    private int rerollSpawn = 5;
    private int randomTimeSpawnMin = 0;
    private int randomTimeSpawnMax = 300;
    private String particleName = "dragon_breath";
    private boolean notifyPersonalLegendarySpawn = true;
    private boolean notifyLegendaryCatch = true;
    private boolean notifyLegendaryDefeat = true;
    private boolean notifyLegendaryDespawn = true;
    private boolean legendaryRepeat = true;
    private boolean legendaryParticle = true;
    private boolean blacklistLegendary = false;
    private List<String> blacklistLegendaryList = Lists.newArrayList("Regieleki", "Regidrago");
    private boolean blacklistDimensions = false;
    private List<String> blacklistDimensionList = Lists.newArrayList("the_nether", "the_end");

    public float getBaseChance() {
        return this.baseChance;
    }

    public float getStepSpawnChance() {
        return this.stepSpawnChance;
    }

    public boolean isNotifyLegendaryCatch() {
        return this.notifyLegendaryCatch;
    }

    public int getRerollSpawn() {
        return this.rerollSpawn;
    }

    public int getLocationTime() {
        return this.locationTime;
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

    public boolean isLegendaryRepeat() {
        return this.legendaryRepeat;
    }

    public boolean isNotifyLegendaryDefeat() {
        return this.notifyLegendaryDefeat;
    }

    public boolean isBlacklistLegendary() {
        return this.blacklistLegendary;
    }

    public boolean isNotifyLegendaryDespawn() {
        return this.notifyLegendaryDespawn;
    }

    public List<String> getBlockedLegendary() {
        return this.blacklistLegendaryList;
    }

    public boolean isBlacklistDimensions() {
        return this.blacklistDimensions;
    }

    public List<String> getBlacklistDimensionList() {
        return this.blacklistDimensionList;
    }
}
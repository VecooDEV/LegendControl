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
    private int despawnTime = 1800;
    private float baseChance = 10.0F;
    private float stepSpawnChance = 5.0F;
    private int randomTimeSpawnMin = 0;
    private int randomTimeSpawnMax = 300;
    private String particleName = "dragon_breath";
    private boolean notifyPersonalLegendarySpawn = true;
    private boolean notifyLegendaryCatch = true;
    private boolean notifyLegendaryDefeat = true;
    private boolean notifyLegendaryDespawn = true;
    private boolean legendaryParticle = true;
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

    public boolean isNotifyLegendaryDespawn() {
        return this.notifyLegendaryDespawn;
    }

    public boolean isBlacklistDimensions() {
        return this.blacklistDimensions;
    }

    public List<String> getBlacklistDimensionList() {
        return this.blacklistDimensionList;
    }
}
package com.vecoo.legendcontrol.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigPath("config/LegendControl/config.yml")
@ConfigSerializable
public class ServerConfig extends AbstractYamlConfig {

    private int trustLimit = 15;

    private int protectedTime = 300;

    private int baseChance = 10;

    private int stepSpawnChance = 5;

    private boolean notifyLegendarySpawn = true;

    private boolean newLegendarySpawn = true;

    private boolean legendaryDefender = true;

    public int getTrustLimit() {
        return this.trustLimit;
    }

    public int getLegendProtectedTime() {
        return this.protectedTime;
    }

    public int getBaseChance() {
        return this.baseChance;
    }

    public int getStepSpawnChance() {
        return stepSpawnChance;
    }

    public boolean isNotifyLegendarySpawn() {
        return notifyLegendarySpawn;
    }

    public boolean isNewLegendarySpawn() {
        return newLegendarySpawn;
    }

    public boolean isLegendaryDefender() {
        return legendaryDefender;
    }
}


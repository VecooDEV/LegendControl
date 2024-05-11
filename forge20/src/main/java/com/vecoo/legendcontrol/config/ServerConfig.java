package com.vecoo.legendcontrol.config;

import com.google.common.collect.Lists;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.config.api.data.ConfigPath;
import com.pixelmonmod.pixelmon.api.config.api.yaml.AbstractYamlConfig;
import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigPath("config/LegendControl/config.yml")
@ConfigSerializable
public class ServerConfig extends AbstractYamlConfig {
    private int trustLimit = 15;

    private int protectedTime = 300;

    private double baseChance = 10.0;

    private double stepSpawnChance = 5.0;

    private int maxPlayersIP = 3;

    private boolean notifyLegendarySpawn = true;

    private boolean newLegendarySpawn = true;

    private boolean legendaryDefender = true;

    private boolean legendaryRepeat = true;

    private boolean repeatSpawnToPlayer = true;

    private List<String> blacklistLegendary = Lists.newArrayList(
            "regieleki", "regidrago");

    private transient List<PokemonSpecification> blacklistLegendaryCache = null;

    public int getTrustLimit() {
        return this.trustLimit;
    }

    public int getProtectedTime() {
        return this.protectedTime;
    }

    public double getBaseChance() {
        return this.baseChance;
    }

    public double getStepSpawnChance() {
        return this.stepSpawnChance;
    }

    public int getMaxPlayersIP() {
        return this.maxPlayersIP;
    }

    public boolean isNotifyLegendarySpawn() {
        return this.notifyLegendarySpawn;
    }

    public boolean isNewLegendarySpawn() {
        return this.newLegendarySpawn;
    }

    public boolean isLegendaryDefender() {
        return this.legendaryDefender;
    }

    public boolean isLegendaryRepeat() {
        return this.legendaryRepeat;
    }

    public boolean isRepeatSpawnToPlayer() {
        return this.repeatSpawnToPlayer;
    }

    public List<PokemonSpecification> getBlockedLegendary() {
        if (this.blacklistLegendaryCache == null) {
            List<PokemonSpecification> blocked = Lists.newArrayList();

            for (String blackList : this.blacklistLegendary) {
                blocked.add(PokemonSpecificationProxy.create(blackList).get());
            }
            this.blacklistLegendaryCache = blocked;
        }
        return this.blacklistLegendaryCache;
    }
}
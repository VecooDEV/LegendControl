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

    private float baseChance = 10.0F;

    private float stepSpawnChance = 5.0F;

    private int maxPlayersIP = 3;

    private boolean notifyLegendarySpawn = true;

    private boolean newLegendarySpawn = true;

    private boolean legendaryDefender = true;

    private boolean legendaryRepeat = true;

    private boolean repeatSpawnToPlayer = true;

    private boolean blacklistLegendary = false;

    private List<String> blacklistLegendaryList = Lists.newArrayList(
            "regieleki", "regidrago");

    private transient List<PokemonSpecification> blacklistLegendaryCache = null;

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

    public boolean isBlacklistLegendary() {
        return this.blacklistLegendary;
    }

    public List<PokemonSpecification> getBlockedLegendary() {
        if (this.blacklistLegendaryCache == null) {
            List<PokemonSpecification> blocked = Lists.newArrayList();

            for (String blackList : this.blacklistLegendaryList) {
                blocked.add(PokemonSpecificationProxy.create(blackList));
            }
            this.blacklistLegendaryCache = blocked;
        }
        return this.blacklistLegendaryCache;
    }
}
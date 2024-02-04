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

    private int baseChance = 10;

    private int stepSpawnChance = 5;

    private boolean notifyLegendarySpawn = true;

    private boolean newLegendarySpawn = true;

    private boolean legendaryDefender = true;

    private List<String> blacklistLegendary = Lists.newArrayList(
            "regieleki", "regidrago");

    private transient List<PokemonSpecification> blacklistLegendaryCache = null;

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

    public List<PokemonSpecification> getBlockedLegendary() {
        if (this.blacklistLegendaryCache == null) {
            List<PokemonSpecification> blocked = Lists.newArrayList();

            for (String blackList : this.blacklistLegendary) {
                blocked.add(PokemonSpecificationProxy.create(blackList));
            }
            this.blacklistLegendaryCache = blocked;
        }
        return this.blacklistLegendaryCache;
    }
}
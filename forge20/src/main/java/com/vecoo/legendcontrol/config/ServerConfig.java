package com.vecoo.legendcontrol.config;

import com.google.common.collect.Lists;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.config.api.data.ConfigPath;
import com.pixelmonmod.pixelmon.api.config.api.yaml.AbstractYamlConfig;
import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;
import info.pixelmon.repack.org.spongepowered.objectmapping.meta.Comment;

import java.util.List;

@ConfigPath("config/LegendControl/config.yml")
@ConfigSerializable
public class ServerConfig extends AbstractYamlConfig {
    @Comment("Maximum number of players in the trust list. Set to 0 to disable.")
    private int trustLimit = 15;

    @Comment("The number of seconds before the protection of a legendary Pokemon is removed. Set to 0 to disable")
    private int protectedTime = 300;

    @Comment("Base chance for legendary Pokemon.")
    private float baseChance = 10.0F;

    @Comment("A chance added to the base chance upon failure.")
    private float stepSpawnChance = 5.0F;

    @Comment("The number of player IPs stored for the non-re-appearance of the legendary Pokemon. Set to 0 to remove the limitation.")
    private int maxPlayersIP = 3;

    @Comment("Maximum number of one IP player on the server. Set to 0 to disable.")
    private int lockPlayerIP = 5;

    @Comment("The minimum number of seconds added to display the team time, it only changes the time visually.")
    private int randomTimeSpawnMin = 300;

    @Comment("The maximum number of seconds added to display the team time, it only changes the time visually.")
    private int randomTimeSpawnMax = 600;

    @Comment("Notifying the player about the appearance of a legendary Pokemon (locally).")
    private boolean notifyLegendarySpawn = true;

    @Comment("Can the same legendary Pokemon be repeated?")
    private boolean legendaryRepeat = true;

    @Comment("Will the blacklist of legendary Pokemon work?")
    private boolean blacklistLegendary = false;

    @Comment("List of Legendary Pokemon that cannot appear.")
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

    public int getLockPlayerIP() {
        return this.lockPlayerIP;
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

    public boolean isBlacklistLegendary() {
        return this.blacklistLegendary;
    }

    public List<PokemonSpecification> getBlockedLegendary() {
        if (this.blacklistLegendaryCache == null) {
            List<PokemonSpecification> blocked = Lists.newArrayList();

            for (String blackList : this.blacklistLegendaryList) {
                blocked.add(PokemonSpecificationProxy.create(blackList).get());
            }
            this.blacklistLegendaryCache = blocked;
        }
        return this.blacklistLegendaryCache;
    }
}
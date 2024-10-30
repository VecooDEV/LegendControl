package com.vecoo.legendcontrol.config;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.config.api.data.ConfigPath;
import com.pixelmonmod.pixelmon.api.config.api.yaml.AbstractYamlConfig;
import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;
import info.pixelmon.repack.org.spongepowered.objectmapping.meta.Comment;

import java.util.List;

@ConfigPath("config/LegendControl/config.yml")
@ConfigSerializable
public class ServerConfig extends AbstractYamlConfig {
    @Comment("Directory for saving player information from root.")
    private String playerStorage = "/%directory%/storage/LegendControl/players/";

    @Comment("Directory for saving server information from root.")
    private String serverStorage = "/%directory%/storage/LegendControl/";

    @Comment("Maximum number of players in the trust list. Set to 0 to disable.")
    private int trustLimit = 15;

    @Comment("The number of seconds before the protection of a legendary Pokemon is removed. Set to 0 to disable.")
    private int protectedTime = 300;

    @Comment("The amount of time before the Pokemon's location is announced. 0 to disable.")
    private int locationTime = 600;

    @Comment("Base chance for legendary Pokemon.")
    private float baseChance = 10.0F;

    @Comment("A chance added to the base chance upon failure.")
    private float stepSpawnChance = 5.0F;

    @Comment("The number of player IPs stored for the non-re-appearance of the legendary Pokemon. Set to 0 to remove the limitation.")
    private int maxPlayersIP = 0;

    @Comment("The minimum number of seconds added to display the team time, it only changes the time visually.")
    private int randomTimeSpawnMin = 0;

    @Comment("The maximum number of seconds added to display the team time, it only changes the time visually.")
    private int randomTimeSpawnMax = 300;

    @Comment("Notifying the player about the appearance of a legendary Pokemon (locally).")
    private boolean notifyLegendarySpawn = true;

    @Comment("Can the same legendary Pokemon be repeated?")
    private boolean legendaryRepeat = true;

    @Comment("Will legendary Pokemon have particles?")
    private boolean legendaryParticle = true;

    @Comment("Will the blacklist of legendary Pokemon work?")
    private boolean blacklistLegendary = false;

    @Comment("List of Legendary Pokemon that cannot appear.")
    private List<String> blacklistLegendaryList = Lists.newArrayList("Regieleki", "Regidrago");

    @Comment("Will the blacklist of dimension be included?")
    private boolean blacklistDimensions = false;

    @Comment("Names of all excluded dimensions.")
    private List<String> blacklistDimensionList = Lists.newArrayList("the_nether", "the_end");

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

    public int getMaxPlayersIP() {
        return this.maxPlayersIP;
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

    public boolean isNotifyLegendarySpawn() {
        return this.notifyLegendarySpawn;
    }

    public boolean isLegendaryParticle() {
        return this.legendaryParticle;
    }

    public boolean isLegendaryRepeat() {
        return this.legendaryRepeat;
    }

    public boolean isBlacklistLegendary() {
        return this.blacklistLegendary;
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
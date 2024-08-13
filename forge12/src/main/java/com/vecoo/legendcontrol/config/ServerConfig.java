package com.vecoo.legendcontrol.config;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.legendcontrol.LegendControl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServerConfig {
    private int trustLimit;
    private int protectedTime;
    private float baseChance;
    private float stepSpawnChance;
    private int maxPlayersIP;
    private int lockPlayerIP;
    private int randomTimeSpawnMin;
    private int randomTimeSpawnMax;
    private boolean notifyLegendarySpawn;
    private boolean legendaryRepeat;
    private boolean blacklistLegendary;
    private boolean blacklistWorld;
    private List<String> blacklistWorldList;
    private transient List<String> blacklistWorldCache;
    private List<String> blacklistLegendaryList;
    private transient List<PokemonSpec> blacklistLegendaryCache;

    public ServerConfig() {
        this.trustLimit = 15;
        this.protectedTime = 300;
        this.baseChance = 10.0F;
        this.stepSpawnChance = 5.0F;
        this.maxPlayersIP = 3;
        this.lockPlayerIP = 5;
        this.randomTimeSpawnMin = 300;
        this.randomTimeSpawnMax = 600;
        this.notifyLegendarySpawn = true;
        this.legendaryRepeat = true;
        this.blacklistLegendary = false;
        this.blacklistWorld = false;
        this.blacklistWorldList = Lists.newArrayList("nether", "end");
        this.blacklistWorldCache = null;
        this.blacklistLegendaryList = Lists.newArrayList("regieleki", "regidrago");
        this.blacklistLegendaryCache = null;
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

    public boolean isBlacklistWorld() {
        return this.blacklistWorld;
    }

    public List<PokemonSpec> getBlockedLegendary() {
        if (this.blacklistLegendaryCache == null) {
            List<PokemonSpec> blocked = Lists.newArrayList();

            for (String blackList : this.blacklistLegendaryList) {
                blocked.add(new PokemonSpec(blackList));
            }
            this.blacklistLegendaryCache = blocked;
        }
        return this.blacklistLegendaryCache;
    }

    public List<String> getBlockedWorld() {
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

                this.trustLimit = config.getTrustLimit();
                this.protectedTime = config.getProtectedTime();
                this.baseChance = config.getBaseChance();
                this.stepSpawnChance = config.getStepSpawnChance();
                this.maxPlayersIP = config.getMaxPlayersIP();
                this.lockPlayerIP = config.getLockPlayerIP();
                this.randomTimeSpawnMin = config.getRandomTimeSpawnMin();
                this.randomTimeSpawnMax = config.getRandomTimeSpawnMax();
                this.notifyLegendarySpawn = config.isNotifyLegendarySpawn();
                this.legendaryRepeat = config.isLegendaryRepeat();
                this.blacklistLegendary = config.isBlacklistLegendary();
                this.blacklistWorld = config.isBlacklistWorld();
                this.blacklistWorldList = config.getBlockedWorld();
                this.blacklistLegendaryCache = config.getBlockedLegendary();
            });
            if (!future.join()) {
                write();
            }
        } catch (Exception e) {
            LegendControl.getLogger().error("Error in config.");
        }
    }
}
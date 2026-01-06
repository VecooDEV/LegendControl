package com.vecoo.legendcontrol.config;

import com.google.common.collect.Sets;
import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import lombok.Getter;

import java.util.Set;

@Getter
@ConfigSerializable
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
}
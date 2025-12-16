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
    private String particleName = "dragon_breath";
    private boolean notifyPersonalLegendarySpawn = true;
    private boolean notifyLegendaryCatch = true;
    private boolean notifyLegendaryDefeat = true;
    private boolean notifyLegendaryDespawn = true;
    private boolean legendaryRepeat = true;
    private boolean blacklistDimensions = false;
    private Set<String> blacklistDimensionList = Sets.newHashSet("the_nether", "the_end");
    private boolean blacklistPlayers = false;
    private Set<String> blacklistPlayersList = Sets.newHashSet("Player");
}
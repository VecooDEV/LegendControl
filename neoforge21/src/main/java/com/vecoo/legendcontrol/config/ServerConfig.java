package com.vecoo.legendcontrol.config;

import com.google.common.collect.Sets;
import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.meta.Comment;
import lombok.Getter;

import java.util.Set;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class ServerConfig {
    @Comment("The amount of seconds time before the pokemon location is announced. 0 to disable.")
    private int locationTime = 600;
    @Comment("he number of seconds until the legendary pokemon is completely despawn. 0 to disable.")
    private int despawnTime = 1800;
    @Comment("Base chance for legendary pokemon.")
    private float baseChance = 10.0F;
    @Comment("A chance added to the base chance upon failure.")
    private float stepSpawnChance = 5.0F;
    @Comment("The minimum number of seconds added to display the command time, it only changes the time visually.")
    private int randomTimeSpawnMin = 0;
    @Comment("The maximum number of seconds added to display the command time, it only changes the time visually.")
    private int randomTimeSpawnMax = 300;
    @Comment("Will legendary pokemon have particles?")
    private boolean legendaryParticle = true;
    @Comment("The name of the particle to display legendary pokemon.")
    private String particleName = "dragon_breath";
    @Comment("Notifying the player about the appearance of a legendary pokemon.")
    private boolean notifyPersonalLegendarySpawn = true;
    @Comment("Message about the capture of a legendary pokemon to the entire server.")
    private boolean notifyLegendaryCatch = true;
    @Comment("Message about the defeat of a legendary pokemon to the entire server.")
    private boolean notifyLegendaryDefeat = true;
    @Comment("Message about the despawn of a legendary pokemon to the entire server.")
    private boolean notifyLegendaryDespawn = true;
    @Comment("Will there be more legendary pokemon in a row?")
    private boolean legendaryRepeat = true;
    @Comment("Will the blacklist of dimension be included?")
    private boolean blacklistDimensions = false;
    @Comment("List of dimensions in which legendary pokemon will not appear.")
    private Set<String> blacklistDimensionList = Sets.newHashSet("the_nether", "the_end");
    @Comment("Will blacklisted players be ignored when a legend appears?")
    private boolean blacklistPlayers = false;
    @Comment("List of players who will not be able to spawn legendary pokemon.")
    private Set<String> blacklistPlayersList = Sets.newHashSet("Player");
}
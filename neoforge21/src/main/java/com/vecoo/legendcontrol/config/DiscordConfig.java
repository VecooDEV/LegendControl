package com.vecoo.legendcontrol.config;

import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import lombok.Getter;

@Getter
@ConfigSerializable
public class DiscordConfig {
    private String webhookUrl = "";
    private int webhookColor = 16711680;
    private long webhookRole = 0;
    private String webhookTitleLegendarySpawn = "%shiny%Legendary pokemon spawned!";
    private String webhookDescriptionLegendarySpawn = "The legendary pokemon **%pokemon%** to **%biome%** biome has appeared.";
    private String webhookTitleLegendaryCatch = "%shiny%Legendary pokemon catching!";
    private String webhookDescriptionLegendaryCatch = "The legendary pokemon **%pokemon%** was caught by a **%player%** player.";
    private String webhookTitleLegendaryDefeat = "%shiny%Legendary pokemon defeat!";
    private String webhookDescriptionLegendaryDefeat = "The legendary pokemon **%pokemon%** was defeated by a **%player%** player.";
    private String webhookTitleLegendaryDespawn = "%shiny%Legendary pokemon despawned!";
    private String webhookDescriptionLegendaryDespawn = "The legendary pokemon **%pokemon%** was despawned.";
    private String webhookTitleLegendaryLocation = "%shiny%Legendary pokemon seen location!";
    private String webhookDescriptionLegendaryLocation = "The legendary pokemon **%pokemon%** was location: X: %x%, Y: %y%, Z: %z%.";
    private String pokedexNormalUrl = "https://img.pokemondb.net/sprites/home/normal/%pokemon%.png";
    private String pokedexShinyUrl = "https://img.pokemondb.net/sprites/home/shiny/%pokemon%.png";
}

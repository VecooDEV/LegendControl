package com.vecoo.legendcontrol_defender.config;

import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import lombok.Getter;

@Getter
@ConfigSerializable
public class DiscordConfig {
    private String webhookUrl = "";
    private String webhookColor = "16711680";
    private String webhookTitleExpiredDefender = "%shiny%Legendary pokemon protection has expired!";
    private String webhookDescriptionExpiredDefender = "The legendary pokemon **%pokemon%** protection has expired.";
    private String pokedexNormalUrl = "https://img.pokemondb.net/sprites/home/normal/%pokemon%.png";
    private String pokedexShinyUrl = "https://img.pokemondb.net/sprites/home/shiny/%pokemon%.png";
}

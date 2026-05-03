package com.vecoo.legendcontrol_defender.config;

import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.meta.Comment;
import lombok.Getter;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class DiscordConfig {
    @Comment("Discord webhook for sending notifications. A restart is required.")
    private String webhookUrl = "";
    @Comment("Color for notification.")
    private int webhookColor = 16711680;
    private String webhookTitleExpiredDefender = "%shiny%Legendary pokemon protection has expired!";
    private String webhookDescriptionExpiredDefender = "The legendary pokemon **%pokemon%** protection has expired.";
    @Comment("The pokedex website where the notification image will be taken from.")
    private String pokedexNormalUrl = "https://img.pokemondb.net/sprites/home/normal/%pokemon%.png";
    @Comment("The shiny pokedex website where the notification image will be taken from.")
    private String pokedexShinyUrl = "https://img.pokemondb.net/sprites/home/shiny/%pokemon%.png";
}

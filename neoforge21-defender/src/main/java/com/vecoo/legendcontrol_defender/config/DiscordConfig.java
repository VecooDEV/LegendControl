package com.vecoo.legendcontrol_defender.config;

import com.pixelmonmod.pixelmon.api.config.api.data.ConfigPath;
import com.pixelmonmod.pixelmon.api.config.api.yaml.AbstractYamlConfig;
import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;

@ConfigPath("config/LegendControl/Defender/discord.yml")
@ConfigSerializable
public class DiscordConfig extends AbstractYamlConfig {
    private String webhookUrl = "";
    private String webhookColor = "16711680";
    private String webhookTitleExpiredDefender = "%shiny%Legendary pokemon protection has expired!";
    private String webhookDescriptionExpiredDefender = "The legendary pokemon **%pokemon%** protection has expired.";
    private String pokedexNormalUrl = "https://img.pokemondb.net/sprites/home/normal/%pokemon%.png";
    private String pokedexShinyUrl = "https://img.pokemondb.net/sprites/home/shiny/%pokemon%.png";

    public String getWebhookUrl() {
        return this.webhookUrl;
    }

    public String getWebhookColor() {
        return this.webhookColor;
    }

    public String getWebhookTitleExpiredDefender() {
        return this.webhookTitleExpiredDefender;
    }

    public String getWebhookDescriptionExpiredDefender() {
        return this.webhookDescriptionExpiredDefender;
    }

    public String getPokedexNormalUrl() {
        return this.pokedexNormalUrl;
    }

    public String getPokedexShinyUrl() {
        return this.pokedexShinyUrl;
    }
}

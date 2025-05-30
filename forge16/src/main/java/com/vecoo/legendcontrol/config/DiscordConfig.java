package com.vecoo.legendcontrol.config;

import com.pixelmonmod.pixelmon.api.config.api.data.ConfigPath;
import com.pixelmonmod.pixelmon.api.config.api.yaml.AbstractYamlConfig;
import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;

@ConfigPath("config/LegendControl/discord.yml")
@ConfigSerializable
public class DiscordConfig extends AbstractYamlConfig {
    private String webhookUrl = "";
    private String webhookColor = "16711680";
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

    public String getWebhookUrl() {
        return this.webhookUrl;
    }

    public String getWebhookColor() {
        return this.webhookColor;
    }

    public long getWebhookRole() {
        return this.webhookRole;
    }

    public String getWebhookTitleLegendarySpawn() {
        return this.webhookTitleLegendarySpawn;
    }

    public String getWebhookDescriptionLegendarySpawn() {
        return this.webhookDescriptionLegendarySpawn;
    }

    public String getWebhookTitleLegendaryCatch() {
        return this.webhookTitleLegendaryCatch;
    }

    public String getWebhookDescriptionLegendaryCatch() {
        return this.webhookDescriptionLegendaryCatch;
    }

    public String getWebhookDescriptionLegendaryDefeat() {
        return this.webhookDescriptionLegendaryDefeat;
    }

    public String getWebhookTitleLegendaryDefeat() {
        return this.webhookTitleLegendaryDefeat;
    }

    public String getWebhookTitleLegendaryDespawn() {
        return this.webhookTitleLegendaryDespawn;
    }

    public String getWebhookDescriptionLegendaryDespawn() {
        return this.webhookDescriptionLegendaryDespawn;
    }

    public String getWebhookTitleLegendaryLocation() {
        return this.webhookTitleLegendaryLocation;
    }

    public String getWebhookDescriptionLegendaryLocation() {
        return this.webhookDescriptionLegendaryLocation;
    }

    public String getPokedexNormalUrl() {
        return this.pokedexNormalUrl;
    }

    public String getPokedexShinyUrl() {
        return this.pokedexShinyUrl;
    }
}

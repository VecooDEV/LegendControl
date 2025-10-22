package com.vecoo.legendcontrol.config;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.legendcontrol.LegendControl;

public class DiscordConfig {
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

    private void write() {
        UtilGson.writeFileAsync("/config/LegendControl/", "discord.json", UtilGson.newGson().toJson(this)).join();
    }

    public void init() {
        boolean completed = UtilGson.readFileAsync("/config/LegendControl/", "discord.json", el -> {
            DiscordConfig config = UtilGson.newGson().fromJson(el, DiscordConfig.class);

            this.webhookUrl = config.getWebhookUrl();
            this.webhookColor = config.getWebhookColor();
            this.webhookRole = config.getWebhookRole();
            this.webhookTitleLegendarySpawn = config.getWebhookTitleLegendarySpawn();
            this.webhookDescriptionLegendarySpawn = config.getWebhookDescriptionLegendarySpawn();
            this.webhookTitleLegendaryCatch = config.getWebhookTitleLegendaryCatch();
            this.webhookDescriptionLegendaryCatch = config.getWebhookDescriptionLegendaryCatch();
            this.webhookTitleLegendaryDefeat = config.getWebhookTitleLegendaryDefeat();
            this.webhookDescriptionLegendaryDefeat = config.getWebhookDescriptionLegendaryCatch();
            this.webhookTitleLegendaryLocation = config.getWebhookTitleLegendaryLocation();
            this.webhookDescriptionLegendaryLocation = config.getWebhookDescriptionLegendaryLocation();
            this.webhookTitleLegendaryDespawn = config.getWebhookTitleLegendaryDespawn();
            this.pokedexNormalUrl = config.getPokedexNormalUrl();
            this.pokedexShinyUrl = config.getPokedexShinyUrl();
        }).join();

        if (!completed) {
            LegendControl.getLogger().error("Failed init discord config.");
            write();
        }
    }
}

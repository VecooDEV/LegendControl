package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.vecoo.extralib.world.UtilBiome;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.config.DiscordConfig;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nonnull;
import java.io.IOException;

public class WebhookUtils {
    public static void spawnWebhook(@Nonnull Pokemon pokemon, @Nonnull Biome biome) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (discordConfig.getWebhookUrl().isEmpty()) {
            return;
        }

        try {
            LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendarySpawn()
                            .replace("%shiny%", getShinyText(pokemon)),
                    discordConfig.getWebhookDescriptionLegendarySpawn()
                            .replace("%pokemon%", pokemon.getDisplayName())
                            .replace("%biome%", getBiomeText(biome)),
                    Utils.pokemonImage(pokemon), discordConfig.getWebhookColor(), true);
        } catch (IOException e) {
            LegendControl.getLogger().error("Error send Discord webhook", e);
        }
    }

    public static void defeatWebhook(@Nonnull Pokemon pokemon, @Nonnull String playerName) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (discordConfig.getWebhookUrl().isEmpty()) {
            return;
        }

        try {
            LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryDefeat()
                            .replace("%shiny%", getShinyText(pokemon)),
                    discordConfig.getWebhookDescriptionLegendaryDefeat()
                            .replace("%pokemon%", pokemon.getDisplayName())
                            .replace("%player%", playerName), Utils.pokemonImage(pokemon), discordConfig.getWebhookColor(), false);
        } catch (IOException e) {
            LegendControl.getLogger().error("Error send Discord webhook", e);
        }
    }

    public static void captureWebhook(@Nonnull Pokemon pokemon, @Nonnull String playerName) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (discordConfig.getWebhookUrl().isEmpty()) {
            return;
        }

        try {
            LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryCatch()
                            .replace("%shiny%", getShinyText(pokemon)),
                    discordConfig.getWebhookDescriptionLegendaryCatch()
                            .replace("%pokemon%", pokemon.getDisplayName())
                            .replace("%player%", playerName), Utils.pokemonImage(pokemon), discordConfig.getWebhookColor(), false);
        } catch (IOException e) {
            LegendControl.getLogger().error("Error send Discord webhook", e);
        }
    }

    public static void locationWebhook(@Nonnull EntityPixelmon pixelmonEntity) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (discordConfig.getWebhookUrl().isEmpty()) {
            return;
        }

        try {
            LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryLocation()
                            .replace("%shiny%", getShinyText(pixelmonEntity.getPokemonData())),
                    discordConfig.getWebhookDescriptionLegendaryLocation()
                            .replace("%pokemon%", pixelmonEntity.getPokemonName())
                            .replace("%x%", String.valueOf(pixelmonEntity.posX))
                            .replace("%y%", String.valueOf(pixelmonEntity.posY))
                            .replace("%z%", String.valueOf(pixelmonEntity.posZ)),
                    Utils.pokemonImage(pixelmonEntity.getPokemonData()), discordConfig.getWebhookColor(), false);
        } catch (IOException e) {
            LegendControl.getLogger().error("Error send Discord webhook", e);
        }
    }

    @Nonnull
    private static String getShinyText(@Nonnull Pokemon pokemon) {
        return pokemon.isShiny() ? ":star2: " : "";
    }

    @Nonnull
    private static String getBiomeText(@Nonnull Biome biome) {
        return biome.getRegistryName() == null ? "Unknown" : UtilBiome.getBiomeName(biome.getRegistryName().toString());
    }
}

package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.vecoo.extralib.world.UtilBiome;
import com.vecoo.legendcontrol.LegendControl;
import lombok.val;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nonnull;

public class WebhookUtils {
    public static void spawnWebhook(@Nonnull Pokemon pokemon, @Nonnull Biome biome) {
        val discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (!discordConfig.getWebhookUrl().isEmpty()) {
            LegendControl.getInstance().getDiscordWebhook().sendEmbed(discordConfig.getWebhookTitleLegendarySpawn()
                            .replace("%shiny%", getShinyText(pokemon)),
                    discordConfig.getWebhookDescriptionLegendarySpawn()
                            .replace("%pokemon%", pokemon.getTranslatedName().getFormattedText())
                            .replace("%biome%", UtilBiome.formatBiomeName(biome.getBiomeName())),
                    Utils.getPokemonImage(pokemon), discordConfig.getWebhookColor(), true);
        }
    }

    public static void defeatWebhook(@Nonnull Pokemon pokemon, @Nonnull String playerName) {
        val discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (!discordConfig.getWebhookUrl().isEmpty()) {
            LegendControl.getInstance().getDiscordWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryDefeat()
                            .replace("%shiny%", getShinyText(pokemon)),
                    discordConfig.getWebhookDescriptionLegendaryDefeat()
                            .replace("%pokemon%", pokemon.getTranslatedName().getFormattedText())
                            .replace("%player%", playerName),
                    Utils.getPokemonImage(pokemon), discordConfig.getWebhookColor(), false);
        }
    }

    public static void captureWebhook(@Nonnull Pokemon pokemon, @Nonnull String playerName) {
        val discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (!discordConfig.getWebhookUrl().isEmpty()) {
            LegendControl.getInstance().getDiscordWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryCatch()
                            .replace("%shiny%", getShinyText(pokemon)),
                    discordConfig.getWebhookDescriptionLegendaryCatch()
                            .replace("%pokemon%", pokemon.getTranslatedName().getFormattedText())
                            .replace("%player%", playerName),
                    Utils.getPokemonImage(pokemon), discordConfig.getWebhookColor(), false);
        }
    }

    public static void locationWebhook(@Nonnull EntityPixelmon entityPixelmon) {
        val discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (!discordConfig.getWebhookUrl().isEmpty()) {
            LegendControl.getInstance().getDiscordWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryLocation()
                            .replace("%shiny%", getShinyText(entityPixelmon.getPokemonData())),
                    discordConfig.getWebhookDescriptionLegendaryLocation()
                            .replace("%pokemon%", entityPixelmon.getPokemonName())
                            .replace("%x%", String.valueOf(entityPixelmon.posX))
                            .replace("%y%", String.valueOf(entityPixelmon.posY))
                            .replace("%z%", String.valueOf(entityPixelmon.posZ)),
                    Utils.getPokemonImage(entityPixelmon.getPokemonData()), discordConfig.getWebhookColor(), false);
        }
    }

    @Nonnull
    private static String getShinyText(@Nonnull Pokemon pokemon) {
        return pokemon.isShiny() ? ":star2: " : "";
    }

    @Nonnull
    private static String getBiomeText(@Nonnull Biome biome) {
        return biome.getRegistryName() == null ? "Unknown" : UtilBiome.formatBiomeName(biome.getRegistryName().toString());
    }
}

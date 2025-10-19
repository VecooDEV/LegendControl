package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.world.UtilBiome;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.config.DiscordConfig;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class WebhookUtils {
    public static void spawnWebhook(@NotNull Pokemon pokemon, @NotNull Holder<Biome> biome) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (!discordConfig.getWebhookUrl().isEmpty()) {
            try {
                LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendarySpawn()
                                .replace("%shiny%", getShinyText(pokemon)),
                        discordConfig.getWebhookDescriptionLegendarySpawn()
                                .replace("%pokemon%", pokemon.getTranslatedName().getString())
                                .replace("%biome%", UtilBiome.getBiomeName(biome.getRegisteredName())),
                        Utils.pokemonImage(pokemon), discordConfig.getWebhookColor(), true);
            } catch (IOException e) {
                LegendControl.getLogger().error("Error send Discord webhook", e);
            }
        }
    }

    public static void defeatWebhook(@NotNull Pokemon pokemon, @NotNull String playerName) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (!discordConfig.getWebhookUrl().isEmpty()) {
            try {
                LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryDefeat()
                                .replace("%shiny%", getShinyText(pokemon)),
                        discordConfig.getWebhookDescriptionLegendaryDefeat()
                                .replace("%pokemon%", pokemon.getTranslatedName().getString())
                                .replace("%player%", playerName), Utils.pokemonImage(pokemon), discordConfig.getWebhookColor(), false);
            } catch (IOException e) {
                LegendControl.getLogger().error("Error send Discord webhook", e);
            }
        }
    }

    public static void captureWebhook(@NotNull Pokemon pokemon, @NotNull String playerName) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (!discordConfig.getWebhookUrl().isEmpty()) {
            try {
                LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryCatch()
                                .replace("%shiny%", getShinyText(pokemon)),
                        discordConfig.getWebhookDescriptionLegendaryCatch()
                                .replace("%pokemon%", pokemon.getTranslatedName().getString())
                                .replace("%player%", playerName), Utils.pokemonImage(pokemon), discordConfig.getWebhookColor(), false);
            } catch (IOException e) {
                LegendControl.getLogger().error("Error send Discord webhook", e);
            }
        }
    }

    public static void despawnWebhook(@NotNull Pokemon pokemon) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (!discordConfig.getWebhookUrl().isEmpty()) {
            try {
                LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryDespawn()
                                .replace("%shiny%", getShinyText(pokemon)),
                        discordConfig.getWebhookDescriptionLegendaryDespawn()
                                .replace("%pokemon%", pokemon.getTranslatedName().getString()),
                        Utils.pokemonImage(pokemon), discordConfig.getWebhookColor(), false);
            } catch (IOException e) {
                LegendControl.getLogger().error("Error send Discord webhook", e);
            }
        }
    }

    public static void locationWebhook(@NotNull PixelmonEntity pixelmonEntity) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (!discordConfig.getWebhookUrl().isEmpty()) {
            try {
                LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryLocation()
                                .replace("%shiny%", getShinyText(pixelmonEntity.getPokemon())),
                        discordConfig.getWebhookDescriptionLegendaryLocation()
                                .replace("%pokemon%", pixelmonEntity.getPokemonName())
                                .replace("%x%", String.valueOf(pixelmonEntity.getBlockX()))
                                .replace("%y%", String.valueOf(pixelmonEntity.getBlockY()))
                                .replace("%z%", String.valueOf(pixelmonEntity.getBlockZ())),
                        Utils.pokemonImage(pixelmonEntity.getPokemon()), discordConfig.getWebhookColor(), false);
            } catch (IOException e) {
                LegendControl.getLogger().error("Error send Discord webhook", e);
            }
        }
    }

    @NotNull
    private static String getShinyText(@NotNull Pokemon pokemon) {
        return pokemon.isShiny() ? ":star2: " : "";
    }
}

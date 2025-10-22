package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
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
                            .replace("%pokemon%", pokemon.getTranslatedName().getString())
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
                            .replace("%pokemon%", pokemon.getTranslatedName().getString())
                            .replace("%player%", playerName),
                    Utils.pokemonImage(pokemon), discordConfig.getWebhookColor(), false);
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
                            .replace("%pokemon%", pokemon.getTranslatedName().getString())
                            .replace("%player%", playerName),
                    Utils.pokemonImage(pokemon), discordConfig.getWebhookColor(), false);
        } catch (IOException e) {
            LegendControl.getLogger().error("Error send Discord webhook", e);
        }
    }

    public static void despawnWebhook(@Nonnull Pokemon pokemon) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (discordConfig.getWebhookUrl().isEmpty()) {
            return;
        }

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

    public static void locationWebhook(@Nonnull PixelmonEntity pixelmonEntity) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (discordConfig.getWebhookUrl().isEmpty()) {
            return;
        }

        try {
            LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryLocation()
                            .replace("%shiny%", getShinyText(pixelmonEntity.getPokemon())),
                    discordConfig.getWebhookDescriptionLegendaryLocation()
                            .replace("%pokemon%", pixelmonEntity.getPokemonName())
                            .replace("%x%", String.valueOf(pixelmonEntity.getX()))
                            .replace("%y%", String.valueOf(pixelmonEntity.getY()))
                            .replace("%z%", String.valueOf(pixelmonEntity.getZ())),
                    Utils.pokemonImage(pixelmonEntity.getPokemon()), discordConfig.getWebhookColor(), false);
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

package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.world.UtilBiome;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.config.DiscordConfig;
import net.minecraft.world.biome.Biome;

import java.io.IOException;

public class WebhookUtils {
    public static void spawnWebhook(PixelmonEntity pixelmonEntity, Biome biome) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (!discordConfig.getWebhookUrl().isEmpty()) {
            try {
                LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendarySpawn()
                                .replace("%shiny%", getShinyText(pixelmonEntity)),
                        discordConfig.getWebhookDescriptionLegendarySpawn()
                                .replace("%pokemon%", pixelmonEntity.getPokemonName())
                                .replace("%biome%", getBiomeText(biome)), Utils.pokemonImage(pixelmonEntity), discordConfig.getWebhookColor(), true);
            } catch (IOException e) {
                LegendControl.getLogger().error("[LegendControl] Error send Discord webhook", e);
            }
        }
    }

    public static void defeatWebhook(PixelmonEntity pixelmonEntity, String playerName) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (!discordConfig.getWebhookUrl().isEmpty()) {
            try {
                LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryDefeat()
                                .replace("%shiny%", getShinyText(pixelmonEntity)),
                        discordConfig.getWebhookDescriptionLegendaryDefeat()
                                .replace("%pokemon%", pixelmonEntity.getPokemonName())
                                .replace("%player%", playerName), Utils.pokemonImage(pixelmonEntity), discordConfig.getWebhookColor(), false);
            } catch (IOException e) {
                LegendControl.getLogger().error("[LegendControl] Error send Discord webhook", e);
            }
        }
    }

    public static void captureWebhook(PixelmonEntity pixelmonEntity, String playerName) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (!discordConfig.getWebhookUrl().isEmpty()) {
            try {
                LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryCatch()
                                .replace("%shiny%", getShinyText(pixelmonEntity)),
                        discordConfig.getWebhookDescriptionLegendaryCatch()
                                .replace("%pokemon%", pixelmonEntity.getPokemonName())
                                .replace("%player%", playerName), Utils.pokemonImage(pixelmonEntity), discordConfig.getWebhookColor(), false);
            } catch (IOException e) {
                LegendControl.getLogger().error("[LegendControl] Error send Discord webhook", e);
            }
        }
    }

    public static void despawnWebhook(PixelmonEntity pixelmonEntity) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (!discordConfig.getWebhookUrl().isEmpty()) {
            try {
                LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryDespawn()
                                .replace("%shiny%", getShinyText(pixelmonEntity)),
                        discordConfig.getWebhookDescriptionLegendaryDespawn()
                                .replace("%pokemon%", pixelmonEntity.getPokemonName()), Utils.pokemonImage(pixelmonEntity), discordConfig.getWebhookColor(), false);
            } catch (IOException e) {
                LegendControl.getLogger().error("[LegendControl] Error send Discord webhook", e);
            }
        }
    }

    public static void locationWebhook(PixelmonEntity pixelmonEntity) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscordConfig();

        if (!discordConfig.getWebhookUrl().isEmpty()) {
            try {
                LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryLocation()
                                .replace("%shiny%", getShinyText(pixelmonEntity)),
                        discordConfig.getWebhookDescriptionLegendaryLocation()
                                .replace("%pokemon%", pixelmonEntity.getPokemonName())
                                .replace("%x%", String.valueOf((int) pixelmonEntity.getX()))
                                .replace("%y%", String.valueOf((int) pixelmonEntity.getY()))
                                .replace("%z%", String.valueOf((int) pixelmonEntity.getZ())), Utils.pokemonImage(pixelmonEntity), discordConfig.getWebhookColor(), false);
            } catch (IOException e) {
                LegendControl.getLogger().error("[LegendControl] Error send Discord webhook", e);
            }
        }
    }

    private static String getShinyText(PixelmonEntity pixelmonEntity) {
        return pixelmonEntity.getPokemon().isShiny() ? ":star2: " : "";
    }

    private static String getBiomeText(Biome biome) {
        return biome.getRegistryName() == null ? "Unknown" : UtilBiome.getBiomeName(biome.getRegistryName().toString());
    }
}

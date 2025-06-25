package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.extralib.world.UtilBiome;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.config.DiscordConfig;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;

import java.io.IOException;

public class WebhookUtils {
    public static void spawnWebhook(Pokemon pokemon, Holder<Biome> biome) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscord();

        if (discordConfig.getWebhookUrl().isEmpty()) {
            return;
        }

        try {
            LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendarySpawn()
                            .replace("%shiny%", pokemon.isShiny() ? ":star2: " : ""),
                    discordConfig.getWebhookDescriptionLegendarySpawn()
                            .replace("%pokemon%", pokemon.getTranslatedName().getString())
                            .replace("%biome%", UtilBiome.getBiomeName(biome.getRegisteredName())), Utils.pokemonImage(pokemon), discordConfig.getWebhookColor(), true);
        } catch (IOException e) {
            LegendControl.getLogger().error("[LegendControl] Error send Discord webhook", e);
        }
    }

    public static void defeatWebhook(Pokemon pokemon, ServerPlayer player) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscord();

        if (discordConfig.getWebhookUrl().isEmpty()) {
            return;
        }

        try {
            LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryDefeat()
                            .replace("%shiny%", pokemon.isShiny() ? ":star2: " : ""),
                    discordConfig.getWebhookDescriptionLegendaryDefeat()
                            .replace("%pokemon%", pokemon.getTranslatedName().getString())
                            .replace("%player%", player.getName().getString()), Utils.pokemonImage(pokemon), discordConfig.getWebhookColor(), false);
        } catch (IOException e) {
            LegendControl.getLogger().error("[LegendControl] Error send Discord webhook", e);
        }
    }

    public static void captureWebhook(Pokemon pokemon, ServerPlayer player) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscord();

        if (discordConfig.getWebhookUrl().isEmpty()) {
            return;
        }

        try {
            LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryCatch()
                            .replace("%shiny%", pokemon.isShiny() ? ":star2: " : ""),
                    discordConfig.getWebhookDescriptionLegendaryCatch()
                            .replace("%pokemon%", pokemon.getTranslatedName().getString())
                            .replace("%player%", player.getName().getString()), Utils.pokemonImage(pokemon), discordConfig.getWebhookColor(), false);
        } catch (IOException e) {
            LegendControl.getLogger().error("[LegendControl] Error send Discord webhook", e);
        }
    }

    public static void despawnWebhook(Pokemon pokemon) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscord();

        if (discordConfig.getWebhookUrl().isEmpty()) {
            return;
        }

        try {
            LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryDespawn()
                            .replace("%shiny%", pokemon.isShiny() ? ":star2: " : ""),
                    discordConfig.getWebhookDescriptionLegendaryDespawn()
                            .replace("%pokemon%", pokemon.getTranslatedName().getString()), Utils.pokemonImage(pokemon), discordConfig.getWebhookColor(), false);
        } catch (IOException e) {
            LegendControl.getLogger().error("[LegendControl] Error send Discord webhook", e);
        }
    }

    public static void locationWebhook(PixelmonEntity pixelmonEntity) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscord();

        if (discordConfig.getWebhookUrl().isEmpty()) {
            return;
        }

        try {
            LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryLocation()
                            .replace("%shiny%", pixelmonEntity.getPokemon().isShiny() ? ":star2: " : ""),
                    discordConfig.getWebhookDescriptionLegendaryLocation()
                            .replace("%pokemon%", pixelmonEntity.getPokemonName())
                            .replace("%x%", String.valueOf((int) pixelmonEntity.getX()))
                            .replace("%y%", String.valueOf((int) pixelmonEntity.getY()))
                            .replace("%z%", String.valueOf((int) pixelmonEntity.getZ())), Utils.pokemonImage(pixelmonEntity.getPokemon()), discordConfig.getWebhookColor(), false);
        } catch (IOException e) {
            LegendControl.getLogger().error("[LegendControl] Error send Discord webhook", e);
        }
    }
}

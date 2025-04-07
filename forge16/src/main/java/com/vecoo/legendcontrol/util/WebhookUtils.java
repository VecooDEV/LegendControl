package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.config.DiscordConfig;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.io.IOException;

public class WebhookUtils {
    public static void spawnWebhook(PixelmonEntity pixelmonEntity) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscord();

        if (discordConfig.getWebhookUrl().isEmpty()) {
            return;
        }

        try {
            LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendarySpawn()
                            .replace("%shiny%", pixelmonEntity.getPokemon().isShiny() ? ":star2: " : ""),
                    discordConfig.getWebhookDescriptionLegendarySpawn()
                            .replace("%pokemon%", pixelmonEntity.getPokemonName()),
                    Utils.pokemonImage(pixelmonEntity),
                    discordConfig.getWebhookColor());
        } catch (IOException e) {
            LegendControl.getLogger().error("[LegendControl] Error send Discord webhook", e);
        }
    }

    public static void defeatWebhook(PixelmonEntity pixelmonEntity, ServerPlayerEntity player) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscord();

        if (discordConfig.getWebhookUrl().isEmpty()) {
            return;
        }

        try {
            LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryDefeat()
                            .replace("%shiny%", pixelmonEntity.getPokemon().isShiny() ? ":star2: " : ""),
                    discordConfig.getWebhookDescriptionLegendaryDefeat()
                            .replace("%pokemon%", pixelmonEntity.getPokemonName())
                            .replace("%player%", player.getName().getString()),
                    Utils.pokemonImage(pixelmonEntity),
                    discordConfig.getWebhookColor());
        } catch (IOException e) {
            LegendControl.getLogger().error("[LegendControl] Error send Discord webhook", e);
        }
    }

    public static void captureWebhook(PixelmonEntity pixelmonEntity, ServerPlayerEntity player) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscord();

        if (discordConfig.getWebhookUrl().isEmpty()) {
            return;
        }

        try {
            LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryCatch()
                            .replace("%shiny%", pixelmonEntity.getPokemon().isShiny() ? ":star2: " : ""),
                    discordConfig.getWebhookDescriptionLegendaryCatch()
                            .replace("%pokemon%", pixelmonEntity.getPokemonName())
                            .replace("%player%", player.getName().getString()),
                    Utils.pokemonImage(pixelmonEntity),
                    discordConfig.getWebhookColor());
        } catch (IOException e) {
            LegendControl.getLogger().error("[LegendControl] Error send Discord webhook", e);
        }
    }

    public static void despawnWebhook(PixelmonEntity pixelmonEntity) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscord();

        if (discordConfig.getWebhookUrl().isEmpty()) {
            return;
        }

        try {
            LegendControl.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleLegendaryDespawn()
                            .replace("%shiny%", pixelmonEntity.getPokemon().isShiny() ? ":star2: " : ""),
                    discordConfig.getWebhookDescriptionLegendaryDespawn()
                            .replace("%pokemon%", pixelmonEntity.getPokemonName()),
                    Utils.pokemonImage(pixelmonEntity),
                    discordConfig.getWebhookColor());
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
                            .replace("%z%", String.valueOf((int) pixelmonEntity.getZ())),
                    Utils.pokemonImage(pixelmonEntity),
                    discordConfig.getWebhookColor());
        } catch (IOException e) {
            LegendControl.getLogger().error("[LegendControl] Error send Discord webhook", e);
        }
    }
}

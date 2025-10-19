package com.vecoo.legendcontrol_defender.util;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.config.DiscordConfig;

import javax.annotation.Nonnull;
import java.io.IOException;

public class WebhookUtils {
    public static void defenderExpiredWebhook(@Nonnull PixelmonEntity pixelmonEntity) {
        DiscordConfig discordConfig = LegendControlDefender.getInstance().getDiscordConfig();

        if (!discordConfig.getWebhookUrl().isEmpty()) {
            try {
                LegendControlDefender.getInstance().getWebhook().sendEmbed(discordConfig.getWebhookTitleExpiredDefender()
                                .replace("%shiny%", getShinyText(pixelmonEntity)),
                        discordConfig.getWebhookDescriptionExpiredDefender()
                                .replace("%pokemon%", pixelmonEntity.getPokemonName()),
                        Utils.pokemonImage(pixelmonEntity), discordConfig.getWebhookColor());
            } catch (IOException e) {
                LegendControlDefender.getLogger().error("Error send Discord webhook", e);
            }
        }
    }

    @Nonnull
    private static String getShinyText(@Nonnull PixelmonEntity pixelmonEntity) {
        return pixelmonEntity.getPokemon().isShiny() ? ":star2: " : "";
    }
}

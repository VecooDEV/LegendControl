package com.vecoo.legendcontrol_defender.util;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import lombok.val;

import javax.annotation.Nonnull;

public class WebhookUtils {
    public static void defenderExpiredWebhook(@Nonnull EntityPixelmon entityPixelmon) {
        val discordConfig = LegendControlDefender.getInstance().getDiscordConfig();

        if (!discordConfig.getWebhookUrl().isEmpty()) {
            LegendControlDefender.getInstance().getDiscordWebhook().sendEmbed(discordConfig.getWebhookTitleExpiredDefender()
                            .replace("%shiny%", getShinyText(entityPixelmon)),
                    discordConfig.getWebhookDescriptionExpiredDefender()
                            .replace("%pokemon%", entityPixelmon.getPokemonName()),
                    Utils.getPokemonImage(entityPixelmon), discordConfig.getWebhookColor());
        }
    }

    @Nonnull
    private static String getShinyText(@Nonnull EntityPixelmon entityPixelmon) {
        return entityPixelmon.getPokemonData().isShiny() ? ":star2: " : "";
    }
}

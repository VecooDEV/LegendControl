package com.vecoo.legendcontrol_defender.util;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import lombok.val;
import org.jetbrains.annotations.NotNull;

public class WebhookUtils {
    public static void defenderExpiredWebhook(@NotNull PixelmonEntity pixelmonEntity) {
        val discordConfig = LegendControlDefender.getInstance().getDiscordConfig();

        if (!discordConfig.getWebhookUrl().isEmpty()) {
            LegendControlDefender.getInstance().getDiscordWebhook().sendEmbed(discordConfig.getWebhookTitleExpiredDefender()
                            .replace("%shiny%", getShinyText(pixelmonEntity)),
                    discordConfig.getWebhookDescriptionExpiredDefender()
                            .replace("%pokemon%", pixelmonEntity.getPokemonName()),
                    Utils.getPokemonImage(pixelmonEntity), discordConfig.getWebhookColor());
        }
    }

    @NotNull
    private static String getShinyText(@NotNull PixelmonEntity pixelmonEntity) {
        return pixelmonEntity.getPokemon().isShiny() ? ":star2: " : "";
    }
}

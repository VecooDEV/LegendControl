package com.vecoo.legendcontrol_defender.util;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.config.DiscordConfig;

import javax.annotation.Nonnull;

public class Utils {
    @Nonnull
    public static String pokemonImage(@Nonnull PixelmonEntity pixelmonEntity) {
        DiscordConfig discordConfig = LegendControlDefender.getInstance().getDiscordConfig();
        String pokemonName = pixelmonEntity.getPokemonName().toLowerCase();

        switch (pokemonName) {
            case "typenull": {
                pokemonName = "type-null";
                break;
            }

            case "tapukoko": {
                pokemonName = "tapu-koko";
                break;
            }

            case "tapulele": {
                pokemonName = "tapu-lele";
                break;
            }

            case "tapubulu": {
                pokemonName = "tapu-bulu";
                break;
            }

            case "tapufini": {
                pokemonName = "tapu-fini";
                break;
            }
        }

        return pixelmonEntity.getPokemon().isShiny() ? discordConfig.getPokedexShinyUrl()
                .replace("%pokemon%", pokemonName) : discordConfig.getPokedexNormalUrl().replace("%pokemon%", pokemonName);
    }
}
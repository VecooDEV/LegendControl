package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.RandomHelper;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.config.DiscordConfig;

import javax.annotation.Nonnull;

public class Utils {
    public static int TIME_DO_LEGEND = RandomHelper.getRandomNumberBetween(
            LegendControl.getInstance().getConfig().getRandomTimeSpawnMin(),
            LegendControl.getInstance().getConfig().getRandomTimeSpawnMax()
    );

    @Nonnull
    public static String pokemonImage(@Nonnull Pokemon pokemon) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscordConfig();
        String pokemonName = pokemon.getTranslatedName().toString().toLowerCase();

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

        return pokemon.isShiny() ? discordConfig.getPokedexShinyUrl().replace("%pokemon%", pokemonName)
                : discordConfig.getPokedexNormalUrl().replace("%pokemon%", pokemonName);
    }
}
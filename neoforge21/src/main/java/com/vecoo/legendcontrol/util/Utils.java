package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.config.DiscordConfig;

public class Utils {
    public static int timeDoLegend = RandomHelper.getRandomNumberBetween(LegendControl.getInstance().getConfig().getRandomTimeSpawnMin(), LegendControl.getInstance().getConfig().getRandomTimeSpawnMax());

    public static String pokemonImage(Pokemon pokemon) {
        DiscordConfig discordConfig = LegendControl.getInstance().getDiscord();
        String pokemonName = pokemon.getTranslatedName().getString().toLowerCase();

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

        return pokemon.isShiny() ? discordConfig.getPokedexShinyUrl().replace("%pokemon%", pokemonName) : discordConfig.getPokedexNormalUrl().replace("%pokemon%", pokemonName);
    }
}
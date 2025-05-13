package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.legendcontrol.LegendControl;

public class Utils {
    public static int timeDoLegend = RandomHelper.getRandomNumberBetween(LegendControl.getInstance().getConfig().getRandomTimeSpawnMin(), LegendControl.getInstance().getConfig().getRandomTimeSpawnMax());

    public static String pokemonImage(PixelmonEntity pixelmonEntity) {
        String url = pixelmonEntity.getPokemon().isShiny() ? "https://img.pokemondb.net/sprites/home/shiny/%pokemon%.png" : "https://img.pokemondb.net/sprites/home/normal/%pokemon%.png";

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

        return url.replace("%pokemon%", pokemonName);
    }
}
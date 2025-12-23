package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.vecoo.legendcontrol.LegendControl;
import lombok.val;
import org.jetbrains.annotations.NotNull;

public class Utils {
    public static int TIME_DO_LEGEND = RandomHelper.getRandomNumberBetween(
            LegendControl.getInstance().getServerConfig().getRandomTimeSpawnMin(),
            LegendControl.getInstance().getServerConfig().getRandomTimeSpawnMax()
    );

    @NotNull
    public static String getPokemonImage(@NotNull Pokemon pokemon) {
        val discordConfig = LegendControl.getInstance().getDiscordConfig();
        var pokemonName = pokemon.getSpecies().getName().toLowerCase();
        LegendControl.getLogger().error(pokemonName);

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

    @NotNull
    public static String formatFloat(float value) {
        var format = String.format("%.3f", value).replaceAll("\\.?0+$", "");

        if (format.endsWith(",")) {
            format = format.replace(",", "");
        }

        return format;
    }
}
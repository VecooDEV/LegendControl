package com.vecoo.legendcontrol_defender.util;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import lombok.val;
import lombok.var;

import javax.annotation.Nonnull;

public class Utils {
    @Nonnull
    public static String getPokemonImage(@Nonnull EntityPixelmon entityPixelmon) {
        val discordConfig = LegendControlDefender.getInstance().getDiscordConfig();
        var pokemonName = entityPixelmon.getSpecies().getPokemonName().toLowerCase();

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

        return entityPixelmon.getPokemonData().isShiny() ? discordConfig.getPokedexShinyUrl()
                .replace("%pokemon%", pokemonName) : discordConfig.getPokedexNormalUrl().replace("%pokemon%", pokemonName);
    }
}

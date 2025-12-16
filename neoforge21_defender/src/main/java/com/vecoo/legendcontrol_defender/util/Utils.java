package com.vecoo.legendcontrol_defender.util;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import lombok.val;
import org.jetbrains.annotations.NotNull;

public class Utils {
    @NotNull
    public static String getPokemonImage(@NotNull PixelmonEntity pixelmonEntity) {
        val discordConfig = LegendControlDefender.getInstance().getDiscordConfig();
        var pokemonName = pixelmonEntity.getPokemonName().toLowerCase();

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
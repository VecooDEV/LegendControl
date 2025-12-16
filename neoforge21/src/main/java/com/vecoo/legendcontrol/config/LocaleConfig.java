package com.vecoo.legendcontrol.config;

import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import lombok.Getter;

@Getter
@ConfigSerializable
public class LocaleConfig {
    private String reload = "&e(!) Configs have been reloaded.";
    private String location = "&e(!) Location of Pokemon %pokemon%: X: %x%, Y: %y%, Z: %z%.";
    private String notifyCatch = "&e(!) Player %player% catch to legendary pokemon %pokemon%!";
    private String notifyDefeat = "&e(!) Player %player% defeat to legendary pokemon %pokemon%!";
    private String notifyDespawn = "&e(!) Legendary pokemon %pokemon% despawned!";
    private String spawnPlayerLegendary = "&e(!) A legendary Pokemon has appeared near you.";
    private String checkLegendary = "&e(!) Chance %chance%%, legendary Pokemon will appear in ~%time%.";
    private String changeChanceLegendary = "&e(!) New chance: %chance%%.";

    private String errorReload = "&c(!) Reload error, checking console and fixes config.";
    private String errorChance = "&c(!) The overall chance should not be less than 0% or greater than 100%.";

    private String seconds = " seconds";
    private String minutes = " minutes";
    private String hours = " hours";
}
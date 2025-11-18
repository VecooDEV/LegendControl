package com.vecoo.legendcontrol.config;

import com.pixelmonmod.pixelmon.api.config.api.data.ConfigPath;
import com.pixelmonmod.pixelmon.api.config.api.yaml.AbstractYamlConfig;
import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;

@ConfigPath("config/LegendControl/locale.yml")
@ConfigSerializable
public class LocaleConfig extends AbstractYamlConfig {
    private String reload = "&e(!) Configs have been reloaded.";
    private String location = "&e(!) Location of Pokemon %pokemon%: X: %x%, Y: %y%, Z: %z%.";
    private String notifyCatch = "&e(!) Player %player% catch to legendary pokemon %pokemon%!";
    private String notifyDefeat = "&e(!) Player %player% defeat to legendary pokemon %pokemon%!";
    private String notifyDespawn = "&e(!) Legendary pokemon %pokemon% despawned!";
    private String spawnPlayerLegendary = "&e(!) A legendary Pokemon has appeared near you.";
    private String checkLegendary = "&e(!) Chance %chance%%, legendary Pokemon will appear in ~%time%.";
    private String changeChanceLegendary = "&e(!) New chance: %chance%%.";

    private String errorChance = "&c(!) The overall chance should not be less than 0% or greater than 100%.";

    private String seconds = " seconds";
    private String minutes = " minutes";
    private String hours = " hours";

    public String getReload() {
        return this.reload;
    }

    public String getNotifyCatch() {
        return this.notifyCatch;
    }

    public String getNotifyDefeat() {
        return this.notifyDefeat;
    }

    public String getNotifyDespawn() {
        return this.notifyDespawn;
    }

    public String getLocation() {
        return this.location;
    }

    public String getSpawnPlayerLegendary() {
        return this.spawnPlayerLegendary;
    }

    public String getCheckLegendary() {
        return this.checkLegendary;
    }

    public String getChangeChanceLegendary() {
        return this.changeChanceLegendary;
    }

    public String getErrorChance() {
        return this.errorChance;
    }

    public String getSeconds() {
        return this.seconds;
    }

    public String getMinutes() {
        return this.minutes;
    }

    public String getHours() {
        return this.hours;
    }
}
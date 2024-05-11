package com.vecoo.legendcontrol.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigPath("config/LegendControl/locale.yml")
@ConfigSerializable
public class LocaleConfig extends AbstractYamlConfig {
    private Messages messages = new Messages();

    public LocaleConfig() {
        super();
    }

    public Messages getMessages() {
        return this.messages;
    }

    @ConfigSerializable
    public static class Messages {
        private String reload = "&e(!) Configs have been reloaded.";

        private String addTrust = "&e(!) Player %player% has been successfully added to your trust list.";
        private String removeTrust = "&e(!) Player %player% has been removed from your trust list.";
        private String removeAllTrust = "&e(!) All players have been removed from your trust list.";
        private String emptyTrust = "&e(!) Your trust list is empty.";
        private String listTrustTitle = "&e&lTrust list (%amount%)";
        private String listTrust = "&e- %player%";
        private String incorrectCause = "&e(!) At the moment, the legendary Pokemon is protected. Try to enter the battle after a while.";
        private String protection = "&e(!) Pokemon %pokemon%'s protection has expired.";

        private String spawnPlayerLegendary = "&e(!) A legendary Pokemon has appeared near you!";
        private String checkLegendary = "&e(!) Chance: %chance% (%time1%-%time2%)";
        private String changeChanceLegendary = "&e(!) New chance: %chance%.";

        private String cantSelfTrust = "&c(!) You cannot add yourself to the trust list.";
        private String alreadyTrusted = "&c(!) This player is already on your trust list.";
        private String notPlayerTrust = "&c(!) This player is not yet on your trust list.";
        private String trustLimit = "&c(!) You have reached the maximum number of players on the trust list.";
        private String errorChance = "&c(!) The overall chance should not be less than 0% or greater than 100%.";
        private String playerNotFound = "&c(!) Player %target% not found.";

        private String seconds = " seconds";
        private String minutes = " minutes";
        private String hours = " hours";

        public Messages() {
        }

        public String getReload() {
            return this.reload;
        }

        public String getCantSelfTrust() {
            return this.cantSelfTrust;
        }

        public String getAlreadyTrusted() {
            return this.alreadyTrusted;
        }

        public String getAddTrust() {
            return this.addTrust;
        }

        public String getNotPlayerTrust() {
            return this.notPlayerTrust;
        }

        public String getRemoveTrust() {
            return this.removeTrust;
        }

        public String getRemoveAllTrust() {
            return this.removeAllTrust;
        }

        public String getEmptyTrust() {
            return this.emptyTrust;
        }

        public String getListTrustTitle() {
            return this.listTrustTitle;
        }

        public String getTrustLimit() {
            return this.trustLimit;
        }

        public String getListTrust() {
            return this.listTrust;
        }

        public String getIncorrectCause() {
            return this.incorrectCause;
        }

        public String getProtection() {
            return this.protection;
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

        public String getPlayerNotFound() {
            return this.playerNotFound;
        }
    }
}
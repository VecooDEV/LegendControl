package com.vecoo.legendcontrol.config;

import com.pixelmonmod.pixelmon.api.config.api.data.ConfigPath;
import com.pixelmonmod.pixelmon.api.config.api.yaml.AbstractYamlConfig;
import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;

@ConfigPath("config/LegendControl/locale.yml")
@ConfigSerializable
public class LocaleConfig extends AbstractYamlConfig {
    private final Messages messages = new Messages();

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
        private String listTrust = "&e&lTrust list:";
        private String incorrectCause = "&e(!) At the moment, the legendary Pokemon is protected. Try to enter the battle after a while.";
        private String protection = "&e(!) Pokemon %pokemon%'s protection has expired.";
        private String location = "&e(!) Location of Pokemon %pokemon%: X: %x%, Y: %y%, Z: %z%.";

        private String blacklist = "&e&lPlayers missing legend spawns";
        private String addBlacklist = "&e(!) Player %player% has been added from the legendary blacklist.";
        private String removeBlacklist = "&e(!) Player %player% has been removed from the legendary blacklist.";
        private String removeAllBlacklist = "&e(!) The all players has been removed from the legendary blacklist.";
        private String emptyBlacklist = "&e(!) Legendary blacklist is empty.";

        private String spawnPlayerLegendary = "&e(!) A legendary Pokemon has appeared near you!";
        private String checkLegendary = "&e(!) Chance %chance%, legendary Pokemon will appear in ~%time%.";
        private String changeChanceLegendary = "&e(!) New chance: %chance%.";

        private String cantSelfTrust = "&c(!) You cannot add yourself to the trust list.";
        private String alreadyTrusted = "&c(!) This player is already on your trust list.";
        private String notPlayerTrust = "&c(!) This player is not yet on your trust list.";
        private String notPlayerBlacklist = "&c(!) This player is not yet on legendary blacklist.";
        private String alreadyBlacklist = "&c(!) This player is already on legendary blacklist.";
        private String trustLimit = "&c(!) You have reached the maximum number of players on the trust list.";
        private String errorChance = "&c(!) The overall chance should not be less than 0% or greater than 100%.";
        private String playerNotFound = "&c(!) Player %player% not found.";

        private String listPlayer = "&e- %player%";

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

        public String getListTrust() {
            return this.listTrust;
        }

        public String getTrustLimit() {
            return this.trustLimit;
        }

        public String getListPlayer() {
            return this.listPlayer;
        }

        public String getBlacklist() {
            return this.blacklist;
        }

        public String getAddBlacklist() {
            return this.addBlacklist;
        }

        public String getRemoveBlacklist() {
            return this.removeBlacklist;
        }

        public String getRemoveAllBlacklist() {
            return this.removeAllBlacklist;
        }

        public String getEmptyBlacklist() {
            return this.emptyBlacklist;
        }

        public String getNotPlayerBlacklist() {
            return this.notPlayerBlacklist;
        }

        public String getLocation() {
            return this.location;
        }

        public String getAlreadyBlacklist() {
            return this.alreadyBlacklist;
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

        public String getPlayerNotFound() {
            return this.playerNotFound;
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
}
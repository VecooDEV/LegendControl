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
        private String reloadedConfigs = "&e(!) Configs have been reloaded.";

        private String cantSelfTrust = "&c(!) You cannot add yourself to the trust list.";
        private String alreadyTrusted = "&c(!) This player is already on your trust list.";
        private String playerAdded = "&e(!) Player %player% has been successfully added to your trust list.";
        private String notPlayerTrust = "&c(!) This player is not yet on your trust list.";
        private String playerRemoved = "&e(!) Player %player% has been removed from your trust list.";
        private String removedAll = "&e(!) All players have been removed from your trust list.";
        private String emptyTrust = "&e(!) Your trust list is empty.";
        private String listingPlayers = "&e&lTrust list (%amount%)";
        private String trustLimit = "&c(!) You have reached the maximum number of players on the trust list.";
        private String trustList = "&e- %player%";
        private String incorrectCause = "&e(!) At the moment, the legendary Pokemon is protected. Try to enter the battle after a while.";
        private String protection = "&e(!) Pokemon %pokemon%'s protection has expired.";

        private String spawnPlayerLegendary = "&e(!) A legendary Pokemon has appeared near you!";
        private String checkLegendary = "&e(!) Chance: %chance% (%time1%-%time2% minutes)";
        private String changeChanceLegendary = "&e(!) New chance: %chance%.";
        private String errorChance = "&c(!) Try another chance.";

        public Messages() {
        }

        public String getReloadedConfigs() {
            return this.reloadedConfigs;
        }

        public String getCantSelfTrust() {
            return this.cantSelfTrust;
        }

        public String getAlreadyTrusted() {
            return this.alreadyTrusted;
        }

        public String getPlayerAdded() {
            return this.playerAdded;
        }

        public String getNotPlayerTrust() {
            return this.notPlayerTrust;
        }

        public String getPlayerRemoved() {
            return this.playerRemoved;
        }

        public String getRemovedAll() {
            return this.removedAll;
        }

        public String getEmptyTrust() {
            return this.emptyTrust;
        }

        public String getListingPlayers() {
            return this.listingPlayers;
        }

        public String getTrustLimit() {
            return this.trustLimit;
        }

        public String getTrustList() {
            return this.trustList;
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
    }
}
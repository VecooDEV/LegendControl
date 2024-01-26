package com.vecoo.legendcontrol.config;

import com.pixelmonmod.pixelmon.api.config.api.data.ConfigPath;
import com.pixelmonmod.pixelmon.api.config.api.yaml.AbstractYamlConfig;
import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;

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
        private String reload = "(!) Configs have been reloaded.";

        private String addTrust = "(!) Player %player% has been successfully added to your trust list.";
        private String removeTrust = "(!) Player %player% has been removed from your trust list.";
        private String removeAllTrust = "(!) All players have been removed from your trust list.";
        private String emptyTrust = "(!) Your trust list is empty.";
        private String listTrustTitle = "Trust list (%amount%)";
        private String listTrust = "- %player%";
        private String incorrectCause = "(!) At the moment, the legendary Pokemon is protected. Try to enter the battle after a while.";
        private String protection = "(!) Pokemon %pokemon%'s protection has expired.";

        private String spawnPlayerLegendary = "(!) A legendary Pokemon has appeared near you!";
        private String checkLegendary = "(!) Chance: %chance% (%time1%-%time2%)";
        private String changeChanceLegendary = "(!) New chance: %chance%.";

        private String cantSelfTrust = "(!) You cannot add yourself to the trust list.";
        private String alreadyTrusted = "(!) This player is already on your trust list.";
        private String notPlayerTrust = "(!) This player is not yet on your trust list.";
        private String trustLimit = "(!) You have reached the maximum number of players on the trust list.";
        private String errorChance = "(!) The total chance should not exceed 100.";
        private String errorConfig = "(!) Set the chance of legendary Pokemon to 100% in Pixelmon configs or disable the modified system of appearance of legendary Pokemon in the mod.";

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

        public String getErrorConfig() {
            return this.errorConfig;
        }
    }
}
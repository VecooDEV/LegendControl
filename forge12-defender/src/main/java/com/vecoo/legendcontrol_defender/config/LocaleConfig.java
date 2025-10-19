package com.vecoo.legendcontrol_defender.config;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.legendcontrol_defender.LegendControlDefender;

public class LocaleConfig {
    private String reload = "&e(!) Configs have been reloaded.";

    private String addTrust = "&e(!) Player %player% has been successfully added to your trust list.";
    private String removeTrust = "&e(!) Player %player% has been removed from your trust list.";
    private String removeAllTrust = "&e(!) All players have been removed from your trust list.";
    private String emptyTrust = "&e(!) Your trust list is empty.";
    private String listTrust = "&e&lTrust list:";
    private String incorrectCause = "&e(!) At the moment, the legendary Pokemon is protected. Try to enter the battle after a while.";
    private String protection = "&e(!) Pokemon %pokemon% protection has expired.";

    private String trustedPlayers = "&e- %player%";

    private String cantSelfTrust = "&c(!) You cannot add yourself to the trust list.";
    private String alreadyTrusted = "&c(!) This player is already on your trust list.";
    private String notPlayerTrust = "&c(!) This player is not yet on your trust list.";
    private String trustLimit = "&c(!) You have reached the maximum number of players on the trust list.";
    private String playerNotFound = "&c(!) Player %player% not found.";

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

    public String getTrustedPlayers() {
        return this.trustedPlayers;
    }

    public String getIncorrectCause() {
        return this.incorrectCause;
    }

    public String getProtection() {
        return this.protection;
    }

    public String getPlayerNotFound() {
        return this.playerNotFound;
    }

    private void write() {
        UtilGson.writeFileAsync("/config/LegendControl/Defender/", "locale.json", UtilGson.newGson().toJson(this)).join();
    }

    public void init() {
        boolean completed = UtilGson.readFileAsync("/config/LegendControl/Defender/", "locale.json", el -> {
            LocaleConfig config = UtilGson.newGson().fromJson(el, LocaleConfig.class);

            this.addTrust = config.getAddTrust();
            this.removeTrust = config.getRemoveTrust();
            this.removeAllTrust = config.getRemoveAllTrust();
            this.emptyTrust = config.getEmptyTrust();
            this.listTrust = config.getListTrust();
            this.trustLimit = config.getTrustLimit();
            this.trustedPlayers = config.getTrustedPlayers();
            this.incorrectCause = config.getIncorrectCause();
            this.protection = config.getProtection();
            this.listTrust = config.getListTrust();
            this.playerNotFound = config.getPlayerNotFound();
            this.notPlayerTrust = config.getNotPlayerTrust();
            this.reload = config.getReload();
            this.alreadyTrusted = config.getAlreadyTrusted();
            this.cantSelfTrust = config.getCantSelfTrust();
        }).join();

        if (!completed) {
            LegendControlDefender.getLogger().error("[LegendControl-Defender] Error in locale config.");
            write();
        }
    }
}
package com.vecoo.legendcontrol.config;

import com.google.gson.Gson;
import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.legendcontrol.LegendControl;

import java.util.concurrent.CompletableFuture;

public class LocaleConfig {
    private String reload = "\u00A7e(!) Configs have been reloaded.";

    private String addTrust = "\u00A7e(!) Player %target% has been successfully added to your trust list.";
    private String removeTrust = "\u00A7e(!) Player %target% has been removed from your trust list.";
    private String removeAllTrust = "\u00A7e(!) All players have been removed from your trust list.";
    private String emptyTrust = "\u00A7e(!) Your trust list is empty.";
    private String listTrust = "&\u00A7e&lTrust list (%amount%)";
    private String incorrectCause = "\u00A7e(!) At the moment, the legendary Pokemon is protected. Try to enter the battle after a while.";
    private String protection = "\u00A7e(!) Pokemon %pokemon%'s protection has expired.";

    private String blacklist = "\u00A7e\u00A7lPlayers missing legend spawns";
    private String addBlacklist = "\u00A7e(!) Player %target% has been added to the legendary blacklist.";
    private String removeBlacklist = "\u00A7e(!) Player %target% has been removed from the legendary blacklist.";
    private String removeAllBlacklist = "\u00A7e(!) All players have been removed from the legendary blacklist.";
    private String emptyBlacklist = "\u00A7e(!) Legendary blacklist is empty.";

    private String spawnPlayerLegendary = "\u00A7e(!) A legendary Pokemon has appeared near you!";
    private String checkLegendary = "\u00A7e(!) Chance %chance%, legendary Pokemon will appear in ~%time%.";
    private String changeChanceLegendary = "\u00A7e(!) New chance: %chance%.";

    private String legendaryTrustCommand = "\u00A7e/legendarytrust add/remove <player> | list";
    private String legendControlCommand = "\u00A7e/legendcontrol add/remove/set <chance> | blacklist add/remove <player> | list | reload";

    private String cantSelfTrust = "\u00A7c(!) You cannot add yourself to the trust list.";
    private String alreadyTrusted = "\u00A7c(!) This player is already on your trust list.";
    private String notPlayerTrust = "\u00A7c(!) This player is not yet on your trust list.";
    private String notPlayerBlacklist = "\u00A7c(!) This player is not yet on legendary blacklist.";
    private String alreadyBlacklist = "\u00A7c(!) This player is already on legendary blacklist.";
    private String trustLimit = "\u00A7c(!) You have reached the maximum number of players on the trust list.";
    private String errorChance = "\u00A7c(!) The overall chance should not be less than 0% or greater than 100%.";
    private String playerNotFound = "\u00A7c(!) Player %target% not found.";
    private String onlyPlayer = "\u00A7c(!) This command can only be executed by a player.";

    private String listPlayer = "\u00A7e- %player%";

    private String seconds = " seconds";
    private String minutes = " minutes";
    private String hours = " hours";

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

    public String getLegendaryTrustCommand() {
        return this.legendaryTrustCommand;
    }

    public String getLegendControlCommand() {
        return this.legendControlCommand;
    }

    public String getNotPlayerBlacklist() {
        return this.notPlayerBlacklist;
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

    public String getOnlyPlayer() {
        return this.onlyPlayer;
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

    private boolean write() {
        Gson gson = UtilGson.newGson();
        CompletableFuture<Boolean> future = UtilGson.writeFileAsync("/config/LegendControl/", "locale.json", gson.toJson(this));
        return future.join();
    }

    public void init() {
        try {
            CompletableFuture<Boolean> future = UtilGson.readFileAsync("/config/LegendControl/", "locale.json", el -> {
                Gson gson = UtilGson.newGson();
                LocaleConfig config = gson.fromJson(el, LocaleConfig.class);

                this.reload = config.getReload();
                this.addTrust = config.getAddTrust();
                this.removeTrust = config.getRemoveTrust();
                this.removeAllTrust = config.getRemoveAllTrust();
                this.emptyTrust = config.getEmptyTrust();
                this.listTrust = config.getListTrust();
                this.incorrectCause = config.getIncorrectCause();
                this.protection = config.getProtection();
                this.blacklist = config.getBlacklist();
                this.addBlacklist = config.getAddBlacklist();
                this.removeBlacklist = config.getRemoveBlacklist();
                this.removeAllBlacklist = config.getRemoveAllBlacklist();
                this.emptyBlacklist = config.getEmptyBlacklist();
                this.spawnPlayerLegendary = config.getSpawnPlayerLegendary();
                this.checkLegendary = config.getCheckLegendary();
                this.legendaryTrustCommand = config.getLegendaryTrustCommand();
                this.legendControlCommand = config.getLegendControlCommand();
                this.changeChanceLegendary = config.getChangeChanceLegendary();
                this.cantSelfTrust = config.getCantSelfTrust();
                this.alreadyTrusted = config.getAlreadyTrusted();
                this.notPlayerTrust = config.getNotPlayerTrust();
                this.notPlayerBlacklist = config.getNotPlayerBlacklist();
                this.alreadyBlacklist = config.getAlreadyBlacklist();
                this.trustLimit = config.getTrustLimit();
                this.errorChance = config.getErrorChance();
                this.playerNotFound = config.getPlayerNotFound();
                this.onlyPlayer = config.getOnlyPlayer();
                this.listPlayer = config.getListPlayer();
                this.seconds = config.getSeconds();
                this.minutes = config.getMinutes();
                this.hours = config.getHours();
            });
            if (!future.join()) {
                write();
            }
        } catch (Exception e) {
            LegendControl.getLogger().error("Error in locale config.");
        }
    }
}
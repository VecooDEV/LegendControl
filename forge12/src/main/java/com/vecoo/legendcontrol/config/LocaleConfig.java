package com.vecoo.legendcontrol.config;

import com.google.gson.Gson;
import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.legendcontrol.LegendControl;

import java.util.concurrent.CompletableFuture;

public class LocaleConfig {
    private String reload = "&e(!) Configs have been reloaded.";

    private String addTrust = "&e(!) Player %player% has been successfully added to your trust list.";
    private String removeTrust = "&e(!) Player %player% has been removed from your trust list.";
    private String removeAllTrust = "&e(!) All players have been removed from your trust list.";
    private String emptyTrust = "&e(!) Your trust list is empty.";
    private String listTrust = "&e&lTrust list:";
    private String incorrectCause = "&e(!) At the moment, the legendary Pokemon is protected. Try to enter the battle after a while.";
    private String protection = "&e(!) Pokemon %pokemon%'s protection has expired.";
    private String location = "&e(!) Location of Pokemon %pokemon%: X: %x%, Y: %y%, Z: %z%.";

    private String blacklist = "&e&lPlayers missing legend spawns:";
    private String addBlacklist = "&e(!) Player %player% has been added to the legendary blacklist.";
    private String removeBlacklist = "&e(!) Player %player% has been removed from the legendary blacklist.";
    private String removeAllBlacklist = "&e(!) All players have been removed from the legendary blacklist.";
    private String emptyBlacklist = "&e(!) Legendary blacklist is empty.";

    private String spawnPlayerLegendary = "&e(!) A legendary Pokemon has appeared near you!";
    private String checkLegendary = "&e(!) Chance %chance%, legendary Pokemon will appear in ~%time%.";
    private String changeChanceLegendary = "&e(!) New chance: %chance%.";

    private String legendaryTrustCommand = "&e/legendarytrust add/remove <player> | list";
    private String legendControlCommand = "&e/legendcontrol add/remove/set <chance> | blacklist add/remove <player> | list | reload";

    private String cantSelfTrust = "&c(!) You cannot add yourself to the trust list.";
    private String alreadyTrusted = "&c(!) This player is already on your trust list.";
    private String notPlayerTrust = "&c(!) This player is not yet on your trust list.";
    private String notPlayerBlacklist = "&c(!) This player is not yet on legendary blacklist.";
    private String alreadyBlacklist = "&c(!) This player is already on legendary blacklist.";
    private String trustLimit = "&c(!) You have reached the maximum number of players on the trust list.";
    private String errorChance = "&c(!) The overall chance should not be less than 0% or greater than 100%.";
    private String playerNotFound = "&c(!) Player %player% not found.";
    private String onlyPlayer = "&c(!) This command can only be executed by a player.";

    private String listPlayer = "&e- %player%";

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

    public String getLocation() {
        return this.location;
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
                this.location = config.getLocation();
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
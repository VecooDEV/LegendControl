package com.vecoo.legendcontrol_defender.config;

import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import lombok.Getter;

@Getter
@ConfigSerializable
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

    private String errorReload = "&c(!) Reload error, checking console and fixes config.";
    private String cantSelfTrust = "&c(!) You cannot add yourself to the trust list.";
    private String alreadyTrusted = "&c(!) This player is already on your trust list.";
    private String notPlayerTrust = "&c(!) This player is not yet on your trust list.";
    private String trustLimit = "&c(!) You have reached the maximum number of players on the trust list.";
    private String playerNotFound = "&c(!) Player %player% not found.";
}
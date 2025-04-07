package com.vecoo.legendcontrol_defender.storage.player;

import com.vecoo.legendcontrol_defender.LegendControlDefender;

import java.util.HashSet;
import java.util.UUID;

public class PlayerStorage {
    private final UUID uuid;
    private final HashSet<UUID> playersTrust;

    public PlayerStorage(UUID playerUUID) {
        this.uuid = playerUUID;
        this.playersTrust = new HashSet<>();
        LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public HashSet<UUID> getPlayersTrust() {
        return this.playersTrust;
    }

    public void addPlayerTrust(UUID playeruUID) {
        this.playersTrust.add(playeruUID);
        LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    public void removePlayerTrust(UUID playerUUID) {
        this.playersTrust.remove(playerUUID);
        LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    public void clearPlayersTrust() {
        this.playersTrust.clear();
        LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }
}
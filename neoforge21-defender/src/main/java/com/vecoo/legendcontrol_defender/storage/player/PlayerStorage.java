package com.vecoo.legendcontrol_defender.storage.player;

import com.vecoo.legendcontrol_defender.LegendControlDefender;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerStorage {
    private final UUID uuid;
    private final Set<UUID> playersTrust;

    public PlayerStorage(UUID playerUUID) {
        this.uuid = playerUUID;
        this.playersTrust = new HashSet<>();
        LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public Set<UUID> getPlayersTrust() {
        return this.playersTrust;
    }

    public void addPlayerTrust(UUID playerUUID, boolean update) {
        this.playersTrust.add(playerUUID);

        if (update) {
            LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
        }
    }

    public void removePlayerTrust(UUID playerUUID, boolean update) {
        this.playersTrust.remove(playerUUID);

        if (update) {
            LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
        }
    }

    public void clearPlayersTrust(boolean update) {
        this.playersTrust.clear();

        if (update) {
            LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
        }
    }
}
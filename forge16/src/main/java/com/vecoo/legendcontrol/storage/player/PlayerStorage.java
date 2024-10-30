package com.vecoo.legendcontrol.storage.player;

import com.vecoo.legendcontrol.LegendControl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerStorage {
    private final UUID uuid;
    private final List<UUID> playersTrust;

    public PlayerStorage(UUID playerUUID) {
        this.uuid = playerUUID;
        this.playersTrust = new ArrayList<>();
        LegendControl.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public List<UUID> getPlayersTrust() {
        return this.playersTrust;
    }

    public void addPlayerTrust(UUID playeruUID) {
        this.playersTrust.add(playeruUID);
        LegendControl.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    public void removePlayerTrust(UUID playerUUID) {
        this.playersTrust.remove(playerUUID);
        LegendControl.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    public void removePlayersTrust() {
        this.playersTrust.clear();
        LegendControl.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }
}
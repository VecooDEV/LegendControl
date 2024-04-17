package com.vecoo.legendcontrol.storage.player;

import com.vecoo.legendcontrol.LegendControl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerStorage {
    private UUID playerUUID;
    private List<UUID> playersUUID;

    public PlayerStorage(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.playersUUID = new ArrayList<>();
        LegendControl.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public List<UUID> getPlayersUUID() {
        return this.playersUUID;
    }

    public void addPlayersUUID(UUID uuid) {
        this.playersUUID.add(uuid);
        LegendControl.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    public void removePlayersUUID(UUID uuid) {
        this.playersUUID.remove(uuid);
        LegendControl.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }
}
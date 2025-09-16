package com.vecoo.legendcontrol_defender.storage;

import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.events.PlayerTrustEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Set;
import java.util.UUID;

public class PlayerStorage {
    private final UUID playerUUID;
    private final Set<UUID> playersTrust;
    private transient boolean dirty = false;

    public PlayerStorage(UUID playerUUID, Set<UUID> playersTrust) {
        this.playerUUID = playerUUID;
        this.playersTrust = playersTrust;
        LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public Set<UUID> getPlayersTrust() {
        return this.playersTrust;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void addPlayerTrust(UUID playerUUID) {
        if (!NeoForge.EVENT_BUS.post(new PlayerTrustEvent.Add(this.playerUUID, playerUUID)).isCanceled()) {
            this.playersTrust.add(playerUUID);
            LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
        }
    }

    public void removePlayerTrust(UUID playerUUID) {
        if (!NeoForge.EVENT_BUS.post(new PlayerTrustEvent.Remove(this.playerUUID, playerUUID)).isCanceled()) {
            this.playersTrust.remove(playerUUID);
            LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
        }
    }

    public void clearPlayersTrust() {
        if (!NeoForge.EVENT_BUS.post(new PlayerTrustEvent.Clear(this.playerUUID)).isCanceled()) {
            this.playersTrust.clear();
            LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
        }
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
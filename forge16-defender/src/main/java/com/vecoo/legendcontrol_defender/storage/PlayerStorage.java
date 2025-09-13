package com.vecoo.legendcontrol_defender.storage;

import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.events.PlayerTrustEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.Set;
import java.util.UUID;

public class PlayerStorage {
    private final UUID playerUUID;
    private final Set<UUID> playersTrust;
    private transient boolean isDirty;

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
        return this.isDirty;
    }

    public void addPlayerTrust(UUID playerUUID) {
        if (!MinecraftForge.EVENT_BUS.post(new PlayerTrustEvent.Add(this.playerUUID, playerUUID))) {
            this.playersTrust.add(playerUUID);
            LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
        }
    }

    public void removePlayerTrust(UUID playerUUID) {
        if (!MinecraftForge.EVENT_BUS.post(new PlayerTrustEvent.Remove(this.playerUUID, playerUUID))) {
            this.playersTrust.remove(playerUUID);
            LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
        }
    }

    public void clearPlayersTrust() {
        if (!MinecraftForge.EVENT_BUS.post(new PlayerTrustEvent.Clear(this.playerUUID))) {
            this.playersTrust.clear();
            LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
        }
    }

    public void setDirty(boolean dirty) {
        this.isDirty = dirty;
    }
}
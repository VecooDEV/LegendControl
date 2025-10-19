package com.vecoo.legendcontrol_defender.storage;

import com.vecoo.legendcontrol_defender.LegendControlDefender;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.UUID;

public class PlayerStorage {
    private final UUID playerUUID;
    private final Set<UUID> playersTrust;

    private transient boolean dirty = false;

    public PlayerStorage(@Nonnull UUID playerUUID, @Nonnull Set<UUID> playersTrust) {
        this.playerUUID = playerUUID;
        this.playersTrust = playersTrust;
        LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    @Nonnull
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    @Nonnull
    public Set<UUID> getPlayersTrust() {
        return this.playersTrust;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void addPlayerTrust(UUID playerUUID) {
        this.playersTrust.add(playerUUID);
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

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
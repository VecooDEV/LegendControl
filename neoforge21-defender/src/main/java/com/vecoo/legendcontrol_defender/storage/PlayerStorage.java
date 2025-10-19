package com.vecoo.legendcontrol_defender.storage;

import com.vecoo.legendcontrol_defender.LegendControlDefender;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class PlayerStorage {
    private final UUID playerUUID;
    private final Set<UUID> playersTrust;

    private transient boolean dirty = false;

    public PlayerStorage(@NotNull UUID playerUUID, @NotNull Set<UUID> playersTrust) {
        this.playerUUID = playerUUID;
        this.playersTrust = playersTrust;
        LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    @NotNull
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    @NotNull
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
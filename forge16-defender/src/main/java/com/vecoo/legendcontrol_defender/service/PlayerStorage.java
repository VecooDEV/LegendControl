package com.vecoo.legendcontrol_defender.service;

import com.vecoo.legendcontrol_defender.LegendControlDefender;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.UUID;

@Getter
@ToString
public class PlayerStorage {
    @Nonnull
    private final UUID playerUUID;
    @Nonnull
    private final Set<UUID> playersTrust;

    @Setter
    private transient volatile boolean dirty = false;

    public PlayerStorage(@Nonnull UUID playerUUID, @Nonnull Set<UUID> playersTrust) {
        this.playerUUID = playerUUID;
        this.playersTrust = playersTrust;
        LegendControlDefender.getInstance().getPlayerService().updatePlayerStorage(this);
    }

    public void addPlayerTrust(@Nonnull UUID playerUUID) {
        this.playersTrust.add(playerUUID);
        LegendControlDefender.getInstance().getPlayerService().updatePlayerStorage(this);
    }

    public void removePlayerTrust(@Nonnull UUID playerUUID) {
        this.playersTrust.remove(playerUUID);
        LegendControlDefender.getInstance().getPlayerService().updatePlayerStorage(this);
    }

    public void clearPlayersTrust() {
        this.playersTrust.clear();
        LegendControlDefender.getInstance().getPlayerService().updatePlayerStorage(this);
    }
}
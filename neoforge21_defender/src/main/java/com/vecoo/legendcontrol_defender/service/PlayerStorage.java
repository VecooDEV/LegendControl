package com.vecoo.legendcontrol_defender.service;

import com.vecoo.legendcontrol_defender.LegendControlDefender;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

@Getter
@ToString
public class PlayerStorage {
    @NotNull
    private final UUID playerUUID;
    @NotNull
    private final Set<UUID> playersTrust;

    @Setter
    private transient boolean dirty = false;

    public PlayerStorage(@NotNull UUID playerUUID, @NotNull Set<UUID> playersTrust) {
        this.playerUUID = playerUUID;
        this.playersTrust = playersTrust;
        LegendControlDefender.getInstance().getPlayerService().updatePlayerStorage(this);
    }

    public void addPlayerTrust(@NotNull UUID playerUUID) {
        this.playersTrust.add(playerUUID);
        LegendControlDefender.getInstance().getPlayerService().updatePlayerStorage(this);
    }

    public void removePlayerTrust(@NotNull UUID playerUUID) {
        this.playersTrust.remove(playerUUID);
        LegendControlDefender.getInstance().getPlayerService().updatePlayerStorage(this);
    }

    public void clearPlayersTrust() {
        this.playersTrust.clear();
        LegendControlDefender.getInstance().getPlayerService().updatePlayerStorage(this);
    }
}
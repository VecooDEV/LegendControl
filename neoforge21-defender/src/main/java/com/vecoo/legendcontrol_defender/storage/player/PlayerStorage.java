package com.vecoo.legendcontrol_defender.storage.player;

import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.events.PlayerTrustEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Set;
import java.util.UUID;

public class PlayerStorage {
    private final UUID uuid;
    private final Set<UUID> playersTrust;

    public PlayerStorage(UUID playerUUID, Set<UUID> playersTrust) {
        this.uuid = playerUUID;
        this.playersTrust = playersTrust;
        LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public Set<UUID> getPlayersTrust() {
        return this.playersTrust;
    }

    public void addPlayerTrust(UUID playerUUID, boolean update) {
        if (!NeoForge.EVENT_BUS.post(new PlayerTrustEvent.Add(this.uuid, playerUUID)).isCanceled()) {
            this.playersTrust.add(playerUUID);

            if (update) {
                LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
            }
        }
    }

    public void removePlayerTrust(UUID playerUUID, boolean update) {
        if (!NeoForge.EVENT_BUS.post(new PlayerTrustEvent.Remove(this.uuid, playerUUID)).isCanceled()) {
            this.playersTrust.remove(playerUUID);

            if (update) {
                LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
            }
        }
    }

    public void clearPlayersTrust(boolean update) {
        if (!NeoForge.EVENT_BUS.post(new PlayerTrustEvent.Clear(this.uuid)).isCanceled()) {
            this.playersTrust.clear();

            if (update) {
                LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
            }
        }
    }
}
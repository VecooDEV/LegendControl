package com.vecoo.legendcontrol_defender.storage.player;

import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.events.PlayerTrustEvent;
import net.minecraftforge.common.MinecraftForge;

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
        if (!MinecraftForge.EVENT_BUS.post(new PlayerTrustEvent.Add(this.uuid, playerUUID))) {
            this.playersTrust.add(playerUUID);

            if (update) {
                LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
            }
        }
    }

    public void removePlayerTrust(UUID playerUUID, boolean update) {
        if (!MinecraftForge.EVENT_BUS.post(new PlayerTrustEvent.Remove(this.uuid, playerUUID))) {
            this.playersTrust.remove(playerUUID);

            if (update) {
                LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
            }
        }
    }

    public void clearPlayersTrust(boolean update) {
        if (!MinecraftForge.EVENT_BUS.post(new PlayerTrustEvent.Clear(this.uuid))) {
            this.playersTrust.clear();

            if (update) {
                LegendControlDefender.getInstance().getPlayerProvider().updatePlayerStorage(this);
            }
        }
    }
}
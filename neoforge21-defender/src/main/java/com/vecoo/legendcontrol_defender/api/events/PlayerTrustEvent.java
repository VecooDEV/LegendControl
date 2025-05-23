package com.vecoo.legendcontrol_defender.api.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

import java.util.UUID;

public class PlayerTrustEvent extends Event implements ICancellableEvent {
    private final UUID playerUUID;

    public PlayerTrustEvent(UUID uuid) {
        this.playerUUID = uuid;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public static class Add extends PlayerTrustEvent implements ICancellableEvent {
        private final UUID targetUUID;

        public Add(UUID playerUUID, UUID targetUUID) {
            super(playerUUID);
            this.targetUUID = targetUUID;
        }

        public UUID getTargetUUID() {
            return this.targetUUID;
        }
    }

    public static class Remove extends PlayerTrustEvent implements ICancellableEvent {
        private final UUID targetUUID;

        public Remove(UUID playerUUID, UUID targetUUID) {
            super(playerUUID);
            this.targetUUID = targetUUID;
        }

        public UUID getTargetUUID() {
            return this.targetUUID;
        }
    }

    public static class Clear extends PlayerTrustEvent implements ICancellableEvent {
        public Clear(UUID playerUUID) {
            super(playerUUID);
        }
    }
}

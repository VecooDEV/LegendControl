package com.vecoo.legendcontrol_defender.api.events;

import net.neoforged.bus.api.Event;

import java.util.UUID;

public class PlayerTrustEvent extends Event {
    private final UUID playerUUID;

    public PlayerTrustEvent(UUID uuid) {
        this.playerUUID = uuid;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public static class Add extends PlayerTrustEvent {
        private final UUID targetUUID;

        public Add(UUID playerUUID, UUID targetUUID) {
            super(playerUUID);
            this.targetUUID = targetUUID;
        }

        public UUID getTargetUUID() {
            return this.targetUUID;
        }
    }

    public static class Remove extends PlayerTrustEvent {
        private final UUID targetUUID;

        public Remove(UUID playerUUID, UUID targetUUID) {
            super(playerUUID);
            this.targetUUID = targetUUID;
        }

        public UUID getTargetUUID() {
            return this.targetUUID;
        }
    }

    public static class Clear extends PlayerTrustEvent {
        public Clear(UUID playerUUID) {
            super(playerUUID);
        }
    }
}

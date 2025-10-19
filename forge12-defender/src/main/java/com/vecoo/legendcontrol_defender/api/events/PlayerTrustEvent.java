package com.vecoo.legendcontrol_defender.api.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.UUID;

@Cancelable
public class PlayerTrustEvent extends Event {
    private final UUID playerUUID;

    public PlayerTrustEvent(UUID uuid) {
        this.playerUUID = uuid;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    @Cancelable
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

    @Cancelable
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

    @Cancelable
    public static class Clear extends PlayerTrustEvent {
        public Clear(UUID playerUUID) {
            super(playerUUID);
        }
    }
}

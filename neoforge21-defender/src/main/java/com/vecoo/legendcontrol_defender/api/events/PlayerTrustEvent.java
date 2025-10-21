package com.vecoo.legendcontrol_defender.api.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerTrustEvent extends Event implements ICancellableEvent {
    private final UUID playerUUID;

    public PlayerTrustEvent(@NotNull UUID uuid) {
        this.playerUUID = uuid;
    }

    @NotNull
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public static class Add extends PlayerTrustEvent implements ICancellableEvent {
        private final UUID targetUUID;

        public Add(@NotNull UUID playerUUID, @NotNull UUID targetUUID) {
            super(playerUUID);
            this.targetUUID = targetUUID;
        }

        @NotNull
        public UUID getTargetUUID() {
            return this.targetUUID;
        }
    }

    public static class Remove extends PlayerTrustEvent implements ICancellableEvent {
        private final UUID targetUUID;

        public Remove(@NotNull UUID playerUUID, @NotNull UUID targetUUID) {
            super(playerUUID);
            this.targetUUID = targetUUID;
        }

        @NotNull
        public UUID getTargetUUID() {
            return this.targetUUID;
        }
    }

    public static class Clear extends PlayerTrustEvent implements ICancellableEvent {
        public Clear(@NotNull UUID playerUUID) {
            super(playerUUID);
        }
    }
}

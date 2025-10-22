package com.vecoo.legendcontrol_defender.api.events;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import java.util.UUID;

@Cancelable
public class PlayerTrustEvent extends Event {
    private final UUID playerUUID;

    public PlayerTrustEvent(@Nonnull UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Nonnull
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    @Cancelable
    public static class Add extends PlayerTrustEvent {
        private final UUID targetUUID;

        public Add(@Nonnull UUID playerUUID, @Nonnull UUID targetUUID) {
            super(playerUUID);
            this.targetUUID = targetUUID;
        }

        @Nonnull
        public UUID getTargetUUID() {
            return this.targetUUID;
        }
    }

    @Cancelable
    public static class Remove extends PlayerTrustEvent {
        private final UUID targetUUID;

        public Remove(@Nonnull UUID playerUUID, @Nonnull UUID targetUUID) {
            super(playerUUID);
            this.targetUUID = targetUUID;
        }

        @Nonnull
        public UUID getTargetUUID() {
            return this.targetUUID;
        }
    }

    @Cancelable
    public static class Clear extends PlayerTrustEvent {
        public Clear(@Nonnull UUID playerUUID) {
            super(playerUUID);
        }
    }
}

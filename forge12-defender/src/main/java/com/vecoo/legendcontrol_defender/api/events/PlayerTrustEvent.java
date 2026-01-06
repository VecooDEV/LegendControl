package com.vecoo.legendcontrol_defender.api.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@Cancelable
public class PlayerTrustEvent extends Event {
    @Nonnull
    private final UUID playerUUID;

    @Getter
    @Cancelable
    public static class Add extends PlayerTrustEvent {
        @Nonnull
        private final UUID targetUUID;

        public Add(@Nonnull UUID playerUUID, @Nonnull UUID targetUUID) {
            super(playerUUID);
            this.targetUUID = targetUUID;
        }
    }

    @Getter
    @Cancelable
    public static class Remove extends PlayerTrustEvent {
        @Nonnull
        private final UUID targetUUID;

        public Remove(@Nonnull UUID playerUUID, UUID targetUUID) {
            super(playerUUID);
            this.targetUUID = targetUUID;
        }
    }

    @Cancelable
    public static class Clear extends PlayerTrustEvent {
        public Clear(@Nonnull UUID playerUUID) {
            super(playerUUID);
        }
    }
}

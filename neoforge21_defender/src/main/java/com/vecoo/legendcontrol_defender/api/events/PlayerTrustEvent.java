package com.vecoo.legendcontrol_defender.api.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class PlayerTrustEvent extends Event implements ICancellableEvent {
    @NotNull
    private final UUID playerUUID;

    @Getter
    public static class Add extends PlayerTrustEvent implements ICancellableEvent {
        @NotNull
        private final UUID targetUUID;

        public Add(@NotNull UUID playerUUID, @NotNull UUID targetUUID) {
            super(playerUUID);
            this.targetUUID = targetUUID;
        }
    }

    @Getter
    public static class Remove extends PlayerTrustEvent implements ICancellableEvent {
        @NotNull
        private final UUID targetUUID;

        public Remove(@NotNull UUID playerUUID, @NotNull UUID targetUUID) {
            super(playerUUID);
            this.targetUUID = targetUUID;
        }
    }

    public static class Clear extends PlayerTrustEvent implements ICancellableEvent {
        public Clear(@NotNull UUID playerUUID) {
            super(playerUUID);
        }
    }
}

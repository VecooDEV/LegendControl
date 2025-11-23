package com.vecoo.legendcontrol_defender.api.events;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LegendControlDefenderEvent extends Event {
    private final PixelmonEntity pixelmonEntity;

    public LegendControlDefenderEvent(@Nullable PixelmonEntity pixelmonEntity) {
        this.pixelmonEntity = pixelmonEntity;
    }

    @Nullable
    public PixelmonEntity getPixelmonEntity() {
        return this.pixelmonEntity;
    }

    public static class ExpiredDefender extends LegendControlDefenderEvent {
        public ExpiredDefender(@Nullable PixelmonEntity pixelmonEntity) {
            super(pixelmonEntity);
        }
    }

    public static class WorkedDefender extends LegendControlDefenderEvent implements ICancellableEvent {
        @NotNull
        public final ServerPlayer player;

        public WorkedDefender(@Nullable PixelmonEntity pixelmonEntity, @NotNull ServerPlayer player) {
            super(pixelmonEntity);
            this.player = player;
        }

        @NotNull
        public ServerPlayer getPlayer() {
            return this.player;
        }
    }
}

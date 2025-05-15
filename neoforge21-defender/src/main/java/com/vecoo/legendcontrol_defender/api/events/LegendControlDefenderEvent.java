package com.vecoo.legendcontrol_defender.api.events;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

public abstract class LegendControlDefenderEvent extends Event {
    private final PixelmonEntity pixelmonEntity;

    public LegendControlDefenderEvent(PixelmonEntity pixelmonEntity) {
        this.pixelmonEntity = pixelmonEntity;
    }

    public PixelmonEntity getPixelmonEntity() {
        return this.pixelmonEntity;
    }

    public static class ExpiredDefender extends LegendControlDefenderEvent {
        public ExpiredDefender(PixelmonEntity pixelmonEntity) {
            super(pixelmonEntity);
        }
    }

    public static class WorkedDefender extends LegendControlDefenderEvent implements ICancellableEvent {
        @Nullable
        public final ServerPlayer player;

        public WorkedDefender(PixelmonEntity pixelmonEntity, @Nullable ServerPlayer player) {
            super(pixelmonEntity);
            this.player = player;
        }

        @Nullable
        public ServerPlayer getPlayer() {
            return this.player;
        }
    }
}

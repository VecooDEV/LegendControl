package com.vecoo.legendcontrol_defender.api.events;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

public class LegendControlDefenderEvent extends Event {
    private final PixelmonEntity pixelmonEntity;

    public LegendControlDefenderEvent(@Nonnull PixelmonEntity pixelmonEntity) {
        this.pixelmonEntity = pixelmonEntity;
    }

    @Nonnull
    public PixelmonEntity getPixelmonEntity() {
        return this.pixelmonEntity;
    }

    public static class ExpiredDefender extends LegendControlDefenderEvent {
        public ExpiredDefender(@Nonnull PixelmonEntity pixelmonEntity) {
            super(pixelmonEntity);
        }
    }

    @Cancelable
    public static class WorkedDefender extends LegendControlDefenderEvent {
        @Nonnull
        public final ServerPlayerEntity player;

        public WorkedDefender(@Nonnull PixelmonEntity pixelmonEntity, @Nonnull ServerPlayerEntity player) {
            super(pixelmonEntity);
            this.player = player;
        }

        @Nonnull
        public ServerPlayerEntity getPlayer() {
            return this.player;
        }
    }
}

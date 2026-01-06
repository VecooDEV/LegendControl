package com.vecoo.legendcontrol_defender.api.events;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

@Getter
@RequiredArgsConstructor
public class LegendControlDefenderEvent extends Event {
    @Nonnull
    private final PixelmonEntity pixelmonEntity;

    public static class ExpiredDefender extends LegendControlDefenderEvent {
        public ExpiredDefender(@Nonnull PixelmonEntity pixelmonEntity) {
            super(pixelmonEntity);
        }
    }

    @Getter
    @Cancelable
    public static class WorkedDefender extends LegendControlDefenderEvent {
        @Nonnull
        public final ServerPlayerEntity player;

        public WorkedDefender(@Nonnull PixelmonEntity pixelmonEntity, @Nonnull ServerPlayerEntity player) {
            super(pixelmonEntity);
            this.player = player;
        }
    }
}

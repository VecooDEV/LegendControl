package com.vecoo.legendcontrol.api.events;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

@Getter
@RequiredArgsConstructor
public class LegendControlEvent extends Event {
    @Nonnull
    private final PixelmonEntity pixelmonEntity;

    @Getter
    @Cancelable
    public static class Location extends LegendControlEvent {
        private final double x, y, z;

        public Location(@Nonnull PixelmonEntity pixelmonEntity, double x, double y, double z) {
            super(pixelmonEntity);
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    @Cancelable
    public static class ForceDespawn extends LegendControlEvent {
        public ForceDespawn(@Nonnull PixelmonEntity pixelmonEntity) {
            super(pixelmonEntity);
        }
    }

    public static class ChunkDespawn extends LegendControlEvent {
        public ChunkDespawn(@Nonnull PixelmonEntity pixelmonEntity) {
            super(pixelmonEntity);
        }
    }
}

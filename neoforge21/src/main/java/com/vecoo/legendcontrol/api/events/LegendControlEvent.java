package com.vecoo.legendcontrol.api.events;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public abstract class LegendControlEvent extends Event {
    @NotNull
    private final PixelmonEntity pixelmonEntity;

    @Getter
    public static class Location extends LegendControlEvent implements ICancellableEvent {
        private final double x, y, z;

        public Location(@NotNull PixelmonEntity pixelmonEntity, double x, double y, double z) {
            super(pixelmonEntity);
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static class ForceDespawn extends LegendControlEvent implements ICancellableEvent {
        public ForceDespawn(@NotNull PixelmonEntity pixelmonEntity) {
            super(pixelmonEntity);
        }
    }

    public static class ChunkDespawn extends LegendControlEvent {
        public ChunkDespawn(@NotNull PixelmonEntity pixelmonEntity) {
            super(pixelmonEntity);
        }
    }
}

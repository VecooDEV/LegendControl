package com.vecoo.legendcontrol.api.events;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class LegendControlEvent extends Event {
    private final PixelmonEntity pixelmonEntity;

    public LegendControlEvent(PixelmonEntity pixelmonEntity) {
        this.pixelmonEntity = pixelmonEntity;
    }

    public PixelmonEntity getPixelmonEntity() {
        return this.pixelmonEntity;
    }

    @Cancelable
    public static class Location extends LegendControlEvent {
        public Location(PixelmonEntity pixelmonEntity) {
            super(pixelmonEntity);
        }
    }

    @Cancelable
    public static class ForceDespawn extends LegendControlEvent {
        public ForceDespawn(PixelmonEntity pixelmonEntity) {
            super(pixelmonEntity);
        }
    }

    public static class ChunkDespawn extends LegendControlEvent {
        public ChunkDespawn(PixelmonEntity pixelmonEntity) {
            super(pixelmonEntity);
        }
    }
}

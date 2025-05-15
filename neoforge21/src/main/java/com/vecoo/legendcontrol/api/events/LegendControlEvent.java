package com.vecoo.legendcontrol.api.events;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public abstract class LegendControlEvent extends Event {
    private final PixelmonEntity pixelmonEntity;

    public LegendControlEvent(PixelmonEntity pixelmonEntity) {
        this.pixelmonEntity = pixelmonEntity;
    }

    public PixelmonEntity getPixelmonEntity() {
        return this.pixelmonEntity;
    }

    public static class Location extends LegendControlEvent implements ICancellableEvent {
        public Location(PixelmonEntity pixelmonEntity) {
            super(pixelmonEntity);
        }
    }

    public static class ForceDespawn extends LegendControlEvent implements ICancellableEvent {
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

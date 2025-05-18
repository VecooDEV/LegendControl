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
        private double x;
        private double y;
        private double z;

        public Location(PixelmonEntity pixelmonEntity, double x, double y, double z) {
            super(pixelmonEntity);
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }

        public void setX(double x) {
            this.x = x;
        }

        public void setY(double y) {
            this.y = y;
        }

        public void setZ(double z) {
            this.z = z;
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

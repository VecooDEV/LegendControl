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

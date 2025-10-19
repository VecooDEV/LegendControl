package com.vecoo.legendcontrol.api.events;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class LegendControlEvent extends Event {
    private final EntityPixelmon pixelmonEntity;

    public LegendControlEvent(EntityPixelmon pixelmonEntity) {
        this.pixelmonEntity = pixelmonEntity;
    }

    public EntityPixelmon getPixelmonEntity() {
        return this.pixelmonEntity;
    }

    @Cancelable
    public static class Location extends LegendControlEvent{
        private double x;
        private double y;
        private double z;

        public Location(EntityPixelmon pixelmonEntity, double x, double y, double z) {
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
    public static class ForceDespawn extends LegendControlEvent{
        public ForceDespawn(EntityPixelmon pixelmonEntity) {
            super(pixelmonEntity);
        }
    }

    public static class ChunkDespawn extends LegendControlEvent {
        public ChunkDespawn(EntityPixelmon pixelmonEntity) {
            super(pixelmonEntity);
        }
    }
}

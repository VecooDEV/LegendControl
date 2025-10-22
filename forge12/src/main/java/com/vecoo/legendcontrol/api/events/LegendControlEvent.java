package com.vecoo.legendcontrol.api.events;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

public class LegendControlEvent extends Event {
    private final EntityPixelmon entityPixelmon;

    public LegendControlEvent(@Nonnull EntityPixelmon pixelmonEntity) {
        this.entityPixelmon = pixelmonEntity;
    }

    @Nonnull
    public EntityPixelmon getEntityPixelmon() {
        return this.entityPixelmon;
    }

    @Cancelable
    public static class Location extends LegendControlEvent{
        private double x;
        private double y;
        private double z;

        public Location(@Nonnull EntityPixelmon entityPixelmon, double x, double y, double z) {
            super(entityPixelmon);
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
        public ForceDespawn(@Nonnull EntityPixelmon entityPixelmon) {
            super(entityPixelmon);
        }
    }
}

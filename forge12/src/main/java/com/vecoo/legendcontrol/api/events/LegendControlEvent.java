package com.vecoo.legendcontrol.api.events;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

@Getter
@RequiredArgsConstructor
public class LegendControlEvent extends Event {
    @Nonnull
    private final EntityPixelmon entityPixelmon;

    @Getter
    @Cancelable
    public static class Location extends LegendControlEvent{
        private final double x, y, z;

        public Location(@Nonnull EntityPixelmon entityPixelmon, double x, double y, double z) {
            super(entityPixelmon);
            this.x = x;
            this.y = y;
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

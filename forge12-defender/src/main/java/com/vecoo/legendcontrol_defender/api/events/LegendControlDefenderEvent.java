package com.vecoo.legendcontrol_defender.api.events;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

public class LegendControlDefenderEvent extends Event {
    private final EntityPixelmon pixelmonEntity;

    public LegendControlDefenderEvent(@Nonnull EntityPixelmon pixelmonEntity) {
        this.pixelmonEntity = pixelmonEntity;
    }

    @Nonnull
    public EntityPixelmon getPixelmonEntity() {
        return this.pixelmonEntity;
    }

    public static class ExpiredDefender extends LegendControlDefenderEvent {
        public ExpiredDefender(@Nonnull EntityPixelmon pixelmonEntity) {
            super(pixelmonEntity);
        }
    }

    @Cancelable
    public static class WorkedDefender extends LegendControlDefenderEvent {
        @Nonnull
        public final EntityPlayerMP player;

        public WorkedDefender(@Nonnull EntityPixelmon pixelmonEntity, @Nonnull EntityPlayerMP player) {
            super(pixelmonEntity);
            this.player = player;
        }

        @Nonnull
        public EntityPlayerMP getPlayer() {
            return this.player;
        }
    }
}

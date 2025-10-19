package com.vecoo.legendcontrol_defender.api.events;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;

public class LegendControlDefenderEvent extends Event {
    private final EntityPixelmon pixelmonEntity;

    public LegendControlDefenderEvent(EntityPixelmon pixelmonEntity) {
        this.pixelmonEntity = pixelmonEntity;
    }

    public EntityPixelmon getPixelmonEntity() {
        return this.pixelmonEntity;
    }

    public static class ExpiredDefender extends LegendControlDefenderEvent {
        public ExpiredDefender(EntityPixelmon pixelmonEntity) {
            super(pixelmonEntity);
        }
    }

    @Cancelable
    public static class WorkedDefender extends LegendControlDefenderEvent {
        @Nullable
        public final EntityPlayerMP player;

        public WorkedDefender(EntityPixelmon pixelmonEntity, @Nullable EntityPlayerMP player) {
            super(pixelmonEntity);
            this.player = player;
        }

        @Nullable
        public EntityPlayerMP getPlayer() {
            return this.player;
        }
    }
}

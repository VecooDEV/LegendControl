package com.vecoo.legendcontrol_defender.api.events;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class LegendControlDefenderEvent extends Event {
    private final PixelmonEntity pixelmonEntity;

    public LegendControlDefenderEvent(PixelmonEntity pixelmonEntity) {
        this.pixelmonEntity = pixelmonEntity;
    }

    public PixelmonEntity getPixelmonEntity() {
        return this.pixelmonEntity;
    }

    public static class ExpiredDefender extends LegendControlDefenderEvent {
        public ExpiredDefender(PixelmonEntity pixelmonEntity) {
            super(pixelmonEntity);
        }
    }

    @Cancelable
    public static class WorkedDefender extends LegendControlDefenderEvent {
        public final ServerPlayerEntity player;

        public WorkedDefender(PixelmonEntity pixelmonEntity, ServerPlayerEntity player) {
            super(pixelmonEntity);
            this.player = player;
        }

        public ServerPlayerEntity getPlayer() {
            return this.player;
        }
    }
}

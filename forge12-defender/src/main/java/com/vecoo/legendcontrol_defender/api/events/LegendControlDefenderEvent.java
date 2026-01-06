package com.vecoo.legendcontrol_defender.api.events;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Getter
@RequiredArgsConstructor
public class LegendControlDefenderEvent extends Event {
    @Nullable
    private final EntityPixelmon pixelmonEntity;

    public static class ExpiredDefender extends LegendControlDefenderEvent {
        public ExpiredDefender(@Nonnull EntityPixelmon pixelmonEntity) {
            super(pixelmonEntity);
        }
    }

    @Getter
    @Cancelable
    public static class WorkedDefender extends LegendControlDefenderEvent {
        @Nonnull
        public final EntityPlayerMP player;

        public WorkedDefender(@Nonnull EntityPixelmon pixelmonEntity, @Nonnull EntityPlayerMP player) {
            super(pixelmonEntity);
            this.player = player;
        }
    }
}

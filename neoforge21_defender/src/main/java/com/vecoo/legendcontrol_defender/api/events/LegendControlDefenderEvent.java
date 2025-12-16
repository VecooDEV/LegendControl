package com.vecoo.legendcontrol_defender.api.events;

import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public abstract class LegendControlDefenderEvent extends Event {
    @Nullable
    private final PixelmonEntity pixelmonEntity;

    public static class ExpiredDefender extends LegendControlDefenderEvent implements ICancellableEvent {
        public ExpiredDefender(@Nullable PixelmonEntity pixelmonEntity) {
            super(pixelmonEntity);
        }
    }

    @Getter
    public static class WorkedDefender extends LegendControlDefenderEvent implements ICancellableEvent {
        @NotNull
        public final ServerPlayer player;

        public WorkedDefender(@Nullable PixelmonEntity pixelmonEntity, @NotNull ServerPlayer player) {
            super(pixelmonEntity);
            this.player = player;
        }
    }
}

package com.vecoo.legendcontrol.api.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

@Getter
@Setter
@AllArgsConstructor
public class ChangeChanceLegendEvent extends Event {
    @Nonnull
    private final String source;
    private float chance;

    @Cancelable
    public static class Set extends ChangeChanceLegendEvent {
        public Set(@Nonnull String source, float chance) {
            super(source, chance);
        }
    }

    @Cancelable
    public static class Add extends ChangeChanceLegendEvent {
        public Add(@Nonnull String source, float chance) {
            super(source, chance);
        }
    }

    @Cancelable
    public static class Remove extends ChangeChanceLegendEvent {
        public Remove(@Nonnull String source, float chance) {
            super(source, chance);
        }
    }
}
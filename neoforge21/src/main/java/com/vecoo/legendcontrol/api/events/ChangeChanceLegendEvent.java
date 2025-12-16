package com.vecoo.legendcontrol.api.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class ChangeChanceLegendEvent extends Event implements ICancellableEvent {
    @NotNull
    private final String source;
    private float chance;

    public static class Set extends ChangeChanceLegendEvent implements ICancellableEvent {
        public Set(@NotNull String source, float chance) {
            super(source, chance);
        }
    }

    public static class Add extends ChangeChanceLegendEvent implements ICancellableEvent {
        public Add(@NotNull String source, float chance) {
            super(source, chance);
        }
    }

    public static class Remove extends ChangeChanceLegendEvent implements ICancellableEvent {
        public Remove(@NotNull String source, float chance) {
            super(source, chance);
        }
    }
}
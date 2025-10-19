package com.vecoo.legendcontrol.api.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class ChangeChanceLegendEvent extends Event implements ICancellableEvent {
    private final String source;
    private float chance;

    public ChangeChanceLegendEvent(String source, float chance) {
        this.source = source;
        this.chance = chance;
    }

    public String getSource() {
        return this.source;
    }

    public float getChance() {
        return this.chance;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public static class Set extends ChangeChanceLegendEvent implements ICancellableEvent {
        public Set(String source, float chance) {
            super(source, chance);
        }
    }

    public static class Add extends ChangeChanceLegendEvent implements ICancellableEvent {
        public Add(String source, float chance) {
            super(source, chance);
        }
    }

    public static class Remove extends ChangeChanceLegendEvent implements ICancellableEvent {
        public Remove(String source, float chance) {
            super(source, chance);
        }
    }
}
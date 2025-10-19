package com.vecoo.legendcontrol.api.events;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ChangeChanceLegendEvent extends Event {
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

    @Cancelable
    public static class Set extends ChangeChanceLegendEvent {
        public Set(String source, float chance) {
            super(source, chance);
        }
    }

    @Cancelable
    public static class Add extends ChangeChanceLegendEvent {
        public Add(String source, float chance) {
            super(source, chance);
        }
    }

    @Cancelable
    public static class Remove extends ChangeChanceLegendEvent {
        public Remove(String source, float chance) {
            super(source, chance);
        }
    }
}
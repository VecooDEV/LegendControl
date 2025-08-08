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
    public static class setChance extends ChangeChanceLegendEvent {
        public setChance(String source, float chance) {
            super(source, chance);
        }
    }

    @Cancelable
    public static class addChance extends ChangeChanceLegendEvent {
        public addChance(String source, float chance) {
            super(source, chance);
        }
    }

    @Cancelable
    public static class removeChance extends ChangeChanceLegendEvent {
        public removeChance(String source, float chance) {
            super(source, chance);
        }
    }
}

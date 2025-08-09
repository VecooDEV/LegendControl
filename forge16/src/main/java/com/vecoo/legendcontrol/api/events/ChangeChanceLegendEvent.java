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
    public static class SetChance extends ChangeChanceLegendEvent {
        public SetChance(String source, float chance) {
            super(source, chance);
        }
    }

    @Cancelable
    public static class AddChance extends ChangeChanceLegendEvent {
        public AddChance(String source, float chance) {
            super(source, chance);
        }
    }

    @Cancelable
    public static class RemoveChance extends ChangeChanceLegendEvent {
        public RemoveChance(String source, float chance) {
            super(source, chance);
        }
    }
}

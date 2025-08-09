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

    public static class SetChance extends ChangeChanceLegendEvent implements ICancellableEvent {
        public SetChance(String source, float chance) {
            super(source, chance);
        }
    }

    public static class AddChance extends ChangeChanceLegendEvent implements ICancellableEvent {
        public AddChance(String source, float chance) {
            super(source, chance);
        }
    }

    public static class RemoveChance extends ChangeChanceLegendEvent implements ICancellableEvent {
        public RemoveChance(String source, float chance) {
            super(source, chance);
        }
    }
}
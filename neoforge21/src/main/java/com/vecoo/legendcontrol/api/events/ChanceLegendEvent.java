package com.vecoo.legendcontrol.api.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class ChanceLegendEvent extends Event implements ICancellableEvent {
    private final String source;
    private final float chance;

    public ChanceLegendEvent(String source, float chance) {
        this.source = source;
        this.chance = chance;
    }

    public String getSource() {
        return this.source;
    }

    public float getChance() {
        return this.chance;
    }
}
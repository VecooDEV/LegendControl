package com.vecoo.legendcontrol.api.events;

import com.vecoo.legendcontrol.api.LegendSourceName;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class ChanceLegendEvent extends Event implements ICancellableEvent {
    private final LegendSourceName sourceName;
    private final float chance;

    public ChanceLegendEvent(LegendSourceName sourceName, float chance) {
        this.sourceName = sourceName;
        this.chance = chance;
    }

    public LegendSourceName getSourceName() {
        return this.sourceName;
    }

    public float getChance() {
        return this.chance;
    }
}
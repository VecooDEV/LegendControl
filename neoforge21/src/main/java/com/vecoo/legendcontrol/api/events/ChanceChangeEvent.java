package com.vecoo.legendcontrol.api.events;

import com.vecoo.legendcontrol.api.LegendSourceName;
import net.neoforged.bus.api.Event;

public class ChanceChangeEvent extends Event {
    private final LegendSourceName sourceName;
    private final float chance;

    public ChanceChangeEvent(LegendSourceName sourceName, float chance) {
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

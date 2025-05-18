package com.vecoo.legendcontrol.api.events;

import com.vecoo.legendcontrol.api.LegendSourceName;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class LegendaryChanceEvent extends Event {
    private final LegendSourceName sourceName;
    private float chance;

    public LegendaryChanceEvent(LegendSourceName sourceName, float chance) {
        this.sourceName = sourceName;
        this.chance = chance;
    }

    public LegendSourceName getSourceName() {
        return this.sourceName;
    }

    public float getChance() {
        return this.chance;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }
}

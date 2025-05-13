package com.vecoo.legendcontrol.listener;

import com.pixelmonmod.pixelmon.api.events.spawning.LegendaryCheckSpawnsEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class OtherListener {
    @SubscribeEvent
    public void onLegendaryCheckSpawns(LegendaryCheckSpawnsEvent event) {
        event.shouldShowTime = false;
        event.shouldShowChance = false;
    }
}

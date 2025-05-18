package com.vecoo.legendcontrol.api.factory;

import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.events.LegendaryChanceEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.Optional;

public class LegendControlFactory {
    public static class ServerProvider {
        public static float getLegendaryChance() {
            return LegendControl.getInstance().getServerProvider().getServerStorage().getLegendaryChance();
        }

        public static String getLastLegend() {
            return LegendControl.getInstance().getServerProvider().getServerStorage().getLastLegend();
        }

        public static Optional<Float> addLegendaryChance(LegendSourceName sourceName, float legendaryChance) {
            return setLegendaryChance(sourceName, Math.min(getLegendaryChance() + legendaryChance, 100F));
        }

        public static Optional<Float> removeLegendaryChance(LegendSourceName sourceName, float legendaryChance) {
            return setLegendaryChance(sourceName, Math.max(getLegendaryChance() - legendaryChance, 0F));
        }

        public static Optional<Float> setLegendaryChance(LegendSourceName sourceName, float legendaryChance) {
            LegendaryChanceEvent event = new LegendaryChanceEvent(sourceName, legendaryChance);

            if (MinecraftForge.EVENT_BUS.post(event)) {
                return Optional.empty();
            }

            float eventAmount = event.getChance();

            LegendControl.getInstance().getServerProvider().getServerStorage().setLegendaryChance(event.getChance());

            return Optional.of(eventAmount);
        }

        public static void setLastLegend(String pokemonName) {
            LegendControl.getInstance().getServerProvider().getServerStorage().setLastLegend(pokemonName);
        }
    }
}
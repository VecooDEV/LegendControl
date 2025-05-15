package com.vecoo.legendcontrol.api.factory;

import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.events.ChanceChangeEvent;
import net.minecraftforge.common.MinecraftForge;

public class LegendControlFactory {
    public static class ServerProvider {
        public static float getLegendaryChance() {
            return LegendControl.getInstance().getServerProvider().getServerStorage().getLegendaryChance();
        }

        public static String getLastLegend() {
            return LegendControl.getInstance().getServerProvider().getServerStorage().getLastLegend();
        }

        public static void addLegendaryChance(LegendSourceName sourceName, float legendaryChance) {
            setLegendaryChance(sourceName, Math.min(getLegendaryChance() + legendaryChance, 100F));
        }

        public static void removeLegendaryChance(LegendSourceName sourceName, float legendaryChance) {
            setLegendaryChance(sourceName, Math.max(getLegendaryChance() - legendaryChance, 0F));
        }

        public static void setLegendaryChance(LegendSourceName sourceName, float legendaryChance) {
            ChanceChangeEvent event = new ChanceChangeEvent(sourceName, legendaryChance);

            if (!MinecraftForge.EVENT_BUS.post(event)) {
                LegendControl.getInstance().getServerProvider().getServerStorage().setLegendaryChance(event.getChance());
            }
        }

        public static void setLastLegend(String pokemonName) {
            LegendControl.getInstance().getServerProvider().getServerStorage().setLastLegend(pokemonName);
        }
    }
}
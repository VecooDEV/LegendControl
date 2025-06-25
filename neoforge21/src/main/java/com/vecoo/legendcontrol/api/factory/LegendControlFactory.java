package com.vecoo.legendcontrol.api.factory;

import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;

public class LegendControlFactory {
    public static class ServerProvider {
        public static float getChanceLegend() {
            return LegendControl.getInstance().getServerProvider().getServerStorage().getChanceLegend();
        }

        public static String getLastLegend() {
            return LegendControl.getInstance().getServerProvider().getServerStorage().getLastLegend();
        }

        public static void addChanceLegend(LegendSourceName sourceName, float amount, boolean update) {
            setChanceLegend(sourceName, Math.min(getChanceLegend() + amount, 100F), update);
        }

        public static void removeChanceLegend(LegendSourceName sourceName, float amount, boolean update) {
            setChanceLegend(sourceName, Math.max(getChanceLegend() - amount, 0F), update);
        }

        public static void setChanceLegend(LegendSourceName sourceName, float amount, boolean update) {
            LegendControl.getInstance().getServerProvider().getServerStorage().setChanceLegend(sourceName, amount, update);
        }

        public static void setLastLegend(String pokemonName, boolean update) {
            LegendControl.getInstance().getServerProvider().getServerStorage().setLastLegend(pokemonName, update);
        }
    }
}
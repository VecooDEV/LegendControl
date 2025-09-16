package com.vecoo.legendcontrol.api.factory;

import com.vecoo.legendcontrol.LegendControl;

public class LegendControlFactory {
    public static class ServerProvider {
        public static float getChanceLegend() {
            return LegendControl.getInstance().getServerProvider().getStorage().getChanceLegend();
        }

        public static String getLastLegend() {
            return LegendControl.getInstance().getServerProvider().getStorage().getLastLegend();
        }

        public static void setChanceLegend(String source, float amount) {
            LegendControl.getInstance().getServerProvider().getStorage().setChanceLegend(source, amount);
        }

        public static void addChanceLegend(String source, float amount) {
            LegendControl.getInstance().getServerProvider().getStorage().addChanceLegend(source, amount);
        }

        public static void removeChanceLegend(String source, float amount) {
            LegendControl.getInstance().getServerProvider().getStorage().removeChanceLegend(source, amount);
        }

        public static void setLastLegend(String pokemonName) {
            LegendControl.getInstance().getServerProvider().getStorage().setLastLegend(pokemonName);
        }
    }
}
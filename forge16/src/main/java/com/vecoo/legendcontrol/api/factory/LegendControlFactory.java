package com.vecoo.legendcontrol.api.factory;

import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.ServerStorage;

public class LegendControlFactory {
    public static class ServerProvider {
        public static float getChanceLegend() {
            return LegendControl.getInstance().getServerProvider().getServerStorage().getChanceLegend();
        }

        public static String getLastLegend() {
            return LegendControl.getInstance().getServerProvider().getServerStorage().getLastLegend();
        }

        public static void setChanceAndLastLegend(String source, float amount, String pokemonName) {
            ServerStorage storage = LegendControl.getInstance().getServerProvider().getServerStorage();

            storage.setChanceLegend(source, amount, false);
            storage.setLastLegend(pokemonName, false);

            LegendControl.getInstance().getServerProvider().updateServerStorage(storage);
        }

        public static void setChanceLegend(String source, float amount) {
            LegendControl.getInstance().getServerProvider().getServerStorage().setChanceLegend(source, amount, true);
        }

        public static void addChanceLegend(String source, float amount) {
            LegendControl.getInstance().getServerProvider().getServerStorage().addChanceLegend(source, amount, true);
        }

        public static void removeChanceLegend(String source, float amount) {
            LegendControl.getInstance().getServerProvider().getServerStorage().removeChanceLegend(source, amount, true);
        }

        public static void setLastLegend(String pokemonName) {
            LegendControl.getInstance().getServerProvider().getServerStorage().setLastLegend(pokemonName, true);
        }
    }
}
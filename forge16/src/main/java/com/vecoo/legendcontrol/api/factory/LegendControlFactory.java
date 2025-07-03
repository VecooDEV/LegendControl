package com.vecoo.legendcontrol.api.factory;

import com.vecoo.legendcontrol.LegendControl;

public class LegendControlFactory {
    public static class ServerProvider {
        public static float getChanceLegend() {
            return LegendControl.getInstance().getServerProvider().getServerStorage().getChanceLegend();
        }

        public static String getLastLegend() {
            return LegendControl.getInstance().getServerProvider().getServerStorage().getLastLegend();
        }

        public static void setChanceLegend(String source, float amount, boolean update) {
            LegendControl.getInstance().getServerProvider().getServerStorage().setChanceLegend(source, amount, update);
        }

        public static void setLastLegend(String source, boolean update) {
            LegendControl.getInstance().getServerProvider().getServerStorage().setLastLegend(source, update);
        }

        public static void addChanceLegend(String source, float amount, boolean update) {
            setChanceLegend(source, Math.min(getChanceLegend() + amount, 100F), update);
        }

        public static void removeChanceLegend(String source, float amount, boolean update) {
            setChanceLegend(source, Math.max(getChanceLegend() - amount, 0F), update);
        }
    }
}
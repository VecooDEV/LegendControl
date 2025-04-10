package com.vecoo.legendcontrol.api.factory;

import com.vecoo.legendcontrol.LegendControl;

public class LegendControlFactory {
    public static class ServerProvider {
        public static float getLegendaryChance() {
            return LegendControl.getInstance().getServerProvider().getServerStorage().getLegendaryChance();
        }

        public static void addLegendaryChance(float legendaryChance) {
            setLegendaryChance(Math.min(getLegendaryChance() + legendaryChance, 100F));
        }

        public static void removeLegendaryChance(float legendaryChance) {
            setLegendaryChance(Math.max(getLegendaryChance() - legendaryChance, 0F));
        }

        public static void setLegendaryChance(float legendaryChance) {
            LegendControl.getInstance().getServerProvider().getServerStorage().setLegendaryChance(legendaryChance);
        }
    }
}
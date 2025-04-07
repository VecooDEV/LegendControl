package com.vecoo.legendcontrol.api.factory;

import com.vecoo.legendcontrol.LegendControl;

import java.util.HashSet;
import java.util.UUID;

public class LegendControlFactory {
    public static class ServerProvider {
        public static float getLegendaryChance() {
            return LegendControl.getInstance().getServerProvider().getServerStorage().getLegendaryChance();
        }

        public static String getLastLegend() {
            return LegendControl.getInstance().getServerProvider().getServerStorage().getLastLegend();
        }

        public static HashSet<UUID> getLegends() {
            return LegendControl.getInstance().getServerProvider().getServerStorage().getLegends();
        }

        public static boolean hasLegends(UUID pokemonUUID) {
            return LegendControl.getInstance().getServerProvider().getServerStorage().getLegends().contains(pokemonUUID);
        }

        public static void addLegendaryChance(float legendaryChance) {
            setLegendaryChance(Math.min(getLegendaryChance() + legendaryChance, 100F));
        }

        public static boolean addLegends(UUID pokemonUUID) {
            if (hasLegends(pokemonUUID)) {
                return false;
            }

            LegendControl.getInstance().getServerProvider().getServerStorage().addLegends(pokemonUUID);
            return true;
        }

        public static void removeLegendaryChance(float legendaryChance) {
            setLegendaryChance(Math.max(getLegendaryChance() - legendaryChance, 0F));
        }

        public static boolean removeLegends(UUID pokemonUUID) {
            if (!hasLegends(pokemonUUID)) {
                return false;
            }

            LegendControl.getInstance().getServerProvider().getServerStorage().removeLegends(pokemonUUID);
            return true;
        }

        public static void setLegendaryChance(float legendaryChance) {
            LegendControl.getInstance().getServerProvider().getServerStorage().setLegendaryChance(legendaryChance);
        }

        public static void setLastLegend(String lastLegend) {
            LegendControl.getInstance().getServerProvider().getServerStorage().setLastLegend(lastLegend);
        }

        public static void clearLegends() {
            LegendControl.getInstance().getServerProvider().getServerStorage().clearLegends();
        }
    }
}
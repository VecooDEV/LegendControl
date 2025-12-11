package com.vecoo.legendcontrol.api.factory;

import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.events.ChangeChanceLegendEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

public class LegendControlFactory {
    public static class ServerProvider {
        public static float getChanceLegend() {
            return LegendControl.getInstance().getServerProvider().getServerStorage().getChanceLegend();
        }

        @NotNull
        public static String getLastLegend() {
            return LegendControl.getInstance().getServerProvider().getServerStorage().getLastLegend();
        }

        public static boolean setChanceLegend(@NotNull String source, float amount) {
            ChangeChanceLegendEvent.Set event = new ChangeChanceLegendEvent.Set(source, amount);

            if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
                return false;
            }

            LegendControl.getInstance().getServerProvider().getServerStorage().setChanceLegend(event.getChance());
            return true;
        }

        public static boolean addChanceLegend(@NotNull String source, float amount) {
            ChangeChanceLegendEvent.Add event = new ChangeChanceLegendEvent.Add(source, amount);

            if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
                return false;
            }

            LegendControl.getInstance().getServerProvider().getServerStorage().addChanceLegend(event.getChance());
            return true;
        }

        public static boolean removeChanceLegend(@NotNull String source, float amount) {
            ChangeChanceLegendEvent.Remove event = new ChangeChanceLegendEvent.Remove(source, amount);

            if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
                return false;
            }

            LegendControl.getInstance().getServerProvider().getServerStorage().removeChanceLegend(event.getChance());
            return true;
        }

        public static void setLastLegend(@NotNull String pokemonName) {
            LegendControl.getInstance().getServerProvider().getServerStorage().setLastLegend(pokemonName);
        }
    }
}
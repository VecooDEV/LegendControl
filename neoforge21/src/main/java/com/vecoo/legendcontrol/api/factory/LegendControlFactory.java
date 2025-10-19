package com.vecoo.legendcontrol.api.factory;

import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.events.ChangeChanceLegendEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

public class LegendControlFactory {
    public static class ServerProvider {
        public static float getChanceLegend() {
            return LegendControl.getInstance().getServerProvider().getStorage().getChanceLegend();
        }

        @NotNull
        public static String getLastLegend() {
            return LegendControl.getInstance().getServerProvider().getStorage().getLastLegend();
        }

        public static boolean setChanceLegend(@NotNull String source, float amount) {
            ChangeChanceLegendEvent.Set event = new ChangeChanceLegendEvent.Set(source, amount);

            if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
                return false;
            }

            LegendControl.getInstance().getServerProvider().getStorage().setChanceLegend(event.getChance());
            return true;
        }

        public static boolean addChanceLegend(@NotNull String source, float amount) {
            ChangeChanceLegendEvent.Add event = new ChangeChanceLegendEvent.Add(source, amount);

            if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
                return false;
            }

            LegendControl.getInstance().getServerProvider().getStorage().addChanceLegend(event.getChance());
            return true;
        }

        public static boolean removeChanceLegend(@NotNull String source, float amount) {
            ChangeChanceLegendEvent.Remove event = new ChangeChanceLegendEvent.Remove(source, amount);

            if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
                return false;
            }

            LegendControl.getInstance().getServerProvider().getStorage().removeChanceLegend(event.getChance());
            return true;
        }

        public static void setLastLegend(@NotNull String pokemonName) {
            LegendControl.getInstance().getServerProvider().getStorage().setLastLegend(pokemonName);
        }
    }
}
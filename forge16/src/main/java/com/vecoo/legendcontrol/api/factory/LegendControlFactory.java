package com.vecoo.legendcontrol.api.factory;

import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.events.ChangeChanceLegendEvent;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;

public class LegendControlFactory {
    public static class ServerProvider {
        public static float getChanceLegend() {
            return LegendControl.getInstance().getServerProvider().getStorage().getChanceLegend();
        }

        @Nonnull
        public static String getLastLegend() {
            return LegendControl.getInstance().getServerProvider().getStorage().getLastLegend();
        }

        public static boolean setChanceLegend(@Nonnull String source, float amount) {
            ChangeChanceLegendEvent.Set event = new ChangeChanceLegendEvent.Set(source, amount);

            if (MinecraftForge.EVENT_BUS.post(event)) {
                return false;
            }

            LegendControl.getInstance().getServerProvider().getStorage().setChanceLegend(event.getChance());
            return true;
        }

        public static boolean addChanceLegend(@Nonnull String source, float amount) {
            ChangeChanceLegendEvent.Add event = new ChangeChanceLegendEvent.Add(source, amount);

            if (MinecraftForge.EVENT_BUS.post(event)) {
                return false;
            }

            LegendControl.getInstance().getServerProvider().getStorage().addChanceLegend(event.getChance());
            return true;
        }

        public static boolean removeChanceLegend(@Nonnull String source, float amount) {
            ChangeChanceLegendEvent.Remove event = new ChangeChanceLegendEvent.Remove(source, amount);

            if (MinecraftForge.EVENT_BUS.post(event)) {
                return false;
            }

            LegendControl.getInstance().getServerProvider().getStorage().removeChanceLegend(event.getChance());
            return true;
        }

        public static void setLastLegend(@Nonnull String pokemonName) {
            LegendControl.getInstance().getServerProvider().getStorage().setLastLegend(pokemonName);
        }
    }
}
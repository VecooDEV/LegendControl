package com.vecoo.legendcontrol.api.service;

import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.events.ChangeChanceLegendEvent;
import lombok.val;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;

public class LegendControlService {
    public static float getChanceLegend() {
        return LegendControl.getInstance().getServerService().getStorage().getChanceLegend();
    }

    @Nonnull
    public static String getLastLegend() {
        return LegendControl.getInstance().getServerService().getStorage().getLastLegend();
    }

    public static boolean setChanceLegend(@Nonnull String source, float amount) {
        val event = new ChangeChanceLegendEvent.Set(source, amount);

        if (MinecraftForge.EVENT_BUS.post(event)) {
            return false;
        }

        LegendControl.getInstance().getServerService().getStorage().setChanceLegend(event.getChance());
        return true;
    }

    public static boolean addChanceLegend(@Nonnull String source, float amount) {
        val event = new ChangeChanceLegendEvent.Add(source, amount);

        if (MinecraftForge.EVENT_BUS.post(event)) {
            return false;
        }

        LegendControl.getInstance().getServerService().getStorage().addChanceLegend(event.getChance());
        return true;
    }

    public static boolean removeChanceLegend(@Nonnull String source, float amount) {
        val event = new ChangeChanceLegendEvent.Remove(source, amount);

        if (MinecraftForge.EVENT_BUS.post(event)) {
            return false;
        }

        LegendControl.getInstance().getServerService().getStorage().removeChanceLegend(event.getChance());
        return true;
    }

    public static void setLastLegend(@Nonnull String pokemonName) {
        LegendControl.getInstance().getServerService().getStorage().setLastLegend(pokemonName);
    }
}
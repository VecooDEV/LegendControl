package com.vecoo.legendcontrol.api.service;

import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.events.ChangeChanceLegendEvent;
import lombok.val;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

public class LegendControlService {
    public static float getChanceLegend() {
        return LegendControl.getInstance().getServerService().getStorage().getChanceLegend();
    }

    @NotNull
    public static String getLastLegend() {
        return LegendControl.getInstance().getServerService().getStorage().getLastLegend();
    }

    public static boolean setChanceLegend(@NotNull String source, float amount) {
        val event = new ChangeChanceLegendEvent.Set(source, amount);

        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return false;
        }

        LegendControl.getInstance().getServerService().getStorage().setChanceLegend(event.getChance());
        return true;
    }

    public static boolean addChanceLegend(@NotNull String source, float amount) {
        val event = new ChangeChanceLegendEvent.Add(source, amount);

        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return false;
        }

        LegendControl.getInstance().getServerService().getStorage().addChanceLegend(event.getChance());
        return true;
    }

    public static boolean removeChanceLegend(@NotNull String source, float amount) {
        val event = new ChangeChanceLegendEvent.Remove(source, amount);

        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return false;
        }

        LegendControl.getInstance().getServerService().getStorage().removeChanceLegend(event.getChance());
        return true;
    }

    public static void setLastLegend(@NotNull String pokemonName) {
        LegendControl.getInstance().getServerService().getStorage().setLastLegend(pokemonName);
    }
}
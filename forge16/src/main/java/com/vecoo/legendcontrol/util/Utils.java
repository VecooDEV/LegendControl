package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.legendcontrol.LegendControl;

public class Utils {
    public static int timeDoLegend = RandomHelper.getRandomNumberBetween(LegendControl.getInstance().getConfig().getRandomTimeSpawnMin(), LegendControl.getInstance().getConfig().getRandomTimeSpawnMax());
    public static int countSpawn = 0;

    public static void doSpawn() {
        if (countSpawn >= 3) {
            return;
        }

        if (ExtraLib.getInstance().getServer().getPlayerList().getPlayerCount() == 0) {
            return;
        }

        PixelmonSpawning.legendarySpawner.forcefullySpawn(null);

        countSpawn++;
    }
}
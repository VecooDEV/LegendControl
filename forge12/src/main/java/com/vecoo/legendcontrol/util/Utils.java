package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.RandomHelper;
import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.server.MinecraftServer;

public class Utils {
    public static int timeDoLegend = RandomHelper.getRandomNumberBetween(LegendControl.getInstance().getConfig().getRandomTimeSpawnMin(), LegendControl.getInstance().getConfig().getRandomTimeSpawnMax());
    public static int countSpawn = 0;

    public static String worldDirectory(String file) {
        MinecraftServer server = ExtraLib.getInstance().getServer();
        if (server.isDedicatedServer()) {
            return file.replace("%directory%", "world");
        } else {
            return file.replace("%directory%", "saves/" + server.getFolderName());
        }
    }

    public static void doSpawn() {
        if (countSpawn >= 3) {
            return;
        }

        if (ExtraLib.getInstance().getServer().getPlayerList().getCurrentPlayerCount() == 0) {
            return;
        }

        PixelmonSpawning.legendarySpawner.forcefullySpawn(null);

        countSpawn++;
    }
}
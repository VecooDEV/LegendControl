package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

public class Utils {
    public static int timeDoLegend = RandomHelper.getRandomNumberBetween(LegendControl.getInstance().getConfig().getRandomTimeSpawnMin(), LegendControl.getInstance().getConfig().getRandomTimeSpawnMax());
    public static int countSpawn = 0;

    public static String worldDirectory(String file) {
        MinecraftServer server = ExtraLib.getInstance().getServer();
        if (server.isDedicatedServer()) {
            return file.replace("%directory%", "world");
        } else {
            String directory = server.getWorldPath(new LevelResource("")).toString().replace("\\", "/");
            return file.replace("%directory%", "saves/" + directory.substring(directory.lastIndexOf("/") + 1));
        }
    }

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
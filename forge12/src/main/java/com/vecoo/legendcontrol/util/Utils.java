package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.RandomHelper;
import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Utils {
    public static int timeDoLegend = RandomHelper.getRandomNumberBetween(LegendControl.getInstance().getConfig().getRandomTimeSpawnMin(), LegendControl.getInstance().getConfig().getRandomTimeSpawnMax());
    public static int countSpawn = 0;

    public static int playerCountIP(EntityPlayerMP player, ArrayList<ArrayList<EntityPlayerMP>> playersList) {
        int ipCount = 0;

        for (ArrayList<EntityPlayerMP> players : playersList) {
            for (EntityPlayerMP p : players) {
                if (player.getPlayerIP().equals(p.getPlayerIP())) {
                    ipCount++;
                }
            }
        }
        return ipCount;
    }

    public static void updatePlayerIP(EntityPlayerMP player) {
        HashMap<UUID, String> playersIP = LegendControl.getInstance().getServerProvider().getServerStorage().getPlayersIP();
        if (playersIP.containsKey(player.getUniqueID())) {
            playersIP.replace(player.getUniqueID(), player.getPlayerIP());
        }
    }

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

        System.out.println("DoSpawn");

        PixelmonSpawning.legendarySpawner.forcefullySpawn(null);

        countSpawn++;
    }
}
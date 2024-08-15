package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.UUID;

public class Utils {
    public static int timeDoLegend = RandomHelper.getRandomNumberBetween(LegendControl.getInstance().getConfig().getRandomTimeSpawnMin(), LegendControl.getInstance().getConfig().getRandomTimeSpawnMax());

    public static int playerCountIP(ServerPlayer player) {
        int ipCount = 0;

        for (ServerPlayer p : ExtraLib.getInstance().getServer().getPlayerList().getPlayers()) {
            if (player.getIpAddress().equals(p.getIpAddress())) {
                ipCount++;
            }
        }
        return ipCount;
    }

    public static void updatePlayerIP(ServerPlayer player) {
        HashMap<UUID, String> playersIP = LegendControl.getInstance().getServerProvider().getServerStorage().getPlayersIP();
        if (playersIP.containsKey(player.getUUID())) {
            playersIP.replace(player.getUUID(), player.getIpAddress());
        }
    }
}
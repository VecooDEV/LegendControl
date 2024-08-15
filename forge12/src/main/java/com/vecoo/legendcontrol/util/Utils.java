package com.vecoo.legendcontrol.util;

import com.pixelmonmod.pixelmon.RandomHelper;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.HashMap;
import java.util.UUID;

public class Utils {
    public static int timeDoLegend = PixelmonConfig.legendaryDespawnTicks / 20 + RandomHelper.getRandomNumberBetween(LegendControl.getInstance().getConfig().getRandomTimeSpawnMin(), LegendControl.getInstance().getConfig().getRandomTimeSpawnMax());

    public static int playerCountIP(EntityPlayerMP player) {
        int ipCount = 0;

        for (EntityPlayerMP p : ExtraLib.getInstance().getServer().getPlayerList().getPlayers()) {
            if (player.getPlayerIP().equals(p.getPlayerIP())) {
                ipCount++;
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
}
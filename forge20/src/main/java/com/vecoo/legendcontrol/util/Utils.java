package com.vecoo.legendcontrol.util;

import com.vecoo.legendcontrol.LegendControl;
import net.minecraft.server.level.ServerPlayer;

public class Utils {
    public static int playerCountIP(ServerPlayer player) {
        int ipCount = 0;

        for (ServerPlayer p : LegendControl.getInstance().getServer().getPlayerList().getPlayers()) {
            if (player.getIpAddress().equals(p.getIpAddress())) {
                ipCount++;
            }
        }
        return ipCount;
    }
}